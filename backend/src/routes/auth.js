const express = require("express");
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");
const db = require("../db");

const router = express.Router();

const getAccountByNumber = db.prepare("SELECT * FROM accounts WHERE account_number = ?");
const insertAccount = db.prepare(`
  INSERT INTO accounts
    (account_number, account_holder_name, password_hash, national_id, branch_code, branch_name, account_tier, daily_limit_cents, balance_cents)
  VALUES (@accountNumber, @accountHolderName, @passwordHash, @nationalId, @branchCode, @branchName, @accountTier, @dailyLimitCents, @balanceCents)
`);
const insertResetToken = db.prepare(
  "INSERT INTO pin_reset_tokens (account_number, token, created_at_millis) VALUES (?, ?, ?)"
);
const latestResetToken = db.prepare(
  "SELECT token FROM pin_reset_tokens WHERE account_number = ? ORDER BY created_at_millis DESC LIMIT 1"
);
const updatePassword = db.prepare("UPDATE accounts SET password_hash = ? WHERE account_number = ?");

function issueToken(account) {
  return jwt.sign(
    { accountId: account.id, accountNumber: account.account_number },
    process.env.JWT_SECRET,
    { expiresIn: "2h" }
  );
}

// Vuln #18: credentials travel as URL query params, not a request body.
// They land in access logs, proxy logs, and shell/browser history verbatim.
// Bonus: no rate limiting, so this is brute-forceable with no lockout.
router.get("/login", (req, res) => {
  const { accountNumber, password } = req.query;
  const account = getAccountByNumber.get(accountNumber);
  if (!account || !bcrypt.compareSync(String(password || ""), account.password_hash)) {
    return res.status(401).json({ error: "Invalid account number or password" });
  }
  res.json({
    token: issueToken(account),
    accountNumber: account.account_number,
    accountHolderName: account.account_holder_name
  });
});

// Vuln #20: mass assignment. dailyLimitCents and accountTier are bound
// straight from the client instead of being forced to safe server defaults,
// so a registration request can grant itself PLATINUM tier and a huge limit.
router.post("/register", (req, res) => {
  const body = req.body || {};
  if (!body.accountNumber || !body.password || !body.accountHolderName) {
    return res.status(400).json({ error: "accountNumber, password, accountHolderName are required" });
  }
  try {
    insertAccount.run({
      accountNumber: body.accountNumber,
      accountHolderName: body.accountHolderName,
      passwordHash: bcrypt.hashSync(String(body.password), 10),
      nationalId: body.nationalId || "00000000",
      branchCode: body.branchCode || "KSB-001",
      branchName: body.branchName || "Nairobi CBD",
      accountTier: body.accountTier || "STANDARD",
      dailyLimitCents: body.dailyLimitCents ?? 1_500_000,
      balanceCents: 0
    });
  } catch (err) {
    return res.status(409).json({ error: "Account number already exists" });
  }
  const account = getAccountByNumber.get(body.accountNumber);
  res.status(201).json({
    accountNumber: account.account_number,
    accountHolderName: account.account_holder_name,
    accountTier: account.account_tier,
    dailyLimitCents: account.daily_limit_cents
  });
});

// Bonus finding folded into #19/#20: a weak, predictable 4-digit reset code
// with no rate limiting on verification, brute-forceable in at most 10,000
// requests, trivial with Burp Intruder. The code is logged server-side
// (standing in for an SMS/email provider) rather than returned in the
// response, so a remote attacker genuinely has to guess it.
router.post("/request-pin-reset", (req, res) => {
  const { accountNumber } = req.body || {};
  const account = getAccountByNumber.get(accountNumber);
  if (account) {
    const token = String(Math.floor(Math.random() * 10000)).padStart(4, "0");
    insertResetToken.run(accountNumber, token, Date.now());
    console.log(`[pin-reset] code for ${accountNumber}: ${token}`);
  }
  res.json({ message: "If this account exists, a reset code was sent." });
});

router.post("/verify-pin-reset", (req, res) => {
  const { accountNumber, token, newPassword } = req.body || {};
  const account = getAccountByNumber.get(accountNumber);
  const latest = account ? latestResetToken.get(accountNumber) : null;
  if (!account || !latest || latest.token !== token) {
    return res.status(401).json({ error: "Invalid reset code" });
  }
  updatePassword.run(bcrypt.hashSync(String(newPassword || ""), 10), accountNumber);
  res.json({ message: "Password updated." });
});

module.exports = router;

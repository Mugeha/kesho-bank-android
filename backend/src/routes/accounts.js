const express = require("express");
const db = require("../db");
const { requireAuth } = require("../middleware/auth");

const router = express.Router();

const getAccountById = db.prepare("SELECT * FROM accounts WHERE id = ?");
const getTransactionsByAccount = db.prepare(
  "SELECT * FROM transactions WHERE account_id = ? ORDER BY timestamp_millis DESC"
);

// Vuln #19: requireAuth only checks the token is validly signed. It never
// confirms req.user.accountId matches the :accountId in the URL, so any logged
// in user can page through every other account's statement just by
// incrementing the ID:
//   curl -H "Authorization: Bearer <any valid token>" \
//     http://localhost:4000/api/accounts/2/statement
router.get("/:accountId/statement", requireAuth, (req, res) => {
  const account = getAccountById.get(req.params.accountId);
  if (!account) {
    return res.status(404).json({ error: "Account not found" });
  }
  const transactions = getTransactionsByAccount.all(account.id).map((t) => ({
    id: String(t.id),
    counterpartyName: t.counterparty_name,
    amountCents: t.amount_cents,
    memo: t.memo,
    timestampMillis: t.timestamp_millis
  }));
  res.json({ accountId: String(account.id), transactions });
});

module.exports = router;

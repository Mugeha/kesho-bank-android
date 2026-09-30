const bcrypt = require("bcryptjs");
const db = require("./db");

// Synthetic data only, matching the Android app's own DemoDataSeeder.
// Two accounts on purpose: 4010312345 is the credential the Android app
// ships with, and 4010999999 exists so vuln #19 (IDOR) has a genuine
// second account's real data to leak, not just a theoretical parameter.
const accounts = [
  {
    accountNumber: "4010312345",
    accountHolderName: "Amani Kariuki",
    password: "Kesho@Demo2026",
    nationalId: "24601837",
    branchCode: "KSB-001",
    branchName: "Nairobi CBD",
    accountTier: "STANDARD",
    dailyLimitCents: 1_500_000,
    balanceCents: 284_500_00
  },
  {
    accountNumber: "4010999999",
    accountHolderName: "Njeri Otieno",
    password: "Kesho@Victim2026",
    nationalId: "31820456",
    branchCode: "KSB-022",
    branchName: "Mombasa Nyali",
    accountTier: "GOLD",
    dailyLimitCents: 3_000_000,
    balanceCents: 917_200_00
  }
];

const insertAccount = db.prepare(`
  INSERT OR IGNORE INTO accounts
    (account_number, account_holder_name, password_hash, national_id, branch_code, branch_name, account_tier, daily_limit_cents, balance_cents)
  VALUES (@accountNumber, @accountHolderName, @passwordHash, @nationalId, @branchCode, @branchName, @accountTier, @dailyLimitCents, @balanceCents)
`);

const insertTransaction = db.prepare(`
  INSERT INTO transactions
    (account_id, type, counterparty_name, counterparty_account_number, amount_cents, memo, timestamp_millis, status)
  VALUES (@accountId, @type, @counterpartyName, @counterpartyAccountNumber, @amountCents, @memo, @timestampMillis, 'COMPLETED')
`);

const getAccountByNumber = db.prepare("SELECT id FROM accounts WHERE account_number = ?");

for (const account of accounts) {
  insertAccount.run({
    accountNumber: account.accountNumber,
    accountHolderName: account.accountHolderName,
    passwordHash: bcrypt.hashSync(account.password, 10),
    nationalId: account.nationalId,
    branchCode: account.branchCode,
    branchName: account.branchName,
    accountTier: account.accountTier,
    dailyLimitCents: account.dailyLimitCents,
    balanceCents: account.balanceCents
  });

  const row = getAccountByNumber.get(account.accountNumber);
  const now = Date.now();
  const sampleTxns = [
    { type: "CREDIT", counterpartyName: "Bidii Sacco", counterpartyAccountNumber: "4010991045", amountCents: 5_000_00, memo: "Dividend payout" },
    { type: "DEBIT", counterpartyName: "Nuru Utilities Co.", counterpartyAccountNumber: "4010102938", amountCents: 1_200_00, memo: "Electricity bill" },
    { type: "DEBIT", counterpartyName: "Safari Mart Supermarket", counterpartyAccountNumber: "4010778213", amountCents: 3_450_00, memo: "Groceries" }
  ];
  sampleTxns.forEach((txn, i) => {
    insertTransaction.run({
      accountId: row.id,
      ...txn,
      timestampMillis: now - i * 86_400_000
    });
  });
}

console.log("Seeded demo accounts:", accounts.map((a) => a.accountNumber).join(", "));

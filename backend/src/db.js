const path = require("path");
const fs = require("fs");
const Database = require("better-sqlite3");

const DB_PATH = process.env.DB_PATH || path.join(__dirname, "..", "data", "kesho.db");
fs.mkdirSync(path.dirname(DB_PATH), { recursive: true });

const db = new Database(DB_PATH);
db.pragma("journal_mode = WAL");

db.exec(`
  CREATE TABLE IF NOT EXISTS accounts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    account_number TEXT UNIQUE NOT NULL,
    account_holder_name TEXT NOT NULL,
    password_hash TEXT NOT NULL,
    national_id TEXT NOT NULL,
    branch_code TEXT NOT NULL,
    branch_name TEXT NOT NULL,
    account_tier TEXT NOT NULL DEFAULT 'STANDARD',
    daily_limit_cents INTEGER NOT NULL DEFAULT 1500000,
    balance_cents INTEGER NOT NULL DEFAULT 0,
    currency TEXT NOT NULL DEFAULT 'KES'
  );

  CREATE TABLE IF NOT EXISTS transactions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    account_id INTEGER NOT NULL REFERENCES accounts(id),
    type TEXT NOT NULL,
    counterparty_name TEXT NOT NULL,
    counterparty_account_number TEXT,
    amount_cents INTEGER NOT NULL,
    currency TEXT NOT NULL DEFAULT 'KES',
    memo TEXT,
    timestamp_millis INTEGER NOT NULL,
    status TEXT NOT NULL DEFAULT 'COMPLETED'
  );

  CREATE TABLE IF NOT EXISTS pin_reset_tokens (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    account_number TEXT NOT NULL,
    token TEXT NOT NULL,
    created_at_millis INTEGER NOT NULL
  );
`);

module.exports = db;

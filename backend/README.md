# Kesho Bank: optional backend

Small Node/Express + SQLite service backing the three vulnerabilities that
need a real server to be honest rather than simulated: #19 (IDOR on the
statement endpoint), #20 (mass assignment on registration), and #18
(credentials sent via URL on login). It also carries a JWT-secret leak and
an unrate-limited, weak PIN-reset flow, folded into the same write-ups.

The Android app works fully without this running. It only affects the
Statement/Support screens and the six backend-tied findings. See the root
`docs/VULNERABILITY_CATALOG.md` for the full list; see
`kesho-bank-operator-docs/SOLUTIONS.md` for exploitation write-ups.

## Run it

```bash
cp .env.example .env
docker compose up --build
```

The API listens on `http://localhost:4000` (`http://10.0.2.2:4000` from an
Android emulator, which is what the app's Settings screen defaults to).

Two demo accounts are seeded on first boot:

| Account number | Password |
|---|---|
| `4010312345` | `Kesho@Demo2026` (matches the Android app's login screen) |
| `4010999999` | `Kesho@Victim2026` |

## Local (non-Docker) run

```bash
npm install
cp .env.example .env
npm run seed
npm start
```

## Reset

`scripts/reset.sh` wipes the backend's data (any accounts created via mass
assignment, changed balances from transfers, PIN-reset password changes)
and re-seeds it back to the two fixed demo accounts above. Works whether
you're running via Docker or locally.

```bash
./scripts/reset.sh
```

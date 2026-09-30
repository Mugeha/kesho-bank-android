#!/usr/bin/env bash
set -euo pipefail

# Wipes the backend's data (including anything created via mass assignment,
# PIN resets, or transfers) and re-seeds it back to the two fixed demo
# accounts. Mirrors Zenith HR's reset.sh.

cd "$(dirname "$0")/.."

if docker compose ps --quiet backend >/dev/null 2>&1 && [ -n "$(docker compose ps --quiet backend)" ]; then
  echo "Stopping backend and wiping its data volume..."
  docker compose down -v
  echo "Rebuilding and starting fresh (this re-seeds automatically on boot)..."
  docker compose up -d --build
  echo "Done. Backend is back to the two seeded demo accounts."
else
  echo "No running Docker container found, resetting the local (non-Docker) database instead..."
  rm -f data/kesho.db data/kesho.db-shm data/kesho.db-wal
  npm run seed
  echo "Done. Local database is back to the two seeded demo accounts."
fi

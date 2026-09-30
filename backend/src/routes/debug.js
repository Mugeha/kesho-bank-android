const express = require("express");

const router = express.Router();

// Bonus finding folded into #19/#20's write-up: an unauthenticated "health
// check" endpoint that dumps process env, including the JWT signing secret.
// Anyone who finds this can forge a token for any accountId/accountNumber
// and hit every authenticated route as any user, including the IDOR
// statement endpoint, with no need to ever log in for real.
router.get("/status", (req, res) => {
  res.json({
    status: "ok",
    uptimeSeconds: process.uptime(),
    env: {
      NODE_ENV: process.env.NODE_ENV || "development",
      JWT_SECRET: process.env.JWT_SECRET,
      DB_PATH: process.env.DB_PATH
    }
  });
});

module.exports = router;

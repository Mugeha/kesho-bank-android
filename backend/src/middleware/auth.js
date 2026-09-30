const jwt = require("jsonwebtoken");

// Verifies the token is validly signed and unexpired, nothing more. It does
// not (and is never asked to) check that the caller owns the resource being
// requested; that's left entirely to each route handler. accounts.js's
// statement route is the one that skips it, landing vuln #19.
function requireAuth(req, res, next) {
  const header = req.headers.authorization || "";
  const token = header.startsWith("Bearer ") ? header.slice(7) : null;
  if (!token) {
    return res.status(401).json({ error: "Missing bearer token" });
  }
  try {
    req.user = jwt.verify(token, process.env.JWT_SECRET);
    next();
  } catch (err) {
    res.status(401).json({ error: "Invalid or expired token" });
  }
}

module.exports = { requireAuth };

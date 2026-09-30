require("dotenv").config();
const express = require("express");
const cors = require("cors");

const authRoutes = require("./routes/auth");
const accountsRoutes = require("./routes/accounts");
const debugRoutes = require("./routes/debug");

if (!process.env.JWT_SECRET) {
  console.error("JWT_SECRET is not set. Copy .env.example to .env first.");
  process.exit(1);
}

const app = express();
app.use(cors());
app.use(express.json());

app.use("/api/auth", authRoutes);
app.use("/api/accounts", accountsRoutes);
app.use("/api/debug", debugRoutes);

app.get("/", (req, res) => {
  res.json({ service: "kesho-bank-backend", status: "ok" });
});

const port = process.env.PORT || 4000;
app.listen(port, () => {
  console.log(`Kesho Bank backend listening on port ${port}`);
});

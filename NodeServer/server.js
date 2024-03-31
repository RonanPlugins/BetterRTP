const express = require("express");
const path = require("path");

const app = require("./routes/app.js");
const port = 3001;

app.listen(port, () => {
  console.log(`BetterRTP Server online at http://localhost:${port}`);
});

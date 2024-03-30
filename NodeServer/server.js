const express = require("express");

const app = express();
const port = 3001;

app.use(express.static("public"));

app.get("/api/servers", (req, res) => {
  res.send("Test");
});

app.listen(port, () => {
  console.log(`BetterRTP Server online at http://localhost:${port}`);
});

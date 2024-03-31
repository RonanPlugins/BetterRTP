const router = require("express").Router();
const path = require("path");

const rtp = require("./rtp");

router.use("/rtp", rtp);

router.get("/", (req, res) => {
  res.sendFile(path(__dirname, "../public/api/index.html"));
});

module.exports = router;

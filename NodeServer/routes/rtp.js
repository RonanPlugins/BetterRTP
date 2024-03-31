const rtpdata = require("express").Router();

rtpdata.post("/", (req, res) => {
  const validate = () =>
    new Promise((resolve, reject) => {
      const json = req.body;
      if (json !== undefined) {
        resolve(json);
      } else {
        reject(new Error("No Json"));
      }
    });
  validate()
    .then((data) => {
      console.log("Validate:", data);
      res.json({
        added: true,
      });
    })
    .catch((err) => {
      console.log("Error:", err);
      res.json({
        added: false,
      });
    });
});

module.exports = rtpdata;

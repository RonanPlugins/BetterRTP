const express = require("express");
const path = require("path");

const app = express();

const api = require("./index.js");

//Middleware
//Allows to send json body
app.use(express.json());

// app.use((req, red, next) => {
//   const yellow = "\x1b[33m%s\x1b[0m";

//   // Log out the request type and resource
//   console.log(yellow, `${req.method} request to ${req.path}`);
//   next();
// });

app.use(express.static("public"));

//Routes
//Send all requests starting with /api to index.js to routes folder
app.use("/api", api);

//https://localhost route to default page
app.get("/", (req, res) => {
  res.sendFile(path.join(__dirname, "/public/index.html"));
});

module.exports = app;

const express = require("express");
const path = require("path");
const app = express();

const http = require("http").createServer(app);
http.listen(8080, function () {
  console.log("listening in 8080");
});

app.use(express.static(path.join(__dirname, "build")));

app.get("/", function (require, response) {
  response.sendFile(path.join(__dirname, "build/index.html"));
});

app.get("*", function (require, response) {
  response.sendFile(path.join(__dirname, "build/index.html"));
});

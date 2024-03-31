const request = require("supertest");
const app = require("../routes/app.js");

describe("GET /api", () => {
  it("responds with html file", async () => {
    const res = await request(app).get("/api");
    // console.log(res.header);
    expect(res.header["content-type"]).toBe("text/html; charset=UTF-8");
  });
});

describe("POST /api/rtp", () => {
  it("responds with json and `added = false`", async () => {
    const res = await request(app).post("/api/rtp");
    expect(res.header["content-type"]).toBe("application/json; charset=utf-8");
    expect(res.body.added).toBe(false);
  });
  it("responds with json and `added = true`", async () => {
    const res = await request(app).post("/api/rtp").send({
      playerId: "test123",
    });
    expect(res.header["content-type"]).toBe("application/json; charset=utf-8");
    expect(res.body.added).toBe(true);
  });
});

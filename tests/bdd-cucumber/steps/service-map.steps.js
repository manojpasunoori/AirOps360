const assert = require("node:assert/strict");
const request = require("supertest");
const { When, Then, Before } = require("@cucumber/cucumber");
const { createApp } = require("../../../services/api-gateway/src/app");

Before(function () {
  this.app = createApp();
  this.response = null;
});

When("I request the service map from the API gateway", async function () {
  this.response = await request(this.app.callback()).get("/api/services");
});

Then("the response status should be {int}", function (statusCode) {
  assert.equal(this.response.status, statusCode);
});

Then("the gateway response should include the flight service URL", function () {
  assert.equal(this.response.body.services.flightService, "http://localhost:8081");
});

Then("the gateway response should include the simulator service URL", function () {
  assert.equal(this.response.body.services.simulatorService, "http://localhost:8001");
});

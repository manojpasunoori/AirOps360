const { test, expect } = require("@playwright/test");

test("operations dashboard renders core sections", async ({ page }) => {
  await page.goto("/");

  await expect(page.getByRole("heading", { name: "Flight turns, arrivals, and gate pressure in one live board." })).toBeVisible();
  await expect(page.getByRole("heading", { name: "Inbound operations board" })).toBeVisible();
  await expect(page.getByRole("heading", { name: "Receiving posture by storage zone" })).toBeVisible();
  await expect(page.getByRole("heading", { name: "Sorter flow and exception posture" })).toBeVisible();
  await expect(page.getByText("Cargo Status")).toBeVisible();
  await expect(page.getByText("Operational Alerts")).toBeVisible();
});

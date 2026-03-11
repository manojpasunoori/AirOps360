const config = {
  port: Number.parseInt(process.env.PORT ?? "3000", 10),
  services: {
    flightService: process.env.FLIGHT_SERVICE_URL ?? "http://localhost:8081",
    warehouseService: process.env.WAREHOUSE_SERVICE_URL ?? "http://localhost:8082",
    inventoryService: process.env.INVENTORY_SERVICE_URL ?? "http://localhost:8083",
    cargoService: process.env.CARGO_SERVICE_URL ?? "http://localhost:8000",
    simulatorService: process.env.SIMULATOR_SERVICE_URL ?? "http://localhost:8001",
  },
};

module.exports = config;

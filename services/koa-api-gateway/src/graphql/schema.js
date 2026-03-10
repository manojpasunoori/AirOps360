const { gql } = require('graphql-tag');
const axios = require('axios');

const WMS_URL = process.env.WMS_SERVICE_URL || 'http://localhost:8081';
const CARGO_URL = process.env.CARGO_SERVICE_URL || 'http://localhost:8082';

const typeDefs = gql`
  type InventoryItem {
    id: ID!
    sku: String!
    description: String!
    category: String!
    quantity: Int!
    status: String!
    locationCode: String
  }

  type CargoShipment {
    id: ID!
    trackingNo: String!
    flightNo: String
    origin: String!
    destination: String!
    weightKg: Float!
    status: String!
    priority: String!
  }

  type InventorySummary {
    zone: String!
    totalItems: Int!
    totalQuantity: Int!
    utilizationPct: Float!
  }

  type DashboardStats {
    totalInventoryItems: Int!
    lowStockAlerts: Int!
    activeShipments: Int!
    pendingBags: Int!
  }

  type Query {
    inventoryItem(sku: String!): InventoryItem
    allInventoryItems: [InventoryItem!]!
    inventorySummary: [InventorySummary!]!
    lowStockItems(threshold: Int): [InventoryItem!]!
    shipment(trackingNo: String!): CargoShipment
    dashboardStats: DashboardStats!
  }

  type Mutation {
    receiveInventory(
      sku: String!
      description: String!
      category: String!
      quantity: Int!
      locationId: ID!
      operatorId: String!
      referenceNo: String!
    ): InventoryItem

    pickInventory(
      sku: String!
      quantity: Int!
      operatorId: String!
      referenceNo: String!
    ): Boolean!
  }
`;

const resolvers = {
  Query: {
    inventoryItem: async (_, { sku }) => {
      const res = await axios.get(`${WMS_URL}/api/v1/inventory/item/${sku}`);
      return res.data;
    },
    allInventoryItems: async () => {
      const res = await axios.get(`${WMS_URL}/api/v1/inventory/items`);
      return res.data;
    },
    inventorySummary: async () => {
      const res = await axios.get(`${WMS_URL}/api/v1/inventory/summary`);
      return res.data;
    },
    lowStockItems: async (_, { threshold = 10 }) => {
      const res = await axios.get(`${WMS_URL}/api/v1/inventory/low-stock?threshold=${threshold}`);
      return res.data;
    },
    dashboardStats: async () => {
      // Aggregate from multiple services
      const [inventoryRes, lowStockRes] = await Promise.all([
        axios.get(`${WMS_URL}/api/v1/inventory/items`).catch(() => ({ data: [] })),
        axios.get(`${WMS_URL}/api/v1/inventory/low-stock`).catch(() => ({ data: [] })),
      ]);
      return {
        totalInventoryItems: inventoryRes.data.length || 0,
        lowStockAlerts: lowStockRes.data.length || 0,
        activeShipments: 0,
        pendingBags: 0,
      };
    },
  },
  Mutation: {
    receiveInventory: async (_, args) => {
      const res = await axios.post(`${WMS_URL}/api/v1/inventory/receive`, args);
      return res.data;
    },
    pickInventory: async (_, args) => {
      const res = await axios.post(`${WMS_URL}/api/v1/inventory/pick`, args);
      return res.data.success;
    },
  },
};

module.exports = { typeDefs, resolvers };

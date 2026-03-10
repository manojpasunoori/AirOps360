# ── Stage 1: Install deps ────────────────────────────────────
FROM node:20-alpine AS deps
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production

# ── Stage 2: Runtime (minimal) ───────────────────────────────
FROM node:20-alpine
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY src ./src
COPY package.json .
RUN addgroup -S airops && adduser -S airops -G airops
USER airops
EXPOSE 3000
CMD ["node", "src/index.js"]

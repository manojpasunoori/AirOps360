# ── Stage 1: Build ──────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apk add --no-cache maven && mvn clean package -DskipTests

# ── Stage 2: Runtime (Distroless for minimal attack surface) ────
FROM gcr.io/distroless/java17-debian11
WORKDIR /app
COPY --from=builder /app/target/wms-inventory-service-*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

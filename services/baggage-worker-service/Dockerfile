# ── Stage 1: Build ──────────────────────────────────────────────
FROM mcr.microsoft.com/dotnet/sdk:8.0 AS builder
WORKDIR /src
COPY BaggageWorker/BaggageWorker.csproj BaggageWorker/
RUN dotnet restore BaggageWorker/BaggageWorker.csproj
COPY . .
RUN dotnet publish BaggageWorker/BaggageWorker.csproj -c Release -o /app/publish

# ── Stage 2: Runtime ────────────────────────────────────────────
FROM mcr.microsoft.com/dotnet/aspnet:8.0
WORKDIR /app
COPY --from=builder /app/publish .
RUN useradd -ms /bin/bash airops
USER airops
EXPOSE 8084
ENTRYPOINT ["dotnet", "BaggageWorker.dll"]

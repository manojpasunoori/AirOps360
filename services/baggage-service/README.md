# baggage-service

.NET 8 worker responsible for baggage scan processing.

## Commit 10 scope

This service now includes a minimal .NET worker skeleton with:
- worker project configuration in `BaggageService.csproj`
- host bootstrap and worker options binding
- background worker loop for simulated scan processing
- baggage scan domain models and a simple processor
- local app settings and launch profile

## Planned responsibilities
- Consume baggage scan simulator events
- Enrich baggage tracking details
- Publish `baggage.scan` events to Kafka

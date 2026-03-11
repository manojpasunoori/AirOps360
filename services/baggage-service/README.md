# baggage-service

.NET 8 worker responsible for baggage scan processing.

## Commit 25 scope
This service now includes automated test coverage with:
- NUnit test project under `tests/`
- processor output assertions for baggage scan normalization
- Moq-based verification of worker logging during a processing cycle

## Planned responsibilities
- Consume baggage scan simulator events
- Enrich baggage tracking details
- Publish `baggage.scan` events to Kafka

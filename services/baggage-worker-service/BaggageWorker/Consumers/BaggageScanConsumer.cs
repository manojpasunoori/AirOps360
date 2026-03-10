using Confluent.Kafka;
using BaggageWorker.Models;
using BaggageWorker.Services;
using System.Text.Json;

namespace BaggageWorker.Consumers;

public class BaggageScanConsumer : BackgroundService
{
    private readonly ILogger<BaggageScanConsumer> _logger;
    private readonly IConfiguration _config;
    private readonly IServiceProvider _serviceProvider;
    private const string Topic = "baggage_scan";

    public BaggageScanConsumer(
        ILogger<BaggageScanConsumer> logger,
        IConfiguration config,
        IServiceProvider serviceProvider)
    {
        _logger = logger;
        _config = config;
        _serviceProvider = serviceProvider;
    }

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        _logger.LogInformation("BaggageScanConsumer starting — topic: {Topic}", Topic);

        var consumerConfig = new ConsumerConfig
        {
            BootstrapServers = _config["Kafka:BootstrapServers"] ?? "localhost:29092",
            GroupId = "baggage-worker-group",
            AutoOffsetReset = AutoOffsetReset.Earliest,
            EnableAutoCommit = false,
            MaxPollIntervalMs = 300000,
        };

        using var consumer = new ConsumerBuilder<string, string>(consumerConfig)
            .SetErrorHandler((_, e) => _logger.LogError("Kafka error: {Error}", e.Reason))
            .SetPartitionsAssignedHandler((_, partitions) =>
                _logger.LogInformation("Partitions assigned: {Partitions}", string.Join(",", partitions)))
            .Build();

        consumer.Subscribe(Topic);

        while (!stoppingToken.IsCancellationRequested)
        {
            try
            {
                var result = consumer.Consume(TimeSpan.FromMilliseconds(500));
                if (result == null) continue;

                await ProcessScanEvent(result.Message.Value, stoppingToken);
                consumer.Commit(result);

                _logger.LogDebug("Processed scan event — key: {Key} partition: {Partition} offset: {Offset}",
                    result.Message.Key,
                    result.TopicPartitionOffset.Partition.Value,
                    result.TopicPartitionOffset.Offset.Value);
            }
            catch (OperationCanceledException)
            {
                break;
            }
            catch (ConsumeException ex)
            {
                _logger.LogError(ex, "Kafka consume error");
                await Task.Delay(5000, stoppingToken);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Unexpected error processing scan event");
                await Task.Delay(1000, stoppingToken);
            }
        }

        consumer.Close();
        _logger.LogInformation("BaggageScanConsumer stopped");
    }

    private async Task ProcessScanEvent(string messageValue, CancellationToken ct)
    {
        var scanEvent = JsonSerializer.Deserialize<BagScanEvent>(messageValue,
            new JsonSerializerOptions { PropertyNameCaseInsensitive = true });

        if (scanEvent == null)
        {
            _logger.LogWarning("Could not deserialize scan event: {Message}", messageValue);
            return;
        }

        using var scope = _serviceProvider.CreateScope();
        var baggageService = scope.ServiceProvider.GetRequiredService<IBaggageService>();
        var notificationService = scope.ServiceProvider.GetRequiredService<INotificationService>();

        await baggageService.RecordScanEvent(scanEvent, ct);

        // Alert on anomalies
        if (scanEvent.ScanStatus == "MISSING" || scanEvent.ScanStatus == "DAMAGED")
        {
            await notificationService.SendBaggageAlert(scanEvent, ct);
            _logger.LogWarning("BAGGAGE ALERT — Tag: {Tag} Status: {Status} Flight: {Flight}",
                scanEvent.TagNo, scanEvent.ScanStatus, scanEvent.FlightNo);
        }
    }
}

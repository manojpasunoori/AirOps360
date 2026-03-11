using AirOps360.BaggageService.Models;
using AirOps360.BaggageService.Processing;
using Microsoft.Extensions.Options;

namespace AirOps360.BaggageService;

public class Worker : BackgroundService
{
    private readonly ILogger<Worker> _logger;
    private readonly BaggageScanProcessor _processor;
    private readonly BaggageWorkerOptions _options;

    public Worker(
        ILogger<Worker> logger,
        BaggageScanProcessor processor,
        IOptions<BaggageWorkerOptions> options)
    {
        _logger = logger;
        _processor = processor;
        _options = options.Value;
    }

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        while (!stoppingToken.IsCancellationRequested)
        {
            var scanEvent = new BaggageScanEvent(
                BagTag: "BG-LOCAL-1001",
                FlightNumber: "AA101",
                ScanPoint: "SORTER-A",
                ScanTimeUtc: DateTimeOffset.UtcNow);

            var processed = _processor.Process(scanEvent);
            _logger.LogInformation(
                "{Processor} processed bag {BagTag} for flight {FlightNumber} from topic {Topic}",
                _options.ProcessorName,
                processed.BagTag,
                processed.FlightNumber,
                _options.SourceTopic);

            await Task.Delay(TimeSpan.FromSeconds(_options.PollIntervalSeconds), stoppingToken);
        }
    }
}

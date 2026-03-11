using AirOps360.BaggageService;
using AirOps360.BaggageService.Processing;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Options;
using Moq;
using NUnit.Framework;

namespace AirOps360.BaggageService.Tests;

public sealed class WorkerTests
{
    [Test]
    public async Task ExecuteAsync_LogsProcessedBagBeforeCancellation()
    {
        var logger = new Mock<ILogger<Worker>>();
        var processor = new BaggageScanProcessor();
        var options = Options.Create(new BaggageWorkerOptions
        {
            PollIntervalSeconds = 5,
            SourceTopic = "baggage.scan",
            ProcessorName = "test-baggage-worker"
        });
        var worker = new TestWorker(logger.Object, processor, options);
        using var cancellationTokenSource = new CancellationTokenSource(TimeSpan.FromMilliseconds(50));

        try
        {
            await worker.RunAsync(cancellationTokenSource.Token);
        }
        catch (TaskCanceledException)
        {
            // Expected when the delay is interrupted by the cancellation token.
        }

        logger.Verify(
            log => log.Log(
                LogLevel.Information,
                It.IsAny<EventId>(),
                It.Is<It.IsAnyType>((state, _) => state.ToString()!.Contains("processed bag BG-LOCAL-1001")),
                It.IsAny<Exception>(),
                It.IsAny<Func<It.IsAnyType, Exception?, string>>()),
            Times.AtLeastOnce);
    }

    private sealed class TestWorker : Worker
    {
        public TestWorker(
            ILogger<Worker> logger,
            BaggageScanProcessor processor,
            IOptions<BaggageWorkerOptions> options)
            : base(logger, processor, options)
        {
        }

        public Task RunAsync(CancellationToken stoppingToken) => ExecuteAsync(stoppingToken);
    }
}

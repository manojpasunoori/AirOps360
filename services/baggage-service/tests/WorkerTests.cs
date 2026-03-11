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
    public void ProcessSampleScan_LogsProcessedBag()
    {
        var logger = new Mock<ILogger<Worker>>();
        var processor = new BaggageScanProcessor();
        var options = Options.Create(new BaggageWorkerOptions
        {
            PollIntervalSeconds = 5,
            SourceTopic = "baggage.scan",
            ProcessorName = "test-baggage-worker"
        });
        var worker = new Worker(logger.Object, processor, options);

        var processed = worker.ProcessSampleScan();

        Assert.That(processed.BagTag, Is.EqualTo("BG-LOCAL-1001"));
        Assert.That(processed.Status, Is.EqualTo("PROCESSED"));
        logger.Verify(
            log => log.Log(
                LogLevel.Information,
                It.IsAny<EventId>(),
                It.Is<It.IsAnyType>((state, _) => state.ToString()!.Contains("processed bag BG-LOCAL-1001")),
                It.IsAny<Exception>(),
                It.IsAny<Func<It.IsAnyType, Exception?, string>>()),
            Times.AtLeastOnce);
    }
}

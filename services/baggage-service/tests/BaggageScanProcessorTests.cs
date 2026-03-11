using AirOps360.BaggageService.Models;
using AirOps360.BaggageService.Processing;
using NUnit.Framework;

namespace AirOps360.BaggageService.Tests;

public sealed class BaggageScanProcessorTests
{
    [Test]
    public void Process_ReturnsProcessedBagWithExpectedStatus()
    {
        var processor = new BaggageScanProcessor();
        var scanEvent = new BaggageScanEvent(
            BagTag: "BG-1001",
            FlightNumber: "AA101",
            ScanPoint: "SORTER-A",
            ScanTimeUtc: DateTimeOffset.Parse("2026-03-11T10:15:00Z"));

        var result = processor.Process(scanEvent);

        Assert.Multiple(() =>
        {
            Assert.That(result.BagTag, Is.EqualTo("BG-1001"));
            Assert.That(result.FlightNumber, Is.EqualTo("AA101"));
            Assert.That(result.ScanPoint, Is.EqualTo("SORTER-A"));
            Assert.That(result.Status, Is.EqualTo("PROCESSED"));
        });
    }
}

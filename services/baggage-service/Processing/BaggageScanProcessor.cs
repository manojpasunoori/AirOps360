using AirOps360.BaggageService.Models;

namespace AirOps360.BaggageService.Processing;

public sealed class BaggageScanProcessor
{
    public ProcessedBaggageScan Process(BaggageScanEvent baggageScanEvent)
    {
        return new ProcessedBaggageScan(
            baggageScanEvent.BagTag,
            baggageScanEvent.FlightNumber,
            baggageScanEvent.ScanPoint,
            DateTimeOffset.UtcNow,
            "PROCESSED"
        );
    }
}

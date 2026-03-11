namespace AirOps360.BaggageService.Models;

public sealed record BaggageScanEvent(
    string BagTag,
    string FlightNumber,
    string ScanPoint,
    DateTimeOffset ScanTimeUtc
);

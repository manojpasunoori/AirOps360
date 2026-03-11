namespace AirOps360.BaggageService.Models;

public sealed record ProcessedBaggageScan(
    string BagTag,
    string FlightNumber,
    string ScanPoint,
    DateTimeOffset ProcessedAtUtc,
    string Status
);

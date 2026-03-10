namespace BaggageWorker.Models;

public record BagScanEvent(
    string TagNo,
    string ScanPoint,
    string ScanStatus,
    string FlightNo,
    string ScannedBy,
    DateTime ScanTime
);

public record BagStatusUpdate(
    string TagNo,
    string NewStatus,
    string UpdatedBy,
    string Reason
);

public enum BagStatus
{
    CheckedIn,
    Screened,
    Loaded,
    InTransit,
    Arrived,
    Delivered,
    Missing,
    Damaged
}

public class Bag
{
    public Guid Id { get; set; }
    public string TagNo { get; set; } = string.Empty;
    public string PassengerRef { get; set; } = string.Empty;
    public string FlightNo { get; set; } = string.Empty;
    public string Origin { get; set; } = string.Empty;
    public string Destination { get; set; } = string.Empty;
    public decimal WeightKg { get; set; }
    public BagStatus Status { get; set; }
    public DateTime CreatedAt { get; set; }
}

public class ScanEventRecord
{
    public Guid Id { get; set; }
    public Guid BagId { get; set; }
    public string ScanPoint { get; set; } = string.Empty;
    public string ScanStatus { get; set; } = string.Empty;
    public string ScannedBy { get; set; } = string.Empty;
    public DateTime ScanTime { get; set; }
}

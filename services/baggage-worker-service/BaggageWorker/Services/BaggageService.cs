using BaggageWorker.Models;

namespace BaggageWorker.Services;

public interface IBaggageService
{
    Task RecordScanEvent(BagScanEvent scanEvent, CancellationToken ct = default);
    Task<Bag?> GetBagByTagNo(string tagNo, CancellationToken ct = default);
    Task UpdateBagStatus(string tagNo, BagStatus newStatus, string updatedBy, CancellationToken ct = default);
}

public class BaggageService : IBaggageService
{
    private readonly ILogger<BaggageService> _logger;
    private readonly IHttpClientFactory _httpClientFactory;

    // In-memory store for demo — replace with EF Core + PostgreSQL
    private static readonly Dictionary<string, Bag> _bags = new();
    private static readonly List<ScanEventRecord> _scanEvents = new();

    public BaggageService(ILogger<BaggageService> logger, IHttpClientFactory httpClientFactory)
    {
        _logger = logger;
        _httpClientFactory = httpClientFactory;
    }

    public async Task RecordScanEvent(BagScanEvent scanEvent, CancellationToken ct = default)
    {
        _logger.LogInformation("Recording scan event — Tag: {Tag} Point: {Point} Status: {Status}",
            scanEvent.TagNo, scanEvent.ScanPoint, scanEvent.ScanStatus);

        // Map scan status to bag status
        var newBagStatus = MapScanStatusToBagStatus(scanEvent.ScanStatus);

        if (_bags.TryGetValue(scanEvent.TagNo, out var bag))
        {
            bag.Status = newBagStatus;
        }

        _scanEvents.Add(new ScanEventRecord
        {
            Id = Guid.NewGuid(),
            ScanPoint = scanEvent.ScanPoint,
            ScanStatus = scanEvent.ScanStatus,
            ScannedBy = scanEvent.ScannedBy,
            ScanTime = scanEvent.ScanTime,
        });

        await Task.CompletedTask;
    }

    public async Task<Bag?> GetBagByTagNo(string tagNo, CancellationToken ct = default)
    {
        _bags.TryGetValue(tagNo, out var bag);
        return await Task.FromResult(bag);
    }

    public async Task UpdateBagStatus(string tagNo, BagStatus newStatus, string updatedBy, CancellationToken ct = default)
    {
        if (_bags.TryGetValue(tagNo, out var bag))
        {
            var oldStatus = bag.Status;
            bag.Status = newStatus;
            _logger.LogInformation("Bag {Tag} status: {OldStatus} → {NewStatus} by {UpdatedBy}",
                tagNo, oldStatus, newStatus, updatedBy);
        }
        await Task.CompletedTask;
    }

    private static BagStatus MapScanStatusToBagStatus(string scanStatus) => scanStatus switch
    {
        "CHECKED_IN"  => BagStatus.CheckedIn,
        "SCREENED"    => BagStatus.Screened,
        "LOADED"      => BagStatus.Loaded,
        "IN_TRANSIT"  => BagStatus.InTransit,
        "ARRIVED"     => BagStatus.Arrived,
        "DELIVERED"   => BagStatus.Delivered,
        "MISSING"     => BagStatus.Missing,
        "DAMAGED"     => BagStatus.Damaged,
        _             => BagStatus.CheckedIn,
    };
}

public interface INotificationService
{
    Task SendBaggageAlert(BagScanEvent scanEvent, CancellationToken ct = default);
}

public class NotificationService : INotificationService
{
    private readonly ILogger<NotificationService> _logger;

    public NotificationService(ILogger<NotificationService> logger) => _logger = logger;

    public async Task SendBaggageAlert(BagScanEvent scanEvent, CancellationToken ct = default)
    {
        // TODO: Integrate Azure Service Bus for alert fan-out
        _logger.LogWarning("ALERT SENT — Tag: {Tag} Flight: {Flight} Status: {Status}",
            scanEvent.TagNo, scanEvent.FlightNo, scanEvent.ScanStatus);
        await Task.CompletedTask;
    }
}

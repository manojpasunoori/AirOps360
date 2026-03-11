namespace AirOps360.BaggageService;

public sealed class BaggageWorkerOptions
{
    public int PollIntervalSeconds { get; set; } = 10;
    public string SourceTopic { get; set; } = "baggage.scan";
    public string ProcessorName { get; set; } = "local-baggage-worker";
}

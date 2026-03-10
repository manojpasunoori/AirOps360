using BaggageWorker.Consumers;
using BaggageWorker.Services;
using Serilog;

Log.Logger = new LoggerConfiguration()
    .WriteTo.Console()
    .CreateLogger();

IHost host = Host.CreateDefaultBuilder(args)
    .UseSerilog()
    .ConfigureServices((context, services) =>
    {
        var config = context.Configuration;

        // Register background workers
        services.AddHostedService<BaggageScanConsumer>();
        services.AddHostedService<BaggageStatusUpdater>();

        // Register services
        services.AddScoped<IBaggageService, BaggageService>();
        services.AddScoped<INotificationService, NotificationService>();

        // HTTP client for downstream calls
        services.AddHttpClient("cargo-service", client =>
        {
            client.BaseAddress = new Uri(config["Services:CargoUrl"] ?? "http://localhost:8082");
            client.Timeout = TimeSpan.FromSeconds(10);
        });

        // Health checks
        services.AddHealthChecks();
    })
    .Build();

await host.RunAsync();

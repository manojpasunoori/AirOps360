using AirOps360.BaggageService;
using AirOps360.BaggageService.Processing;

var builder = Host.CreateApplicationBuilder(args);
builder.Services.Configure<BaggageWorkerOptions>(builder.Configuration.GetSection("BaggageWorker"));
builder.Services.AddSingleton<BaggageScanProcessor>();
builder.Services.AddHostedService<Worker>();

var host = builder.Build();
host.Run();

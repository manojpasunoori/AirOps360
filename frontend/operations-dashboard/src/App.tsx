const flights = [
  {
    flightNumber: "AA101",
    route: "DFW -> ORD",
    status: "En Route",
    gate: "B12",
    arrival: "18:35",
    delay: "+15m",
  },
  {
    flightNumber: "UA220",
    route: "DEN -> ORD",
    status: "Scheduled",
    gate: "C7",
    arrival: "19:05",
    delay: "On time",
  },
  {
    flightNumber: "DL417",
    route: "ATL -> ORD",
    status: "Landed",
    gate: "A4",
    arrival: "17:52",
    delay: "-3m",
  },
];

const warehouseZones = [
  {
    zone: "Zone A",
    utilization: "74%",
    inboundLoads: 6,
    lowStock: 1,
  },
  {
    zone: "Zone B",
    utilization: "58%",
    inboundLoads: 4,
    lowStock: 3,
  },
  {
    zone: "Zone C",
    utilization: "88%",
    inboundLoads: 9,
    lowStock: 0,
  },
];

const inventorySignals = [
  {
    sku: "BAG-TAG-STD",
    label: "Standard Baggage Tags",
    onHand: 5000,
    threshold: 1000,
    status: "Healthy",
  },
  {
    sku: "ULD-NET-01",
    label: "Cargo Restraint Net",
    onHand: 24,
    threshold: 6,
    status: "Watch",
  },
  {
    sku: "RAMP-CONE-02",
    label: "Ramp Safety Cones",
    onHand: 8,
    threshold: 12,
    status: "Reorder",
  },
];

const panels = [
  {
    title: "Baggage Scans",
    description: "Checkpoint, sorter, and loading milestones for bag flow visibility.",
  },
  {
    title: "Cargo Status",
    description: "Unload progress, manifest readiness, and transfer coordination snapshots.",
  },
  {
    title: "Operational Alerts",
    description: "Disruptions and exceptions surfaced from event-driven workflows.",
  },
];

export default function App() {
  return (
    <main className="dashboard-shell">
      <section className="hero">
        <div className="hero-copy">
          <p className="eyebrow">AirOps360</p>
          <h1>Flight turns, arrivals, and gate pressure in one live board.</h1>
          <p className="lede">
            The flight view surfaces inbound posture first so ramp, baggage, cargo, and warehouse
            teams can coordinate off the same arrival picture.
          </p>
          <div className="hero-metrics">
            <div>
              <strong>18</strong>
              <span>Inbound flights</span>
            </div>
            <div>
              <strong>4</strong>
              <span>Delayed turns</span>
            </div>
            <div>
              <strong>92%</strong>
              <span>Gate readiness</span>
            </div>
          </div>
        </div>
        <div className="hero-mark" aria-hidden="true">
          <div className="runway-line" />
          <div className="aircraft">FLT</div>
        </div>
      </section>

      <section className="flight-board">
        <div className="section-heading">
          <div>
            <p className="section-kicker">Flight Monitoring</p>
            <h2>Inbound operations board</h2>
          </div>
          <span className="section-note">Updated from telemetry + simulation feeds</span>
        </div>

        <div className="flight-grid">
          {flights.map((flight) => (
            <article className="flight-card" key={flight.flightNumber}>
              <div className="flight-card-top">
                <div>
                  <p className="flight-number">{flight.flightNumber}</p>
                  <h3>{flight.route}</h3>
                </div>
                <span className="flight-status">{flight.status}</span>
              </div>
              <dl className="flight-details">
                <div>
                  <dt>Gate</dt>
                  <dd>{flight.gate}</dd>
                </div>
                <div>
                  <dt>Arrival</dt>
                  <dd>{flight.arrival}</dd>
                </div>
                <div>
                  <dt>Variance</dt>
                  <dd>{flight.delay}</dd>
                </div>
              </dl>
            </article>
          ))}
        </div>
      </section>

      <section className="warehouse-board">
        <div className="section-heading">
          <div>
            <p className="section-kicker">Warehouse Inventory</p>
            <h2>Receiving posture by storage zone</h2>
          </div>
          <span className="section-note">Redis-backed inventory cache snapshot</span>
        </div>

        <div className="warehouse-layout">
          <div className="zone-grid">
            {warehouseZones.map((zone) => (
              <article className="zone-card" key={zone.zone}>
                <div className="zone-card-top">
                  <h3>{zone.zone}</h3>
                  <span>{zone.utilization}</span>
                </div>
                <p>Inbound loads: {zone.inboundLoads}</p>
                <p>Low-stock alerts: {zone.lowStock}</p>
              </article>
            ))}
          </div>

          <div className="inventory-signals">
            <h3>Inventory watchlist</h3>
            <div className="inventory-table">
              {inventorySignals.map((item) => (
                <article className="inventory-row" key={item.sku}>
                  <div>
                    <p className="inventory-sku">{item.sku}</p>
                    <h4>{item.label}</h4>
                  </div>
                  <dl>
                    <div>
                      <dt>On Hand</dt>
                      <dd>{item.onHand}</dd>
                    </div>
                    <div>
                      <dt>Threshold</dt>
                      <dd>{item.threshold}</dd>
                    </div>
                  </dl>
                  <span className={`inventory-status inventory-status-${item.status.toLowerCase()}`}>
                    {item.status}
                  </span>
                </article>
              ))}
            </div>
          </div>
        </div>
      </section>

      <section className="panel-grid">
        {panels.map((panel) => (
          <article className="panel-card" key={panel.title}>
            <h2>{panel.title}</h2>
            <p>{panel.description}</p>
            <span>Planned view</span>
          </article>
        ))}
      </section>
    </main>
  );
}

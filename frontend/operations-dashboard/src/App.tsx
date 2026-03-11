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

const panels = [
  {
    title: "Baggage Scans",
    description: "Checkpoint, sorter, and loading milestones for bag flow visibility.",
  },
  {
    title: "Warehouse Inventory",
    description: "Receiving posture, stock movement, and low inventory signals by zone.",
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

const panels = [
  {
    title: "Flight Monitoring",
    description: "Live arrivals, gate changes, and delay posture across inbound operations.",
  },
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
          <h1>Airport ground operations in one live control surface.</h1>
          <p className="lede">
            This dashboard will unify flight telemetry, baggage movement, cargo unloading,
            warehouse intake, and operational alerts for the AirOps360 platform.
          </p>
        </div>
        <div className="hero-mark" aria-hidden="true">
          <div className="runway-line" />
          <div className="aircraft">A360</div>
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

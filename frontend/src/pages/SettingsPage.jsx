function SettingsPage() {
  return (
    <section className="page-wrap">
      <article className="section-card surface-elevated">
        <div className="block-head">
          <h2>Monitoring Preferences</h2>
          <p className="helper-text">Configure notification and workflow defaults.</p>
        </div>

        <form className="settings-form">
          <label className="switch-row">
            <div>
              <strong>Instant high-risk notifications</strong>
              <p>Send immediate alerts to analyst on-call channels.</p>
            </div>
            <input type="checkbox" defaultChecked />
          </label>

          <label className="switch-row">
            <div>
              <strong>Auto-assign new alerts</strong>
              <p>Route alerts to queue based on analyst workload.</p>
            </div>
            <input type="checkbox" defaultChecked />
          </label>

          <label className="switch-row">
            <div>
              <strong>Require closure note</strong>
              <p>Force a documented reason when closing or dismissing an alert.</p>
            </div>
            <input type="checkbox" />
          </label>

          <label>
            SLA Target (hours)
            <input type="number" min="1" defaultValue="4" />
          </label>

          <button type="button" className="primary-btn">
            Save Preferences
          </button>
        </form>
      </article>
    </section>
  )
}

export default SettingsPage

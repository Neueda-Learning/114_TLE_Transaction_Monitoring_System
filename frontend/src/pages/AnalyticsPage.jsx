import { useMemo } from 'react'
import Card from '../components/Common/Card'
import AlertChart from '../components/Charts/AlertChart'
import TransactionChart from '../components/Charts/TransactionChart'
import { useAppData } from '../context/useAppData'

function AnalyticsPage() {
  const { alerts, transactions } = useAppData()

  const severityData = useMemo(
    () =>
      ['HIGH', 'MEDIUM', 'LOW'].map((level) => ({
        name: level,
        value: alerts.filter((alert) => alert.riskLevel === level).length,
      })),
    [alerts],
  )

  const statusData = useMemo(
    () =>
      ['OPEN', 'ACKNOWLEDGED', 'INVESTIGATING', 'CLOSED', 'DISMISSED'].map(
        (status) => ({
          name: status,
          value: alerts.filter((alert) => alert.status === status).length,
        }),
      ),
    [alerts],
  )

  const trendData = useMemo(
    () =>
      transactions.map((transaction, index) => ({
        time: `T${index + 1}`,
        volume: transaction.amount / 1000,
        alerts: transaction.riskStatus === 'NORMAL' ? 0 : 1,
      })),
    [transactions],
  )

  return (
    <section className="page-wrap">
      <div className="cards-grid cards-grid-3">
        <Card title="Model Precision" value="93.8%" subtitle="Last validated cycle" />
        <Card title="False Positive Rate" value="4.2%" subtitle="Within target threshold" />
        <Card title="Escalation Rate" value="18%" subtitle="Cases requiring manager review" />
      </div>

      <div className="chart-grid">
        <article className="chart-card surface-elevated">
          <h2>Severity Distribution</h2>
          <AlertChart type="severity" data={severityData} />
        </article>
        <article className="chart-card surface-elevated">
          <h2>Status Breakdown</h2>
          <AlertChart type="status" data={statusData} />
        </article>
      </div>

      <article className="chart-card surface-elevated">
        <h2>Transaction Intensity Snapshot</h2>
        <TransactionChart data={trendData.slice(0, 10)} />
      </article>
    </section>
  )
}

export default AnalyticsPage

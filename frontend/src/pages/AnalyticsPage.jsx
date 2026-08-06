import { useCallback, useEffect, useMemo, useState } from 'react'
import Card from '../components/Common/Card'
import Table from '../components/Common/Table'
import TransactionChart from '../components/Charts/TransactionChart'
import { useAppData } from '../context/useAppData'
import { getLogs } from '../services/api'
import { formatDateTime } from '../utils/formatters'

const PAGE_SIZE = 10

function AnalyticsPage() {
  const { transactions, alerts } = useAppData()
  const [logs, setLogs] = useState([])
  const [logsLoading, setLogsLoading] = useState(true)
  const [logsError, setLogsError] = useState('')
  const [page, setPage] = useState(1)

  // Calculate real metrics from alerts
  const metrics = useMemo(() => {
    const totalAlerts = alerts.length
    if (totalAlerts === 0) {
      return {
        modelPrecision: 'N/A',
        falsePositiveRate: 'N/A',
        escalationRate: 'N/A',
      }
    }

    // False Positive Rate: dismissed alerts / total alerts * 100
    const dismissedAlerts = alerts.filter((alert) => alert.status === 'DISMISSED').length
    const falsePositiveRate = totalAlerts > 0 ? ((dismissedAlerts / totalAlerts) * 100).toFixed(1) : 0

    // Model Precision: (True Positives) / (True Positives + False Positives)
    // True Positives = Closed/Investigating alerts with HIGH severity
    // False Positives = Dismissed alerts
    const truePositives = alerts.filter(
      (alert) => (alert.status === 'CLOSED' || alert.status === 'INVESTIGATING' || alert.status === 'ACKNOWLEDGED') && alert.riskLevel === 'HIGH'
    ).length
    const falsePositives = dismissedAlerts
    const modelPrecision = truePositives + falsePositives > 0 
      ? ((truePositives / (truePositives + falsePositives)) * 100).toFixed(1) 
      : 'N/A'

    // Escalation Rate: HIGH severity alerts / total alerts * 100
    const highRiskAlerts = alerts.filter((alert) => alert.riskLevel === 'HIGH').length
    const escalationRate = totalAlerts > 0 ? ((highRiskAlerts / totalAlerts) * 100).toFixed(1) : 0

    return {
      modelPrecision: modelPrecision !== 'N/A' ? `${modelPrecision}%` : 'N/A',
      falsePositiveRate: `${falsePositiveRate}%`,
      escalationRate: `${escalationRate}%`,
    }
  }, [alerts])

  const loadLogs = useCallback(async () => {
    setLogsLoading(true)
    setLogsError('')
    try {
      const apiLogs = await getLogs()
      setLogs(apiLogs)
    } catch (error) {
      setLogsError(error.message || 'Unable to load logs.')
    } finally {
      setLogsLoading(false)
    }
  }, [])

  useEffect(() => {
    const timer = window.setTimeout(() => {
      loadLogs()
    }, 0)

    return () => window.clearTimeout(timer)
  }, [loadLogs])

  useEffect(() => {
    const intervalId = window.setInterval(() => {
      loadLogs()
    }, 15000)

    return () => window.clearInterval(intervalId)
  }, [loadLogs])

  const trendData = useMemo(
    () =>
      transactions.map((transaction, index) => ({
        time: `T${index + 1}`,
        volume: transaction.amount / 1000,
        alerts: transaction.riskStatus === 'NORMAL' ? 0 : 1,
      })),
    [transactions],
  )

  const totalPages = Math.max(1, Math.ceil(logs.length / PAGE_SIZE))

  const pagedLogs = useMemo(() => {
    const start = (page - 1) * PAGE_SIZE
    return logs.slice(start, start + PAGE_SIZE)
  }, [logs, page])

  const logColumns = [
    { key: 'alertId', label: 'Alert ID', sortable: true },
    { key: 'action', label: 'Action', sortable: true },
    { key: 'description', label: 'Description', sortable: true },
    { key: 'oldStatus', label: 'Old Status' },
    { key: 'newStatus', label: 'New Status' },
    { key: 'changes', label: 'Changes' },
    {
      key: 'createdAt',
      label: 'Timestamp',
      sortable: true,
      render: (value) => formatDateTime(value),
    },
  ]

  return (
    <section className="page-wrap">
      <div className="cards-grid cards-grid-3">
        <Card title="Model Precision" value={metrics.modelPrecision} subtitle="Last validated cycle" />
        <Card title="False Positive Rate" value={metrics.falsePositiveRate} subtitle="Within target threshold" />
        <Card title="Escalation Rate" value={metrics.escalationRate} subtitle="Cases requiring manager review" />
      </div>

      <article className="chart-card surface-elevated">
        <h2>Transaction Intensity Snapshot</h2>
        <TransactionChart data={trendData.slice(0, 10)} />
      </article>

      <article className="section-card surface-elevated">
        <h3>System Activity Logs</h3>
        {logsError && <p className="helper-text">{logsError}</p>}
        <Table
          columns={logColumns}
          data={pagedLogs}
          rowKey="id"
          isLoading={logsLoading}
          emptyMessage="No activity logs found."
        />

        <div className="pagination-bar">
          <p>
            Showing {pagedLogs.length} of {logs.length} logs
          </p>

          <div className="pagination-controls">
            <button
              type="button"
              className="ghost-btn"
              disabled={page <= 1}
              onClick={() => setPage((currentPage) => Math.max(1, currentPage - 1))}
            >
              Previous
            </button>
            <span>
              Page {page} / {totalPages}
            </span>
            <button
              type="button"
              className="ghost-btn"
              disabled={page >= totalPages}
              onClick={() =>
                setPage((currentPage) => Math.min(totalPages, currentPage + 1))
              }
            >
              Next
            </button>
          </div>
        </div>
      </article>
    </section>
  )
}

export default AnalyticsPage

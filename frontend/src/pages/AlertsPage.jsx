import { useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Table from '../components/Common/Table'
import SearchBar from '../components/Common/SearchBar'
import Filter from '../components/Common/Filter'
import Badge from '../components/Common/Badge'
import Card from '../components/Common/Card'
import { useAppData } from '../context/useAppData'
import { formatCurrency, formatDateTime } from '../utils/formatters'

function AlertsPage() {
  const navigate = useNavigate()
  const { alerts, isLoading, error } = useAppData()
  const [severityFilter, setSeverityFilter] = useState('ALL')
  const [statusFilter, setStatusFilter] = useState('ALL')
  const [search, setSearch] = useState('')

  const filteredAlerts = useMemo(() => {
    let result = [...alerts]

    if (search.trim()) {
      const query = search.toLowerCase()
      result = result.filter(
        (alert) =>
          alert.id.toLowerCase().includes(query) ||
          alert.ruleName.toLowerCase().includes(query) ||
          alert.accountId.toLowerCase().includes(query),
      )
    }

    if (severityFilter !== 'ALL') {
      result = result.filter((alert) => alert.riskLevel === severityFilter)
    }

    if (statusFilter !== 'ALL') {
      result = result.filter((alert) => alert.status === statusFilter)
    }

    return result.sort(
      (a, b) => new Date(b.createdTime).getTime() - new Date(a.createdTime).getTime(),
    )
  }, [alerts, severityFilter, statusFilter, search])

  const metrics = useMemo(
    () => ({
      open: alerts.filter((alert) => alert.status === 'OPEN').length,
      investigating: alerts.filter((alert) => alert.status === 'INVESTIGATING').length,
      closed: alerts.filter((alert) => alert.status === 'CLOSED').length,
    }),
    [alerts],
  )

  const columns = [
    { key: 'id', label: 'Alert ID' },
    {
      key: 'riskLevel',
      label: 'Risk Level',
      render: (value) => <Badge label={value} variant={value} />,
    },
    { key: 'ruleName', label: 'Rule Name' },
    { key: 'accountId', label: 'Account' },
    { key: 'amount', label: 'Amount', render: (value) => formatCurrency(value) },
    {
      key: 'createdTime',
      label: 'Created Time',
      render: (value) => formatDateTime(value),
    },
    {
      key: 'status',
      label: 'Status',
      render: (value) => <Badge label={value} variant={value} />,
    },
    {
      key: 'action',
      label: 'Action',
      render: (_, row) => (
        <button
          type="button"
          className="table-action"
          onClick={() => navigate(`/alerts/${row.id}`)}
        >
          Investigate
        </button>
      ),
    },
  ]

  if (error) {
    return (
      <section className="state-panel surface-elevated">
        <h2>Unable to load alert queue</h2>
        <p>{error}</p>
      </section>
    )
  }

  return (
    <section className="page-wrap">
      <div className="cards-grid cards-grid-3">
        <Card title="Open Alerts" value={metrics.open} subtitle="Pending analyst action" />
        <Card
          title="Investigating"
          value={metrics.investigating}
          subtitle="Cases in progress"
        />
        <Card title="Closed" value={metrics.closed} subtitle="Finalized this cycle" />
      </div>

      <article className="section-card">
        <div className="block-head">
          <h2>Alert Monitoring Dashboard</h2>
          <p className="helper-text">Filter by severity and workflow status.</p>
        </div>

        <div className="toolbar">
          <SearchBar
            value={search}
            onChange={setSearch}
            placeholder="Search by alert ID, rule, account"
          />

          <Filter
            label="Severity"
            value={severityFilter}
            onChange={setSeverityFilter}
            options={[
              { label: 'All Severity', value: 'ALL' },
              { label: 'HIGH', value: 'HIGH' },
              { label: 'MEDIUM', value: 'MEDIUM' },
              { label: 'LOW', value: 'LOW' },
            ]}
          />

          <Filter
            label="Status"
            value={statusFilter}
            onChange={setStatusFilter}
            options={[
              { label: 'All Statuses', value: 'ALL' },
              { label: 'OPEN', value: 'OPEN' },
              { label: 'ACKNOWLEDGED', value: 'ACKNOWLEDGED' },
              { label: 'INVESTIGATING', value: 'INVESTIGATING' },
              { label: 'CLOSED', value: 'CLOSED' },
              { label: 'DISMISSED', value: 'DISMISSED' },
            ]}
          />
        </div>

        <Table
          columns={columns}
          data={filteredAlerts}
          rowKey="id"
          isLoading={isLoading}
          emptyMessage="No alerts match the selected filters."
        />
      </article>
    </section>
  )
}

export default AlertsPage

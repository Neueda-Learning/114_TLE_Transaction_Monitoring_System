import { useMemo, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import Badge from '../components/Common/Badge'
import { useAppData } from '../context/useAppData'
import { formatCurrency, formatDateTime } from '../utils/formatters'

function AlertDetailsPage() {
  const { alertId } = useParams()
  const { alerts, transactions, changeAlertStatus } = useAppData()
  const [actionLoading, setActionLoading] = useState(false)

  const alert = useMemo(
    () => alerts.find((item) => item.id === alertId),
    [alerts, alertId],
  )

  const linkedTransaction = useMemo(
    () =>
      transactions.find((transaction) => transaction.id === alert?.transactionId),
    [transactions, alert],
  )

  const handleStatusChange = async (nextStatus) => {
    if (!alert) {
      return
    }

    setActionLoading(true)
    try {
      await changeAlertStatus(alert.id, nextStatus)
    } finally {
      setActionLoading(false)
    }
  }

  if (!alert) {
    return (
      <section className="state-panel surface-elevated">
        <h2>Alert not found</h2>
        <p>The requested alert does not exist in the active queue.</p>
        <Link className="table-action" to="/alerts">
          Return to alert list
        </Link>
      </section>
    )
  }

  return (
    <section className="page-wrap">
      <article className="section-card alert-header-panel">
        <div>
          <p className="eyebrow">Investigation Case</p>
          <h2>{alert.id}</h2>
        </div>

        <div className="header-badges">
          <Badge label={alert.riskLevel} variant={alert.riskLevel} />
          <Badge label={alert.status} variant={alert.status} />
        </div>
      </article>

      <div className="details-grid">
        <article className="surface-elevated section-card">
          <h3>Alert Summary</h3>
          <div className="key-value-list">
            <p>
              Rule Name <span>{alert.ruleName}</span>
            </p>
            <p>
              Account <span>{alert.accountId}</span>
            </p>
            <p>
              Customer <span>{alert.customer}</span>
            </p>
            <p>
              Amount <span>{formatCurrency(alert.amount)}</span>
            </p>
            <p>
              Created Time <span>{formatDateTime(alert.createdTime)}</span>
            </p>
          </div>
        </article>

        <article className="surface-elevated section-card">
          <h3>Trigger Information</h3>
          <div className="key-value-list">
            <p>
              Rule <span>{alert.triggerInfo.rule}</span>
            </p>
            <p>
              Threshold <span>{alert.triggerInfo.threshold}</span>
            </p>
            <p>
              Observed <span>{alert.triggerInfo.observed}</span>
            </p>
          </div>
          <p className="reason-text">{alert.reason}</p>
        </article>

        <article className="surface-elevated section-card">
          <h3>Transaction Details</h3>
          {linkedTransaction ? (
            <div className="key-value-list">
              <p>
                Transaction ID <span>{linkedTransaction.id}</span>
              </p>
              <p>
                Payee <span>{linkedTransaction.payee}</span>
              </p>
              <p>
                Type <span>{linkedTransaction.transactionType}</span>
              </p>
              <p>
                Risk Status <span>{linkedTransaction.riskStatus}</span>
              </p>
              <p>
                Timestamp <span>{formatDateTime(linkedTransaction.timestamp)}</span>
              </p>
            </div>
          ) : (
            <p className="helper-text">No linked transaction found.</p>
          )}
        </article>

        <article className="surface-elevated section-card">
          <h3>Investigation Timeline</h3>
          <ul className="timeline">
            {alert.timeline.map((entry) => (
              <li key={`${entry.time}-${entry.action}`}>
                <span>{formatDateTime(entry.time)}</span>
                <p>{entry.action}</p>
              </li>
            ))}
          </ul>
        </article>
      </div>

      <article className="section-card actions-panel surface-elevated">
        <h3>Actions</h3>
        <div className="action-buttons">
          <button
            type="button"
            className="primary-btn"
            disabled={actionLoading}
            onClick={() => handleStatusChange('ACKNOWLEDGED')}
          >
            Acknowledge
          </button>
          <button
            type="button"
            className="primary-btn secondary"
            disabled={actionLoading}
            onClick={() => handleStatusChange('INVESTIGATING')}
          >
            Start Investigation
          </button>
          <button
            type="button"
            className="primary-btn success"
            disabled={actionLoading}
            onClick={() => handleStatusChange('CLOSED')}
          >
            Close Alert
          </button>
          <button
            type="button"
            className="primary-btn danger"
            disabled={actionLoading}
            onClick={() => handleStatusChange('DISMISSED')}
          >
            Dismiss Alert
          </button>
        </div>
      </article>
    </section>
  )
}

export default AlertDetailsPage

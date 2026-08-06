import { useCallback, useEffect, useMemo, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import Badge from '../components/Common/Badge'
import Modal from '../components/Common/Modal'
import { useAppData } from '../context/useAppData'
import { getAlertLogs } from '../services/api'
import { formatCurrency, formatDateTime } from '../utils/formatters'

const noteRequiredStatuses = new Set(['CLOSED', 'DISMISSED'])

const formatLogEntry = (log) => {
  const action = log.description?.trim()
  if (action) {
    return action
  }

  if (log.oldStatus && log.newStatus) {
    return `Status changed from ${log.oldStatus} to ${log.newStatus}.`
  }

  return 'Alert activity recorded.'
}

function AlertDetailsPage() {
  const { alertId } = useParams()
  const { alerts, transactions, changeAlertStatus } = useAppData()
  const [actionLoading, setActionLoading] = useState(false)
  const [pendingStatus, setPendingStatus] = useState('')
  const [note, setNote] = useState('')
  const [noteError, setNoteError] = useState('')
  const [logs, setLogs] = useState([])
  const [logsError, setLogsError] = useState('')

  const alert = useMemo(
    () => alerts.find((item) => item.id === alertId),
    [alerts, alertId],
  )

  const linkedTransaction = useMemo(
    () =>
      transactions.find((transaction) => transaction.id === alert?.transactionId),
    [transactions, alert],
  )

  const loadLogs = useCallback(async () => {
    if (!alertId) {
      return
    }

    try {
      setLogsError('')
      const nextLogs = await getAlertLogs(alertId)
      setLogs(nextLogs)
    } catch (error) {
      setLogsError(error.message || 'Unable to load alert activity history.')
    }
  }, [alertId])

  useEffect(() => {
    const timer = window.setTimeout(() => {
      loadLogs()
    }, 0)

    return () => window.clearTimeout(timer)
  }, [loadLogs])

  const handleStatusChange = async (nextStatus, nextNote = '') => {
    if (!alert) {
      return
    }

    setActionLoading(true)
    try {
      await changeAlertStatus(alert.id, nextStatus, nextNote)
      await loadLogs()
    } finally {
      setActionLoading(false)
    }
  }

  const openNoteModal = (nextStatus) => {
    setPendingStatus(nextStatus)
    setNote('')
    setNoteError('')
  }

  const closeNoteModal = () => {
    if (actionLoading) {
      return
    }

    setPendingStatus('')
    setNote('')
    setNoteError('')
  }

  const submitStatusWithNote = async (event) => {
    event.preventDefault()

    const trimmedNote = note.trim()
    if (!trimmedNote) {
      setNoteError('Add a note before closing or dismissing this alert.')
      return
    }

    await handleStatusChange(pendingStatus, trimmedNote)
    closeNoteModal()
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

        <article className="surface-elevated section-card">
          <h3>Status Notes</h3>
          {logsError ? <p className="helper-text">{logsError}</p> : null}
          {logs.length ? (
            <ul className="timeline">
              {logs.map((log) => (
                <li key={log.logId ?? `${log.createdAt}-${log.action}`}>
                  <span>{formatDateTime(log.createdAt)}</span>
                  <p>{formatLogEntry(log)}</p>
                </li>
              ))}
            </ul>
          ) : (
            <p className="helper-text">No status notes recorded yet.</p>
          )}
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
            onClick={() => openNoteModal('CLOSED')}
          >
            Close Alert
          </button>
          <button
            type="button"
            className="primary-btn danger"
            disabled={actionLoading}
            onClick={() => openNoteModal('DISMISSED')}
          >
            Dismiss Alert
          </button>
        </div>
      </article>

      <Modal
        title={pendingStatus === 'DISMISSED' ? 'Dismiss Alert' : 'Close Alert'}
        isOpen={noteRequiredStatuses.has(pendingStatus)}
        onClose={closeNoteModal}
        footer={
          <>
            <button
              type="button"
              className="ghost-btn"
              onClick={closeNoteModal}
              disabled={actionLoading}
            >
              Cancel
            </button>
            <button
              type="submit"
              form="alert-note-form"
              className="primary-btn"
              disabled={actionLoading}
            >
              Save Note and Continue
            </button>
          </>
        }
      >
        <form id="alert-note-form" className="rule-form" onSubmit={submitStatusWithNote}>
          <label>
            Why are you {pendingStatus === 'DISMISSED' ? 'dismissing' : 'closing'} this alert?
            <textarea
              rows={4}
              required
              value={note}
              onChange={(event) => {
                setNote(event.target.value)
                if (noteError) {
                  setNoteError('')
                }
              }}
            />
          </label>
          <p className="helper-text">
            This note is saved in the alert history so analysts can review the reason later.
          </p>
          {noteError ? <p className="auth-error">{noteError}</p> : null}
        </form>
      </Modal>
    </section>
  )
}

export default AlertDetailsPage

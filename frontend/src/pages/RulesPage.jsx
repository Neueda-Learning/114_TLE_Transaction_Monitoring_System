import { useState } from 'react'
import Table from '../components/Common/Table'
import Badge from '../components/Common/Badge'
import Modal from '../components/Common/Modal'
import { useAppData } from '../context/useAppData'
import { useAuth } from '../context/useAuth'
import { canManageRules } from '../utils/roles'
import { formatDateTime } from '../utils/formatters'

const emptyFormState = {
  name: '',
  type: 'AMOUNT',
  threshold: '',
  severity: 'MEDIUM',
  status: 'ENABLED',
  description: '',
}

function RulesPage() {
  const { rules, upsertRule, toggleRuleStatus, isLoading, error } = useAppData()
  const { role } = useAuth()
  const canManage = canManageRules(role)
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [editingRuleId, setEditingRuleId] = useState('')
  const [formState, setFormState] = useState(emptyFormState)

  const openAddRule = () => {
    setEditingRuleId('')
    setFormState(emptyFormState)
    setIsModalOpen(true)
  }

  const openEditRule = (rule) => {
    setEditingRuleId(rule.id)
    setFormState({
      name: rule.name,
      type: rule.type,
      threshold: rule.threshold,
      severity: rule.severity,
      status: rule.status,
      description: rule.description,
    })
    setIsModalOpen(true)
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    await upsertRule(editingRuleId, formState)
    setIsModalOpen(false)
  }

  const columns = [
    { key: 'name', label: 'Rule Name' },
    { key: 'type', label: 'Type' },
    { key: 'threshold', label: 'Threshold' },
    {
      key: 'severity',
      label: 'Severity',
      render: (value) => <Badge label={value} variant={value} />,
    },
    {
      key: 'status',
      label: 'Status',
      render: (value) => <Badge label={value} variant={value} />,
    },
    {
      key: 'lastUpdated',
      label: 'Last Updated',
      render: (value) => formatDateTime(value),
    },
    canManage
      ? {
          key: 'actions',
          label: 'Actions',
          render: (_, row) => (
            <div className="row-actions">
              <button
                type="button"
                className="table-action"
                onClick={() => openEditRule(row)}
              >
                Edit
              </button>
              <button
                type="button"
                className="table-action"
                onClick={() => toggleRuleStatus(row.id)}
              >
                {row.status === 'ENABLED' ? 'Disable' : 'Enable'}
              </button>
            </div>
          ),
        }
      : null,
  ].filter(Boolean)

  if (error) {
    return (
      <section className="state-panel surface-elevated">
        <h2>Unable to load rule catalog</h2>
        <p>{error}</p>
      </section>
    )
  }

  return (
    <section className="page-wrap">
      <article className="section-card">
        <div className="block-head">
          <h2>Rule Management</h2>
          {canManage ? (
            <button type="button" className="primary-btn" onClick={openAddRule}>
              Add Rule
            </button>
          ) : (
            <p className="helper-text">View-only — contact an administrator to change rules.</p>
          )}
        </div>

        <Table
          columns={columns}
          data={rules}
          rowKey="id"
          isLoading={isLoading}
          emptyMessage="No monitoring rules configured."
        />
      </article>

      {canManage ? (
        <Modal
          title={editingRuleId ? 'Edit Rule' : 'Add Rule'}
          isOpen={isModalOpen}
          onClose={() => setIsModalOpen(false)}
          footer={
            <>
              <button type="button" className="ghost-btn" onClick={() => setIsModalOpen(false)}>
                Cancel
              </button>
              <button type="submit" form="rule-form" className="primary-btn">
                Save Rule
              </button>
            </>
          }
        >
        <form id="rule-form" className="rule-form" onSubmit={handleSubmit}>
          <label>
            Rule Name
            <input
              required
              value={formState.name}
              onChange={(event) =>
                setFormState((current) => ({ ...current, name: event.target.value }))
              }
            />
          </label>
          <label>
            Type
            <select
              value={formState.type}
              onChange={(event) =>
                setFormState((current) => ({ ...current, type: event.target.value }))
              }
            >
              <option value="AMOUNT">AMOUNT</option>
              <option value="VELOCITY">VELOCITY</option>
              <option value="BEHAVIORAL">BEHAVIORAL</option>
              <option value="LIMIT">LIMIT</option>
              <option value="GEO">GEO</option>
            </select>
          </label>
          <label>
            Threshold
            <input
              required
              value={formState.threshold}
              onChange={(event) =>
                setFormState((current) => ({
                  ...current,
                  threshold: event.target.value,
                }))
              }
            />
          </label>
          <label>
            Severity
            <select
              value={formState.severity}
              onChange={(event) =>
                setFormState((current) => ({
                  ...current,
                  severity: event.target.value,
                }))
              }
            >
              <option value="HIGH">HIGH</option>
              <option value="MEDIUM">MEDIUM</option>
              <option value="LOW">LOW</option>
            </select>
          </label>
          <label>
            Status
            <select
              value={formState.status}
              onChange={(event) =>
                setFormState((current) => ({
                  ...current,
                  status: event.target.value,
                }))
              }
            >
              <option value="ENABLED">ENABLED</option>
              <option value="DISABLED">DISABLED</option>
            </select>
          </label>
          <label>
            Description
            <textarea
              rows={3}
              value={formState.description}
              onChange={(event) =>
                setFormState((current) => ({
                  ...current,
                  description: event.target.value,
                }))
              }
            />
          </label>
        </form>
      </Modal>
      ) : null}
    </section>
  )
}

export default RulesPage

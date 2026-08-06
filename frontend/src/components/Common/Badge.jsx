const classByVariant = {
  HIGH: 'badge-high',
  MEDIUM: 'badge-medium',
  LOW: 'badge-low',
  OPEN: 'badge-open',
  ACKNOWLEDGED: 'badge-ack',
  INVESTIGATING: 'badge-investigating',
  CLOSED: 'badge-closed',
  DISMISSED: 'badge-dismissed',
  ROLLBACK: 'badge-rollback',
  NORMAL: 'badge-normal',
  SUSPICIOUS: 'badge-suspicious',
  FRAUDULENT: 'badge-fraudulent',
  BLOCKED: 'badge-blocked',
  ENABLED: 'badge-enabled',
  DISABLED: 'badge-disabled',
}

function Badge({ label, variant }) {
  return <span className={`badge ${classByVariant[variant] || ''}`}>{label}</span>
}

export default Badge

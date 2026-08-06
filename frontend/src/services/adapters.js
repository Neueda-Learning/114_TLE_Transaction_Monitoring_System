const toIsoOrNow = (value) => {
  if (!value) {
    return new Date().toISOString()
  }

  const parsed = new Date(value)
  return Number.isNaN(parsed.getTime()) ? new Date().toISOString() : parsed.toISOString()
}

const riskFromStatus = (status) => {
  if (status === 'BLOCKED') {
    return 'HIGH'
  }

  if (status === 'SUSPICIOUS') {
    return 'MEDIUM'
  }

  return 'LOW'
}

const alertTimelineDefaults = (createdTime, status) => [
  {
    time: createdTime,
    action: `Alert created with status ${status}.`,
  },
]

const mapApiRuleTypeToUiType = (ruleType) => {
  switch (ruleType) {
    case 'AMOUNT_THRESHOLD':
      return 'AMOUNT'
    case 'DAILY_LIMIT':
      return 'LIMIT'
    case 'NEW_PAYEE':
      return 'BEHAVIORAL'
    default:
      return ruleType || 'UNKNOWN'
  }
}

export const normalizeTransaction = (transaction = {}) => {
  const transactionId =
    transaction.id ||
    transaction.transactionId ||
    transaction.transaction_id ||
    `TXN-${Date.now()}`

  const payee = transaction.payee || transaction.payeeName || transaction.payeeid || ''
  const timestamp = toIsoOrNow(transaction.timestamp || transaction.transactionDate)

  return {
    id: String(transactionId),
    accountId: transaction.accountId || transaction.account_id || 'N/A',
    customer: transaction.customer || 'Unknown Customer',
    payee,
    amount: Number(transaction.amount ?? 0),
    currency: transaction.currency || transaction.Currency || 'USD',
    transactionType: transaction.transactionType || transaction.transaction_type || 'UNKNOWN',
    timestamp,
    riskStatus: transaction.riskStatus || transaction.fraudStatus || transaction.fraud_status || 'NORMAL',
  }
}

export const normalizeAlert = (alert = {}, transactions = [], rules = []) => {
  const alertId = alert.id || alert.alertId || alert.alert_id || `ALRT-${Date.now()}`
  const transactionId =
    alert.transactionId ||
    alert.transaction_id ||
    (alert.transactionId === 0 ? alert.transactionId : null)

  const ruleId = alert.ruleId || alert.rule_id || null

  const linkedTransaction = transactions.find(
    (transaction) => String(transaction.id) === String(transactionId),
  )

  const linkedRule = rules.find((rule) => String(rule.id) === String(ruleId))

  const createdTime = toIsoOrNow(alert.createdTime || alert.createdAt || alert.created_at)
  const status = alert.status || alert.alertStatus || alert.alert_status || 'OPEN'
  const riskLevel =
    alert.riskLevel ||
    alert.severity ||
    alert.alertSeverity ||
    riskFromStatus(linkedTransaction?.riskStatus)

  return {
    id: String(alertId),
    transactionId: linkedTransaction?.id || (transactionId ? String(transactionId) : 'N/A'),
    ruleId: ruleId ? String(ruleId) : null,
    ruleName: alert.ruleName || linkedRule?.name || alert.alertType || 'Unspecified Rule',
    accountId: alert.accountId || linkedTransaction?.accountId || 'N/A',
    customer: alert.customer || linkedTransaction?.customer || 'Unknown Customer',
    amount: Number(alert.amount ?? linkedTransaction?.amount ?? 0),
    riskLevel,
    status,
    reason: alert.reason || alert.alertMessage || 'No trigger reason provided.',
    createdTime,
    triggerInfo: {
      rule: alert.triggerInfo?.rule || alert.ruleName || linkedRule?.name || 'Rule unavailable',
      threshold:
        alert.triggerInfo?.threshold || linkedRule?.threshold || linkedRule?.thresholdValue || 'N/A',
      observed: alert.triggerInfo?.observed || 'N/A',
    },
    timeline:
      Array.isArray(alert.timeline) && alert.timeline.length
        ? alert.timeline
        : alertTimelineDefaults(createdTime, status),
  }
}

export const normalizeRule = (rule = {}) => {
  const ruleId = rule.id || rule.ruleId || rule.rule_id || `RULE-${Date.now()}`
  const isActive = typeof rule.isActive === 'boolean' ? rule.isActive : undefined
  const ruleType = rule.type || rule.ruleType

  return {
    id: String(ruleId),
    name: rule.name || rule.ruleName || 'Unnamed Rule',
    type: mapApiRuleTypeToUiType(ruleType),
    threshold: rule.threshold || rule.thresholdValue || 'N/A',
    severity: rule.severity || 'MEDIUM',
    status: rule.status || (isActive === false ? 'DISABLED' : 'ENABLED'),
    description: rule.description || `Rule on ${rule.fieldName || 'transaction field'}.`,
    lastUpdated: toIsoOrNow(rule.lastUpdated || rule.createdAt || rule.created_at),
  }
}

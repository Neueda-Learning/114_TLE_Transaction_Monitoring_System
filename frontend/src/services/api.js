import axios from 'axios'
import { normalizeAlert, normalizeLog, normalizeRule, normalizeTransaction } from './adapters'

const SESSION_KEY = 'tm_auth_session'

const apiClient = axios.create({
  baseURL: '/api',
  timeout: 8000,
})

apiClient.interceptors.request.use((config) => {
  const stored = localStorage.getItem(SESSION_KEY) || sessionStorage.getItem(SESSION_KEY)
  if (!stored) {
    return config
  }

  try {
    const session = JSON.parse(stored)
    if (session?.token) {
      config.headers = config.headers || {}
      config.headers.Authorization = `Bearer ${session.token}`
    }
  } catch {
    // Ignore invalid storage values and continue without auth header.
  }

  return config
})

const toTransactionList = (items = []) => items.map((item) => normalizeTransaction(item))
const toRuleList = (items = []) => items.map((item) => normalizeRule(item))
const toLogList = (items = []) => items.map((item) => normalizeLog(item))
const toAlertList = (items = [], transactions = [], rules = []) =>
  items.map((item) => normalizeAlert(item, transactions, rules))

const mapUiRuleTypeToApiRuleType = (type) => {
  switch (type) {
    case 'AMOUNT':
      return 'AMOUNT_THRESHOLD'
    case 'LIMIT':
      return 'DAILY_LIMIT'
    case 'BEHAVIORAL':
      return 'NEW_PAYEE'
    default:
      return type
  }
}

const mapRulePayloadToApi = (payload = {}) => ({
  ruleName: payload.name,
  ruleType: mapUiRuleTypeToApiRuleType(payload.type),
  thresholdValue: payload.threshold,
  isActive: payload.status !== 'DISABLED',
})

const getStatusAction = (status) => {
  switch (status) {
    case 'ACKNOWLEDGED':
      return 'ACKNOWLEDGE'
    case 'INVESTIGATING':
      return 'START_INVESTIGATION'
    case 'CLOSED':
      return 'CLOSE'
    case 'DISMISSED':
      return 'DISMISS'
    case 'ROLLBACK':
      return 'ROLLBACK'
    default:
      return 'UPDATE_STATUS'
  }
}

export const getTransactions = async () => {
  try {
    const response = await apiClient.get('/transactions')
    return toTransactionList(response.data)
  } catch (error) {
    throw new Error('Unable to load transactions.', { cause: error })
  }
}

export const getAlerts = async (dependencies = {}) => {
  try {
    const dependencyTransactions = Array.isArray(dependencies.transactions)
      ? dependencies.transactions
      : []
    const dependencyRules = Array.isArray(dependencies.rules) ? dependencies.rules : []

    const response = await apiClient.get('/alerts')
    const alerts = response.data

    const transactions = dependencyTransactions.length
      ? dependencyTransactions
      : await getTransactions()

    const rules = dependencyRules.length ? dependencyRules : await getRules()

    return toAlertList(alerts, transactions, rules)
  } catch (error) {
    throw new Error('Unable to load alerts.', { cause: error })
  }
}

export const getAlertById = async (alertId) => {
  try {
    const response = await apiClient.get(`/alerts/${alertId}`)
    const transactions = await getTransactions()
    const rules = await getRules()
    return normalizeAlert(response.data, transactions, rules)
  } catch (error) {
    throw new Error('Unable to load alert details.', { cause: error })
  }
}

export const getAlertLogs = async (alertId) => {
  try {
    const response = await apiClient.get(`/alerts/${alertId}/logs`)
    return Array.isArray(response.data) ? response.data : []
  } catch (error) {
    throw new Error('Unable to load alert activity history.', { cause: error })
  }
}

export const getRules = async () => {
  try {
    const response = await apiClient.get('/rules')
    return toRuleList(response.data)
  } catch (error) {
    throw new Error('Unable to load rules.', { cause: error })
  }
}

export const getLogs = async () => {
  try {
    const response = await apiClient.get('/logs')
    return toLogList(response.data)
  } catch (error) {
    throw new Error('Unable to load system activity logs.', { cause: error })
  }
}

export const updateAlertStatus = async (alertId, status, note = '') => {
  try {
    const trimmedNote = note.trim()
    await apiClient.patch(`/alerts/${alertId}/status`, {
      status,
      action: getStatusAction(status),
      description: trimmedNote || `Status changed to ${status}`,
    })
    return { alertId, status, note: trimmedNote }
  } catch (error) {
    throw new Error('Unable to update alert status.', { cause: error })
  }
}

export const updateRule = async (ruleId, payload) => {
  try {
    await apiClient.put(`/rules/${ruleId}`, mapRulePayloadToApi(payload))
    return { ruleId, ...payload }
  } catch (error) {
    throw new Error('Unable to update rule.', { cause: error })
  }
}

export const createRule = async (payload) => {
  try {
    const response = await apiClient.post('/rules', mapRulePayloadToApi(payload))
    return normalizeRule(response.data)
  } catch (error) {
    throw new Error('Unable to create rule.', { cause: error })
  }
}

export { apiClient }

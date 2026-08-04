import axios from 'axios'
import { mockTransactions } from '../data/mockTransactions'
import { mockAlerts } from '../data/mockAlerts'
import { mockRules } from '../data/mockRules'
import { normalizeAlert, normalizeRule, normalizeTransaction } from './adapters'

const apiClient = axios.create({
  baseURL: '/api',
  timeout: 8000,
})

const withLatency = (data) =>
  new Promise((resolve) => {
    setTimeout(() => resolve(structuredClone(data)), 450)
  })

const toTransactionList = (items = []) => items.map((item) => normalizeTransaction(item))
const toRuleList = (items = []) => items.map((item) => normalizeRule(item))
const toAlertList = (items = [], transactions = [], rules = []) =>
  items.map((item) => normalizeAlert(item, transactions, rules))

export const getTransactions = async () => {
  try {
    // Future integration:
    // const response = await apiClient.get('/transactions')
    // return toTransactionList(response.data)
    const data = await withLatency(mockTransactions)
    return toTransactionList(data)
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

    // Future integration:
    // const response = await apiClient.get('/alerts')
    // return toAlertList(response.data, dependencyTransactions, dependencyRules)
    const alerts = await withLatency(mockAlerts)

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
    // Future integration:
    // const response = await apiClient.get(`/alerts/${alertId}`)
    // const transactions = await getTransactions()
    // const rules = await getRules()
    // return normalizeAlert(response.data, transactions, rules)
    const alerts = await getAlerts()
    const alert = alerts.find((item) => item.id === alertId)
    return alert || null
  } catch (error) {
    throw new Error('Unable to load alert details.', { cause: error })
  }
}

export const getRules = async () => {
  try {
    // Future integration:
    // const response = await apiClient.get('/rules')
    // return toRuleList(response.data)
    const data = await withLatency(mockRules)
    return toRuleList(data)
  } catch (error) {
    throw new Error('Unable to load rules.', { cause: error })
  }
}

export const updateAlertStatus = async (alertId, status) => {
  try {
    // Future integration:
    // await apiClient.patch(`/alerts/${alertId}`, { status })
    return await withLatency({ alertId, status })
  } catch (error) {
    throw new Error('Unable to update alert status.', { cause: error })
  }
}

export const updateRule = async (ruleId, payload) => {
  try {
    // Future integration:
    // await apiClient.put(`/rules/${ruleId}`, payload)
    return await withLatency({ ruleId, ...payload })
  } catch (error) {
    throw new Error('Unable to update rule.', { cause: error })
  }
}

export const createRule = async (payload) => {
  try {
    // Future integration:
    // const response = await apiClient.post('/rules', payload)
    // return response.data
    return await withLatency(payload)
  } catch (error) {
    throw new Error('Unable to create rule.', { cause: error })
  }
}

export { apiClient }

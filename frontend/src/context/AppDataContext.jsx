import { useCallback, useEffect, useState } from 'react'
import {
  createRule,
  getAlerts,
  getRules,
  getTransactions,
  updateAlertStatus,
  updateRule,
} from '../services/api'
import { AppDataContext } from './appDataContextObject'

const appendTimelineEvent = (timeline = [], action) => {
  const time = new Date().toISOString()
  return [...timeline, { time, action }]
}

export function AppDataProvider({ children }) {
  const [transactions, setTransactions] = useState([])
  const [alerts, setAlerts] = useState([])
  const [rules, setRules] = useState([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState('')

  const loadData = useCallback(async () => {
    setIsLoading(true)
    setError('')

    try {
      const [txData, ruleData] = await Promise.all([
        getTransactions(),
        getRules(),
      ])

      const alertData = await getAlerts({
        transactions: txData,
        rules: ruleData,
      })

      setTransactions(txData)
      setAlerts(alertData)
      setRules(ruleData)
    } catch (loadError) {
      setError(loadError.message || 'Failed to load dashboard data.')
    } finally {
      setIsLoading(false)
    }
  }, [])

  useEffect(() => {
    const timer = setTimeout(() => {
      loadData()
    }, 0)

    return () => clearTimeout(timer)
  }, [loadData])

  const changeAlertStatus = async (alertId, status) => {
    await updateAlertStatus(alertId, status)

    setAlerts((currentAlerts) =>
      currentAlerts.map((alert) => {
        if (alert.id !== alertId) {
          return alert
        }

        return {
          ...alert,
          status,
          timeline: appendTimelineEvent(
            alert.timeline,
            `Alert status changed to ${status}.`,
          ),
        }
      }),
    )
  }

  const upsertRule = async (ruleId, payload) => {
    if (ruleId) {
      await updateRule(ruleId, payload)

      setRules((currentRules) =>
        currentRules.map((rule) =>
          rule.id === ruleId
            ? {
                ...rule,
                ...payload,
                lastUpdated: new Date().toISOString(),
              }
            : rule,
        ),
      )
      return
    }

    const newRule = {
      id: `RULE-${String(Date.now()).slice(-4)}`,
      ...payload,
      lastUpdated: new Date().toISOString(),
    }

    await createRule(newRule)
    setRules((currentRules) => [newRule, ...currentRules])
  }

  const toggleRuleStatus = (ruleId) => {
    setRules((currentRules) =>
      currentRules.map((rule) =>
        rule.id === ruleId
          ? {
              ...rule,
              status: rule.status === 'ENABLED' ? 'DISABLED' : 'ENABLED',
              lastUpdated: new Date().toISOString(),
            }
          : rule,
      ),
    )
  }

  const value = {
    transactions,
    alerts,
    rules,
    isLoading,
    error,
    loadData,
    changeAlertStatus,
    upsertRule,
    toggleRuleStatus,
  }

  return (
    <AppDataContext.Provider value={value}>{children}</AppDataContext.Provider>
  )
}

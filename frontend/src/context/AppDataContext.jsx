import { useCallback, useEffect, useMemo, useState } from 'react'
import {
  createRule,
  getAlerts,
  getRules,
  getTransactions,
  updateAlertStatus,
  updateRule,
} from '../services/api'
import { AppDataContext } from './appDataContextObject'
import { useAuth } from './useAuth'

const appendTimelineEvent = (action, timeline = []) => {
  const time = new Date().toISOString()
  return [...timeline, { time, action }]
}

export function AppDataProvider({ children }) {
  const { isAuthenticated } = useAuth()
  const [transactions, setTransactions] = useState([])
  const [alerts, setAlerts] = useState([])
  const [rules, setRules] = useState([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState('')

  const loadData = useCallback(async () => {
    if (!isAuthenticated) {
      return
    }

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
  }, [isAuthenticated])

  useEffect(() => {
    if (!isAuthenticated) {
      setTransactions([])
      setAlerts([])
      setRules([])
      setError('')
      setIsLoading(false)
      return
    }

    const timer = setTimeout(() => {
      loadData()
    }, 0)

    return () => clearTimeout(timer)
  }, [isAuthenticated, loadData])

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
            `Alert status changed to ${status}.`,
            alert.timeline,
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

    const createdRule = await createRule(payload)
    setRules((currentRules) => [createdRule, ...currentRules])
  }

  const toggleRuleStatus = async (ruleId) => {
    const currentRule = rules.find((rule) => rule.id === ruleId)
    if (!currentRule) {
      return
    }

    const nextStatus = currentRule.status === 'ENABLED' ? 'DISABLED' : 'ENABLED'

    await updateRule(ruleId, {
      name: currentRule.name,
      type: currentRule.type,
      threshold: currentRule.threshold,
      severity: currentRule.severity,
      status: nextStatus,
      description: currentRule.description,
    })

    setRules((currentRules) =>
      currentRules.map((rule) =>
        rule.id === ruleId
          ? {
              ...rule,
              status: nextStatus,
              lastUpdated: new Date().toISOString(),
            }
          : rule,
      ),
    )
  }

  const value = useMemo(
    () => ({
      transactions,
      alerts,
      rules,
      isLoading,
      error,
      loadData,
      changeAlertStatus,
      upsertRule,
      toggleRuleStatus,
    }),
    [
      transactions,
      alerts,
      rules,
      isLoading,
      error,
      loadData,
      changeAlertStatus,
      upsertRule,
      toggleRuleStatus,
    ],
  )

  return (
    <AppDataContext.Provider value={value}>{children}</AppDataContext.Provider>
  )
}

import { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import {
  createRule,
  getAlerts,
  rollbackTransaction,
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

const TRANSACTION_POLL_INTERVAL_MS = 5000

const resolveAlertStreamBaseUrl = () =>
  import.meta.env.VITE_API_BASE_URL || `${window.location.protocol}//${window.location.hostname}:8080`

export function AppDataProvider({ children }) {
  const { isAuthenticated, token } = useAuth()
  const [transactions, setTransactions] = useState([])
  const [alerts, setAlerts] = useState([])
  const [rules, setRules] = useState([])
  const [alertNotifications, setAlertNotifications] = useState([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState('')
  const notificationTimeoutsRef = useRef(new Map())

  const dismissAlertNotification = useCallback((notificationId) => {
    const timeoutId = notificationTimeoutsRef.current.get(notificationId)

    if (timeoutId) {
      window.clearTimeout(timeoutId)
      notificationTimeoutsRef.current.delete(notificationId)
    }

    setAlertNotifications((currentNotifications) =>
      currentNotifications.filter((notification) => notification.id !== notificationId),
    )
  }, [])

  const pushAlertNotification = useCallback(
    (payload) => {
      const notificationId = `alert-${payload.alertId ?? Date.now()}-${Date.now()}`

      setAlertNotifications((currentNotifications) => [
        {
          id: notificationId,
          alertId: payload.alertId,
          severity: payload.severity || 'LOW',
          title: payload.alertType || 'New alert detected',
          message: payload.alertMessage || 'A new monitoring alert has been raised.',
          createdAt: payload.createdAt || new Date().toISOString(),
        },
        ...currentNotifications,
      ].slice(0, 4))

      const timeoutId = window.setTimeout(() => {
        dismissAlertNotification(notificationId)
      }, 5000)

      notificationTimeoutsRef.current.set(notificationId, timeoutId)
    },
    [dismissAlertNotification],
  )

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
      const resetTimer = window.setTimeout(() => {
        setTransactions([])
        setAlerts([])
        setRules([])
        setAlertNotifications([])
        setError('')
        setIsLoading(false)
      }, 0)

      return () => window.clearTimeout(resetTimer)
    }

    const timer = setTimeout(() => {
      loadData()
    }, 0)

    return () => clearTimeout(timer)
  }, [isAuthenticated, loadData])

  useEffect(() => {
    if (!isAuthenticated || !token) {
      return undefined
    }

    const streamUrl = `${resolveAlertStreamBaseUrl()}/api/alerts/stream?token=${encodeURIComponent(token)}`
    const eventSource = new EventSource(streamUrl)

    const handleAlertCreated = (event) => {
      try {
        const payload = JSON.parse(event.data)
        pushAlertNotification(payload)
        loadData()
      } catch {
        // Ignore malformed event payloads and keep the stream alive.
      }
    }

    eventSource.addEventListener('alert-created', handleAlertCreated)

    return () => {
      eventSource.removeEventListener('alert-created', handleAlertCreated)
      eventSource.close()
    }
  }, [isAuthenticated, token, loadData, pushAlertNotification])

  useEffect(() => {
    if (!isAuthenticated) {
      return undefined
    }

    const intervalId = window.setInterval(async () => {
      try {
        const latestTransactions = await getTransactions()
        setTransactions(latestTransactions)
      } catch {
        // Keep last known data if background refresh fails.
      }
    }, TRANSACTION_POLL_INTERVAL_MS)

    return () => window.clearInterval(intervalId)
  }, [isAuthenticated])

  useEffect(() => () => {
    notificationTimeoutsRef.current.forEach((timeoutId) => window.clearTimeout(timeoutId))
    notificationTimeoutsRef.current.clear()
  }, [])

  const changeAlertStatus = useCallback(async (alertId, status, note = '') => {
    const trimmedNote = note.trim()

    if (status === 'ROLLBACK') {
      const targetAlert = alerts.find((alert) => alert.id === alertId)
      const transactionId = targetAlert?.transactionId

      if (!transactionId || transactionId === 'N/A') {
        throw new Error('Unable to rollback alert without a linked transaction.')
      }

      await rollbackTransaction(transactionId, trimmedNote)
      await loadData()
      return
    }

    await updateAlertStatus(alertId, status, trimmedNote)

    setAlerts((currentAlerts) =>
      currentAlerts.map((alert) => {
        if (alert.id !== alertId) {
          return alert
        }

        return {
          ...alert,
          status,
          timeline: appendTimelineEvent(
            trimmedNote
              ? `Alert status changed to ${status}. Note: ${trimmedNote}`
              : `Alert status changed to ${status}.`,
            alert.timeline,
          ),
        }
      }),
    )
  }, [alerts, loadData])

  const upsertRule = useCallback(async (ruleId, payload) => {
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
  }, [])

  const toggleRuleStatus = useCallback(async (ruleId) => {
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
  }, [rules])

  const value = useMemo(
    () => ({
      transactions,
      alerts,
      rules,
      alertNotifications,
      isLoading,
      error,
      loadData,
      changeAlertStatus,
      upsertRule,
      toggleRuleStatus,
      dismissAlertNotification,
    }),
    [
      transactions,
      alerts,
      rules,
      alertNotifications,
      isLoading,
      error,
      loadData,
      changeAlertStatus,
      upsertRule,
      toggleRuleStatus,
      dismissAlertNotification,
    ],
  )

  return (
    <AppDataContext.Provider value={value}>{children}</AppDataContext.Provider>
  )
}

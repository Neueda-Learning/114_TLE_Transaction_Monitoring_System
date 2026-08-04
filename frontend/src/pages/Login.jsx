import { useState } from 'react'
import { Navigate, useLocation, useNavigate } from 'react-router-dom'
import Modal from '../components/Common/Modal'
import LoginCard from '../components/LoginCard'
import { useAuth } from '../context/useAuth'
import '../styles/login.css'

const REMEMBER_KEY = 'tm_remember_username'

const stats = [
  { value: '2.4M+', label: 'Transactions Monitored Daily', icon: '⇄' },
  { value: '99.98%', label: 'Platform Uptime', icon: '◍' },
  { value: '180ms', label: 'Avg. Fraud Detection Time', icon: '⚑' },
  { value: '24/7', label: 'Security Operations Coverage', icon: '🛡' },
]

function Login() {
  const { isAuthenticated, login, authError, verifyEmployeeId, updatePassword } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [isSubmitting, setIsSubmitting] = useState(false)

  const [isForgotOpen, setIsForgotOpen] = useState(false)
  const [forgotStep, setForgotStep] = useState('request')
  const [forgotEmployeeId, setForgotEmployeeId] = useState('')
  const [forgotNewPassword, setForgotNewPassword] = useState('')
  const [forgotError, setForgotError] = useState('')
  const [forgotMessage, setForgotMessage] = useState('')
  const [isForgotSubmitting, setIsForgotSubmitting] = useState(false)

  const redirectTo = location.state?.from?.pathname || '/dashboard'
  const rememberedUsername = localStorage.getItem(REMEMBER_KEY) || ''

  if (isAuthenticated) {
    return <Navigate to={redirectTo} replace />
  }

  const handleSubmit = async ({ username, password, rememberMe }) => {
    setIsSubmitting(true)
    const success = await login(username, password, rememberMe)
    setIsSubmitting(false)

    if (success) {
      if (rememberMe) {
        localStorage.setItem(REMEMBER_KEY, username)
      } else {
        localStorage.removeItem(REMEMBER_KEY)
      }
      navigate(redirectTo, { replace: true })
    }
  }

  const openForgotPassword = () => {
    setForgotStep('request')
    setForgotEmployeeId('')
    setForgotNewPassword('')
    setForgotError('')
    setForgotMessage('')
    setIsForgotOpen(true)
  }

  const closeForgotPassword = () => setIsForgotOpen(false)

  const handleForgotRequest = async (event) => {
    event.preventDefault()
    setForgotError('')
    setIsForgotSubmitting(true)

    try {
      await verifyEmployeeId(forgotEmployeeId)
      setForgotMessage('Employee ID verified. Enter a new password below.')
      setForgotStep('reset')
    } catch (error) {
      setForgotError(error.message || 'Unable to verify Employee ID.')
    } finally {
      setIsForgotSubmitting(false)
    }
  }

  const handleForgotReset = async (event) => {
    event.preventDefault()
    setForgotError('')
    setIsForgotSubmitting(true)

    try {
      await updatePassword(forgotEmployeeId, forgotNewPassword)
      setForgotStep('done')
    } catch (error) {
      setForgotError(error.message || 'Unable to update password.')
    } finally {
      setIsForgotSubmitting(false)
    }
  }

  // Direct DOM writes (no re-render) keep the cursor spotlight smooth on every mouse move.
  const handleSpotlightMove = (event) => {
    const spotlight = event.currentTarget.querySelector('.auth-spotlight')
    if (!spotlight) {
      return
    }
    const rect = event.currentTarget.getBoundingClientRect()
    spotlight.style.left = `${event.clientX - rect.left}px`
    spotlight.style.top = `${event.clientY - rect.top}px`
  }

  const handleSpotlightEnter = (event) => {
    const spotlight = event.currentTarget.querySelector('.auth-spotlight')
    if (spotlight) {
      spotlight.style.opacity = '1'
    }
  }

  const handleSpotlightLeave = (event) => {
    const spotlight = event.currentTarget.querySelector('.auth-spotlight')
    if (spotlight) {
      spotlight.style.opacity = '0'
    }
  }

  return (
    <div className="auth-page">
      <section
        className="auth-visual"
        onMouseMove={handleSpotlightMove}
        onMouseEnter={handleSpotlightEnter}
        onMouseLeave={handleSpotlightLeave}
      >
        <div className="auth-visual-pattern" aria-hidden="true" />
        <div className="auth-visual-nodes" aria-hidden="true" />
        <div className="auth-visual-blob auth-visual-blob-a" aria-hidden="true" />
        <div className="auth-visual-blob auth-visual-blob-b" aria-hidden="true" />
        <div className="auth-visual-blob auth-visual-blob-c" aria-hidden="true" />
        <div className="auth-spotlight auth-spotlight-visual" aria-hidden="true" />

        <div className="auth-brand">
          <div className="auth-shield">
            <span className="auth-shield-pulse" aria-hidden="true" />
            <svg viewBox="0 0 24 24" width="30" height="30" fill="none" aria-hidden="true">
              <path
                d="M12 3l7 3v5c0 4.5-3 7.8-7 10-4-2.2-7-5.5-7-10V6l7-3Z"
                stroke="currentColor"
                strokeWidth="1.6"
                strokeLinejoin="round"
              />
              <path
                d="M9 12l2 2 4-4"
                stroke="currentColor"
                strokeWidth="1.6"
                strokeLinecap="round"
                strokeLinejoin="round"
              />
            </svg>
          </div>

          <h1>Transaction Monitoring System</h1>
          <p>Real-time fraud detection and risk monitoring platform.</p>
        </div>

        <div className="auth-stats-grid">
          {stats.map((stat, index) => (
            <div
              key={stat.label}
              className="auth-stat-card"
              style={{ animationDelay: `${index * 0.12}s` }}
            >
              <span className="auth-stat-icon" aria-hidden="true">
                {stat.icon}
              </span>
              <p className="auth-stat-value">{stat.value}</p>
              <p className="auth-stat-label">{stat.label}</p>
            </div>
          ))}
        </div>
      </section>

      <section
        className="auth-form-wrap"
        onMouseMove={handleSpotlightMove}
        onMouseEnter={handleSpotlightEnter}
        onMouseLeave={handleSpotlightLeave}
      >
        <div className="auth-spotlight auth-spotlight-form" aria-hidden="true" />
        <div className="auth-form-glow" aria-hidden="true" />
        <LoginCard
          defaultUsername={rememberedUsername}
          onSubmit={handleSubmit}
          isSubmitting={isSubmitting}
          error={authError}
          onForgotPassword={openForgotPassword}
        />
      </section>

      <footer className="auth-footer">
        <span>© 2026 TrustMonitor</span>
        <span className="auth-footer-dot" aria-hidden="true">•</span>
        <span>Enterprise-grade security &amp; compliance</span>
      </footer>

      <Modal
        title="Reset Password"
        isOpen={isForgotOpen}
        onClose={closeForgotPassword}
        footer={
          forgotStep === 'done' ? (
            <button type="button" className="primary-btn" onClick={closeForgotPassword}>
              Done
            </button>
          ) : (
            <>
              <button type="button" className="ghost-btn" onClick={closeForgotPassword}>
                Cancel
              </button>
              <button
                type="submit"
                form="forgot-password-form"
                className="primary-btn"
                disabled={isForgotSubmitting}
              >
                {forgotStep === 'request' ? 'Verify Employee ID' : 'Update Password'}
              </button>
            </>
          )
        }
      >
        {forgotStep === 'done' ? (
          <p>Your password has been reset. You can now sign in with your new password.</p>
        ) : (
          <form
            id="forgot-password-form"
            onSubmit={forgotStep === 'request' ? handleForgotRequest : handleForgotReset}
          >
            {forgotStep === 'request' ? (
              <label className="rule-form">
                Employee ID
                <input
                  required
                  value={forgotEmployeeId}
                  onChange={(event) => setForgotEmployeeId(event.target.value)}
                />
              </label>
            ) : (
              <label className="rule-form">
                New Password
                <input
                  required
                  type="password"
                  minLength={6}
                  value={forgotNewPassword}
                  onChange={(event) => setForgotNewPassword(event.target.value)}
                />
              </label>
            )}

            {forgotMessage ? <p className="helper-text">{forgotMessage}</p> : null}
            {forgotError ? (
              <p className="auth-error" role="alert">
                {forgotError}
              </p>
            ) : null}
          </form>
        )}
      </Modal>
    </div>
  )
}

export default Login

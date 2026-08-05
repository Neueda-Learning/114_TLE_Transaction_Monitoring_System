import { useState } from 'react'

// Small inline icons keep this dependency-free (no icon library allowed).
function IdIcon() {
  return (
    <svg viewBox="0 0 24 24" width="18" height="18" fill="none" aria-hidden="true">
      <rect x="3" y="5" width="18" height="14" rx="2" stroke="currentColor" strokeWidth="1.6" />
      <circle cx="9" cy="12" r="2" stroke="currentColor" strokeWidth="1.6" />
      <path d="M13 10.5h5M13 13.5h5" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" />
    </svg>
  )
}

function LockIcon() {
  return (
    <svg viewBox="0 0 24 24" width="18" height="18" fill="none" aria-hidden="true">
      <rect x="5" y="11" width="14" height="9" rx="2" stroke="currentColor" strokeWidth="1.6" />
      <path d="M8 11V8a4 4 0 0 1 8 0v3" stroke="currentColor" strokeWidth="1.6" />
    </svg>
  )
}

function EyeIcon({ hidden }) {
  return hidden ? (
    <svg viewBox="0 0 24 24" width="18" height="18" fill="none" aria-hidden="true">
      <path
        d="M3 3l18 18M10.6 10.7a2.5 2.5 0 0 0 3.4 3.4M9.3 5.5A10.4 10.4 0 0 1 12 5c5 0 8.5 4 9.5 7-.4 1.1-1 2.2-1.8 3.2M6.5 6.7C4.4 8.1 2.9 10 2 12c1 3 4.5 7 10 7 1.4 0 2.7-.25 3.9-.7"
        stroke="currentColor"
        strokeWidth="1.6"
        strokeLinecap="round"
      />
    </svg>
  ) : (
    <svg viewBox="0 0 24 24" width="18" height="18" fill="none" aria-hidden="true">
      <path
        d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7-10-7-10-7Z"
        stroke="currentColor"
        strokeWidth="1.6"
      />
      <circle cx="12" cy="12" r="3" stroke="currentColor" strokeWidth="1.6" />
    </svg>
  )
}

function ShieldIcon() {
  return (
    <svg viewBox="0 0 24 24" width="16" height="16" fill="none" aria-hidden="true">
      <path
        d="M12 3l7 3v5c0 4.5-3 7.8-7 10-4-2.2-7-5.5-7-10V6l7-3Z"
        stroke="currentColor"
        strokeWidth="1.6"
        strokeLinejoin="round"
      />
      <path d="M9 12l2 2 4-4" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  )
}

function CheckIcon() {
  return (
    <svg viewBox="0 0 24 24" width="16" height="16" fill="none" aria-hidden="true">
      <circle cx="12" cy="12" r="9" stroke="currentColor" strokeWidth="1.6" />
      <path d="M8 12.5l2.5 2.5L16 9.5" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  )
}

// Presentational login form (common for all roles). Auth logic/state lives in
// the page (Login.jsx) + services/auth.js.
function LoginCard({ defaultUsername, onSubmit, isSubmitting, error, onForgotPassword }) {
  const [username, setUsername] = useState(defaultUsername || '')
  const [password, setPassword] = useState('')
  const [rememberMe, setRememberMe] = useState(Boolean(defaultUsername))
  const [showPassword, setShowPassword] = useState(false)

  const [usernameFocused, setUsernameFocused] = useState(false)
  const [passwordFocused, setPasswordFocused] = useState(false)

  const [usernameTouched, setUsernameTouched] = useState(false)
  const [passwordTouched, setPasswordTouched] = useState(false)

  const isUsernameValid = username.trim().length >= 3
  const isPasswordValid = password.length >= 6

  const usernameFloating = usernameFocused || username.length > 0
  const passwordFloating = passwordFocused || password.length > 0

  const handleSubmit = (event) => {
    event.preventDefault()
    onSubmit({ username, password, rememberMe })
  }

  // Ripple travels from the exact click point for tactile, modern button feedback.
  const handleRipple = (event) => {
    const button = event.currentTarget
    const rect = button.getBoundingClientRect()
    const size = Math.max(rect.width, rect.height)
    const ripple = document.createElement('span')

    ripple.className = 'auth-ripple'
    ripple.style.width = `${size}px`
    ripple.style.height = `${size}px`
    ripple.style.left = `${event.clientX - rect.left - size / 2}px`
    ripple.style.top = `${event.clientY - rect.top - size / 2}px`

    button.appendChild(ripple)
    ripple.addEventListener('animationend', () => ripple.remove())
  }

  return (
    <article className="auth-card">
      <div className="auth-card-heading">
        <span className="auth-eyebrow">Secure Access Portal</span>
        <h1>
          Welcome back, <span className="auth-heading-accent">Analyst</span>
        </h1>
        <p>Sign in to access the fraud monitoring console.</p>
      </div>

      <form className="auth-form" onSubmit={handleSubmit} noValidate>
        <div
          className={`auth-field ${usernameFloating ? 'auth-field-float' : ''} ${
            usernameTouched && isUsernameValid ? 'is-valid' : ''
          }`}
        >
          <span className="auth-field-icon">
            <IdIcon />
          </span>
          <input
            id="username"
            type="text"
            required
            autoFocus
            autoComplete="username"
            aria-invalid={usernameTouched && !isUsernameValid}
            value={username}
            onFocus={() => setUsernameFocused(true)}
            onBlur={() => {
              setUsernameFocused(false)
              setUsernameTouched(true)
            }}
            onChange={(event) => setUsername(event.target.value)}
          />
          <label htmlFor="username">Username</label>
          <div className="auth-field-trailing">
            {usernameTouched && isUsernameValid ? (
              <span className="auth-field-check" aria-hidden="true">
                <CheckIcon />
              </span>
            ) : null}
          </div>
        </div>

        <div
          className={`auth-field ${passwordFloating ? 'auth-field-float' : ''} ${
            passwordTouched && isPasswordValid ? 'is-valid' : ''
          }`}
        >
          <span className="auth-field-icon">
            <LockIcon />
          </span>
          <input
            id="password"
            type={showPassword ? 'text' : 'password'}
            required
            autoComplete="current-password"
            aria-invalid={passwordTouched && !isPasswordValid}
            value={password}
            onFocus={() => setPasswordFocused(true)}
            onBlur={() => {
              setPasswordFocused(false)
              setPasswordTouched(true)
            }}
            onChange={(event) => setPassword(event.target.value)}
          />
          <label htmlFor="password">Password</label>
          <div className="auth-field-trailing">
            {passwordTouched && isPasswordValid ? (
              <span className="auth-field-check" aria-hidden="true">
                <CheckIcon />
              </span>
            ) : null}
            <button
              type="button"
              className="auth-field-toggle"
              onClick={() => setShowPassword((current) => !current)}
              aria-label={showPassword ? 'Hide password' : 'Show password'}
            >
              <EyeIcon hidden={showPassword} />
            </button>
          </div>
        </div>

        <div className="auth-row">
          <label className="auth-remember">
            <input
              type="checkbox"
              checked={rememberMe}
              onChange={(event) => setRememberMe(event.target.checked)}
            />
            Remember me
          </label>

          <button type="button" className="auth-forgot" onClick={onForgotPassword}>
            Forgot password?
          </button>
        </div>

        {error ? (
          <p className="auth-error" role="alert" aria-live="assertive">
            {error}
          </p>
        ) : null}

        <button
          type="submit"
          className="auth-submit"
          disabled={isSubmitting}
          onClick={handleRipple}
        >
          {isSubmitting ? <span className="auth-spinner" aria-hidden="true" /> : null}
          {isSubmitting ? 'Verifying credentials…' : 'Sign In'}
        </button>
      </form>

      <div className="auth-security-note">
        <div className="auth-security-badges">
          <span className="auth-security-badge">
            <LockIcon />
            Encrypted
          </span>
          <span className="auth-security-badge">
            <ShieldIcon />
            Audited
          </span>
          <span className="auth-security-badge">
            <CheckIcon />
            Compliant
          </span>
        </div>
        <p>Protected banking environment</p>
        <p>All activities are monitored and audited.</p>
      </div>
    </article>
  )
}

export default LoginCard

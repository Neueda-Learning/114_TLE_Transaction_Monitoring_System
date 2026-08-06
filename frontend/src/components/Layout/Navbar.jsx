import { useLocation, useNavigate } from 'react-router-dom'

const titleByPath = {
  '/dashboard': 'Transaction Monitoring Dashboard',
  '/transactions': 'Transaction Monitoring',
  '/alerts': 'Alert Management',
  '/rules': 'Rule Management',
  '/analytics': 'Risk Analytics',
  '/settings': 'Platform Settings',
}

function Navbar({ onOpenSidebar }) {
  const location = useLocation()
  const navigate = useNavigate()

  const currentTitle =
    titleByPath[location.pathname] ||
    (location.pathname.startsWith('/alerts/')
      ? 'Alert Investigation Details'
      : 'Transaction Monitoring Dashboard')

  return (
    <header className="topbar">
      <button type="button" className="menu-btn" onClick={onOpenSidebar}>
        Menu
      </button>

      <div>
        <p className="eyebrow">Operations Center</p>
        <h1>{currentTitle}</h1>
      </div>

      <div className="topbar-right">
        <button
          type="button"
          className="notification-pill"
          title="Unread notifications"
          onClick={() => navigate('/alerts')}
        >
          <span className="dot" />
          4 new alerts
        </button>
      </div>
    </header>
  )
}

export default Navbar

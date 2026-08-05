import { useLocation } from 'react-router-dom'

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
        <div className="notification-pill" title="Unread notifications">
          <span className="dot" />
          4 new alerts
        </div>
      </div>
    </header>
  )
}

export default Navbar

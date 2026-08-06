import { useLocation, useNavigate } from 'react-router-dom'
import { useAppData } from '../../context/useAppData'

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
  const { alerts, alertNotifications } = useAppData()

  // Active alerts from DB; pulse dot only when SSE has pushed new ones this session
  const activeAlertCount = alerts.filter((a) =>
    ['OPEN', 'ACKNOWLEDGED', 'INVESTIGATING'].includes(a.status),
  ).length
  const hasNewPush = alertNotifications.length > 0

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
          title="View active alerts"
          onClick={() => navigate('/alerts')}
        >
          {hasNewPush && <span className="dot" />}
          {!hasNewPush && activeAlertCount > 0 && <span className="dot dot-static" />}
          {activeAlertCount} {activeAlertCount === 1 ? 'alert' : 'alerts'} active
        </button>
      </div>
    </header>
  )
}

export default Navbar

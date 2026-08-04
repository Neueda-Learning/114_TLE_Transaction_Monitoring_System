import { NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/useAuth'
import { NAV_ITEMS, getRoleDisplayName } from '../../utils/roles'

const initialsOf = (name = '') =>
  name
    .split(' ')
    .map((part) => part[0])
    .join('')
    .slice(0, 2)
    .toUpperCase() || 'U'

function Sidebar({ isOpen, onClose }) {
  const { user, role, logout } = useAuth()
  const navigate = useNavigate()

  const visibleNavItems = NAV_ITEMS.filter((item) => item.roles.includes(role))

  const handleLogout = async () => {
    await logout()
    navigate('/login', { replace: true })
  }

  return (
    <aside className={`sidebar ${isOpen ? 'open' : ''}`}>
      <div className="sidebar-brand">
        <div className="logo-mark">TM</div>
        <div>
          <p className="brand-title">TrustMonitor</p>
          <p className="brand-subtitle">Fraud Control Desk</p>
        </div>
      </div>

      <nav className="sidebar-nav" aria-label="Main navigation">
        {visibleNavItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            className={({ isActive }) =>
              `nav-item ${isActive ? 'active' : ''}`
            }
            onClick={onClose}
          >
            <span className="nav-icon" aria-hidden="true">
              {item.icon}
            </span>
            <span>{item.label}</span>
          </NavLink>
        ))}
      </nav>

      <section className="sidebar-user surface-elevated">
        <div className="avatar">{initialsOf(user?.name)}</div>
        <div>
          <p className="user-name">{user?.name || 'Guest'}</p>
          <p className="user-role">{getRoleDisplayName(role)}</p>
        </div>
        <span className="live-indicator" title="Online" />
      </section>

      <button type="button" className="ghost-btn sidebar-logout" onClick={handleLogout}>
        Sign Out
      </button>
    </aside>
  )
}

export default Sidebar

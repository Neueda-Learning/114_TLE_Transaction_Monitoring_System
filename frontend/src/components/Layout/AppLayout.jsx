import { Outlet } from 'react-router-dom'
import { useState } from 'react'
import Sidebar from './Sidebar'
import Navbar from './Navbar'
import Footer from './Footer'
import { useAppData } from '../../context/useAppData'

const severityLabel = (severity) => (severity || 'LOW').toLowerCase()

function AppLayout() {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false)
  const { alertNotifications, dismissAlertNotification } = useAppData()

  return (
    <div className="app-shell">
      <Sidebar isOpen={isSidebarOpen} onClose={() => setIsSidebarOpen(false)} />

      <div className="app-main">
        <Navbar onOpenSidebar={() => setIsSidebarOpen(true)} />
        <main className="content-area">
          <Outlet />
        </main>
        <Footer />
      </div>

      {isSidebarOpen ? (
        <button
          type="button"
          className="sidebar-backdrop"
          onClick={() => setIsSidebarOpen(false)}
          aria-label="Close navigation"
        />
      ) : null}

      {alertNotifications.length ? (
        <section className="alert-toast-stack" aria-live="polite" aria-label="Realtime alerts">
          {alertNotifications.map((notification) => (
            <article
              key={notification.id}
              className={`alert-toast severity-${severityLabel(notification.severity)}`}
            >
              <div className="alert-toast-copy">
                <p className="alert-toast-title">{notification.title}</p>
                <p className="alert-toast-message">{notification.message}</p>
              </div>
              <button
                type="button"
                className="alert-toast-close"
                onClick={() => dismissAlertNotification(notification.id)}
                aria-label="Close alert notification"
              >
                Close
              </button>
            </article>
          ))}
        </section>
      ) : null}
    </div>
  )
}

export default AppLayout

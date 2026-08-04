import { Outlet } from 'react-router-dom'
import { useState } from 'react'
import Sidebar from './Sidebar'
import Navbar from './Navbar'

function AppLayout() {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false)

  return (
    <div className="app-shell">
      <Sidebar isOpen={isSidebarOpen} onClose={() => setIsSidebarOpen(false)} />

      <div className="app-main">
        <Navbar onOpenSidebar={() => setIsSidebarOpen(true)} />
        <main className="content-area">
          <Outlet />
        </main>
      </div>

      {isSidebarOpen ? (
        <button
          type="button"
          className="sidebar-backdrop"
          onClick={() => setIsSidebarOpen(false)}
          aria-label="Close navigation"
        />
      ) : null}
    </div>
  )
}

export default AppLayout

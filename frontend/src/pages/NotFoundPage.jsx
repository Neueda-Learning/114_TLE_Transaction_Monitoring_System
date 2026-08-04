import { Link } from 'react-router-dom'

function NotFoundPage() {
  return (
    <section className="state-panel surface-elevated">
      <h2>Page not found</h2>
      <p>This route is not part of the monitoring workspace.</p>
      <Link className="table-action" to="/dashboard">
        Go to dashboard
      </Link>
    </section>
  )
}

export default NotFoundPage

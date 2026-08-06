import { Link } from 'react-router-dom'

function Footer() {
  const currentYear = new Date().getFullYear()

  return (
    <footer className="app-footer">
      <div className="footer-inner">
        <div className="footer-brand">
          <div className="footer-logo">
            <span className="footer-logo-mark">TM</span>
            <div>
              <p className="footer-brand-name">TrustMonitor</p>
              <p className="footer-brand-tagline">Fraud Control Desk</p>
            </div>
          </div>
          <p className="footer-description">
            Enterprise-grade transaction monitoring and fraud detection platform for financial institutions.
          </p>
          <div className="footer-status-badge">
            <span className="footer-status-dot" />
            <span>All Systems Operational</span>
          </div>
        </div>

        <div className="footer-links-group">
          <div className="footer-col">
            <p className="footer-col-heading">Navigate</p>
            <ul className="footer-link-list">
              <li><Link to="/dashboard">Dashboard</Link></li>
              <li><Link to="/transactions">Transactions</Link></li>
              <li><Link to="/alerts">Alerts</Link></li>
              <li><Link to="/rules">Rules Management</Link></li>
              <li><Link to="/analytics">Reports</Link></li>
            </ul>
          </div>

          <div className="footer-col">
            <p className="footer-col-heading">Support</p>
            <ul className="footer-link-list">
              <li><a href="mailto:support@transactionmonitor.com">Contact Support</a></li>
              <li><a href="mailto:admin@transactionmonitor.com">Admin Contact</a></li>
              <li><span className="footer-link-plain">24/7 Helpdesk</span></li>
              <li><span className="footer-link-plain">Incident Response</span></li>
            </ul>
          </div>

          <div className="footer-col">
            <p className="footer-col-heading">Compliance</p>
            <ul className="footer-link-list">
              <li><span className="footer-link-plain">Privacy Policy</span></li>
              <li><span className="footer-link-plain">Terms of Use</span></li>
              <li><span className="footer-link-plain">Data Security</span></li>
              <li><span className="footer-link-plain">AML Guidelines</span></li>
            </ul>
          </div>
        </div>
      </div>

      <div className="footer-bottom">
        <p>&copy; {currentYear} TrustMonitor. All rights reserved.</p>
        <p>Built for financial compliance &amp; fraud prevention</p>
      </div>
    </footer>
  )
}

export default Footer

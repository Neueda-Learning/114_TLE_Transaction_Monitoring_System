# User Stories - Transaction Monitoring and Alerting System

## Project Overview

The Transaction Monitoring and Alerting System is designed to detect suspicious transaction patterns in real-time by evaluating transactions against configurable monitoring rules.

The system generates alerts, assigns fraud probability scores to transactions, and provides operators with tools to investigate and manage suspicious activity.

The system supports a single operator/user with authentication to ensure sensitive transaction data is protected.

## Story Quality Checklist

Use this checklist when adding or revising stories:

- Keep each story scoped to one user outcome.
- Ensure acceptance criteria are testable and unambiguous.
- Reference impacted module(s) where applicable.
- Include at least one negative/error-path criterion for sensitive flows.
- Keep story IDs stable once shared with team/test documentation.

---

# Epic 1: User Authentication and Security

## US-001: User Login

**As a** system operator  
**I want to** securely authenticate into the monitoring system  
**So that** only authorized users can access sensitive transaction data.

### Acceptance Criteria:
- User can login using valid credentials.
- Invalid credentials should be rejected.
- Authentication token/session should be generated after successful login.
- Protected APIs should require authentication.
- Sensitive transaction and alert data should not be accessible without authentication.

---

## US-002: User Logout

**As a** system operator  
**I want to** logout from the application  
**So that** unauthorized users cannot access my session.

### Acceptance Criteria:
- User can terminate their active session.
- Authentication token/session should become invalid after logout.

---

# Epic 2: Transaction Management

## US-003: Record Transaction

**As a** transaction monitoring system  
**I want to** store incoming transactions  
**So that** they can be evaluated for suspicious activity.

### Acceptance Criteria:
- System should allow creating transactions through REST API.
- Transaction details should include:
  - Transaction ID
  - Account ID
  - Sender details
  - Receiver/payee details
  - Amount
  - Currency
  - Timestamp
  - Transaction type
- Transactions should be persisted in the database.
- Every transaction should receive a fraud probability score.

---

## US-004: View Transactions

**As a** system operator  
**I want to** view all transactions  
**So that** I can review transaction activity.

### Acceptance Criteria:
- Operator can view transaction list.
- Transactions can be filtered by:
  - Date range
  - Account
  - Amount
  - Fraud score
  - Transaction status
- Operator can search transactions.

---

## US-005: Generate Fraud Probability Score

**As a** fraud monitoring system  
**I want to** calculate a fraud probability score for every transaction  
**So that** suspicious transactions can be prioritized.

### Acceptance Criteria:
- Every transaction receives a fraud score between 0-100%.
- Score should indicate likelihood of fraudulent activity.
- Fraud score should be stored with transaction data.
- High-risk transactions should be highlighted.

Example:


Transaction Amount: $15000
New Payee: Yes
Velocity Violation: Yes

Fraud Score: 87%
Risk Level: HIGH


---

# Epic 3: Transaction Monitoring Rules

## US-006: Configure Monitoring Rules

**As a** system operator  
**I want to** create and manage monitoring rules  
**So that** fraud detection criteria can be customized.

### Acceptance Criteria:
- Operator can create rules.
- Operator can update existing rules.
- Operator can enable/disable rules.
- Rules should contain:
  - Rule name
  - Rule type
  - Threshold values
  - Active status

---

# Rule Types

## US-007: Amount Threshold Rule

**As a** fraud monitoring system  
**I want to** detect transactions exceeding a configured amount  
**So that** unusually large transactions generate alerts.

### Example:

Alert when:


Transaction Amount > $10,000


### Acceptance Criteria:
- System checks transaction amount against configured threshold.
- Alert is generated when threshold is exceeded.

---

## US-008: Velocity Rule

**As a** fraud monitoring system  
**I want to** detect multiple transactions within a short time period  
**So that** suspicious transaction patterns can be identified.

### Example:


More than 5 transactions
within 10 minutes
from same account


### Acceptance Criteria:
- System tracks transaction frequency.
- Alert generated when velocity limit is exceeded.

---

## US-009: New Payee Rule

**As a** fraud monitoring system  
**I want to** detect transactions to previously unused payees  
**So that** potentially risky payments can be reviewed.

### Example:


First transaction from Account A
to Payee B


### Acceptance Criteria:
- System checks transaction history.
- New counterparties trigger alerts.

---

## US-010: Daily Transaction Limit Rule

**As a** fraud monitoring system  
**I want to** monitor cumulative daily transaction amounts  
**So that** unusual daily spending patterns can be detected.

### Example:


Daily transaction total > $50,000


### Acceptance Criteria:
- System calculates daily transaction totals.
- Alert generated when limit exceeded.

---

# Epic 4: Alert Management

## US-011: Generate Alert

**As a** monitoring system  
**I want to** generate alerts when rules are violated  
**So that** suspicious activity can be investigated.

### Acceptance Criteria:
- Alert contains:
  - Alert ID
  - Triggering rule
  - Related transaction(s)
  - Fraud score
  - Creation timestamp
  - Current status
- Alert starts with OPEN status.

---

# Alert Lifecycle


OPEN ---->ACKNOWLEDGED -----> INVESTIGATING ---->CLOSED

OPEN ---------> DISMISSED

INVESTIGATING -> DISMISSED


---

## US-012: View Active Alerts

**As a** system operator  
**I want to** view active alerts  
**So that** I can prioritize investigations.

### Acceptance Criteria:
- Operator can view OPEN, ACKNOWLEDGED, and INVESTIGATING alerts.
- Alerts can be sorted by:
  - Severity
  - Fraud score
  - Date generated

---

## US-013: View Alert Details

**As a** system operator  
**I want to** view complete alert information  
**So that** I can understand why it was generated.

### Acceptance Criteria:
Alert details should include:

- Triggering rule
- Related transactions
- Fraud scores
- Account details
- Alert history
- Previous actions

---

## US-014: Acknowledge Alert

**As a** system operator  
**I want to** acknowledge an alert  
**So that** the system knows it has been reviewed.

### Acceptance Criteria:
- OPEN alerts can move to ACKNOWLEDGED.
- Timestamp should be recorded.

---

## US-015: Investigate Alert

**As a** system operator  
**I want to** mark alerts as investigating  
**So that** ongoing investigations can be tracked.

### Acceptance Criteria:
- ACKNOWLEDGED alerts can move to INVESTIGATING.
- Investigation history should be recorded.

---

## US-016: Close Alert

**As a** system operator  
**I want to** close resolved alerts  
**So that** completed investigations are recorded.

### Acceptance Criteria:
- INVESTIGATING alerts can move to CLOSED.
- Closure reason should be stored.
- Closing timestamp should be recorded.

---

## US-017: Dismiss Alert

**As a** system operator  
**I want to** dismiss false positive alerts  
**So that** unnecessary investigations are removed.

### Acceptance Criteria:
- OPEN or INVESTIGATING alerts can be dismissed.
- Dismissal reason should be stored.

---

## US-018: View Alert History

**As a** system operator  
**I want to** view historical alerts  
**So that** previous fraud investigations can be reviewed.

### Acceptance Criteria:
- Closed and dismissed alerts should remain available.
- All status changes should be logged.

---

# Epic 5: Audit Trail

## US-019: Maintain Audit Logs

**As a** compliance system  
**I want to** record all important actions  
**So that** investigations can be audited.

### Acceptance Criteria:
Audit logs should record:

- Transaction creation
- Rule execution
- Alert generation
- Status changes
- User actions
- Timestamps

---

# Epic 6: Dashboard

## US-020: View Monitoring Dashboard

**As a** system operator  
**I want to** view an overview dashboard  
**So that** I can understand current fraud activity.

### Acceptance Criteria:

Dashboard should display:

- Total transactions
- Active alerts
- High-risk transactions
- Average fraud score
- Alert trends
- Rule violations

---

# Future Enhancements

## US-021: Machine Learning Fraud Detection

**As a** fraud analyst  
**I want to** use machine learning models  
**So that** unknown fraud patterns can be detected.

Possible features:

- Transaction behaviour analysis
- Account risk profiling
- Historical fraud learning
- Dynamic fraud scoring


---

# Priority Order

| Priority | Feature |
|----------|---------|
| P0 | Dashboard |
| P0 | Transaction storage |
| P0 | Rule engine |
| P0 | Alert generation |
| P0 | Alert lifecycle |
| P1 | Fraud probability scoring |
| P1 | Authentication |
| P1 | Rule configuration |
| P2 | Advanced analytics |
| P2 | Machine learning detection |

# 05 Project Traceability Matrix

## Scope
This matrix maps key project features and engineering practices to repository evidence available in the current snapshot.

Repository artifact evidence available.
Git history unavailable in current snapshot.

Status conventions used in this document:
- Implemented
- Partially Implemented
- Future Sprint
- Limitation

---

## 1. System Feature Traceability

| Feature | Backend Evidence | Frontend Evidence | Database Evidence | Test Evidence | Status |
|---|---|---|---|---|---|
| User Authentication | AuthController, JwtAuthFilter, JwtUtil, SecurityConfig | Login.jsx, AuthContext.jsx, auth.js, ProtectedRoute.jsx | users table in database/schema.sql | frontend/src/services/auth.test.js | Implemented |
| Transaction Monitoring | TransactionController, TransactionService | TransactionsPage.jsx, AppDataContext.jsx | transactions table in database/schema.sql | backend/src/test/java/com/transactionmonitoring/backend/service/TransactionServiceTest.java | Implemented |
| Fraud Detection | FraudService, RulesService, AlertService | AlertsPage.jsx, DashboardPage.jsx, adapters.js | rules and alerts tables in database/schema.sql | backend/src/test/java/com/transactionmonitoring/backend/service/FraudServiceTest.java | Implemented |
| Rule Management | RulesController, RulesService | RulesPage.jsx, utils/roles.js | rules table in database/schema.sql | No dedicated rules test found in current snapshot | Partially Implemented |
| Transaction Simulation | TransactionSimulationController, TransactionSimulationService, SimulatorSchedular, RandomTransactionGenerator | DashboardPage.jsx consumes simulated transaction/alert data through AppDataContext.jsx | transactions table in database/schema.sql | backend/src/test/java/com/transactionmonitoring/backend/service/TransactionSimulationServiceTest.java; backend/src/test/java/com/transactionmonitoring/backend/controller/TransactionSimulationControllerTest.java | Implemented |
| Real-Time Alert Streaming | AlertStreamController, AlertStreamService | AppDataContext.jsx EventSource listener, Navbar.jsx alert indicator | alerts table in database/schema.sql | No dedicated SSE automation test found in current snapshot | Partially Implemented |
| Alert Lifecycle Management | AlertController, AlertService, LogService | AlertsPage.jsx, AlertDetailsPage.jsx | alerts and logs tables in database/schema.sql | backend/src/test/java/com/transactionmonitoring/backend/service/TransactionServiceTest.java covers related rollback/logging transitions | Partially Implemented |
| Transaction Rollback | TransactionController rollback endpoint, TransactionService rollback flow | AlertDetailsPage.jsx admin-only rollback action | transactions and logs tables in database/schema.sql | backend/src/test/java/com/transactionmonitoring/backend/service/TransactionServiceTest.java | Partially Implemented |
| Settings Management | No backend persistence or settings API found in current snapshot | SettingsPage.jsx static form | No settings table found in database/schema.sql | No test found in current snapshot | Future Sprint |

### Known Limitations
- Rule management has implementation evidence but no dedicated rule-specific automated test file in the current snapshot.
- Rollback is admin-only in the frontend UI, but backend enforcement does not consistently match that restriction because transaction routes are open in backend security configuration.
- Real-time alert streaming is implemented, but there is no dedicated automated SSE test in the repository.

---

## 2. Architecture Traceability

| Layer | Technology | Evidence |
|---|---|---|
| Frontend | React + Vite | frontend/src; frontend/package.json; frontend/vite.config.js |
| Backend | Spring Boot | backend/src/main/java; backend/pom.xml |
| Database | SQL + MySQL-oriented schema | database/schema.sql; docker-compose.yml |
| Authentication | JWT + Spring Security + BCrypt | backend/src/main/java/com/transactionmonitoring/backend/security/JwtAuthFilter.java; backend/src/main/java/com/transactionmonitoring/backend/security/JwtUtil.java; backend/src/main/java/com/transactionmonitoring/backend/config/SecurityConfig.java; backend/src/main/java/com/transactionmonitoring/backend/controller/AuthController.java |
| Real-Time Messaging | Server-Sent Events | backend/src/main/java/com/transactionmonitoring/backend/controller/AlertStreamController.java; backend/src/main/java/com/transactionmonitoring/backend/service/AlertStreamService.java; frontend/src/context/AppDataContext.jsx |
| Deployment | Docker + Nginx reverse proxy | backend/Dockerfile; frontend/Dockerfile; frontend/nginx/default.conf; docker-compose.yml |
| CI/CD | Jenkins | Jenkinsfile |

### Known Limitations
- No OpenAPI specification, ADR set, or infrastructure-as-code manifests are present in the repository.
- Cloud deployment evidence is not present; deployment evidence is local/container-oriented.

---

## 3. Testing Traceability

| Test Type | Evidence | Status |
|---|---|---|
| Backend Unit Testing | FraudServiceTest.java, TransactionServiceTest.java, TransactionSimulationServiceTest.java | Implemented |
| Backend Controller Testing | TransactionSimulationControllerTest.java | Implemented |
| Backend Context/Wiring Testing | BackendApplicationTests.java | Implemented |
| Frontend Component/Page Testing | LoginCard.test.jsx, DashboardPage.test.jsx | Implemented |
| Frontend Service Testing | auth.test.js | Implemented |
| API Testing | scripts/api-smoke.ps1, scripts/api-smoke-v2.ps1, scripts/api-smoke.mjs | Implemented |
| Integration Testing | scripts/integration-flow.ps1 | Implemented |
| End-to-End Browser Testing | No Cypress, Playwright, or equivalent suite found in current snapshot | Future Sprint |

### Known Limitations
- Automated testing is stronger at service/component/API-script level than at browser end-to-end level.
- No repository evidence of enforced coverage thresholds or CI quality gates for tests.

---

## 4. Security Traceability

| Security Area | Evidence | Status |
|---|---|---|
| Authentication | JWT token generation and parsing, BCrypt password verification, protected frontend routes | Implemented |
| Authorization | PreAuthorize role checks on rules endpoints; frontend admin-only rollback visibility | Partially Implemented |
| Audit Logging | Logs entity, LogsController, LogService, alert log retrieval, rollback/status log creation | Implemented |
| Input Validation | Selected controller checks for required status and password length | Partially Implemented |
| Secret Management | Environment-variable overrides exist, but local defaults remain in application.properties and docker-compose.yml | Partially Implemented |
| Dependency Scanning | Not present in current snapshot | Future Sprint |
| Centralized Security Monitoring | Not present in current snapshot | Future Sprint |

### Known Limitations
- Authorization is uneven: rules endpoints are role-protected, but transaction and simulator routes are open through permitAll configuration.
- Password reset flow is simplified and does not use OTP, reset token, or possession-factor verification.
- SSE authentication uses a token passed through the query string from the frontend.

---

## 5. Delivery Process Evidence

| Practice | Evidence | Status |
|---|---|---|
| Documentation | docs folder, README.md, workflow/demo docs, meeting minutes | Implemented |
| Dockerization | backend/Dockerfile, frontend/Dockerfile, docker-compose.yml, nginx/default.conf | Implemented |
| CI Pipeline | Jenkinsfile build/deploy/health-check stages | Implemented |
| Smoke/Operational Scripts | scripts/api-smoke.ps1, scripts/api-smoke-v2.ps1, scripts/api-smoke.mjs, scripts/integration-flow.ps1 | Implemented |
| Git History | Repository artifact evidence available. Git history unavailable in current snapshot. | Limitation |
| Code Review Workflow | No pull request artifacts or review records present in current snapshot | Limitation |

### Known Limitations
- Delivery-process evidence is artifact-based rather than source-control-history-based in this workspace snapshot.
- Jenkins pipeline exists, but no stored pipeline-run results or release records are present in the repository.

---

## 6. Instructor Validation Notes

| Validation Area | Instructor-Visible Repository Evidence | Status |
|---|---|---|
| Full-stack implementation exists | backend, frontend, database, docs, scripts, Docker assets all present | Implemented |
| Security-aware design exists | JWT, BCrypt, role checks on selected endpoints, audit logs | Partially Implemented |
| Automation exists | Jenkins pipeline, Docker Compose, smoke scripts, simulation workflows | Implemented |
| Enterprise-hardening completeness | Uniform authorization, dependency scanning, secure recovery, observability not yet complete | Future Sprint |

### Known Limitations
- This matrix is constrained to evidence directly verifiable from the current repository snapshot.
- It does not infer features from intent, meeting notes, or documentation unless code or executable artifacts support them.
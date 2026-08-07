# 04 Compliance Security and Risk

## Scope and Security Framework
This assessment maps current repository implementation against OWASP Top 10:2025 categories requested for review.

Assessment method:
- Review of backend security configuration, auth/token flows, controller access patterns, and data handling.
- Review of frontend auth/session handling and API usage patterns.
- Review of deployment/configuration, dependency manifests, and automation scripts.

Status conventions used in this document:
- Implemented Features
- Partially Implemented
- In Progress Features
- Future Sprint

Repository artifact evidence available.
Git history unavailable in current snapshot.

---

## A01:2025 Broken Access Control

Current Implementation:
- JWT-based authentication implemented.
- Method-level authorization present on rules endpoints (admin mutation, analyst/admin read).
- Protected frontend routes require authenticated session.
- Some backend routes are intentionally permitAll for compatibility and simulator convenience.

Security Strength:
- Role model exists (ADMIN, ANALYST).
- Security filter and role claims integration operational.

Potential Risk:
- Inconsistent authorization coverage across all endpoints may allow unintended access paths.
- Simulator and some transaction paths are exposed without authentication by configuration.

Recommended Improvement:
- Apply least-privilege policy across all mutation endpoints.
- Restrict simulator endpoints to authenticated operational roles.
- Introduce endpoint-level authorization review checklist in CI.

Future Sprint:
- Sprint 4: complete endpoint authorization hardening and negative-access integration tests.

### Repository traceability table

| Feature | Repository Evidence | Files | Status |
|---|---|---|---|
| JWT-based request security | JWT filter and security chain | backend/src/main/java/com/transactionmonitoring/backend/config/SecurityConfig.java; backend/src/main/java/com/transactionmonitoring/backend/security/JwtAuthFilter.java | Implemented |
| Role-protected rules endpoints | PreAuthorize on rules read/write operations | backend/src/main/java/com/transactionmonitoring/backend/controller/RulesController.java | Implemented |
| Protected frontend routes | Redirect unauthenticated users to login | frontend/src/components/Layout/ProtectedRoute.jsx | Implemented |
| Transaction and simulator route exposure | permitAll on transaction and simulator routes | backend/src/main/java/com/transactionmonitoring/backend/config/SecurityConfig.java | Partially Implemented |

Known Limitation:
- UI restriction exists for some actions, but backend enforcement does not consistently match it. For example, rollback is admin-only in the UI while transaction routes remain open in backend security configuration.

---

## A02:2025 Security Misconfiguration

Current Implementation:
- Environment-variable overrides exist for datasource, JWT secret, and CORS origins.
- CSRF is disabled, and CORS is configurable.
- Development defaults include placeholder/example secrets.

Security Strength:
- Configuration is externalizable and deploy-time overridable.
- CORS allowed origins are centralized in configuration.

Potential Risk:
- Default secrets in config are unsafe for production if not overridden.
- Broad CORS and open routes can be misused if production configuration drifts.

Recommended Improvement:
- Enforce non-default secret checks at startup in non-dev profiles.
- Use environment/profile separation with strict production defaults.
- Add secrets management via vault/secret manager.

Future Sprint:
- Sprint 4: production profile baseline with fail-fast secret validation.

### Repository traceability table

| Feature | Repository Evidence | Files | Status |
|---|---|---|---|
| Env-driven configuration | Property placeholders and compose env variables | backend/src/main/resources/application.properties; docker-compose.yml | Implemented |
| Configurable CORS | Allowed origins property and CORS bean | backend/src/main/java/com/transactionmonitoring/backend/config/SecurityConfig.java | Implemented |
| Secure-by-default production profile | No dedicated hardened production profile in repository | Repository snapshot | Future Sprint |

Known Limitation:
- Default local-development secrets and credentials exist in repository configuration and must not be treated as production-safe settings.

---

## A03:2025 Software Supply Chain Failures

Current Implementation:
- Dependency manifests exist for backend (Maven) and frontend (npm).
- Docker build process is defined.

Security Strength:
- Dependency sources are explicit and versioned.

Potential Risk:
- No repository evidence of automated vulnerability scans (SCA/SAST/container).
- No dependency update policy automation observed.

Recommended Improvement:
- Add dependency CVE scanning and image scanning to CI.
- Enforce patch cadence and update thresholds.

Future Sprint:
- Sprint 5: integrate OWASP Dependency-Check and Trivy in pipeline.

### Repository traceability table

| Feature | Repository Evidence | Files | Status |
|---|---|---|---|
| Versioned backend dependencies | Maven dependency manifest | backend/pom.xml | Implemented |
| Versioned frontend dependencies | npm package manifest | frontend/package.json | Implemented |
| Automated dependency scanning | No scanner stage or report in repository | Repository snapshot | Future Sprint |

---

## A04:2025 Cryptographic Failures

Current Implementation:
- Password hashing uses BCrypt.
- JWTs are signed with HMAC secret and expiration.
- Token is transported as bearer auth header for REST calls.

Security Strength:
- Passwords are not stored in plaintext.
- Token expiration is configured.

Potential Risk:
- Secret management relies on environment override discipline.
- Password reset flow is simplified (employee ID + new password) without reset token/OTP.
- Alert stream authentication uses a token passed as a query parameter from the frontend, which may increase exposure through logs or browser history depending on deployment setup.

Recommended Improvement:
- Implement secure reset token flow with expiry and one-time use.
- Rotate JWT secrets per environment and audit secret lifecycle.

Future Sprint:
- Sprint 4: implement tokenized password-reset and secret rotation policy.

### Repository traceability table

| Feature | Repository Evidence | Files | Status |
|---|---|---|---|
| BCrypt password hashing | Password encoder bean and password verification | backend/src/main/java/com/transactionmonitoring/backend/config/SecurityConfig.java; backend/src/main/java/com/transactionmonitoring/backend/controller/AuthController.java | Implemented |
| JWT signing and expiration | JWT utility secret and expiration configuration | backend/src/main/java/com/transactionmonitoring/backend/security/JwtUtil.java; backend/src/main/resources/application.properties | Implemented |
| SSE token transport | Frontend appends token to alert stream query string | frontend/src/context/AppDataContext.jsx; backend/src/main/java/com/transactionmonitoring/backend/controller/AlertStreamController.java | Partially Implemented |
| Secure password recovery flow | Simplified employee-id-based reset only | backend/src/main/java/com/transactionmonitoring/backend/controller/AuthController.java | Partially Implemented |

Known Limitation:
- The password reset flow is explicitly simplified for training-project use and does not include OTP, reset token, or possession-factor verification.

---

## A05:2025 Injection

Current Implementation:
- Data access uses Spring Data JPA repository methods rather than string-concatenated SQL.
- No direct dynamic SQL string construction observed in business services.

Security Strength:
- ORM/repository pattern reduces classic SQL injection surface.

Potential Risk:
- Request DTO/entity validation is inconsistent across endpoints.
- Free-form text fields could be used for log/content injection if not sanitized for downstream consumers.

Recommended Improvement:
- Add bean validation annotations and centralized validation error handling.
- Add input length and character policy checks for note/description fields.

Future Sprint:
- Sprint 5: complete API boundary validation hardening and negative tests.

### Repository traceability table

| Feature | Repository Evidence | Files | Status |
|---|---|---|---|
| ORM-based data access | JPA repositories and entity model | backend/src/main/java/com/transactionmonitoring/backend/repository/**; backend/src/main/java/com/transactionmonitoring/backend/entity/** | Implemented |
| Basic request validation | Null/blank checks and minimum password length checks | backend/src/main/java/com/transactionmonitoring/backend/controller/AlertController.java; backend/src/main/java/com/transactionmonitoring/backend/controller/AuthController.java | Partially Implemented |
| Centralized validation framework | No global validation/error model across all endpoints | Repository snapshot | Future Sprint |

Known Limitation:
- Input validation exists in selected paths only. It is not comprehensive enough to claim strong validation coverage across the full API surface.

---

## A06:2025 Insecure Design

Current Implementation:
- Rule-based fraud checks, audit logs, and rollback constraints are implemented.
- Role-aware behavior exists in UI and backend for key rule-management actions.

Security Strength:
- Business logic includes rollback preconditions and state transitions.
- Monitoring workflow supports investigative traceability.

Potential Risk:
- Threat-model artifacts are not present in repository.
- Some sensitive operational flows are convenience-open for demo compatibility.

Recommended Improvement:
- Add formal threat modeling per feature (auth, rollback, simulator, alert stream).
- Define misuse/abuse cases and security acceptance criteria per sprint.

Future Sprint:
- Sprint 5: introduce threat modeling and secure-design review checklist.

### Repository traceability table

| Feature | Repository Evidence | Files | Status |
|---|---|---|---|
| Business-rule safeguards | Rollback preconditions and alert-state transitions | backend/src/main/java/com/transactionmonitoring/backend/service/TransactionService.java; backend/src/main/java/com/transactionmonitoring/backend/service/AlertService.java | Implemented |
| Threat-model artifacts | No explicit threat-model repository artifact | Repository snapshot | Future Sprint |

---

## A07:2025 Authentication Failures

Current Implementation:
- Login validates BCrypt password hash.
- JWT expiry is enforced by token claims parsing.
- Session storage/local storage split based on remember-me behavior.

Security Strength:
- Basic authentication lifecycle is functional.
- Invalid credentials return unauthorized response.

Potential Risk:
- No account lockout, brute-force throttling, or MFA controls observed.
- Forgot/reset flow does not currently verify possession factors.

Recommended Improvement:
- Add rate limiting and lockout strategy.
- Add MFA for privileged roles.
- Replace simplified reset with secure, challenge-based flow.

Future Sprint:
- Sprint 4: authentication hardening (rate limit + secure recovery path).

### Repository traceability table

| Feature | Repository Evidence | Files | Status |
|---|---|---|---|
| Password verification | BCrypt match on login | backend/src/main/java/com/transactionmonitoring/backend/controller/AuthController.java | Implemented |
| JWT-backed session usage | Bearer token injection on frontend API calls | frontend/src/services/api.js | Implemented |
| Account lockout/rate limit/MFA | No such controls in repository | Repository snapshot | Future Sprint |

---

## A08:2025 Software or Data Integrity Failures

Current Implementation:
- Entity persistence and foreign-key relationships defined in schema.
- Rollback flow writes both transaction and log updates.

Security Strength:
- Core integrity constraints exist in relational model.
- Audit logging captures status transitions.

Potential Risk:
- End-to-end transactional integrity assertions are limited to selected tests.
- Build artifact signing and provenance controls are not present.

Recommended Improvement:
- Add transaction-boundary integration tests for multi-entity updates.
- Introduce artifact integrity controls in CI/CD pipeline.

Future Sprint:
- Sprint 6: add release integrity controls and stronger integrity tests.

### Repository traceability table

| Feature | Repository Evidence | Files | Status |
|---|---|---|---|
| Relational integrity model | Foreign keys in alerts/logs tables | database/schema.sql | Implemented |
| Multi-entity rollback logic | Transaction + refund + alert log updates | backend/src/main/java/com/transactionmonitoring/backend/service/TransactionService.java | Implemented |
| Artifact signing/provenance | No artifact integrity controls in repository | Repository snapshot | Future Sprint |

---

## A09:2025 Security Logging and Alerting Failures

Current Implementation:
- Application logs are present in backend services and simulator operations.
- Alert lifecycle actions are persisted into logs table.
- Analytics page consumes and displays log data.

Security Strength:
- Auditable record exists for key workflow transitions.
- Real-time alerts improve operational responsiveness.

Potential Risk:
- No centralized SIEM export or alerting thresholds observed.
- Log tamper detection and retention policy are not defined in repository.

Recommended Improvement:
- Add structured logging format, central log sink, and retention policy.
- Add suspicious-auth and access-denial monitoring dashboards.

Future Sprint:
- Sprint 6: SIEM-ready logging pipeline and alerting rules.

### Repository traceability table

| Feature | Repository Evidence | Files | Status |
|---|---|---|---|
| Application logging | Logger usage in services/controllers | backend/src/main/java/com/transactionmonitoring/backend/service/TransactionService.java; backend/src/main/java/com/transactionmonitoring/backend/controller/TransactionSimulationController.java | Implemented |
| Audit trail storage | Logs entity/repository/controller and analytics display | backend/src/main/java/com/transactionmonitoring/backend/entity/Logs.java; backend/src/main/java/com/transactionmonitoring/backend/controller/LogsController.java; frontend/src/pages/AnalyticsPage.jsx | Implemented |
| Centralized SIEM integration | No centralized logging pipeline in repository | Repository snapshot | Future Sprint |

Known Limitation:
- The analytics page displays application audit/activity logs, but this is not evidence of centralized security monitoring or SIEM integration.

---

## A10:2025 Mishandling of Exceptional Conditions

Current Implementation:
- Several controllers return explicit 400/404/401 responses for known invalid states.
- Frontend surfaces user-readable error messages for data and auth failures.

Security Strength:
- Common invalid-state paths are handled for core APIs.

Potential Risk:
- Global exception handling strategy is not uniformly centralized.
- Some runtime exceptions are converted ad hoc and may create inconsistent error semantics.

Recommended Improvement:
- Introduce centralized exception handler with standardized error schema.
- Add chaos/failure-path tests for rollback, stream, and auth boundaries.

Future Sprint:
- Sprint 5: implement global exception model and failure-path test suite.

### Repository traceability table

| Feature | Repository Evidence | Files | Status |
|---|---|---|---|
| Localized exception handling | Explicit 400/401/404 mappings in selected controllers | backend/src/main/java/com/transactionmonitoring/backend/controller/AlertController.java; backend/src/main/java/com/transactionmonitoring/backend/controller/AuthController.java; backend/src/main/java/com/transactionmonitoring/backend/controller/TransactionController.java | Implemented |
| Global exception strategy | No central exception handler in repository | Repository snapshot | Future Sprint |

---

## Implemented Features
- JWT auth and BCrypt hashing
- Role model and selected method-level authorization
- Configurable CORS and env-driven runtime settings
- JPA-based data access model reducing direct SQL injection risk
- Audit logs for alert state changes and rollback actions

## Partially Implemented
- Input validation exists on selected request paths, but not as a comprehensive cross-API validation model.
- Access control is stronger on rules endpoints than on transaction and simulator endpoints.
- Password recovery is functional but simplified and not production-grade.

## In Progress Features
- Full endpoint authorization consistency
- Production-grade secrets and reset-flow security hardening
- CI-integrated dependency and container security scanning

## Future Sprint Backlog
- MFA, rate limiting, and secure account recovery
- Threat modeling and secure design gates
- Centralized SIEM integration and release integrity controls

---

## Security Checklist

[x] Authentication
[x] Authorization
[ ] Input Validation
[ ] Secure API Design
[x] Logging
[ ] Dependency Management
[x] Error Handling

Checklist note:
- Checked items have direct repository evidence of implementation.
- Unchecked items are partially implemented or missing at the repository level and should not be represented as fully complete.
- Gaps and risk items are explicitly captured in the OWASP sections and future sprint recommendations.

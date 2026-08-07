# Transaction Monitoring System

This repository contains a full-stack transaction monitoring application built to detect suspicious financial activity, generate alerts, support analyst investigation workflows, and demonstrate deployment and testing practices across frontend, backend, database, and containerized environments.

## Project Overview

The system supports these implemented repository capabilities:
- User authentication with JWT-based session handling.
- Transaction ingestion and monitoring.
- Fraud classification using rule-based evaluation.
- Alert generation and alert lifecycle management.
- Transaction simulation for demo and verification workflows.
- Dockerized multi-service deployment.
- Jenkins pipeline-based deployment automation.

## Technology Stack Checklist

An instructor can verify these stacks directly from the repository:

- [x] React
- [x] Vite
- [x] React Router
- [x] Axios
- [x] Recharts
- [x] Spring Boot
- [x] Spring Security
- [x] Spring Data JPA
- [x] JWT Authentication
- [x] MySQL
- [x] H2 Database
- [x] SQL Schema Scripts
- [x] Docker
- [x] Docker Compose
- [x] Nginx
- [x] Jenkins Pipeline
- [x] Maven
- [x] Vitest
- [x] JUnit-style backend tests
- [x] PowerShell API smoke scripts

## Repository Evidence for Stack

| Area | Technology | Repository Evidence |
|---|---|---|
| Frontend | React + Vite | frontend/package.json; frontend/src |
| Frontend Routing | React Router | frontend/package.json; frontend/src/App.jsx |
| Frontend API Client | Axios | frontend/package.json; frontend/src/services/api.js |
| Frontend Charts | Recharts | frontend/package.json; frontend/src/components/Charts |
| Backend | Spring Boot | backend/pom.xml; backend/src/main/java |
| Backend Security | Spring Security + JWT | backend/pom.xml; backend/src/main/java/com/transactionmonitoring/backend/config/SecurityConfig.java; backend/src/main/java/com/transactionmonitoring/backend/security |
| Persistence | Spring Data JPA | backend/pom.xml; backend/src/main/java/com/transactionmonitoring/backend/repository |
| Database | MySQL | docker-compose.yml; backend/pom.xml |
| Test Database | H2 | backend/pom.xml; backend/src/test/resources/application-test.properties |
| Database Schema | SQL | database/schema.sql |
| Containerization | Docker + Docker Compose | backend/Dockerfile; frontend/Dockerfile; docker-compose.yml |
| Frontend Runtime Proxy | Nginx | frontend/nginx/default.conf |
| CI/CD | Jenkins | Jenkinsfile |
| Backend Build Tool | Maven Wrapper | backend/mvnw; backend/pom.xml |
| Frontend Testing | Vitest + Testing Library | frontend/package.json; frontend/src/test/setup.js |
| Backend Testing | Spring Boot test dependencies and Java test suite | backend/pom.xml; backend/src/test/java |
| API Verification | PowerShell and Node smoke scripts | scripts/api-smoke.ps1; scripts/api-smoke-v2.ps1; scripts/api-smoke.mjs; scripts/integration-flow.ps1 |

## Project Structure

| Folder | Purpose |
|---|---|
| backend | Spring Boot REST API, security, services, repositories, tests |
| frontend | React user interface, routing, context, components, tests |
| database | SQL schema and seed scripts |
| docs | Engineering documentation, user stories, test cases, meeting notes |
| scripts | Smoke tests and integration verification scripts |

## Key Implemented Features

| Feature | Repository Evidence |
|---|---|
| User Authentication | backend/src/main/java/com/transactionmonitoring/backend/controller/AuthController.java; frontend/src/pages/Login.jsx |
| Transaction Monitoring | backend/src/main/java/com/transactionmonitoring/backend/controller/TransactionController.java; frontend/src/pages/TransactionsPage.jsx |
| Fraud Detection | backend/src/main/java/com/transactionmonitoring/backend/service/FraudService.java |
| Rule Management | backend/src/main/java/com/transactionmonitoring/backend/controller/RulesController.java; frontend/src/pages/RulesPage.jsx |
| Alert Management | backend/src/main/java/com/transactionmonitoring/backend/controller/AlertController.java; frontend/src/pages/AlertsPage.jsx; frontend/src/pages/AlertDetailsPage.jsx |
| Real-Time Alert Streaming | backend/src/main/java/com/transactionmonitoring/backend/controller/AlertStreamController.java; frontend/src/context/AppDataContext.jsx |
| Transaction Simulation | backend/src/main/java/com/transactionmonitoring/backend/controller/TransactionSimulationController.java |
| Rollback Workflow | backend/src/main/java/com/transactionmonitoring/backend/service/TransactionService.java; frontend/src/pages/AlertDetailsPage.jsx |

## Testing and Verification

The repository includes these verification assets:
- Backend unit and controller tests under backend/src/test/java.
- Frontend unit tests under frontend/src.
- API smoke scripts under scripts.
- Integration workflow script under scripts.
- Workflow and demo evidence in WORKFLOW_DEMO.md and COMPLETE_WORKFLOW_SUMMARY.md.

## Documentation Links

- [Technical Proficiency and Dual-Skilling](docs/01_Technical_Proficiency_and_Dual_Skilling.md)
- [Solution Design and Implementation](docs/02_Solution_Design_and_Implementation.md)
- [Automation and Modernization](docs/03_Automation_and_Modernization.md)
- [Compliance, Security and Risk](docs/04_Compliance_Security_and_Risk.md)
- [Project Traceability Matrix](docs/05_Project_Traceability_Matrix.md)
- [Commit Cadence Guide](docs/Commit_Cadence_Guide.md)
- [User Stories](docs/UserStories.md)
- [Frontend Test Cases](docs/Frontend_Test_Cases.md)
- [Workflow Demo](WORKFLOW_DEMO.md)
- [Frontend README](frontend/README.md)

## Instructor Notes

- Repository artifact evidence is available across code, tests, scripts, Docker assets, and engineering documentation.
- Git history is unavailable in the current snapshot, so contribution-history claims should not be inferred from this README.
- Some controls are partially implemented. For example, role-based UI restrictions exist in places where backend authorization is not yet uniformly enforced across all endpoints.

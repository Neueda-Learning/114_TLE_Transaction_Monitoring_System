# Frontend Test Cases

## 1. Overview

This document contains manual frontend test cases for the Transaction Monitoring System React application.

The test cases cover:

- Authentication
- Role-based authorization
- Navigation and routing
- UI components
- Form validation
- API integration
- Error handling
- Responsive behavior


---

# 2. Authentication Test Cases


## FTC-001: Successful Login with Admin Credentials

**Module:** Authentication

**Test Scenario:**  
Verify that an administrator can successfully login using valid credentials.


**Test Steps:**

1. Open the login page.
2. Enter valid administrator username.
3. Enter valid password.
4. Click the Sign In button.


**Expected Result:**

User should be authenticated successfully and redirected to the dashboard.


**Actual Result:**  
________________


**Status:**  
Pass / Fail



---

## FTC-002: Successful Login with Analyst Credentials

**Module:** Authentication

**Test Scenario:**  
Verify that an analyst user can login successfully.


**Test Steps:**

1. Open the login page.
2. Enter valid analyst credentials.
3. Click Sign In.


**Expected Result:**

Analyst should be redirected to the dashboard with analyst permissions.


**Actual Result:**  
________________


**Status:**  
Pass / Fail



---

## FTC-003: Invalid Login Credentials

**Module:** Authentication

**Test Scenario:**  
Verify login failure with incorrect credentials.


**Test Steps:**

1. Open login page.
2. Enter invalid username or password.
3. Click Sign In.


**Expected Result:**

System should reject login and display an authentication error message.


**Actual Result:**  
________________


**Status:**  
Pass / Fail



---

# 3. Authorization Test Cases


## FTC-004: Admin Rule Management Access

**Module:** Role-Based Access

**Test Scenario:**  
Verify administrator can manage transaction monitoring rules.


**Test Steps:**

1. Login as Administrator.
2. Navigate to Rules page.


**Expected Result:**

Admin should see Add, Edit, and Update rule options.


**Actual Result:**  
________________


**Status:**  
Pass / Fail



---

## FTC-005: Analyst View Only Access

**Module:** Role-Based Access

**Test Scenario:**  
Verify analyst has restricted rule management access.


**Test Steps:**

1. Login as Analyst.
2. Open Rules page.


**Expected Result:**

Analyst should view rules but should not see modification options.


**Actual Result:**  
________________


**Status:**  
Pass / Fail



---

# 4. UI Component Test Cases


## FTC-006: Dashboard Components Rendering

**Module:** Dashboard UI

**Test Scenario:**  
Verify dashboard cards and charts load correctly.


**Test Steps:**

1. Login successfully.
2. Open Dashboard page.


**Expected Result:**

Dashboard metrics, charts, and components should render without UI issues.


**Actual Result:**  
________________


**Status:**  
Pass / Fail



---

# 5. Form Validation Test Cases


## FTC-007: Empty Login Submission

**Module:** Login Form

**Test Scenario:**  
Verify validation when submitting empty login fields.


**Test Steps:**

1. Open login page.
2. Leave username and password empty.
3. Click Sign In.


**Expected Result:**

Required field validation should prevent submission.


**Actual Result:**  
________________


**Status:**  
Pass / Fail



---

# 6. API Integration Test Cases


## FTC-008: Authentication Service Integration

**Module:** API Integration

**Test Scenario:**  
Verify frontend communicates correctly with backend authentication API.


**Test Steps:**

1. Enter valid login credentials.
2. Submit login request.
3. Observe response.


**Expected Result:**

Authentication response should be handled and user session should be created.


**Actual Result:**  
________________


**Status:**  
Pass / Fail



---

# 7. Error Handling Test Cases


## FTC-009: Backend Service Failure Handling

**Module:** Error Handling

**Test Scenario:**  
Verify application behavior when backend service is unavailable.


**Test Steps:**

1. Stop backend service.
2. Attempt login.


**Expected Result:**

Application should display a meaningful error message instead of crashing.


**Actual Result:**  
________________


**Status:**  
Pass / Fail



---

# 8. Responsive Testing


## FTC-010: Mobile Screen Compatibility

**Module:** Responsive Design

**Test Scenario:**  
Verify application usability on mobile screen sizes.


**Test Steps:**

1. Open application in mobile viewport.
2. Navigate through pages.


**Expected Result:**

UI components should adjust properly without overlap or broken layouts.


**Actual Result:**  
________________


**Status:**  
Pass / Fail
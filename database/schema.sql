
use transaction_monitoringdb;

-- 1. Transactions Table
CREATE TABLE transactions (
    transaction_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_id VARCHAR(50) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    currency VARCHAR(10),
    transaction_type VARCHAR(50),
    payee_id VARCHAR(50),
    payee_name VARCHAR(100),
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20)
);


-- 2. Rules Table
CREATE TABLE rules (
    rule_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rule_name VARCHAR(100) NOT NULL,
    rule_type VARCHAR(50) NOT NULL,
    field_name VARCHAR(50),
    operator VARCHAR(20),
    threshold_value VARCHAR(100),
    time_window_minutes INT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- 3. Alerts Table
CREATE TABLE alerts (
    alert_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    transaction_id BIGINT NOT NULL,
    rule_id BIGINT NOT NULL,
    alert_type VARCHAR(50),
    severity VARCHAR(20),
    alert_status VARCHAR(30) DEFAULT 'OPEN',
    alert_message VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY(transaction_id)
    REFERENCES transactions(transaction_id),

    FOREIGN KEY(rule_id)
    REFERENCES rules(rule_id)
);


-- 4. Logs Table
CREATE TABLE logs (
    log_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    alert_id BIGINT NOT NULL,
    action VARCHAR(50),
    old_status VARCHAR(30),
    new_status VARCHAR(30),
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY(alert_id)
    REFERENCES alerts(alert_id)
);


-- 5. Users Table
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id VARCHAR(50) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    email VARCHAR(150),
    role VARCHAR(50)
);

--inserting amount threshold rule
insert into rules(rule_name,rule_type,field_name,operator,threshold_value,is_active) Values ('Amount Threshold','AMOUNT_THRESHOLD','amount','>','20000',true);
INSERT INTO rules(rule_name,rule_type,threshold_value,time_window_minutes,is_active) Values ('Velocity Rule', 'VELOCITY','5',10,true);
Insert into rules(rule_name,rule_type,threshold_value,is_active) values('Daily Limit Rule','DAILY_LIMIT','50000',true);
insert into rules(rule_name,rule_type,threshold_value,is_active) values('New Payee Rule','NEW_PAYEE',NULL,True);
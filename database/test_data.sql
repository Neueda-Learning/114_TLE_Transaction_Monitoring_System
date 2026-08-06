-- Insert test transactions with different risk statuses
INSERT INTO transactions (account_id, amount, currency, transaction_type, payee_id, payee_name, fraud_status, investigation_status, transaction_date, status)
VALUES 
  ('ACC-1001', 5000.00, 'USD', 'TRANSFER', 'PYEE-001', 'Amazon Inc', 'NORMAL', 'OPEN', CURRENT_TIMESTAMP, 'PENDING'),
  ('ACC-1002', 25000.00, 'USD', 'WIRE_TRANSFER', 'PYEE-002', 'Unknown Merchant', 'SUSPICIOUS', 'OPEN', CURRENT_TIMESTAMP, 'PENDING'),
  ('ACC-1003', 50000.00, 'USD', 'TRANSFER', 'PYEE-003', 'Crypto Exchange XYZ', 'FRAUDULENT', 'OPEN', CURRENT_TIMESTAMP, 'PENDING'),
  ('ACC-1004', 150.00, 'USD', 'PURCHASE', 'PYEE-004', 'Local Store', 'NORMAL', 'OPEN', CURRENT_TIMESTAMP, 'PENDING');

$base='http://localhost:8082'
$login=Invoke-RestMethod -Method Post -Uri "$base/api/auth/login" -ContentType 'application/json' -Body '{"username":"admin","password":"admin123"}'
$h=@{ Authorization = "Bearer $($login.token)" }

$tx = Invoke-RestMethod -Method Post -Uri "$base/api/transactions" -ContentType 'application/json' -Body '{"accountId":"ACC-INTEG-01","amount":68000,"currency":"USD","transactionType":"TRANSFER","payeeId":"PAY-INTEG-01","payeeName":"Integration Payee","status":"PENDING"}'
"CREATED_TX_ID=$($tx.transactionId)"

$alerts = @(Invoke-RestMethod -Method Get -Uri "$base/api/alerts" -Headers $h)
"ALERTS_COUNT=$($alerts.Count)"

$related = $alerts | Where-Object { $_.transactionId -eq $tx.transactionId } | Select-Object -First 1
if (-not $related) {
    Write-Output 'NO_RELATED_ALERT'
    exit 3
}

"RELATED_ALERT_ID=$($related.alertId) STATUS=$($related.alertStatus)"

$inv = Invoke-RestMethod -Method Patch -Uri "$base/api/alerts/$($related.alertId)/status" -Headers $h -ContentType 'application/json' -Body '{"status":"INVESTIGATING","action":"START_INVESTIGATION","description":"integration flow"}'
"PATCHED_STATUS=$($inv.alertStatus)"

$rolled = Invoke-RestMethod -Method Patch -Uri "$base/api/transactions/$($tx.transactionId)/rollback"
"ROLLBACK_STATUS=$($rolled.investigationStatus)"

$logs = @(Invoke-RestMethod -Method Get -Uri "$base/api/alerts/$($related.alertId)/logs" -Headers $h)
"RELATED_LOG_COUNT=$($logs.Count)"

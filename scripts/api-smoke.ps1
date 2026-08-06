$base = 'http://localhost:8082'
$results = New-Object System.Collections.ArrayList

function Add-Result($name, $ok, $detail) {
    [void]$results.Add([pscustomobject]@{
        Test = $name
        Status = $(if ($ok) { 'PASS' } else { 'FAIL' })
        Detail = $detail
    })
}

function StatusCodeFromError($err) {
    try { return [int]$err.Exception.Response.StatusCode } catch { return -1 }
}

try {
    Invoke-RestMethod -Method Get -Uri "$base/api/auth/me" -ErrorAction Stop | Out-Null
    Add-Result 'GET /api/auth/me without token' $false 'Expected 401 but got success'
} catch {
    $code = StatusCodeFromError $_
    Add-Result 'GET /api/auth/me without token' (($code -eq 401) -or ($code -eq 403)) "HTTP $code"
}

$loginBody = @{ username = 'admin'; password = 'admin123' } | ConvertTo-Json
$token = $null
try {
    $login = Invoke-RestMethod -Method Post -Uri "$base/api/auth/login" -ContentType 'application/json' -Body $loginBody -ErrorAction Stop
    $token = $login.token
    Add-Result 'POST /api/auth/login' (-not [string]::IsNullOrWhiteSpace($token)) "tokenPresent=$(-not [string]::IsNullOrWhiteSpace($token))"
} catch {
    $code = StatusCodeFromError $_
    Add-Result 'POST /api/auth/login' $false "HTTP $code"
}

if ([string]::IsNullOrWhiteSpace($token)) {
    $results | Format-Table -AutoSize | Out-String | Write-Output
    exit 1
}

$headers = @{ Authorization = "Bearer $token" }

try {
    $me = Invoke-RestMethod -Method Get -Uri "$base/api/auth/me" -Headers $headers -ErrorAction Stop
    Add-Result 'GET /api/auth/me with token' ($me.username -eq 'admin') "username=$($me.username)"
} catch { Add-Result 'GET /api/auth/me with token' $false "HTTP $(StatusCodeFromError $_)" }

$ruleId = $null
try {
    $rules = Invoke-RestMethod -Method Get -Uri "$base/api/rules" -Headers $headers -ErrorAction Stop
    $count = @($rules).Count
    Add-Result 'GET /api/rules' ($count -ge 1) "count=$count"

    $newRule = @{
        ruleName = "API Smoke Rule $(Get-Date -Format 'yyyyMMddHHmmss')"
        ruleType = 'AMOUNT_THRESHOLD'
        fieldName = 'amount'
        operator = 'GREATER_THAN'
        thresholdValue = '12345'
        isActive = $true
    } | ConvertTo-Json
    $createdRule = Invoke-RestMethod -Method Post -Uri "$base/api/rules" -Headers $headers -ContentType 'application/json' -Body $newRule -ErrorAction Stop
    $ruleId = $createdRule.ruleId
    Add-Result 'POST /api/rules' ($null -ne $ruleId) "ruleId=$ruleId"

    $updateRule = @{
        ruleName = $createdRule.ruleName
        ruleType = $createdRule.ruleType
        fieldName = $createdRule.fieldName
        operator = $createdRule.operator
        thresholdValue = '15000'
        timeWindowMinutes = $createdRule.timeWindowMinutes
        isActive = $createdRule.isActive
    } | ConvertTo-Json
    $updatedRule = Invoke-RestMethod -Method Put -Uri "$base/api/rules/$ruleId" -Headers $headers -ContentType 'application/json' -Body $updateRule -ErrorAction Stop
    Add-Result 'PUT /api/rules/{id}' ($updatedRule.thresholdValue -eq '15000') "threshold=$($updatedRule.thresholdValue)"

    Invoke-RestMethod -Method Delete -Uri "$base/api/rules/$ruleId" -Headers $headers -ErrorAction Stop
    Add-Result 'DELETE /api/rules/{id}' $true 'deleted'
} catch {
    Add-Result 'Rules flow' $false "HTTP $(StatusCodeFromError $_)"
}

$txnId = $null
try {
    $beforeTx = @(Invoke-RestMethod -Method Get -Uri "$base/api/transactions" -ErrorAction Stop).Count
    $txBody = @{
        accountId = 'ACC-SMOKE-01'
        amount = 65000
        currency = 'USD'
        transactionType = 'TRANSFER'
        payeeId = 'PAYEE-SMOKE-1'
        payeeName = 'Smoke Test Beneficiary'
        status = 'PENDING'
    } | ConvertTo-Json
    $createdTx = Invoke-RestMethod -Method Post -Uri "$base/api/transactions" -ContentType 'application/json' -Body $txBody -ErrorAction Stop
    $txnId = $createdTx.transactionId
    $afterTx = @(Invoke-RestMethod -Method Get -Uri "$base/api/transactions" -ErrorAction Stop).Count
    Add-Result 'POST /api/transactions + list growth' (($null -ne $txnId) -and ($afterTx -ge ($beforeTx + 1))) "txnId=$txnId before=$beforeTx after=$afterTx"

    $alertsForTx = @(Invoke-RestMethod -Method Get -Uri "$base/api/alerts" -Headers $headers -ErrorAction Stop | Where-Object { $_.transactionId -eq $txnId })
    if ($alertsForTx.Count -gt 0) {
        $txAlert = $alertsForTx[0]
        $investigateBody = @{ status = 'INVESTIGATING'; action = 'START_INVESTIGATION'; description = 'rollback precondition for smoke test' } | ConvertTo-Json
        Invoke-RestMethod -Method Patch -Uri "$base/api/alerts/$($txAlert.alertId)/status" -Headers $headers -ContentType 'application/json' -Body $investigateBody -ErrorAction Stop | Out-Null

        $rolled = Invoke-RestMethod -Method Patch -Uri "$base/api/transactions/$txnId/rollback" -ErrorAction Stop
        Add-Result 'PATCH /api/transactions/{id}/rollback' ($rolled.investigationStatus -eq 'ROLLED_BACK') "investigationStatus=$($rolled.investigationStatus)"
    } else {
        Add-Result 'PATCH /api/transactions/{id}/rollback' $false "No related alert found for txnId=$txnId"
    }
} catch {
    Add-Result 'Transactions flow' $false "HTTP $(StatusCodeFromError $_)"
}

$alertId = $null
try {
    $alerts = @(Invoke-RestMethod -Method Get -Uri "$base/api/alerts" -Headers $headers -ErrorAction Stop)
    Add-Result 'GET /api/alerts' ($alerts.Count -ge 1) "count=$($alerts.Count)"

    if ($txnId) {
        $related = $alerts | Where-Object { $_.transactionId -eq $txnId } | Select-Object -First 1
        if ($related) { $alertId = $related.alertId }
    }
    if (-not $alertId -and $alerts.Count -gt 0) { $alertId = $alerts[0].alertId }

    if ($alertId) {
        $patchBody = @{ status = 'ACKNOWLEDGED'; action = 'ACKNOWLEDGE'; description = 'api smoke test status update' } | ConvertTo-Json
        $patched = Invoke-RestMethod -Method Patch -Uri "$base/api/alerts/$alertId/status" -Headers $headers -ContentType 'application/json' -Body $patchBody -ErrorAction Stop
        Add-Result 'PATCH /api/alerts/{id}/status' ($null -ne $patched.alertStatus) "alertId=$alertId status=$($patched.alertStatus)"

        $logs = @(Invoke-RestMethod -Method Get -Uri "$base/api/alerts/$alertId/logs" -Headers $headers -ErrorAction Stop)
        Add-Result 'GET /api/alerts/{id}/logs' ($logs.Count -ge 1) "alertId=$alertId logCount=$($logs.Count)"
    } else {
        Add-Result 'Alerts mutation flow' $false 'No alert ID available'
    }
} catch {
    Add-Result 'Alerts flow' $false "HTTP $(StatusCodeFromError $_)"
}

try {
    $status1 = Invoke-RestMethod -Method Get -Uri "$base/api/simulator/status" -ErrorAction Stop
    Add-Result 'GET /api/simulator/status' ($null -ne $status1.running) "running=$($status1.running)"

    $gen = Invoke-RestMethod -Method Post -Uri "$base/api/simulator/generate/2" -ErrorAction Stop
    Add-Result 'POST /api/simulator/generate/2' ($gen.generated -eq 2) "generated=$($gen.generated)"

    $start = Invoke-RestMethod -Method Post -Uri "$base/api/simulator/start" -ErrorAction Stop
    Add-Result 'POST /api/simulator/start' ($start.message -match 'started|already running') "message=$($start.message)"

    $stop = Invoke-RestMethod -Method Post -Uri "$base/api/simulator/stop" -ErrorAction Stop
    Add-Result 'POST /api/simulator/stop' ($stop.message -match 'stopped') "message=$($stop.message)"
} catch {
    Add-Result 'Simulator flow' $false "HTTP $(StatusCodeFromError $_)"
}

try {
    $allLogs = @(Invoke-RestMethod -Method Get -Uri "$base/api/logs" -Headers $headers -ErrorAction Stop)
    Add-Result 'GET /api/logs' ($allLogs.Count -ge 0) "count=$($allLogs.Count)"
} catch {
    Add-Result 'GET /api/logs' $false "HTTP $(StatusCodeFromError $_)"
}

$results | Format-Table -AutoSize | Out-String | Write-Output
if (($results | Where-Object { $_.Status -eq 'FAIL' }).Count -gt 0) { exit 2 }

$base = 'http://localhost:8082'
$results = New-Object System.Collections.ArrayList

function Add-Result($name, $ok, $detail) {
    [void]$results.Add([pscustomobject]@{
        Test = $name
        Status = $(if ($ok) { 'PASS' } else { 'FAIL' })
        Detail = $detail
    })
}

function Invoke-Api {
    param(
        [string]$Method,
        [string]$Url,
        [hashtable]$Headers,
        [string]$Body,
        [string]$ContentType = 'application/json'
    )

    try {
        $params = @{
            Method = $Method
            Uri = $Url
            ErrorAction = 'Stop'
        }
        if ($Headers) { $params.Headers = $Headers }
        if ($Body -ne $null) {
            $params.Body = $Body
            $params.ContentType = $ContentType
        }

        $resp = Invoke-WebRequest @params
        $json = $null
        if (-not [string]::IsNullOrWhiteSpace($resp.Content)) {
            $json = $resp.Content | ConvertFrom-Json
        }

        return [pscustomobject]@{
            Ok = $true
            Status = [int]$resp.StatusCode
            Body = $json
            Raw = $resp.Content
        }
    }
    catch {
        $status = -1
        $raw = ''
        if ($_.Exception.Response) {
            $status = [int]$_.Exception.Response.StatusCode
            $sr = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            $raw = $sr.ReadToEnd()
        }

        $json = $null
        if (-not [string]::IsNullOrWhiteSpace($raw)) {
            try { $json = $raw | ConvertFrom-Json } catch { }
        }

        return [pscustomobject]@{
            Ok = $false
            Status = $status
            Body = $json
            Raw = $raw
        }
    }
}

# Auth unauthorized check
$r = Invoke-Api -Method 'GET' -Url "$base/api/auth/me"
Add-Result 'GET /api/auth/me without token' (($r.Status -eq 401) -or ($r.Status -eq 403)) "HTTP $($r.Status)"

# Login
$r = Invoke-Api -Method 'POST' -Url "$base/api/auth/login" -Body (@{ username='admin'; password='admin123' } | ConvertTo-Json)
$token = if ($r.Ok) { $r.Body.token } else { $null }
Add-Result 'POST /api/auth/login' (-not [string]::IsNullOrWhiteSpace($token)) "HTTP $($r.Status)"
if ([string]::IsNullOrWhiteSpace($token)) {
    $results | Format-Table -AutoSize | Out-String | Write-Output
    exit 1
}
$auth = @{ Authorization = "Bearer $token" }

# Me with token
$r = Invoke-Api -Method 'GET' -Url "$base/api/auth/me" -Headers $auth
Add-Result 'GET /api/auth/me with token' ($r.Ok -and $r.Body.username -eq 'admin') "HTTP $($r.Status) username=$($r.Body.username)"

# Rules get + CRUD
$r = Invoke-Api -Method 'GET' -Url "$base/api/rules" -Headers $auth
$rules = @($r.Body)
Add-Result 'GET /api/rules' ($r.Ok -and $rules.Count -ge 1) "HTTP $($r.Status) count=$($rules.Count)"

$newRuleBody = @{
    ruleName = "API Smoke Rule $(Get-Date -Format 'yyyyMMddHHmmss')"
    ruleType = 'AMOUNT_THRESHOLD'
    fieldName = 'amount'
    operator = 'GREATER_THAN'
    thresholdValue = '12345'
    isActive = $true
} | ConvertTo-Json
$rCreate = Invoke-Api -Method 'POST' -Url "$base/api/rules" -Headers $auth -Body $newRuleBody
$ruleId = if ($rCreate.Ok) { $rCreate.Body.ruleId } else { $null }
Add-Result 'POST /api/rules' ($rCreate.Ok -and $null -ne $ruleId) "HTTP $($rCreate.Status) ruleId=$ruleId"

if ($null -ne $ruleId) {
    $rUpdate = Invoke-Api -Method 'PUT' -Url "$base/api/rules/$ruleId" -Headers $auth -Body (@{
        ruleName = $rCreate.Body.ruleName
        ruleType = $rCreate.Body.ruleType
        fieldName = $rCreate.Body.fieldName
        operator = $rCreate.Body.operator
        thresholdValue = '15000'
        timeWindowMinutes = $rCreate.Body.timeWindowMinutes
        isActive = $rCreate.Body.isActive
    } | ConvertTo-Json)
    Add-Result 'PUT /api/rules/{id}' ($rUpdate.Ok -and $rUpdate.Body.thresholdValue -eq '15000') "HTTP $($rUpdate.Status)"

    $rDelete = Invoke-Api -Method 'DELETE' -Url "$base/api/rules/$ruleId" -Headers $auth
    Add-Result 'DELETE /api/rules/{id}' ($rDelete.Status -eq 200 -or $rDelete.Status -eq 204) "HTTP $($rDelete.Status)"
}

# Transactions create + alert + rollback integration
$rBeforeTx = Invoke-Api -Method 'GET' -Url "$base/api/transactions"
$beforeCount = @($rBeforeTx.Body).Count

$rTx = Invoke-Api -Method 'POST' -Url "$base/api/transactions" -Body (@{
    accountId = 'ACC-SMOKE-V2-01'
    amount = 72000
    currency = 'USD'
    transactionType = 'TRANSFER'
    payeeId = 'PAYEE-SMOKE-V2-01'
    payeeName = 'Smoke V2 Beneficiary'
    status = 'PENDING'
} | ConvertTo-Json)
$txnId = if ($rTx.Ok) { $rTx.Body.transactionId } else { $null }

$rAfterTx = Invoke-Api -Method 'GET' -Url "$base/api/transactions"
$afterCount = @($rAfterTx.Body).Count
Add-Result 'POST /api/transactions + list growth' ($rTx.Ok -and $null -ne $txnId -and $afterCount -ge ($beforeCount + 1)) "HTTP $($rTx.Status) txnId=$txnId before=$beforeCount after=$afterCount"

$rAlerts = Invoke-Api -Method 'GET' -Url "$base/api/alerts" -Headers $auth
$alerts = @($rAlerts.Body)
Add-Result 'GET /api/alerts' ($rAlerts.Ok -and $alerts.Count -ge 1) "HTTP $($rAlerts.Status) count=$($alerts.Count)"

$relatedAlert = $null
if ($null -ne $txnId) {
    $relatedAlert = $alerts | Where-Object { $_.transactionId -eq $txnId } | Select-Object -First 1
}

if ($relatedAlert) {
    $rInvestigate = Invoke-Api -Method 'PATCH' -Url "$base/api/alerts/$($relatedAlert.alertId)/status" -Headers $auth -Body (@{
        status = 'INVESTIGATING'
        action = 'START_INVESTIGATION'
        description = 'rollback precondition from smoke v2'
    } | ConvertTo-Json)
    Add-Result 'PATCH /api/alerts/{id}/status' ($rInvestigate.Ok -and $rInvestigate.Body.alertStatus -eq 'INVESTIGATING') "HTTP $($rInvestigate.Status) alertId=$($relatedAlert.alertId)"

    $rRollback = Invoke-Api -Method 'PATCH' -Url "$base/api/transactions/$txnId/rollback"
    Add-Result 'PATCH /api/transactions/{id}/rollback' ($rRollback.Ok -and $rRollback.Body.investigationStatus -eq 'ROLLED_BACK') "HTTP $($rRollback.Status)"

    $rAlertLogs = Invoke-Api -Method 'GET' -Url "$base/api/alerts/$($relatedAlert.alertId)/logs" -Headers $auth
    $alertLogs = @($rAlertLogs.Body)
    Add-Result 'GET /api/alerts/{id}/logs' ($rAlertLogs.Ok -and $alertLogs.Count -ge 1) "HTTP $($rAlertLogs.Status) logCount=$($alertLogs.Count)"
} else {
    Add-Result 'Alert linked to new transaction' $false "No alert found for txnId=$txnId"
}

# Simulator
$rStatus = Invoke-Api -Method 'GET' -Url "$base/api/simulator/status"
Add-Result 'GET /api/simulator/status' ($rStatus.Ok -and $null -ne $rStatus.Body.running) "HTTP $($rStatus.Status) running=$($rStatus.Body.running)"

$rGenerate = Invoke-Api -Method 'POST' -Url "$base/api/simulator/generate/2"
Add-Result 'POST /api/simulator/generate/2' ($rGenerate.Ok -and $rGenerate.Body.generated -eq 2) "HTTP $($rGenerate.Status) generated=$($rGenerate.Body.generated)"

$rStart = Invoke-Api -Method 'POST' -Url "$base/api/simulator/start"
Add-Result 'POST /api/simulator/start' ($rStart.Ok) "HTTP $($rStart.Status)"

$rStop = Invoke-Api -Method 'POST' -Url "$base/api/simulator/stop"
Add-Result 'POST /api/simulator/stop' ($rStop.Ok) "HTTP $($rStop.Status)"

# Logs
$rLogs = Invoke-Api -Method 'GET' -Url "$base/api/logs" -Headers $auth
Add-Result 'GET /api/logs' ($rLogs.Ok) "HTTP $($rLogs.Status) count=$(@($rLogs.Body).Count)"

$results | Format-Table -AutoSize | Out-String | Write-Output
if (($results | Where-Object { $_.Status -eq 'FAIL' }).Count -gt 0) { exit 2 }

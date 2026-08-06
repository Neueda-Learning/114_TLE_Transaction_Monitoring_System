const base = 'http://localhost:8082';

const results = [];
const add = (test, ok, detail) => results.push({ test, status: ok ? 'PASS' : 'FAIL', detail });

async function api(method, path, { token, body } = {}) {
  const headers = {};
  if (token) headers.Authorization = `Bearer ${token}`;
  if (body !== undefined) headers['Content-Type'] = 'application/json';

  const res = await fetch(`${base}${path}`, {
    method,
    headers,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });

  let json = null;
  const text = await res.text();
  if (text) {
    try {
      json = JSON.parse(text);
    } catch {
      json = text;
    }
  }

  return { ok: res.ok, status: res.status, body: json };
}

try {
  const unauth = await api('GET', '/api/auth/me');
  add('GET /api/auth/me without token', unauth.status === 401 || unauth.status === 403, `HTTP ${unauth.status}`);

  const login = await api('POST', '/api/auth/login', {
    body: { username: 'admin', password: 'admin123' },
  });
  const token = login.body?.token;
  add('POST /api/auth/login', Boolean(token), `HTTP ${login.status}`);

  if (!token) {
    console.table(results);
    process.exit(1);
  }

  const me = await api('GET', '/api/auth/me', { token });
  add('GET /api/auth/me with token', me.ok && me.body?.username === 'admin', `HTTP ${me.status} username=${me.body?.username ?? ''}`);

  const rules = await api('GET', '/api/rules', { token });
  const ruleList = Array.isArray(rules.body) ? rules.body : [];
  add('GET /api/rules', rules.ok && ruleList.length >= 1, `HTTP ${rules.status} count=${ruleList.length}`);

  const createRule = await api('POST', '/api/rules', {
    token,
    body: {
      ruleName: `API Smoke Rule ${Date.now()}`,
      ruleType: 'AMOUNT_THRESHOLD',
      fieldName: 'amount',
      operator: 'GREATER_THAN',
      thresholdValue: '12345',
      isActive: true,
    },
  });
  const ruleId = createRule.body?.ruleId;
  add('POST /api/rules', createRule.ok && !!ruleId, `HTTP ${createRule.status} ruleId=${ruleId ?? ''}`);

  if (ruleId) {
    const updateRule = await api('PUT', `/api/rules/${ruleId}`, {
      token,
      body: {
        ...createRule.body,
        thresholdValue: '15000',
      },
    });
    add('PUT /api/rules/{id}', updateRule.ok && updateRule.body?.thresholdValue === '15000', `HTTP ${updateRule.status}`);

    const delRule = await api('DELETE', `/api/rules/${ruleId}`, { token });
    add('DELETE /api/rules/{id}', delRule.status === 204 || delRule.status === 200, `HTTP ${delRule.status}`);
  }

  const beforeTx = await api('GET', '/api/transactions');
  const beforeTxList = Array.isArray(beforeTx.body) ? beforeTx.body : [];

  const createTx = await api('POST', '/api/transactions', {
    body: {
      accountId: 'ACC-SMOKE-NODE-01',
      amount: 72000,
      currency: 'USD',
      transactionType: 'TRANSFER',
      payeeId: 'PAYEE-SMOKE-NODE-01',
      payeeName: 'Smoke Node Beneficiary',
      status: 'PENDING',
    },
  });
  const txnId = createTx.body?.transactionId;

  const afterTx = await api('GET', '/api/transactions');
  const afterTxList = Array.isArray(afterTx.body) ? afterTx.body : [];

  add(
    'POST /api/transactions + list growth',
    createTx.ok && !!txnId && afterTxList.length >= beforeTxList.length + 1,
    `HTTP ${createTx.status} txnId=${txnId ?? ''} before=${beforeTxList.length} after=${afterTxList.length}`
  );

  const alerts = await api('GET', '/api/alerts', { token });
  const alertList = Array.isArray(alerts.body) ? alerts.body : [];
  add('GET /api/alerts', alerts.ok && alertList.length >= 1, `HTTP ${alerts.status} count=${alertList.length}`);

  const relatedAlert = alertList.find((a) => a.transactionId === txnId);
  if (relatedAlert) {
    const investigate = await api('PATCH', `/api/alerts/${relatedAlert.alertId}/status`, {
      token,
      body: {
        status: 'INVESTIGATING',
        action: 'START_INVESTIGATION',
        description: 'rollback precondition from node smoke',
      },
    });
    add('PATCH /api/alerts/{id}/status', investigate.ok && investigate.body?.alertStatus === 'INVESTIGATING', `HTTP ${investigate.status}`);

    const rollback = await api('PATCH', `/api/transactions/${txnId}/rollback`);
    add('PATCH /api/transactions/{id}/rollback', rollback.ok && rollback.body?.investigationStatus === 'ROLLED_BACK', `HTTP ${rollback.status}`);

    const alertLogs = await api('GET', `/api/alerts/${relatedAlert.alertId}/logs`, { token });
    const logsList = Array.isArray(alertLogs.body) ? alertLogs.body : [];
    add('GET /api/alerts/{id}/logs', alertLogs.ok && logsList.length >= 1, `HTTP ${alertLogs.status} logCount=${logsList.length}`);
  } else {
    add('Alert linked to new transaction', false, `No alert found for txnId=${txnId ?? ''}`);
  }

  const simStatus = await api('GET', '/api/simulator/status');
  add('GET /api/simulator/status', simStatus.ok && typeof simStatus.body?.running === 'boolean', `HTTP ${simStatus.status}`);

  const simGenerate = await api('POST', '/api/simulator/generate/2');
  add('POST /api/simulator/generate/2', simGenerate.ok && simGenerate.body?.generated === 2, `HTTP ${simGenerate.status} generated=${simGenerate.body?.generated ?? ''}`);

  const simStart = await api('POST', '/api/simulator/start');
  add('POST /api/simulator/start', simStart.ok || simStart.status === 409, `HTTP ${simStart.status}`);

  const simStop = await api('POST', '/api/simulator/stop');
  add('POST /api/simulator/stop', simStop.ok, `HTTP ${simStop.status}`);

  const logs = await api('GET', '/api/logs', { token });
  const logsList = Array.isArray(logs.body) ? logs.body : [];
  add('GET /api/logs', logs.ok, `HTTP ${logs.status} count=${logsList.length}`);

  console.table(results);
  if (results.some((r) => r.status === 'FAIL')) process.exit(2);
} catch (err) {
  console.error('Smoke run crashed:', err.message);
  process.exit(3);
}

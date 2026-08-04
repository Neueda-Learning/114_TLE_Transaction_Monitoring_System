import {
  Cell,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
  Legend,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
} from 'recharts'

const severityColors = {
  HIGH: 'var(--danger-500)',
  MEDIUM: 'var(--warning-500)',
  LOW: 'var(--brand-500)',
}

const statusColors = {
  OPEN: 'var(--danger-500)',
  ACKNOWLEDGED: 'var(--brand-500)',
  INVESTIGATING: 'var(--warning-500)',
  CLOSED: 'var(--success-500)',
  DISMISSED: 'var(--text-muted)',
}

function AlertChart({ data, type }) {
  if (type === 'severity') {
    return (
      <ResponsiveContainer width="100%" height={260}>
        <PieChart>
          <Pie
            data={data}
            dataKey="value"
            nameKey="name"
            cx="50%"
            cy="50%"
            outerRadius={88}
            innerRadius={54}
            label
          >
            {data.map((entry) => (
              <Cell key={entry.name} fill={severityColors[entry.name]} />
            ))}
          </Pie>
          <Tooltip />
          <Legend />
        </PieChart>
      </ResponsiveContainer>
    )
  }

  return (
    <ResponsiveContainer width="100%" height={260}>
      <BarChart data={data}>
        <CartesianGrid strokeDasharray="3 3" stroke="var(--slate-300)" />
        <XAxis dataKey="name" stroke="var(--text-muted)" />
        <YAxis stroke="var(--text-muted)" />
        <Tooltip />
        <Bar dataKey="value" radius={[6, 6, 0, 0]}>
          {data.map((entry) => (
            <Cell key={entry.name} fill={statusColors[entry.name]} />
          ))}
        </Bar>
      </BarChart>
    </ResponsiveContainer>
  )
}

export default AlertChart

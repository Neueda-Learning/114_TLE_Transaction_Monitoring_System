import {
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  Area,
  AreaChart,
} from 'recharts'

function TransactionChart({ data }) {
  return (
    <ResponsiveContainer width="100%" height={300}>
      <AreaChart data={data}>
        <defs>
          <linearGradient id="txArea" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stopColor="var(--brand-500)" stopOpacity={0.4} />
            <stop offset="100%" stopColor="var(--brand-500)" stopOpacity={0.06} />
          </linearGradient>
        </defs>
        <CartesianGrid strokeDasharray="3 3" stroke="var(--slate-300)" />
        <XAxis dataKey="time" stroke="var(--text-muted)" />
        <YAxis stroke="var(--text-muted)" />
        <Tooltip />
        <Area
          type="monotone"
          dataKey="volume"
          stroke="var(--brand-500)"
          fill="url(#txArea)"
          strokeWidth={2}
        />
        <Line
          type="monotone"
          dataKey="alerts"
          stroke="var(--danger-500)"
          strokeWidth={2}
          dot={{ r: 3 }}
        />
      </AreaChart>
    </ResponsiveContainer>
  )
}

export default TransactionChart

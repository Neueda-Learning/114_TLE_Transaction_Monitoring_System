function Card({ title, value, subtitle, trend, children }) {
  return (
    <article className="card surface-elevated">
      <header className="card-head">
        <p className="card-title">{title}</p>
        {trend ? <span className={`trend trend-${trend.type}`}>{trend.label}</span> : null}
      </header>
      <div className="card-value">{value}</div>
      {subtitle ? <p className="card-subtitle">{subtitle}</p> : null}
      {children}
    </article>
  )
}

export default Card

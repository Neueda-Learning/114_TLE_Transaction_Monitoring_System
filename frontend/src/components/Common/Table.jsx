function Table({
  columns,
  data,
  rowKey,
  sortConfig,
  onSort,
  isLoading,
  emptyMessage = 'No records found.',
}) {
  const renderSortIndicator = (key) => {
    if (!sortConfig || sortConfig.key !== key) {
      return '↕'
    }

    return sortConfig.direction === 'asc' ? '↑' : '↓'
  }

  return (
    <div className="table-shell surface-elevated">
      <table className="data-table">
        <thead>
          <tr>
            {columns.map((column) => (
              <th key={column.key} style={{ width: column.width || 'auto' }}>
                {column.sortable ? (
                  <button
                    type="button"
                    className="sort-btn"
                    onClick={() => onSort(column.key)}
                  >
                    {column.label}
                    <span>{renderSortIndicator(column.key)}</span>
                  </button>
                ) : (
                  column.label
                )}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {isLoading ? (
            <tr>
              <td colSpan={columns.length}>
                <div className="table-state">Loading records...</div>
              </td>
            </tr>
          ) : null}

          {!isLoading && data.length === 0 ? (
            <tr>
              <td colSpan={columns.length}>
                <div className="table-state">{emptyMessage}</div>
              </td>
            </tr>
          ) : null}

          {!isLoading
            ? data.map((row) => (
                <tr key={typeof rowKey === 'function' ? rowKey(row) : row[rowKey]}>
                  {columns.map((column) => (
                    <td key={column.key}>
                      {column.render ? column.render(row[column.key], row) : row[column.key]}
                    </td>
                  ))}
                </tr>
              ))
            : null}
        </tbody>
      </table>
    </div>
  )
}

export default Table

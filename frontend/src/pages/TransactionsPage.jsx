import { useMemo, useState } from 'react'
import Table from '../components/Common/Table'
import SearchBar from '../components/Common/SearchBar'
import Filter from '../components/Common/Filter'
import Badge from '../components/Common/Badge'
import { useAppData } from '../context/useAppData'
import { formatCurrency, formatDateTime } from '../utils/formatters'

const PAGE_SIZE = 8

function TransactionsPage() {
  const { transactions, isLoading, error } = useAppData()
  const [search, setSearch] = useState('')
  const [riskFilter, setRiskFilter] = useState('ALL')
  const [typeFilter, setTypeFilter] = useState('ALL')
  const [sortConfig, setSortConfig] = useState({ key: 'timestamp', direction: 'desc' })
  const [page, setPage] = useState(1)

  const filteredTransactions = useMemo(() => {
    let result = [...transactions]

    if (search.trim()) {
      const query = search.toLowerCase()
      result = result.filter(
        (transaction) =>
          transaction.id.toLowerCase().includes(query) ||
          transaction.accountId.toLowerCase().includes(query) ||
          transaction.customer.toLowerCase().includes(query) ||
          transaction.payee.toLowerCase().includes(query),
      )
    }

    if (riskFilter !== 'ALL') {
      result = result.filter((transaction) => transaction.riskStatus === riskFilter)
    }

    if (typeFilter !== 'ALL') {
      result = result.filter((transaction) => transaction.transactionType === typeFilter)
    }

    result.sort((a, b) => {
      const { key, direction } = sortConfig
      let aValue = a[key]
      let bValue = b[key]

      if (key === 'timestamp') {
        aValue = new Date(aValue).getTime()
        bValue = new Date(bValue).getTime()
      }

      if (typeof aValue === 'string') {
        const compare = aValue.localeCompare(bValue)
        return direction === 'asc' ? compare : -compare
      }

      return direction === 'asc' ? aValue - bValue : bValue - aValue
    })

    return result
  }, [transactions, search, riskFilter, typeFilter, sortConfig])

  const totalPages = Math.max(1, Math.ceil(filteredTransactions.length / PAGE_SIZE))

  const pagedTransactions = useMemo(() => {
    const start = (page - 1) * PAGE_SIZE
    return filteredTransactions.slice(start, start + PAGE_SIZE)
  }, [filteredTransactions, page])

  const handleSort = (key) => {
    setSortConfig((current) => {
      if (current.key === key) {
        return {
          key,
          direction: current.direction === 'asc' ? 'desc' : 'asc',
        }
      }

      return { key, direction: 'asc' }
    })
  }

  const columns = [
    { key: 'id', label: 'Transaction ID', sortable: true },
    { key: 'accountId', label: 'Account ID', sortable: true },
    { key: 'customer', label: 'Customer', sortable: true },
    { key: 'payee', label: 'Payee', sortable: true },
    {
      key: 'amount',
      label: 'Amount',
      sortable: true,
      render: (value, row) => formatCurrency(value, row.currency),
    },
    { key: 'currency', label: 'Currency' },
    { key: 'transactionType', label: 'Transaction Type', sortable: true },
    {
      key: 'timestamp',
      label: 'Timestamp',
      sortable: true,
      render: (value) => formatDateTime(value),
    },
    {
      key: 'riskStatus',
      label: 'Risk Status',
      sortable: true,
      render: (value) => <Badge label={value} variant={value} />,
    },
  ]

  if (error) {
    return (
      <section className="state-panel surface-elevated">
        <h2>Unable to load transactions</h2>
        <p>{error}</p>
      </section>
    )
  }

  return (
    <section className="page-wrap">
      <article className="section-card">
        <div className="block-head">
          <h2>Enterprise Transaction Monitoring</h2>
          <p className="helper-text">Search, filter, and prioritize high-risk payments.</p>
        </div>

        <div className="toolbar">
          <SearchBar
            value={search}
            onChange={(value) => {
              setSearch(value)
              setPage(1)
            }}
            placeholder="Search by ID, account, customer, or payee"
          />

          <Filter
            label="Risk"
            value={riskFilter}
            onChange={(value) => {
              setRiskFilter(value)
              setPage(1)
            }}
            options={[
              { label: 'All Risk Status', value: 'ALL' },
              { label: 'Normal', value: 'NORMAL' },
              { label: 'Suspicious', value: 'SUSPICIOUS' },
              { label: 'Blocked', value: 'BLOCKED' },
            ]}
          />

          <Filter
            label="Type"
            value={typeFilter}
            onChange={(value) => {
              setTypeFilter(value)
              setPage(1)
            }}
            options={[
              { label: 'All Types', value: 'ALL' },
              ...Array.from(new Set(transactions.map((t) => t.transactionType))).map(
                (type) => ({ label: type, value: type }),
              ),
            ]}
          />
        </div>

        <Table
          columns={columns}
          data={pagedTransactions}
          rowKey="id"
          sortConfig={sortConfig}
          onSort={handleSort}
          isLoading={isLoading}
          emptyMessage="No transactions match the selected criteria."
        />

        <div className="pagination-bar">
          <p>
            Showing {pagedTransactions.length} of {filteredTransactions.length} transactions
          </p>

          <div className="pagination-controls">
            <button
              type="button"
              className="ghost-btn"
              disabled={page <= 1}
              onClick={() => setPage((currentPage) => Math.max(1, currentPage - 1))}
            >
              Previous
            </button>
            <span>
              Page {page} / {totalPages}
            </span>
            <button
              type="button"
              className="ghost-btn"
              disabled={page >= totalPages}
              onClick={() =>
                setPage((currentPage) => Math.min(totalPages, currentPage + 1))
              }
            >
              Next
            </button>
          </div>
        </div>
      </article>
    </section>
  )
}

export default TransactionsPage

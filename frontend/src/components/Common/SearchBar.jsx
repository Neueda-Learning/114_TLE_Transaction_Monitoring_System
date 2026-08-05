function SearchBar({ value, onChange, placeholder = 'Search' }) {
  return (
    <label className="searchbar" htmlFor="table-search">
      <span className="searchbar-icon">⌕</span>
      <input
        id="table-search"
        value={value}
        onChange={(event) => onChange(event.target.value)}
        placeholder={placeholder}
        type="text"
      />
    </label>
  )
}

export default SearchBar

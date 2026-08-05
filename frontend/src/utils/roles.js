export const ROLE_DISPLAY_NAMES = {
  ADMIN: 'Administrator',
  ANALYST: 'Fraud Analyst',
}

export const getRoleDisplayName = (role) => ROLE_DISPLAY_NAMES[role] || role || 'User'

// Analysts can view monitoring rules but only ADMIN can create/edit/delete/toggle them.
export const canManageRules = (role) => role === 'ADMIN'

// Centralized nav config: which roles can see which sidebar entry.
export const NAV_ITEMS = [
  { to: '/dashboard', label: 'Dashboard', icon: '◫', roles: ['ADMIN', 'ANALYST'] },
  { to: '/transactions', label: 'Transactions', icon: '⇄', roles: ['ADMIN', 'ANALYST'] },
  { to: '/alerts', label: 'Alerts', icon: '⚑', roles: ['ADMIN', 'ANALYST'] },
  { to: '/rules', label: 'Rules Management', icon: '⌘', roles: ['ADMIN', 'ANALYST'] },
  { to: '/analytics', label: 'Reports', icon: '◍', roles: ['ADMIN', 'ANALYST'] },
]

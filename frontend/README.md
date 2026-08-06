# Frontend - Transaction Monitoring System

This folder contains the React + Vite frontend for the Transaction Monitoring System.

## Prerequisites

- Node.js 20+ recommended
- npm 10+

## Local Development

1. Install dependencies:

```bash
npm install
```

2. Start development server:

```bash
npm run dev
```

3. Open the app in your browser:

```text
http://localhost:5173
```

## Available Scripts

- `npm run dev` - Start Vite dev server
- `npm run build` - Create production build
- `npm run preview` - Preview production build locally
- `npm run lint` - Run ESLint checks
- `npm run test` - Run unit tests (Vitest)

## Notes

- Frontend API integration is configured in `src/services/`.
- Alert stream and authentication state are managed via context providers in `src/context/`.

import { Navigate, useLocation } from 'react-router-dom'

export default function RequireAuth({ children }: { children: JSX.Element }) {
  const token = localStorage.getItem('access_token')
  const loc = useLocation()
  if (!token) return <Navigate to="/login" state={{ from: loc }} replace />
  return children
}
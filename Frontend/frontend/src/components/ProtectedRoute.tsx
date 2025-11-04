import { Navigate, useLocation } from 'react-router-dom';
import { Role, useAuth } from '../state/AuthContext';
import React from 'react';

export const ProtectedRoute: React.FC<{ children: React.ReactNode; allowedRoles?: Role[] }> = ({ children, allowedRoles }) => {
  const { token, user, isAuthenticated } = useAuth();
  const loc = useLocation();

  if (!token && !isAuthenticated) return <Navigate to="/login" state={{ from: loc }} replace />;
  if (allowedRoles && user && !allowedRoles.includes(user.role)) return <Navigate to="/login" replace />;

  return <>{children}</>;
};

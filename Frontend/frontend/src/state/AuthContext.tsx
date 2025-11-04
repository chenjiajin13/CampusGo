import React, { createContext, useContext, useMemo, useState } from 'react';
import api from '../services/api/client';

export type Role = 'USER' | 'MERCHANT' | 'RUNNER' | 'ADMIN';
export interface SessionUser { id: string | number; username: string; role: Role; }

interface AuthState {
  token: string | null;
  user: SessionUser | null;
  isAuthenticated: boolean;
  login: (args: { username: string; password: string; role: Role }) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthState | null>(null);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  // ✅ 这里声明 state（注意 useState 已从 React 引入）
  const [token, setToken] = useState<string | null>(localStorage.getItem('token'));
  const [user, setUser] = useState<SessionUser | null>(() => {
    const raw = localStorage.getItem('user');
    return raw ? (JSON.parse(raw) as SessionUser) : null;
  });
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>( // ✅ 有它
    localStorage.getItem('isAuthenticated') === 'true'
  );

  // ✅ login 必须在 AuthProvider 内部定义，才能访问 setIsAuthenticated
  const login = async ({ username, password, role }: { username: string; password: string; role: Role }) => {
    const endpointMap: Record<Role, string> = {
      USER: '/auth/login',
      MERCHANT: '/auth/login',
      RUNNER: '/auth/login',
      ADMIN: '/auth/admin/login',
    };

    const res = await api.post(endpointMap[role], { username, password });

    const d = res?.data, h = res?.headers || {};
    const tkn =
      d?.token || d?.accessToken || d?.jwt ||
      d?.data?.token || d?.data?.accessToken || d?.data?.jwt ||
      h['authorization'] || h['Authorization'] || h['x-auth-token'] || h['X-Auth-Token'] || null;

    if (tkn) {
      localStorage.setItem('token', tkn);
      setToken(tkn);
    } else {
      // 如果你校验接口是 internal，则改成 '/internal/auth/validate'
      await api.get('/internal/auth/validate');
    }

    const u = (d?.user || d?.data?.user || d?.data?.loginUser) as SessionUser
      ?? { id: '', username, role };

    localStorage.setItem('user', JSON.stringify(u));
    localStorage.setItem('isAuthenticated', 'true');
    setUser(u);
    setIsAuthenticated(true);          // ✅ 现在可用
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    localStorage.removeItem('isAuthenticated');
    setToken(null);
    setUser(null);
    setIsAuthenticated(false);
  };

  const value = useMemo(() => ({ token, user, isAuthenticated, login, logout }), [token, user, isAuthenticated]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
};

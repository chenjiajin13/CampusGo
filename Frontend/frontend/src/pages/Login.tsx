import { FormEvent, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Role, useAuth } from '../state/AuthContext';


const roles: Role[] = ['USER', 'MERCHANT', 'RUNNER', 'ADMIN'];


export default function Login() {
const { login } = useAuth();
const nav = useNavigate();
const [tab, setTab] = useState<Role>('USER');
const [username, setUsername] = useState('user1');
const [password, setPassword] = useState('123456');
const [error, setError] = useState<string | null>(null);
const [loading, setLoading] = useState(false);


const onSubmit = async (e: FormEvent) => {
e.preventDefault();
setLoading(true);
setError(null);
try {
await login({ username, password, role: tab });
// Route to the right dashboard. For now, only user dashboard is implemented.
if (tab === 'USER') nav('/user/dashboard');
else nav('/login'); // placeholder – extend for other roles
} catch (err: any) {
setError(err?.message || 'Login failed');
} finally {
setLoading(false);
}
};


return (
<div className="auth">
<h1>CampusGo Login</h1>


<div className="tabs">
{roles.map((r) => (
<button key={r} className={r === tab ? 'active' : ''} onClick={() => setTab(r)}>
{r}
</button>
))}
</div>


<form onSubmit={onSubmit} className="card">
<label>
Username
<input value={username} onChange={(e) => setUsername(e.target.value)} placeholder="username" />
</label>
<label>
Password
<input type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="password" />
</label>
<button type="submit" disabled={loading}>{loading ? 'Signing in…' : `Sign in as ${tab}`}</button>
{error && <p className="error">{error}</p>}
</form>


<p className="hint">Gateways used: /api/auth/login, /api/auth/admin/login via Vite proxy → http://localhost:8080</p>
</div>
);
}
import { useEffect, useState } from 'react';
import api from '../../services/api/client';
import { useAuth } from '../../state/AuthContext';


interface InboxItem {
id?: number | string;
title?: string;
message?: string;
time?: string;
}


export default function UserDashboard() {
const { user, logout } = useAuth();
const [items, setItems] = useState<InboxItem[]>([]);
const [loading, setLoading] = useState(false);
const [error, setError] = useState<string | null>(null);


useEffect(() => {
const run = async () => {
if (!user?.id) return;
setLoading(true);
setError(null);
try {
// notification-service: GET /api/notifications/inbox/user/{userId}
const res = await api.get(`/notifications/inbox/user/${user.id}`);
setItems(res.data?.data || res.data || []);
} catch (err: any) {
setError(err?.message || 'Failed to load inbox');
} finally {
setLoading(false);
}
};
run();
}, [user?.id]);


return (
<div className="page">
<header className="topbar">
<div>
<strong>Logged in as:</strong> {user?.username || 'Unknown'} (id: {String(user?.id || '')})
</div>
<button onClick={logout}>Logout</button>
</header>


<h2>My Inbox</h2>
{loading && <p>Loading…</p>}
{error && <p className="error">{error}</p>}
{!loading && !error && (
<ul className="list">
{items.map((it, idx) => (
<li key={it.id || idx} className="item">
<div className="title">{it.title || 'Notification'}</div>
<div className="msg">{it.message}</div>
<div className="time">{it.time}</div>
</li>
))}
{items.length === 0 && <li>No notifications yet.</li>}
</ul>
)}
</div>
);
}
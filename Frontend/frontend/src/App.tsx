import { Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import UserDashboard from './pages/user/UserDashboard';
import { ProtectedRoute } from './components/ProtectedRoute';


export default function App() {
return (
<Routes>
<Route path="/login" element={<Login />} />


{/* USER area */}
<Route
path="/user/*"
element={
<ProtectedRoute allowedRoles={["USER"]}>
<Routes>
<Route path="dashboard" element={<UserDashboard />} />
<Route path="*" element={<Navigate to="/user/dashboard" replace />} />
</Routes>
</ProtectedRoute>
}
/>


{/* default */}
<Route path="*" element={<Navigate to="/login" replace />} />
</Routes>
);
}
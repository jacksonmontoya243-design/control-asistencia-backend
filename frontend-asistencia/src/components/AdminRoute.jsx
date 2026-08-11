import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

/**
 * Guard de rutas para ADMIN: si el usuario no es ADMIN, redirige al dashboard.
 */
const AdminRoute = ({ children }) => {
    const { user } = useAuth();

    if (!user.isAuthenticated) {
        return <Navigate to="/" replace />;
    }

    if (user.role !== 'ADMIN') {
        return <Navigate to="/dashboard" replace />;
    }

    return children;
};

export default AdminRoute;
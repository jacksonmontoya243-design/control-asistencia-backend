import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

/**
 * Guard de rutas: si el usuario no está autenticado, redirige al login.
 */
const ProtectedRoute = ({ children }) => {
    const { user } = useAuth();

    if (!user.isAuthenticated) {
        return <Navigate to="/" replace />;
    }

    return children;
};

export default ProtectedRoute;
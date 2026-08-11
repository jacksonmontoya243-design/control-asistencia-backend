import { createContext, useState, useContext, useCallback, useEffect } from 'react';
import api from '../api/axiosConfig';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(() => ({
        username: localStorage.getItem('username') || '',
        role: localStorage.getItem('role') || '',
        isAuthenticated: !!localStorage.getItem('token'),
    }));

    // Validar el token al cargar la aplicación
    useEffect(() => {
        const token = localStorage.getItem('token');
        if (token) {
            // Verificar que el token sea válido consultando un endpoint protegido
            api.get('/api/empleados/count')
                .then(() => {
                    // Token válido, mantener sesión
                })
                .catch(() => {
                    // Token inválido o expirado, limpiar sesión
                    localStorage.removeItem('token');
                    localStorage.removeItem('username');
                    localStorage.removeItem('role');
                    setUser({ username: '', role: '', isAuthenticated: false });
                });
        }
    }, []);

    const login = useCallback(async (username, password) => {
        const response = await api.post('/api/auth/login', { username, password });
        const { token, role } = response.data;

        localStorage.setItem('token', token);
        localStorage.setItem('username', username);
        localStorage.setItem('role', role);

        setUser({ username, role, isAuthenticated: true });
        return response.data;
    }, []);

    const logout = useCallback(() => {
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        localStorage.removeItem('role');
        setUser({ username: '', role: '', isAuthenticated: false });
    }, []);

    const hasRole = useCallback((roles) => {
        const validos = Array.isArray(roles) ? roles : [roles];
        return validos.includes(user.role);
    }, [user.role]);

    return (
        <AuthContext.Provider value={{ user, login, logout, hasRole }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth debe usarse dentro de AuthProvider');
    }
    return context;
};
import axios from 'axios';

// Instancia de axios con interceptor que agrega el token JWT automáticamente
const api = axios.create({
    baseURL: '/',
});

// Interceptor de peticiones: agrega el header Authorization si hay token
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Interceptor de respuestas: si el backend devuelve 401, limpia el token y redirige al login
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response && error.response.status === 401) {
            localStorage.removeItem('token');
            localStorage.removeItem('username');
            localStorage.removeItem('role');
            window.location.href = '/';
        }
        return Promise.reject(error);
    }
);

export default api;
import axios from 'axios';

// URL base del backend:
// - En desarrollo: usa el proxy de Vite (vite.config.js) hacia localhost:8080
// - En producción: usa la variable VITE_API_URL (definida en .env.production)
export const API_URL = import.meta.env.VITE_API_URL || '';

const api = axios.create({
    baseURL: API_URL,
});

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
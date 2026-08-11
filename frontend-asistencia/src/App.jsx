import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import AdminRoute from './components/AdminRoute';
import Login from './components/Login';
import Dashboard from './components/Dashboard';
import Empleados from './components/Empleados';
import Usuarios from './components/Usuarios';
import Scanner from './components/Scanner';

function App() {
    return (
        <AuthProvider>
            <Router>
                <Routes>
                    <Route path="/" element={<Login />} />
                    <Route path="/dashboard" element={
                        <ProtectedRoute>
                            <Dashboard />
                        </ProtectedRoute>
                    } />
                    <Route path="/empleados" element={
                        <ProtectedRoute>
                            <Empleados />
                        </ProtectedRoute>
                    } />
                    <Route path="/usuarios" element={
                        <AdminRoute>
                            <Usuarios />
                        </AdminRoute>
                    } />
                    <Route path="/scanner" element={
                        <ProtectedRoute>
                            <Scanner />
                        </ProtectedRoute>
                    } />
                </Routes>
            </Router>
        </AuthProvider>
    );
}

export default App;
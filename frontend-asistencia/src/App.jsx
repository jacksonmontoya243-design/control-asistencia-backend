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
import Asistencias from './components/Asistencias';
import PageTransition from './components/PageTransition';

function App() {
    return (
        <AuthProvider>
            <Router>
                <Routes>
                    <Route path="/" element={<Login />} />
                    <Route path="/dashboard" element={
                        <ProtectedRoute>
                            <PageTransition><Dashboard /></PageTransition>
                        </ProtectedRoute>
                    } />
                    <Route path="/empleados" element={
                        <ProtectedRoute>
                            <PageTransition><Empleados /></PageTransition>
                        </ProtectedRoute>
                    } />
                    <Route path="/asistencias" element={
                        <ProtectedRoute>
                            <PageTransition><Asistencias /></PageTransition>
                        </ProtectedRoute>
                    } />
                    <Route path="/usuarios" element={
                        <AdminRoute>
                            <PageTransition><Usuarios /></PageTransition>
                        </AdminRoute>
                    } />
                    <Route path="/scanner" element={
                        <ProtectedRoute>
                            <PageTransition><Scanner /></PageTransition>
                        </ProtectedRoute>
                    } />
                </Routes>
            </Router>
        </AuthProvider>
    );
}

export default App;
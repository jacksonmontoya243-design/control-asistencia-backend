import React, { useState, useEffect } from 'react';
import api from '../api/axiosConfig';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './empleados.css';

const ROLES = ['ADMIN', 'SUPERVISOR', 'EMPLEADO'];

const Usuarios = () => {
    const { user } = useAuth();
    const [usuarios, setUsuarios] = useState([]);
    const [mensaje, setMensaje] = useState('');
    const [tipoMensaje, setTipoMensaje] = useState('');
    const [cargando, setCargando] = useState(false);
    const [confirmarEliminar, setConfirmarEliminar] = useState(null);

    useEffect(() => {
        cargarUsuarios();
    }, []);

    const cargarUsuarios = async () => {
        try {
            const respuesta = await api.get('/api/usuarios');
            setUsuarios(respuesta.data);
        } catch (error) {
            setMensaje(`❌ Error al cargar usuarios: ${error.response?.data || 'No se pudo conectar'}`);
            setTipoMensaje('error');
        }
    };

    const handleCambiarRol = async (id, role) => {
        setCargando(true);
        setMensaje('');
        try {
            await api.put(`/api/usuarios/${id}/role`, { role });
            setMensaje('✅ Rol actualizado correctamente');
            setTipoMensaje('success');
            cargarUsuarios();
        } catch (error) {
            setMensaje(`❌ Error: ${error.response?.data || 'No se pudo actualizar el rol'}`);
            setTipoMensaje('error');
        } finally {
            setCargando(false);
        }
    };

    const handleEliminar = async (id) => {
        try {
            await api.delete(`/api/usuarios/${id}`);
            setMensaje('✅ Usuario eliminado correctamente');
            setTipoMensaje('success');
            setConfirmarEliminar(null);
            cargarUsuarios();
        } catch (error) {
            setMensaje(`❌ Error: ${error.response?.data || 'No se pudo eliminar el usuario'}`);
            setTipoMensaje('error');
            setConfirmarEliminar(null);
        }
    };

    return (
        <main className="employees-page">
            <header className="employees-header">
                <div>
                    <p className="eyebrow">Administracion</p>
                    <h1>Gestion de usuarios</h1>
                    <p>Administra los accesos de los usuarios y sus roles.</p>
                </div>
                <div className="header-actions">
                    <Link to="/dashboard" className="back-link">Volver al tablero</Link>
                </div>
            </header>

            {mensaje && (
                <div className={`crud-message ${tipoMensaje}`}>
                    {mensaje}
                </div>
            )}

            <section className="employees-table-card">
                <div className="table-toolbar">
                    <h2>Usuarios del sistema</h2>
                    <span>{usuarios.length} registros</span>
                </div>

                <div className="table-wrap">
                    <table className="employees-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Usuario</th>
                                <th>Rol</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            {usuarios.map((u) => (
                                <tr key={u.id}>
                                    <td>{u.id}</td>
                                    <td>{u.username} {user.username === u.username && ' (tú)'}</td>
                                    <td>
                                        <select
                                            className="role-select"
                                            value={u.role}
                                            disabled={user.username === u.username || cargando}
                                            onChange={(e) => handleCambiarRol(u.id, e.target.value)}
                                        >
                                            {ROLES.map((role) => (
                                                <option key={role} value={role}>{role}</option>
                                            ))}
                                        </select>
                                    </td>
                                    <td className="action-buttons">
                                        <button
                                            className="delete-btn"
                                            disabled={user.username === u.username}
                                            onClick={() => setConfirmarEliminar(u)}
                                        >
                                            🗑️ Eliminar
                                        </button>
                                    </td>
                                </tr>
                            ))}
                            {usuarios.length === 0 && (
                                <tr>
                                    <td colSpan="4" className="empty-state">No hay usuarios para mostrar.</td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </div>
            </section>

            {confirmarEliminar && (
                <div className="modal-overlay" onClick={() => setConfirmarEliminar(null)}>
                    <div className="modal-content confirm-modal" onClick={(e) => e.stopPropagation()}>
                        <div className="modal-header">
                            <h2>Confirmar eliminacion</h2>
                            <button className="modal-close" onClick={() => setConfirmarEliminar(null)}>×</button>
                        </div>
                        <p className="confirm-text">
                            ¿Estas seguro de que deseas eliminar al usuario <strong>{confirmarEliminar.username}</strong>?
                            Esta accion no se puede deshacer.
                        </p>
                        <div className="modal-actions">
                            <button className="cancel-btn" onClick={() => setConfirmarEliminar(null)}>
                                Cancelar
                            </button>
                            <button className="confirm-delete-btn" onClick={() => handleEliminar(confirmarEliminar.id)}>
                                Si, eliminar
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </main>
    );
};

export default Usuarios;
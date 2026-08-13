import React, { useState, useEffect } from 'react';
import api from '../api/axiosConfig';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './empleados.css';

const ROLES = ['ADMIN', 'SUPERVISOR', 'EMPLEADO'];

const Usuarios = () => {
    const { user } = useAuth();
    const [usuarios, setUsuarios] = useState([]);
    const [empleados, setEmpleados] = useState([]);
    const [mensaje, setMensaje] = useState('');
    const [tipoMensaje, setTipoMensaje] = useState('');
    const [cargando, setCargando] = useState(false);
    const [cargandoLista, setCargandoLista] = useState(true);
    const [confirmarEliminar, setConfirmarEliminar] = useState(null);

    // Modal de creación
    const [modalCrear, setModalCrear] = useState(false);
    const [formCrear, setFormCrear] = useState({
        username: '',
        password: '',
        role: 'EMPLEADO',
        empleadoId: ''
    });

    // Modal de cambio de contraseña
    const [modalPassword, setModalPassword] = useState(null);
    const [nuevaPassword, setNuevaPassword] = useState('');

    useEffect(() => {
        cargarUsuarios();
        cargarEmpleados();
    }, []);

    const cargarUsuarios = async () => {
        setCargandoLista(true);
        try {
            const respuesta = await api.get('/api/usuarios');
            setUsuarios(respuesta.data);
        } catch (error) {
            setMensaje(`❌ Error al cargar usuarios: ${error.response?.data?.mensaje || 'No se pudo conectar'}`);
            setTipoMensaje('error');
        } finally {
            setCargandoLista(false);
        }
    };

    const cargarEmpleados = async () => {
        try {
            const respuesta = await api.get('/api/empleados/all');
            setEmpleados(respuesta.data);
        } catch (error) {
            console.error('Error al cargar empleados:', error);
        }
    };

    const handleCrearUsuario = async (e) => {
        e.preventDefault();
        setCargando(true);
        setMensaje('');

        try {
            const payload = {
                username: formCrear.username,
                password: formCrear.password,
                role: formCrear.role,
                empleadoId: formCrear.empleadoId ? Number(formCrear.empleadoId) : null
            };

            await api.post('/api/usuarios', payload);
            setMensaje('✅ Usuario creado correctamente');
            setTipoMensaje('success');
            setModalCrear(false);
            setFormCrear({ username: '', password: '', role: 'EMPLEADO', empleadoId: '' });
            cargarUsuarios();
        } catch (error) {
            setMensaje(`❌ Error: ${error.response?.data?.mensaje || 'No se pudo crear el usuario'}`);
            setTipoMensaje('error');
        } finally {
            setCargando(false);
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
            setMensaje(`❌ Error: ${error.response?.data?.mensaje || 'No se pudo actualizar el rol'}`);
            setTipoMensaje('error');
        } finally {
            setCargando(false);
        }
    };

    const handleCambiarPassword = async (e) => {
        e.preventDefault();
        setCargando(true);
        setMensaje('');

        try {
            await api.put(`/api/usuarios/${modalPassword.id}/password`, { password: nuevaPassword });
            setMensaje('✅ Contraseña actualizada correctamente');
            setTipoMensaje('success');
            setModalPassword(null);
            setNuevaPassword('');
        } catch (error) {
            setMensaje(`❌ Error: ${error.response?.data?.mensaje || 'No se pudo cambiar la contraseña'}`);
            setTipoMensaje('error');
        } finally {
            setCargando(false);
        }
    };

    const handleToggleActivo = async (usuario) => {
        setCargando(true);
        setMensaje('');

        try {
            await api.put(`/api/usuarios/${usuario.id}/activo`, { activo: !usuario.activo });
            setMensaje(usuario.activo
                ? `✅ Usuario "${usuario.username}" desactivado`
                : `✅ Usuario "${usuario.username}" activado`);
            setTipoMensaje('success');
            cargarUsuarios();
        } catch (error) {
            setMensaje(`❌ Error: ${error.response?.data?.mensaje || 'No se pudo cambiar el estado'}`);
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
            setMensaje(`❌ Error: ${error.response?.data?.mensaje || 'No se pudo eliminar el usuario'}`);
            setTipoMensaje('error');
            setConfirmarEliminar(null);
        }
    };

    const obtenerNombreEmpleado = (empleadoId) => {
        if (!empleadoId) return '—';
        const emp = empleados.find((e) => e.id === empleadoId);
        return emp ? `${emp.nombre} (${emp.documento})` : `ID ${empleadoId}`;
    };

    return (
        <main className="employees-page">
            <header className="employees-header">
                <div>
                    <p className="eyebrow">Administración</p>
                    <h1>Gestión de usuarios</h1>
                    <p>Administra los accesos de los usuarios, sus roles y estados.</p>
                </div>
                <div className="header-actions">
                    <button className="new-employee-btn" onClick={() => setModalCrear(true)}>
                        + Nuevo usuario
                    </button>
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

                {cargandoLista ? (
                    <div className="table-loading">
                        <div className="spinner"></div>
                        <p>Cargando usuarios...</p>
                    </div>
                ) : (
                    <div className="table-wrap">
                        <table className="employees-table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Usuario</th>
                                    <th>Empleado</th>
                                    <th>Rol</th>
                                    <th>Estado</th>
                                    <th>Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                {usuarios.map((u) => (
                                    <tr key={u.id} className={!u.activo ? 'row-inactive' : ''}>
                                        <td>{u.id}</td>
                                        <td>
                                            {u.username} {user.username === u.username && <span className="you-badge">(tú)</span>}
                                        </td>
                                        <td>{obtenerNombreEmpleado(u.empleadoId)}</td>
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
                                        <td>
                                            <span className={`status-badge ${u.activo ? 'active' : 'inactive'}`}>
                                                {u.activo ? 'Activo' : 'Inactivo'}
                                            </span>
                                        </td>
                                        <td className="action-buttons">
                                            <button
                                                className="edit-btn"
                                                disabled={cargando}
                                                onClick={() => {
                                                    setModalPassword(u);
                                                    setNuevaPassword('');
                                                }}
                                                title="Cambiar contraseña"
                                            >
                                                🔑 Contraseña
                                            </button>
                                            <button
                                                className={u.activo ? 'deactivate-btn' : 'activate-btn'}
                                                disabled={user.username === u.username || cargando}
                                                onClick={() => handleToggleActivo(u)}
                                                title={u.activo ? 'Desactivar usuario' : 'Activar usuario'}
                                            >
                                                {u.activo ? '⏸ Desactivar' : '▶ Activar'}
                                            </button>
                                            <button
                                                className="delete-btn"
                                                disabled={user.username === u.username || cargando}
                                                onClick={() => setConfirmarEliminar(u)}
                                            >
                                                🗑️ Eliminar
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                                {usuarios.length === 0 && (
                                    <tr>
                                        <td colSpan="6" className="empty-state">No hay usuarios para mostrar.</td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                )}
            </section>

            {/* Modal de creación de usuario */}
            {modalCrear && (
                <div className="modal-overlay" onClick={() => setModalCrear(false)}>
                    <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                        <div className="modal-header">
                            <h2>Nuevo usuario</h2>
                            <button className="modal-close" onClick={() => setModalCrear(false)}>×</button>
                        </div>

                        <form onSubmit={handleCrearUsuario} className="employee-form">
                            <label>
                                Nombre de usuario
                                <input
                                    type="text"
                                    value={formCrear.username}
                                    onChange={(e) => setFormCrear({ ...formCrear, username: e.target.value })}
                                    placeholder="Ej: jperez"
                                    required
                                    minLength={3}
                                />
                            </label>

                            <label>
                                Contraseña inicial
                                <input
                                    type="password"
                                    value={formCrear.password}
                                    onChange={(e) => setFormCrear({ ...formCrear, password: e.target.value })}
                                    placeholder="Mínimo 6 caracteres"
                                    required
                                    minLength={6}
                                />
                            </label>

                            <label>
                                Rol
                                <select
                                    className="role-select"
                                    value={formCrear.role}
                                    onChange={(e) => setFormCrear({ ...formCrear, role: e.target.value })}
                                >
                                    {ROLES.map((role) => (
                                        <option key={role} value={role}>{role}</option>
                                    ))}
                                </select>
                            </label>

                            <label>
                                Empleado asociado (opcional)
                                <select
                                    className="role-select"
                                    value={formCrear.empleadoId}
                                    onChange={(e) => setFormCrear({ ...formCrear, empleadoId: e.target.value })}
                                >
                                    <option value="">— Sin asociar —</option>
                                    {empleados.map((emp) => (
                                        <option key={emp.id} value={emp.id}>
                                            {emp.nombre} ({emp.documento})
                                        </option>
                                    ))}
                                </select>
                            </label>

                            <div className="modal-actions">
                                <button type="button" className="cancel-btn" onClick={() => setModalCrear(false)}>
                                    Cancelar
                                </button>
                                <button type="submit" className="save-btn" disabled={cargando}>
                                    {cargando ? 'Creando...' : 'Crear usuario'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {/* Modal de cambio de contraseña */}
            {modalPassword && (
                <div className="modal-overlay" onClick={() => setModalPassword(null)}>
                    <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                        <div className="modal-header">
                            <h2>Cambiar contraseña</h2>
                            <button className="modal-close" onClick={() => setModalPassword(null)}>×</button>
                        </div>

                        <form onSubmit={handleCambiarPassword} className="employee-form">
                            <p className="confirm-text">
                                Nueva contraseña para <strong>{modalPassword.username}</strong>
                            </p>

                            <label>
                                Nueva contraseña
                                <input
                                    type="password"
                                    value={nuevaPassword}
                                    onChange={(e) => setNuevaPassword(e.target.value)}
                                    placeholder="Mínimo 6 caracteres"
                                    required
                                    minLength={6}
                                />
                            </label>

                            <div className="modal-actions">
                                <button type="button" className="cancel-btn" onClick={() => setModalPassword(null)}>
                                    Cancelar
                                </button>
                                <button type="submit" className="save-btn" disabled={cargando}>
                                    {cargando ? 'Guardando...' : 'Actualizar contraseña'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {/* Modal de confirmación para eliminar */}
            {confirmarEliminar && (
                <div className="modal-overlay" onClick={() => setConfirmarEliminar(null)}>
                    <div className="modal-content confirm-modal" onClick={(e) => e.stopPropagation()}>
                        <div className="modal-header">
                            <h2>Confirmar eliminación</h2>
                            <button className="modal-close" onClick={() => setConfirmarEliminar(null)}>×</button>
                        </div>
                        <p className="confirm-text">
                            ¿Estás seguro de que deseas eliminar al usuario <strong>{confirmarEliminar.username}</strong>?
                            Esta acción no se puede deshacer.
                        </p>
                        <div className="modal-actions">
                            <button className="cancel-btn" onClick={() => setConfirmarEliminar(null)}>
                                Cancelar
                            </button>
                            <button className="confirm-delete-btn" onClick={() => handleEliminar(confirmarEliminar.id)}>
                                Sí, eliminar
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </main>
    );
};

export default Usuarios;
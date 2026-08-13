import React, { useState, useEffect } from 'react';
import api, { API_URL } from '../api/axiosConfig';
import { Link } from 'react-router-dom';
import './empleados.css';

const Empleados = () => {
    const [empleados, setEmpleados] = useState([]);
    const [termino, setTermino] = useState('');
    const [pagina, setPagina] = useState(0);
    const [totalPaginas, setTotalPaginas] = useState(0);
    const [totalElementos, setTotalElementos] = useState(0);
    const [modalAbierto, setModalAbierto] = useState(false);
    const [editando, setEditando] = useState(null); // null = crear, objeto = editar
    const [formData, setFormData] = useState({ nombre: '', documento: '', cargo: '' });
    const [mensaje, setMensaje] = useState('');
    const [tipoMensaje, setTipoMensaje] = useState(''); // 'success' | 'error'
    const [cargando, setCargando] = useState(false);
    const [confirmarEliminar, setConfirmarEliminar] = useState(null);

    useEffect(() => {
        cargarEmpleados();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [pagina]);

    const cargarEmpleados = async () => {
        try {
            const params = { page: pagina, size: 10 };
            if (termino.trim()) {
                params.termino = termino.trim();
            }
            const respuesta = await api.get('/api/empleados', { params });
            setEmpleados(respuesta.data.content || []);
            setTotalPaginas(respuesta.data.totalPages || 0);
            setTotalElementos(respuesta.data.totalElements || 0);
        } catch (error) {
            console.error('Error al conectar con Spring Boot:', error);
        }
    };

    const handleBuscar = (e) => {
        e.preventDefault();
        setPagina(0);
        cargarEmpleados();
    };

    const abrirModalCrear = () => {
        setEditando(null);
        setFormData({ nombre: '', documento: '', cargo: '' });
        setModalAbierto(true);
    };

    const abrirModalEditar = (empleado) => {
        setEditando(empleado);
        setFormData({
            nombre: empleado.nombre,
            documento: empleado.documento,
            cargo: empleado.cargo
        });
        setModalAbierto(true);
    };

    const cerrarModal = () => {
        setModalAbierto(false);
        setEditando(null);
        setFormData({ nombre: '', documento: '', cargo: '' });
    };

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setCargando(true);
        setMensaje('');

        try {
            if (editando) {
                // Editar empleado existente
                await api.put(`/api/empleados/${editando.id}`, formData);
                setMensaje('✅ Empleado actualizado correctamente');
            } else {
                // Crear nuevo empleado
                await api.post('/api/empleados', formData);
                setMensaje('✅ Empleado creado correctamente (QR generado)');
            }
            setTipoMensaje('success');
            cerrarModal();
            cargarEmpleados();
        } catch (error) {
            if (error.response?.status === 404) {
                setMensaje('⚠️ El empleado ya no existe en el sistema. Se recargará el listado.');
                cerrarModal();
                cargarEmpleados();
            } else {
                setMensaje(`❌ Error: ${error.response?.data || 'No se pudo guardar el empleado'}`);
            }
            setTipoMensaje('error');
        } finally {
            setCargando(false);
        }
    };

    const handleEliminar = async (id) => {
        try {
            await api.delete(`/api/empleados/${id}`);
            setMensaje('✅ Empleado eliminado correctamente');
            setTipoMensaje('success');
            setConfirmarEliminar(null);
            cargarEmpleados();
        } catch (error) {
            if (error.response?.status === 404) {
                setMensaje('⚠️ El empleado ya no existe en el sistema. Se recargará el listado.');
                cargarEmpleados();
            } else {
                setMensaje(`❌ Error: ${error.response?.data || 'No se pudo eliminar el empleado'}`);
            }
            setTipoMensaje('error');
            setConfirmarEliminar(null);
        }
    };

    return (
        <main className="employees-page">
            <header className="employees-header">
                <div>
                    <p className="eyebrow">Talento humano</p>
                    <h1>Gestion de empleados</h1>
                    <p>Consulta, crea, edita y elimina el personal registrado.</p>
                </div>
                <div className="header-actions">
                    <button className="new-employee-btn" onClick={abrirModalCrear}>
                        + Nuevo empleado
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
                    <h2>Listado general</h2>
                    <form className="search-bar" onSubmit={handleBuscar}>
                        <input
                            type="text"
                            placeholder="Buscar por nombre, documento o cargo..."
                            value={termino}
                            onChange={(e) => setTermino(e.target.value)}
                        />
                        <button type="submit" className="search-btn">Buscar</button>
                    </form>
                    <span>{totalElementos} registros</span>
                </div>

                <div className="table-wrap">
                    <table className="employees-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Nombre</th>
                                <th>Documento</th>
                                <th>Cargo</th>
                                <th>QR</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            {empleados.map((emp) => (
                                <tr key={emp.id}>
                                    <td>{emp.id}</td>
                                    <td>{emp.nombre}</td>
                                    <td>{emp.documento}</td>
                                    <td>{emp.cargo}</td>
                                    <td>
                                        <button 
                                            className="qr-view-btn"
                                            onClick={() => window.open(`${API_URL}/qr/empleado_${emp.id}.png`, '_blank')}
                                        >
                                            Ver QR
                                        </button>
                                    </td>
                                    <td className="action-buttons">
                                        <button 
                                            className="edit-btn"
                                            onClick={() => abrirModalEditar(emp)}
                                        >
                                            ✏️ Editar
                                        </button>
                                        <button 
                                            className="delete-btn"
                                            onClick={() => setConfirmarEliminar(emp)}
                                        >
                                            🗑️ Eliminar
                                        </button>
                                    </td>
                                </tr>
                            ))}
                            {empleados.length === 0 && (
                                <tr>
                                    <td colSpan="6" className="empty-state">No hay empleados para mostrar.</td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </div>

                {totalPaginas > 0 && (
                    <div className="pagination">
                        <button
                            className="page-btn"
                            disabled={pagina === 0}
                            onClick={() => setPagina((p) => p - 1)}
                        >
                            Anterior
                        </button>
                        <span className="page-info">Pagina {pagina + 1} de {totalPaginas}</span>
                        <button
                            className="page-btn"
                            disabled={pagina >= totalPaginas - 1}
                            onClick={() => setPagina((p) => p + 1)}
                        >
                            Siguiente
                        </button>
                    </div>
                )}
            </section>

            {/* Modal para crear/editar empleado */}
            {modalAbierto && (
                <div className="modal-overlay" onClick={cerrarModal}>
                    <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                        <div className="modal-header">
                            <h2>{editando ? 'Editar empleado' : 'Nuevo empleado'}</h2>
                            <button className="modal-close" onClick={cerrarModal}>×</button>
                        </div>

                        <form onSubmit={handleSubmit} className="employee-form">
                            <label>
                                Nombre
                                <input
                                    type="text"
                                    name="nombre"
                                    value={formData.nombre}
                                    onChange={handleChange}
                                    placeholder="Nombre completo"
                                    required
                                />
                            </label>

                            <label>
                                Documento
                                <input
                                    type="text"
                                    name="documento"
                                    value={formData.documento}
                                    onChange={handleChange}
                                    placeholder="Número de documento"
                                    required
                                />
                            </label>

                            <label>
                                Cargo
                                <input
                                    type="text"
                                    name="cargo"
                                    value={formData.cargo}
                                    onChange={handleChange}
                                    placeholder="Cargo del empleado"
                                    required
                                />
                            </label>

                            <div className="modal-actions">
                                <button type="button" className="cancel-btn" onClick={cerrarModal}>
                                    Cancelar
                                </button>
                                <button type="submit" className="save-btn" disabled={cargando}>
                                    {cargando ? 'Guardando...' : (editando ? 'Actualizar' : 'Crear')}
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
                            ¿Estás seguro de que deseas eliminar a <strong>{confirmarEliminar.nombre}</strong>?
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

export default Empleados;
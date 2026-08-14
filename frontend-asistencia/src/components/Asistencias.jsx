import React, { useState, useEffect } from 'react';
import api from '../api/axiosConfig';
import { Link } from 'react-router-dom';
import './asistencias.css';

const TAMANO_PAGINA = 20;

const Asistencias = () => {
    const [datos, setDatos] = useState([]);
    const [cargando, setCargando] = useState(true);
    const [mensaje, setMensaje] = useState('');
    const [tipoMensaje, setTipoMensaje] = useState(''); // 'success' | 'error'

    // Filtros
    const [termino, setTermino] = useState('');
    const [tipo, setTipo] = useState('');
    const [desde, setDesde] = useState('');
    const [hasta, setHasta] = useState('');

    // Paginación (client-side)
    const [pagina, setPagina] = useState(0);

    useEffect(() => {
        cargarAsistencias();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    const cargarAsistencias = async (filtros = {}) => {
        try {
            const params = {};
            if (filtros.termino?.trim()) {
                params.termino = filtros.termino.trim();
            }
            if (filtros.tipo) {
                params.tipo = filtros.tipo;
            }
            if (filtros.desde) {
                params.desde = `${filtros.desde}T00:00:00`;
            }
            if (filtros.hasta) {
                params.hasta = `${filtros.hasta}T23:59:59`;
            }
            const respuesta = await api.get('/api/asistencias/consulta', { params });
            setMensaje('');
            setDatos(respuesta.data || []);
            setPagina(0);
        } catch (error) {
            setMensaje(`❌ Error al cargar asistencias: ${error.response?.data?.mensaje || 'No se pudo conectar'}`);
            setTipoMensaje('error');
            setDatos([]);
        } finally {
            setCargando(false);
        }
    };

    const handleBuscar = (e) => {
        e.preventDefault();
        setCargando(true);
        cargarAsistencias({ termino, tipo, desde, hasta });
    };

    const limpiarFiltros = () => {
        setTermino('');
        setTipo('');
        setDesde('');
        setHasta('');
        setCargando(true);
        cargarAsistencias();
    };

    const formatFecha = (fechaHora) => {
        if (!fechaHora) return '';
        return new Date(fechaHora).toLocaleString('es-CO');
    };

    const totalPaginas = Math.max(1, Math.ceil(datos.length / TAMANO_PAGINA));
    const inicio = pagina * TAMANO_PAGINA;
    const paginaActual = datos.slice(inicio, inicio + TAMANO_PAGINA);

    return (
        <main className="employees-page">
            <header className="employees-header">
                <div>
                    <p className="eyebrow">Control de asistencia</p>
                    <h1>Consulta de asistencias</h1>
                    <p>Visualiza y filtra los registros de entrada y salida del personal.</p>
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
                <form className="asistencias-filtros" onSubmit={handleBuscar}>
                    <div className="filter-field filter-grow">
                        <label htmlFor="termino">Buscar</label>
                        <input
                            id="termino"
                            type="text"
                            placeholder="Nombre o documento del empleado..."
                            value={termino}
                            onChange={(e) => setTermino(e.target.value)}
                        />
                    </div>
                    <div className="filter-field">
                        <label htmlFor="tipo">Tipo</label>
                        <select id="tipo" value={tipo} onChange={(e) => setTipo(e.target.value)}>
                            <option value="">Todos</option>
                            <option value="ENTRADA">Entrada</option>
                            <option value="SALIDA">Salida</option>
                        </select>
                    </div>
                    <div className="filter-field">
                        <label htmlFor="desde">Desde</label>
                        <input
                            id="desde"
                            type="date"
                            value={desde}
                            onChange={(e) => setDesde(e.target.value)}
                        />
                    </div>
                    <div className="filter-field">
                        <label htmlFor="hasta">Hasta</label>
                        <input
                            id="hasta"
                            type="date"
                            value={hasta}
                            onChange={(e) => setHasta(e.target.value)}
                        />
                    </div>
                    <div className="filter-actions">
                        <button type="submit" className="search-btn">Buscar</button>
                        <button type="button" className="clear-btn" onClick={limpiarFiltros}>Limpiar</button>
                    </div>
                </form>
        

                <div className="table-toolbar">
                    <h2>Registros de asistencia</h2>
                    <span>{datos.length} registros</span>
                </div>

                {cargando ? (
                    <div className="table-loading">
                        <div className="spinner" aria-hidden="true"></div>
                        <p>Cargando asistencias...</p>
                    </div>
                ) : (
                    <>
                        <div className="table-wrap">
                            <table className="employees-table">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Empleado</th>
                                        <th>Documento</th>
                                        <th>Cargo</th>
                                        <th>Fecha y hora</th>
                                        <th>Tipo</th>
                                        <th>Origen</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {paginaActual.map((a) => (
                                        <tr key={a.id}>
                                            <td>{a.id}</td>
                                            <td>{a.nombreEmpleado || '—'}</td>
                                            <td>{a.documento || '—'}</td>
                                            <td>{a.cargo || '—'}</td>
                                            <td>{formatFecha(a.fechaHora)}</td>
                                            <td>
                                                <span className={`asistencia-badge ${(a.tipo || '').toLowerCase()}`}>
                                                    {a.tipo}
                                                </span>
                                            </td>
                                            <td>
                                                {a.demo ? (
                                                    <span className="demo-badge">Demo</span>
                                                ) : (
                                                    <span className="real-badge">Real</span>
                                                )}
                                            </td>
                                        </tr>
                                    ))}
                                    {paginaActual.length === 0 && (
                                        <tr>
                                            <td colSpan="7" className="empty-state">
                                                No hay registros de asistencia que coincidan con los filtros.
                                            </td>
                                        </tr>
                                    )}
                                </tbody>
                            </table>
                        </div>

                        {datos.length > 0 && (
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
                    </>
                )}
            </section>
        </main>
    );
};

export default Asistencias;

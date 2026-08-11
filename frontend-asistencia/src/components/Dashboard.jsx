import React, { useState, useEffect } from 'react';
import api from '../api/axiosConfig';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './dashboard.css';

const Dashboard = () => {
    const navigate = useNavigate();
    const { user, logout, hasRole } = useAuth();
    const esAdmin = hasRole('ADMIN');
    const gestionaEmpleados = hasRole(['ADMIN', 'SUPERVISOR']);
    const [stats, setStats] = useState({
        empleados: 0,
        admins: 0,
        supervisores: 0
    });
    const [asistencias, setAsistencias] = useState([]);

    useEffect(() => {
        const fetchDashboardData = async () => {
            try {
                const [statsRes, asistenciasRes] = await Promise.all([
                    api.get('/api/empleados/count-stats'),
                    api.get('/api/asistencias')
                ]);

                setStats({
                    empleados: statsRes.data.empleados || 0,
                    admins: statsRes.data.admins || 0,
                    supervisores: statsRes.data.supervisores || 0
                });

                // Mostrar solo las 5 más recientes
                setAsistencias(asistenciasRes.data.slice(0, 5));
            } catch (error) {
                console.error('Error al cargar datos del dashboard:', error);
            }
        };

        fetchDashboardData();
    }, []);

    const formatFecha = (fechaHora) => {
        if (!fechaHora) return '';
        return new Date(fechaHora).toLocaleString('es-CO');
    };

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    return (
        <div className="dashboard-shell">
            <aside className="sidebar">
                <div className="brand">
                    <div className="brand-icon">CA</div>
                    <div>
                        <h2>AsistenciaPro</h2>
                        <span>Panel administrativo</span>
                    </div>
                </div>

                <nav className="side-nav" aria-label="Navegacion principal">
                    <li>
                        <Link to="/dashboard" className="active">
                            <span>Inicio</span>
                        </Link>
                    </li>
                    {gestionaEmpleados && (
                        <li>
                            <Link to="/empleados">
                                <span>Empleados</span>
                            </Link>
                        </li>
                    )}
                    {esAdmin && (
                        <li>
                            <Link to="/usuarios">
                                <span>Usuarios</span>
                            </Link>
                        </li>
                    )}
                </nav>

                <div className="user-info">
                    <span className="user-role">{user.role}</span>
                    <span className="user-name">{user.username}</span>
                </div>

                <button className="logout-btn" onClick={handleLogout}>
                    Cerrar sesion
                </button>
            </aside>

            <main className="main-content">
                <header className="page-header">
                    <div>
                        <p className="eyebrow">Resumen general</p>
                        <h1>Control de asistencia</h1>
                        <p>Monitorea empleados, permisos y accesos desde un tablero claro y centralizado.</p>
                    </div>
                    <button 
                        className="secondary-button"
                        onClick={() => navigate('/scanner')}
                    >
                        Abrir escaner QR
                    </button>
                </header>

                <section className="stats-grid" aria-label="Indicadores principales">
                    <article className="stat-card">
                        <span className="stat-label">Total empleados</span>
                        <strong>{stats.empleados}</strong>
                        <p>Personas registradas en el sistema.</p>
                    </article>
                    <article className="stat-card">
                        <span className="stat-label">Administradores</span>
                        <strong>{stats.admins}</strong>
                        <p>Usuarios con permisos de gestion.</p>
                    </article>
                    <article className="stat-card">
                        <span className="stat-label">Supervisores</span>
                        <strong>{stats.supervisores}</strong>
                        <p>Responsables de seguimiento operativo.</p>
                    </article>
                </section>

                <section className="recent-assists">
                    <div className="assists-header">
                        <h2>Últimas asistencias</h2>
                        <span>{asistencias.length} registros recientes</span>
                    </div>
                    {asistencias.length > 0 ? (
                        <table className="assists-table">
                            <thead>
                                <tr>
                                    <th>Empleado ID</th>
                                    <th>Fecha y hora</th>
                                    <th>Tipo</th>
                                </tr>
                            </thead>
                            <tbody>
                                {asistencias.map((a) => (
                                    <tr key={a.id}>
                                        <td>#{a.empleadoId}</td>
                                        <td>{formatFecha(a.fechaHora)}</td>
                                        <td>
                                            <span className={`tipo-badge ${a.tipo.toLowerCase()}`}>
                                                {a.tipo}
                                            </span>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    ) : (
                        <p className="empty-state">No hay asistencias registradas aún.</p>
                    )}
                </section>

                <section className="work-panel">
                    <div>
                        <p className="eyebrow">Proximo modulo</p>
                        <h2>Registro por codigo QR</h2>
                        <p>El espacio queda preparado para activar el escaner y consultar registros recientes sin saturar el tablero.</p>
                    </div>
                    {gestionaEmpleados && <Link to="/empleados" className="text-link">Ver empleados</Link>}
                </section>
            </main>
        </div>
    );
};

export default Dashboard;
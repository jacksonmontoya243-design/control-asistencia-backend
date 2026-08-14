import React, { useState, useEffect, useRef } from 'react';
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
    const statRefs = useRef({});

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

    // Contadores animados con GSAP cuando los datos llegan
    useEffect(() => {
        if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
            return undefined;
        }

        let tweens = [];

        import('gsap').then(({ gsap }) => {
            const targets = [
                { el: statRefs.current.empleados, value: stats.empleados },
                { el: statRefs.current.admins, value: stats.admins },
                { el: statRefs.current.supervisores, value: stats.supervisores },
            ];

            targets.forEach((t) => {
                if (!t.el) return;
                const obj = { val: 0 };
                tweens.push(gsap.to(obj, {
                    val: t.value,
                    duration: 1.2,
                    ease: 'power2.out',
                    onUpdate: () => {
                        t.el.textContent = Math.round(obj.val);
                    },
                }));
            });
        });

        return () => {
            tweens.forEach((tween) => tween && tween.kill());
        };
    }, [stats]);

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
                <div className="sidebar-top">
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
                                <span className="nav-ico" aria-hidden="true">
                                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="3" y="3" width="7" height="9" rx="1"/><rect x="14" y="3" width="7" height="5" rx="1"/><rect x="14" y="12" width="7" height="9" rx="1"/><rect x="3" y="16" width="7" height="5" rx="1"/></svg>
                                </span>
                                <span>Inicio</span>
                            </Link>
                        </li>
                        {gestionaEmpleados && (
                            <li>
                                <Link to="/empleados">
                                    <span className="nav-ico" aria-hidden="true">
                                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                                    </span>
                                    <span>Empleados</span>
                                </Link>
                            </li>
                        )}
                        {gestionaEmpleados && (
                            <li>
                                <Link to="/asistencias">
                                    <span className="nav-ico" aria-hidden="true">
                                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/><path d="m9 16 2 2 4-4"/></svg>
                                    </span>
                                    <span>Asistencias</span>
                                </Link>
                            </li>
                        )}
                        {esAdmin && (
                            <li>
                                <Link to="/usuarios">
                                    <span className="nav-ico" aria-hidden="true">
                                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                                    </span>
                                    <span>Usuarios</span>
                                </Link>
                            </li>
                        )}
                    </nav>
                </div>

                <div className="sidebar-bottom">
                    <div className="user-info">
                        <span className="user-avatar" aria-hidden="true">
                            {(user.username || 'U').charAt(0).toUpperCase()}
                        </span>
                        <div className="user-meta">
                            <span className="user-role">{user.role}</span>
                            <span className="user-name">{user.username}</span>
                        </div>
                    </div>

                    <button className="logout-btn" onClick={handleLogout}>
                        Cerrar sesion
                    </button>
                </div>
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
                        <div className="stat-card-top">
                            <span className="stat-label">Total empleados</span>
                            <span className="stat-ico" aria-hidden="true">
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                            </span>
                        </div>
                        <strong ref={(el) => (statRefs.current.empleados = el)}>{stats.empleados}</strong>
                        <p>Personas registradas en el sistema.</p>
                    </article>
                    <article className="stat-card">
                        <div className="stat-card-top">
                            <span className="stat-label">Administradores</span>
                            <span className="stat-ico" aria-hidden="true">
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
                            </span>
                        </div>
                        <strong ref={(el) => (statRefs.current.admins = el)}>{stats.admins}</strong>
                        <p>Usuarios con permisos de gestion.</p>
                    </article>
                    <article className="stat-card">
                        <div className="stat-card-top">
                            <span className="stat-label">Supervisores</span>
                            <span className="stat-ico" aria-hidden="true">
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="10"/><circle cx="12" cy="12" r="6"/><circle cx="12" cy="12" r="2"/></svg>
                            </span>
                        </div>
                        <strong ref={(el) => (statRefs.current.supervisores = el)}>{stats.supervisores}</strong>
                        <p>Responsables de seguimiento operativo.</p>
                    </article>
                </section>

                <section className="recent-assists">
                    <div className="assists-header">
                        <h2>Últimas asistencias</h2>
                        <span>{asistencias.length} registros recientes</span>
                    </div>
                    {asistencias.length > 0 ? (
                        <div className="assists-table-wrap">
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
                        </div>
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
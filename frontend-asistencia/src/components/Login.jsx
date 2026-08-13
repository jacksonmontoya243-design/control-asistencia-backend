import React, { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './login.css';

const Login = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [mensaje, setMensaje] = useState('');
    const [cargando, setCargando] = useState(false);

    const { login } = useAuth();
    const navigate = useNavigate();
    const summaryRef = useRef(null);
    const parallaxTl = useRef(null);

    useEffect(() => {
        if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
            return undefined;
        }

        const el = summaryRef.current;
        if (!el) return undefined;

        let gsapMod;
        let isDesktop = false;
        const onMove = (e) => {
            if (!gsapMod || !parallaxTl.current) return;
            const x = (e.clientX / window.innerWidth - 0.5) * 2;
            parallaxTl.current.progress((x + 1) / 2);
        };

        import('gsap').then(({ gsap }) => {
            gsapMod = gsap;
            isDesktop = window.matchMedia('(min-width: 861px)').matches;
            if (!isDesktop) return;

            const layer = el.querySelector('[data-parallax-layer]');
            if (!layer) return;

            parallaxTl.current = gsap.timeline({ paused: true });
            parallaxTl.current.to(layer, {
                xPercent: 2.5,
                duration: 1.4,
                ease: 'none',
            }, 0);

            window.addEventListener('mousemove', onMove);
        });

        return () => {
            window.removeEventListener('mousemove', onMove);
            if (parallaxTl.current) {
                parallaxTl.current.kill();
                parallaxTl.current = null;
            }
        };
    }, []);

    const handleLogin = async (e) => {
        e.preventDefault();

        // Evitar múltiples envíos mientras se autentica
        if (cargando) return;

        setMensaje('');
        setCargando(true);

        try {
            await login(username, password);

            console.log('Login correcto');

            navigate('/dashboard');
        } catch (error) {
            console.error('Error completo de login:', error);
            console.error('Respuesta del servidor:', error.response?.data);
            console.error('Código:', error.response?.status);

            if (error.response) {
                // El servidor respondió con un error
                const status = error.response?.status;
                const data = error.response?.data;

                if (status === 401) {
                    setMensaje('Credenciales incorrectas. Verifica tu usuario y contraseña.');
                } else if (data?.mensaje) {
                    setMensaje(data.mensaje);
                } else {
                    setMensaje(`Error del servidor (${status}). Intenta nuevamente.`);
                }
            } else if (error.code === 'ECONNABORTED') {
                setMensaje('La conexión tardó demasiado. Verifica tu conexión a internet e intenta nuevamente.');
            } else if (!error.response) {
                // Sin respuesta del servidor (error de red)
                setMensaje('No se pudo conectar con el servidor. Verifica tu conexión a internet e intenta nuevamente.');
            } else {
                setMensaje('Ocurrió un error inesperado. Intenta nuevamente.');
            }
        } finally {
            setCargando(false);
        }
    };

    return (
        <div className="login-page">
            <div className="login-orb o1" aria-hidden="true" />
            <div className="login-orb o2" aria-hidden="true" />
            <div className="login-orb o3" aria-hidden="true" />

            <section className="login-panel" aria-label="Inicio de sesion">
                <div className="login-card">
                    <div className="login-card-head">
                        <p className="eyebrow">
                            Control de asistencia
                        </p>

                        <h1>Ingreso seguro</h1>

                        <p className="login-copy">
                            Accede al panel administrativo para consultar empleados,
                            roles y registros de asistencia.
                        </p>
                    </div>

                    <form
                        className="login-form"
                        onSubmit={handleLogin}
                    >
                        <label>
                            Usuario

                            <span className="input-wrap">
                                <input
                                    type="text"
                                    placeholder="Ingresa tu usuario"
                                    value={username}
                                    onChange={(e) =>
                                        setUsername(e.target.value)
                                    }
                                    autoComplete="username"
                                    required
                                    disabled={cargando}
                                />
                                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
                                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                                    <circle cx="12" cy="7" r="4" />
                                </svg>
                            </span>
                        </label>

                        <label>
                            Contrasena

                            <span className="input-wrap">
                                <input
                                    type="password"
                                    placeholder="Ingresa tu contrasena"
                                    value={password}
                                    onChange={(e) =>
                                        setPassword(e.target.value)
                                    }
                                    autoComplete="current-password"
                                    required
                                    disabled={cargando}
                                />
                                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
                                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                                    <path d="M7 11V7a5 5 0 0 1 10 0v4" />
                                </svg>
                            </span>
                        </label>

                    {mensaje && (
                        <p className="login-message">
                            {mensaje}
                        </p>
                    )}

                    <button
                        type="submit"
                        className="primary-button"
                        disabled={cargando}
                    >
                        {cargando
                            ? 'Validando...'
                            : 'Ingresar'}
                    </button>
                </form>

                    <div className="login-help">
                        <p>
                            ¿No tienes credenciales de acceso?{' '}
                            <strong>Contacta al administrador de tu organización.</strong>
                        </p>
                    </div>
                </div>
            </section>

            <aside
                ref={summaryRef}
                className="login-summary"
                data-parallax
                aria-label="Resumen del sistema"
            >
                <p className="eyebrow" data-parallax-layer>
                    Panel empresarial
                </p>

                <h2 data-parallax-layer>
                    Gestion centralizada para equipos operativos.
                </h2>

                <div className="summary-list" data-parallax-layer>
                    <div>
                        <span>01</span>
                        Consulta rapida de empleados activos.
                    </div>

                    <div>
                        <span>02</span>
                        Seguimiento de roles administrativos.
                    </div>

                    <div>
                        <span>03</span>
                        Base preparada para registro por QR.
                    </div>
                </div>
            </aside>
        </div>
    );
};

export default Login;
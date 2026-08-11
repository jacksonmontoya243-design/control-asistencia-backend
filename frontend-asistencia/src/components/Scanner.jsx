import React, { useEffect, useState, useRef } from 'react';
import { Html5QrcodeScanner } from 'html5-qrcode';
import { useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig';
import './Scanner.css';

const Scanner = () => {
    const navigate = useNavigate();
    const [mensaje, setMensaje] = useState('');
    const [tipoMensaje, setTipoMensaje] = useState(''); // 'success' | 'error'
    const [proximoTipo, setProximoTipo] = useState('ENTRADA'); // Solo para mostrar en UI
    const ultimoTipoRef = useRef('ENTRADA'); // Alterna ENTRADA/SALIDA usando ref (sin reiniciar scanner)

    useEffect(() => {
        const scanner = new Html5QrcodeScanner("reader", {
            fps: 10,
            qrbox: { width: 250, height: 250 },
        });

        const onScanSuccess = async (decodedText) => {
            scanner.clear();

            // El QR contiene una URL tipo: http://.../empleado/{id}
            // Extraemos el ID del empleado de la URL
            const match = decodedText.match(/\/empleado\/(\d+)/);
            if (!match) {
                setMensaje('QR inválido: no contiene un ID de empleado válido');
                setTipoMensaje('error');
                return;
            }

            const empleadoId = parseInt(match[1], 10);

            try {
                // Alternar entre ENTRADA y SALIDA usando la ref (sin reiniciar el scanner)
                const tipo = ultimoTipoRef.current;
                ultimoTipoRef.current = tipo === 'ENTRADA' ? 'SALIDA' : 'ENTRADA';
                setProximoTipo(ultimoTipoRef.current);

                const response = await api.post('/api/asistencias', {
                    empleadoId,
                    tipo
                });

                const fecha = new Date(response.data.fechaHora).toLocaleString('es-CO');
                setMensaje(`✅ Asistencia de ${tipo} registrada para empleado #${empleadoId} a las ${fecha}`);
                setTipoMensaje('success');
            } catch (error) {
                setMensaje(`❌ Error: ${error.response?.data || 'No se pudo registrar la asistencia'}`);
                setTipoMensaje('error');
            }
        };

        const onScanFailure = (error) => {
            // console.warn(error);
        };

        scanner.render(onScanSuccess, onScanFailure);

        return () => {
            scanner.clear();
        };
    }, [navigate]); // Solo depende de navigate, no de ultimoTipo (evita reinicio del scanner)

    return (
        <div className="scanner-container">
            <div className="scanner-card">
                <header className="scanner-header">
                    <button className="back-link" onClick={() => navigate('/dashboard')}>
                        ← Volver al Dashboard
                    </button>
                    <h1>Escanear Código QR</h1>
                    <p>Coloque el código QR del empleado frente a la cámara</p>
                </header>
                
                <div id="reader"></div>

                {mensaje && (
                    <div className={`scanner-message ${tipoMensaje}`}>
                        {mensaje}
                    </div>
                )}
                
                <div className="scanner-footer">
                    <p>Asegúrese de tener buena iluminación</p>
                    <p className="scanner-tipo">Próximo registro: <strong>{proximoTipo}</strong></p>
                </div>
            </div>
        </div>
    );
};

export default Scanner;
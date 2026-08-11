package com.example.controlasistenciabackend.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * SERVICIO: QrGeneratorService
 * Componente especializado en la generación algorítmica y exportación física
 * de códigos QR utilizando la librería de código abierto ZXing (Zebra Crossing).
 */
@Service
public class QrGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(QrGeneratorService.class);

    /**
     * Genera un código QR en formato PNG a partir de una cadena de texto y lo guarda en el servidor.
     *
     * @param texto        Contenido codificado que tendrá el QR (ej: el documento de identidad del empleado).
     * @param nombreArchivo Nombre con el que se guardará el archivo físico PNG resultante.
     */
    public void generarQR(String texto, String nombreArchivo) {

        try {
            // 1. Instanciar el escritor de códigos QR de la librería ZXing
            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            // 2. Codificar el texto en una matriz de bits (BitMatrix) de 300x300 píxeles
            BitMatrix bitMatrix = qrCodeWriter.encode(
                    texto,
                    BarcodeFormat.QR_CODE,
                    300,
                    300
            );

            // 3. Crear un búfer de imagen en memoria (RGB) con las mismas dimensiones (300x300)
            BufferedImage image = new BufferedImage(
                    300,
                    300,
                    BufferedImage.TYPE_INT_RGB
            );

            // 4. ALGORITMO DE DIBUJO: Recorrer la matriz bidimensional píxel por píxel
            for (int x = 0; x < 300; x++) {
                for (int y = 0; y < 300; y++) {

                    // Si el bit en la posición (x, y) es verdadero, pinta Negro (0xFF000000)
                    // De lo contrario, pinta Blanco (0xFFFFFFFF) en formato hexadecimal ARGB
                    image.setRGB(
                            x,
                            y,
                            bitMatrix.get(x, y)
                                    ? 0xFF000000
                                    : 0xFFFFFFFF
                    );
                }
            }

            // 5. RUTA DE DESTINO: Se define el almacenamiento
            // Usamos una ruta absoluta o relativa al directorio de ejecución
            String directorioBase = "static/qr/";
            File directorio = new File(directorioBase);
            if (!directorio.exists()) {
                directorio.mkdirs();
            }

            String ruta = directorioBase + nombreArchivo + ".png";

            // 6. PERSISTENCIA FÍSICA: Escribir y guardar el archivo PNG
            ImageIO.write(
                    image,
                    "png",
                    new File(ruta)
            );
            log.info("QR generado correctamente: {}", ruta);

        } catch (Exception e) {
            // Manejo de excepciones en caso de fallos en la codificación o en la escritura del archivo
            log.error("Error al generar el QR '{}'", nombreArchivo, e);
        }
    }
}
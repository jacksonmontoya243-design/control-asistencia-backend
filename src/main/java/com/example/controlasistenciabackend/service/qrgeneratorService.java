package com.example.controlasistenciabackend.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

@Service
public class qrgeneratorService {

    public void generarQR(String texto, String nombreArchivo) {

        try {

            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            BitMatrix bitMatrix = qrCodeWriter.encode(
                    texto,
                    BarcodeFormat.QR_CODE,
                    300,
                    300
            );

            BufferedImage image = new BufferedImage(
                    300,
                    300,
                    BufferedImage.TYPE_INT_RGB
            );

            for (int x = 0; x < 300; x++) {

                for (int y = 0; y < 300; y++) {

                    image.setRGB(
                            x,
                            y,
                            bitMatrix.get(x, y)
                                    ? 0xFF000000
                                    : 0xFFFFFFFF
                    );
                }
            }

            String ruta =
                    "src/main/resources/static/qr/"
                            + nombreArchivo
                            + ".png";

            ImageIO.write(
                    image,
                    "png",
                    new File(ruta)
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
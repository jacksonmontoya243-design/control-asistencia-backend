package com.example.controlasistenciabackend;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba de integración del ResourceHandler de códigos QR.
 * <p>
 * Verifica el problema corregido: que {@link WebConfig} sirve los PNG generados por
 * {@link com.example.controlasistenciabackend.service.QrGeneratorService} desde
 * {@code file:static/qr/} a través de la ruta pública {@code /qr/**}.
 */
@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc(addFilters = false)
class QrResourceHandlerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    static void prepararRecurso() throws Exception {
        File dir = new File("static/qr");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // PNG real mínimo para comprobar que se sirve con el Content-Type correcto
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        ImageIO.write(image, "png", new File(dir, "empleado_test.png"));
    }

    @AfterAll
    static void limpiarRecurso() {
        new File("static/qr/empleado_test.png").delete();
        new File("static/qr").delete();
    }

    @Test
    void qrExistente_seSirveCorrectamente() throws Exception {
        mockMvc.perform(get("/qr/empleado_test.png"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/png"));
    }

    @Test
    void qrInexistente_devuelve404() throws Exception {
        mockMvc.perform(get("/qr/no_existe.png"))
                .andExpect(status().isNotFound());
    }
}

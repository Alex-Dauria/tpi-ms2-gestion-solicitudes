package ar.edu.utn.tupad.solicitudes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ar.edu.utn.tupad.solicitudes.datos.BaseDeDatos;

// Pruebas de caracterización de la zona C (consultas y persistencia), sobre el código de estado-inicial.
@SpringBootTest
@AutoConfigureMockMvc
class SolicitudZonaCTest {

    @Autowired
    private MockMvc mockMvc;

    private static final Path ARCHIVO_DATOS = Path.of("datos", "solicitudes.json");
    private static byte[] contenidoOriginal;
    private static boolean archivoExistia;

    @BeforeAll
    static void guardarDatosOriginales() throws IOException {
        archivoExistia = Files.exists(ARCHIVO_DATOS);
        if (archivoExistia) {
            contenidoOriginal = Files.readAllBytes(ARCHIVO_DATOS);
        }
    }

    @BeforeEach
    void prepararDatos() throws IOException {
        restaurarArchivo();
        BaseDeDatos.cargarDatosDePrueba();
    }

    @AfterEach
    void limpiarDespuesDeCadaPrueba() throws IOException {
        restaurarArchivo();
    }

    @AfterAll
    static void restaurarDatosOriginales() throws IOException {
        restaurarArchivo();
    }

    private static void restaurarArchivo() throws IOException {
        if (archivoExistia) {
            Files.createDirectories(ARCHIVO_DATOS.getParent());
            Files.write(ARCHIVO_DATOS, contenidoOriginal);
        } else {
            Files.deleteIfExists(ARCHIVO_DATOS);
        }
    }

    // Lista sin filtros devuelve las 5 solicitudes de prueba.
    @Test
    void listarSinFiltrosDevuelveTodas() throws Exception {
        mockMvc.perform(get("/solicitudes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));
    }

    // Filtrar por estado PENDIENTE devuelve las solicitudes 5 y 1.
    @Test
    void filtrarPorEstadoPendiente() throws Exception {
        mockMvc.perform(get("/solicitudes").param("estado", "PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(5))
                .andExpect(jsonPath("$[1].id").value(1));
    }

    // Filtrar por tipo CERTIFICADO devuelve la solicitud 1.
    @Test
    void filtrarPorTipoCertificado() throws Exception {
        mockMvc.perform(get("/solicitudes").param("tipo", "CERTIFICADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    // Filtrar por prioridad ALTA devuelve las solicitudes 3 y 5.
    @Test
    void filtrarPorPrioridadAlta() throws Exception {
        mockMvc.perform(get("/solicitudes").param("prioridad", "ALTA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[1].id").value(5));
    }

    // Filtrar por legajo devuelve las solicitudes de ese estudiante.
    @Test
    void filtrarPorLegajo() throws Exception {
        mockMvc.perform(get("/solicitudes").param("legajo", "10234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    // Filtrar por responsable devuelve las solicitudes asignadas a ese responsable.
    @Test
    void filtrarPorResponsable() throws Exception {
        mockMvc.perform(get("/solicitudes").param("responsableId", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(2));
    }

    // Filtrar vencidas devuelve las solicitudes abiertas vencidas.
    @Test
    void filtrarVencidas() throws Exception {
        mockMvc.perform(get("/solicitudes").param("vencidas", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    // Combinación de filtros (estado + prioridad).
    @Test
    void filtrarPorEstadoYPrioridad() throws Exception {
        mockMvc.perform(get("/solicitudes").param("estado", "PENDIENTE").param("prioridad", "ALTA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(5));
    }

    // Filtro sin resultados devuelve lista vacía.
    @Test
    void filtrarSinResultados() throws Exception {
        mockMvc.perform(get("/solicitudes").param("estado", "CERRADA").param("tipo", "CERTIFICADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // El reporte resumen tiene la estructura y los totales esperados.
    @Test
    void resumenTieneEstructuraYTotales() throws Exception {
        mockMvc.perform(get("/reportes/resumen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(5))
                .andExpect(jsonPath("$.vencidas").value(3))
                .andExpect(jsonPath("$.porEstado.PENDIENTE").value(2))
                .andExpect(jsonPath("$.porEstado.EN_REVISION").value(1))
                .andExpect(jsonPath("$.porEstado.RESUELTA").value(1))
                .andExpect(jsonPath("$.porEstado.RECHAZADA").value(1))
                .andExpect(jsonPath("$.porEstado.CERRADA").value(0))
                .andExpect(jsonPath("$.porTipo.CERTIFICADO").value(1))
                .andExpect(jsonPath("$.porPrioridad.ALTA").value(2))
                .andExpect(jsonPath("$.porPrioridad.MEDIA").value(2))
                .andExpect(jsonPath("$.porPrioridad.BAJA").value(1));
    }

    // El catálogo de estudiantes devuelve los 4 estudiantes de prueba.
    @Test
    void catalogoEstudiantes() throws Exception {
        mockMvc.perform(get("/estudiantes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].legajo").value("10234"));
    }

    // El catálogo de responsables devuelve los 4 responsables de prueba.
    @Test
    void catalogoResponsables() throws Exception {
        mockMvc.perform(get("/responsables"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].activo").value(true));
    }
}
package ar.edu.utn.tupad.solicitudes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ar.edu.utn.tupad.solicitudes.datos.BaseDeDatos;
import ar.edu.utn.tupad.solicitudes.modelo.Solicitud;

// Pruebas de caracterización de la zona C (consultas y persistencia), sobre el código de estado-inicial.
@SpringBootTest
@AutoConfigureMockMvc
class SolicitudZonaCTest {

    // Herramienta para simular pedidos HTTP en las pruebas.
    @Autowired
    private MockMvc mockMvc;

    // Archivo real que usa la persistencia; se restaura para no pisar los datos locales.
    private static final Path ARCHIVO_DATOS = Path.of("datos", "solicitudes.json");
    private static byte[] contenidoOriginal;
    private static boolean archivoExistia;

    // Guarda el archivo de datos original antes de ejecutar las pruebas.
    @BeforeAll
    static void guardarDatosOriginales() throws IOException {
        archivoExistia = Files.exists(ARCHIVO_DATOS);
        if (archivoExistia) {
            contenidoOriginal = Files.readAllBytes(ARCHIVO_DATOS);
        }
    }

    // Antes de cada prueba restaura el archivo y carga los datos de prueba iniciales.
    @BeforeEach
    void prepararDatos() throws IOException {
        restaurarArchivo();
        BaseDeDatos.cargarDatosDePrueba();
    }

    // Después de cada prueba restaura el archivo original.
    @AfterEach
    void limpiarDespuesDeCadaPrueba() throws IOException {
        restaurarArchivo();
    }

    // Al finalizar todas las pruebas restaura el archivo original.
    @AfterAll
    static void restaurarDatosOriginales() throws IOException {
        restaurarArchivo();
    }

    // Restaura el archivo de datos a su estado original.
    private static void restaurarArchivo() throws IOException {
        if (archivoExistia) {
            Files.createDirectories(ARCHIVO_DATOS.getParent());
            Files.write(ARCHIVO_DATOS, contenidoOriginal);
        } else {
            Files.deleteIfExists(ARCHIVO_DATOS);
        }
    }

    // Lista sin filtros devuelve las 5 solicitudes de prueba.
    @DisplayName("CP-C01 – Listar sin filtros")
    @Test
    void listarSinFiltrosDevuelveTodas() throws Exception {
        mockMvc.perform(get("/solicitudes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));
    }

    // Filtrar por estado PENDIENTE devuelve las solicitudes 5 y 1.
    @DisplayName("CP-C02 – Filtrar por estado")
    @Test
    void filtrarPorEstadoPendiente() throws Exception {
        mockMvc.perform(get("/solicitudes").param("estado", "PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(5))
                .andExpect(jsonPath("$[1].id").value(1));
    }

    // Filtrar por tipo CERTIFICADO devuelve la solicitud 1.
    @DisplayName("CP-C03 – Filtrar por tipo")
    @Test
    void filtrarPorTipoCertificado() throws Exception {
        mockMvc.perform(get("/solicitudes").param("tipo", "CERTIFICADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    // Filtrar por prioridad ALTA devuelve las solicitudes 3 y 5.
    @DisplayName("CP-C04 – Filtrar por prioridad")
    @Test
    void filtrarPorPrioridadAlta() throws Exception {
        mockMvc.perform(get("/solicitudes").param("prioridad", "ALTA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[1].id").value(5));
    }

    // Filtrar por legajo devuelve las solicitudes de ese estudiante.
    @DisplayName("CP-C05 – Filtrar por legajo")
    @Test
    void filtrarPorLegajo() throws Exception {
        mockMvc.perform(get("/solicitudes").param("legajo", "10234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(5))
                .andExpect(jsonPath("$[1].id").value(1));
    }

    // Filtrar por responsable devuelve las solicitudes asignadas a ese responsable.
    @DisplayName("CP-C06 – Filtrar por responsable")
    @Test
    void filtrarPorResponsable() throws Exception {
        mockMvc.perform(get("/solicitudes").param("responsableId", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(2));
    }

    // Filtrar vencidas devuelve las solicitudes abiertas vencidas.
    @DisplayName("CP-C07 – Filtrar vencidas")
    @Test
    void filtrarVencidas() throws Exception {
        mockMvc.perform(get("/solicitudes").param("vencidas", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    // Combinación de filtros (estado + prioridad).
    @DisplayName("CP-C08 – Filtros combinados")
    @Test
    void filtrarPorEstadoYPrioridad() throws Exception {
        mockMvc.perform(get("/solicitudes").param("estado", "PENDIENTE").param("prioridad", "ALTA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(5));
    }

    // Filtro sin resultados devuelve lista vacía.
    @DisplayName("CP-C09 – Filtro sin resultados")
    @Test
    void filtrarSinResultados() throws Exception {
        mockMvc.perform(get("/solicitudes").param("estado", "CERRADA").param("tipo", "CERTIFICADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // El reporte resumen tiene la estructura y los totales esperados.
    @DisplayName("CP-C10 – Reporte resumen")
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
                .andExpect(jsonPath("$.porPrioridad.BAJA").value(1))
                .andExpect(jsonPath("$.abiertasPorResponsable['Carla Ibáñez (Coordinación de Carrera)']").value(1))
                .andExpect(jsonPath("$.porTipo.OTRO").value(0));
    }

    // El catálogo de estudiantes devuelve los 4 estudiantes de prueba.
    @DisplayName("CP-C11 – Catálogo estudiantes")
    @Test
    void catalogoEstudiantes() throws Exception {
        mockMvc.perform(get("/estudiantes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].legajo").value("10234"));
    }

    // El catálogo de responsables devuelve los 4 responsables de prueba.
    @DisplayName("CP-C12 – Catálogo responsables")
    @Test
    void catalogoResponsables() throws Exception {
        mockMvc.perform(get("/responsables"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].activo").value(true));
    }

    // El listado sin filtros devuelve los ids en el orden: 3, 5, 2, 4, 1.
    @DisplayName("CP-C13 – Orden del listado")
    @Test
    void listadoSinFiltrosOrdenPorPrioridadEId() throws Exception {
        mockMvc.perform(get("/solicitudes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[1].id").value(5))
                .andExpect(jsonPath("$[2].id").value(2))
                .andExpect(jsonPath("$[3].id").value(4))
                .andExpect(jsonPath("$[4].id").value(1));
    }

    // vencidas=false devuelve todas las solicitudes.
    @DisplayName("CP-C14 – vencidas=false")
    @Test
    void filtrarVencidasFalseDevuelveTodas() throws Exception {
        mockMvc.perform(get("/solicitudes").param("vencidas", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));
    }

    // Un filtro con valor vacío no filtra.
    @DisplayName("CP-C15 – Estado vacío")
    @Test
    void filtroEstadoVacioNoFiltra() throws Exception {
        mockMvc.perform(get("/solicitudes").param("estado", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));
    }

    // Los filtros no distinguen mayúsculas de minúsculas.
    @DisplayName("CP-C16 – Filtros en minúsculas")
    @Test
    void filtrosNoDistinguenMayusculas() throws Exception {
        mockMvc.perform(get("/solicitudes")
                        .param("tipo", "revision_nota")
                        .param("estado", "pendiente")
                        .param("prioridad", "alta"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(5));
    }

    // responsableId no numérico responde 400.
    @DisplayName("CP-C17 – responsableId no numérico")
    @Test
    void responsableIdNoNumericoResponde400() throws Exception {
        mockMvc.perform(get("/solicitudes").param("responsableId", "abc"))
                .andExpect(status().isBadRequest());
    }

    // Persistencia: sin archivo se cargan los datos de prueba y se crea el archivo.
    @DisplayName("CP-C18 – Persistencia sin archivo")
    @Test
    void cargarSinArchivoCreaDatosDePrueba() throws Exception {
        java.nio.file.Files.deleteIfExists(ARCHIVO_DATOS);
        BaseDeDatos.cargar();
        org.junit.jupiter.api.Assertions.assertEquals(5, BaseDeDatos.solicitudes.size());
        org.junit.jupiter.api.Assertions.assertTrue(java.nio.file.Files.exists(ARCHIVO_DATOS));
    }

    // Persistencia: guardar y cargar recuperan las solicitudes y ultimoId.
    @DisplayName("CP-C19 – Persistencia ida y vuelta")
    @Test
    void guardarYCargarRecuperaDatos() throws Exception {
        int cantidad = BaseDeDatos.solicitudes.size();
        int ultimoId = BaseDeDatos.ultimoId;
        BaseDeDatos.guardar();
        BaseDeDatos.cargar();
        org.junit.jupiter.api.Assertions.assertEquals(cantidad, BaseDeDatos.solicitudes.size());
        org.junit.jupiter.api.Assertions.assertEquals(ultimoId, BaseDeDatos.ultimoId);
    }

    // Persistencia: archivo corrupto arranca con datos de prueba.
    @DisplayName("CP-C20 – Archivo corrupto")
    @Test
    void cargarArchivoCorruptoUsaDatosDePrueba() throws Exception {
        java.nio.file.Files.writeString(ARCHIVO_DATOS, "{basura");
        BaseDeDatos.cargar();
        org.junit.jupiter.api.Assertions.assertEquals(5, BaseDeDatos.solicitudes.size());
    }

    // Límites exactos de vencimiento: 3/7/15 días no vencida, 4/8/16 sí.
    @DisplayName("CP-C21 – Límites de vencimiento")
    @Test
    void limitesExactosDeVencimiento() throws Exception {
        BaseDeDatos.solicitudes.clear();
        agregarSolicitudAbierta(10, "ALTA", "PENDIENTE", 3);
        agregarSolicitudAbierta(11, "ALTA", "PENDIENTE", 4);
        agregarSolicitudAbierta(12, "MEDIA", "PENDIENTE", 7);
        agregarSolicitudAbierta(13, "MEDIA", "PENDIENTE", 8);
        agregarSolicitudAbierta(14, "BAJA", "PENDIENTE", 15);
        agregarSolicitudAbierta(15, "BAJA", "PENDIENTE", 16);
        agregarSolicitudAbierta(16, "ALTA", "RESUELTA", 10);

        mockMvc.perform(get("/solicitudes").param("vencidas", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
        mockMvc.perform(get("/reportes/resumen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vencidas").value(3));
    }

    private void agregarSolicitudAbierta(int id, String prioridad, String estado, int diasAtras) {
        Solicitud solicitud = new Solicitud();
        solicitud.setId(id);
        solicitud.setLegajo("10234");
        solicitud.setTipo("CERTIFICADO");
        solicitud.setDescripcion("Solicitud sintética para pruebas");
        solicitud.setPrioridad(prioridad);
        solicitud.setEstado(estado);
        solicitud.setFechaCreacion(java.time.LocalDate.now().minusDays(diasAtras).toString());
        solicitud.setFechaUltimoCambio(java.time.LocalDate.now().minusDays(diasAtras).toString());
        BaseDeDatos.solicitudes.add(solicitud);
    }
}


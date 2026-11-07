package ar.edu.utn.tupad.solicitudes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ar.edu.utn.tupad.solicitudes.datos.BaseDeDatos;
import ar.edu.utn.tupad.solicitudes.modelo.Solicitud;

// Pruebas de caracterización de la zona B, sobre el comportamiento original.
@SpringBootTest
@AutoConfigureMockMvc
class SolicitudZonaBTest {

    @Autowired
    private MockMvc mockMvc;

    // Archivo real utilizado por la persistencia del sistema.
    private static final Path ARCHIVO_DATOS
            = Path.of("datos", "solicitudes.json");

    private static byte[] contenidoOriginal;
    private static boolean archivoExistia;

    // Conserva el contenido original antes de ejecutar las pruebas.
    @BeforeAll
    static void guardarDatosOriginales() throws IOException {
        archivoExistia = Files.exists(ARCHIVO_DATOS);

        if (archivoExistia) {
            contenidoOriginal = Files.readAllBytes(ARCHIVO_DATOS);
        }
    }

    // Cada prueba comienza con los mismos datos iniciales.
    @BeforeEach
    void prepararDatos() throws IOException {
        restaurarArchivo();
        BaseDeDatos.cargarDatosDePrueba();
    }

    // Evita que una prueba deje cambios en el JSON para la siguiente.
    @AfterEach
    void limpiarDespuesDeCadaPrueba() throws IOException {
        restaurarArchivo();
    }

    // Recupera el archivo original al terminar toda la clase.
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

    // Crea una solicitud sintética para probar el límite de asignaciones.
    private void agregarSolicitudAbierta(int id, int responsableId) {
        Solicitud solicitud = new Solicitud();
        solicitud.setId(id);
        solicitud.setLegajo("10234");
        solicitud.setTipo("CERTIFICADO");
        solicitud.setDescripcion("Solicitud sintética para pruebas");
        solicitud.setPrioridad("BAJA");
        solicitud.setEstado("EN_REVISION");
        solicitud.setResponsableId(responsableId);
        solicitud.setFechaCreacion("2026-10-01");
        solicitud.setFechaUltimoCambio("2026-10-01");

        BaseDeDatos.solicitudes.add(solicitud);
    }

    // CP-B01: consulta de una solicitud existente.
    @Test
    void consultarSolicitudExistente() throws Exception {
        mockMvc.perform(get("/solicitudes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.legajo").value("10234"));
    }

    // CP-B02: consulta de una solicitud inexistente.
    @Test
    void consultarSolicitudInexistente() throws Exception {
        mockMvc.perform(get("/solicitudes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("No existe la solicitud 99"));
    }

    // RB-04: la solicitud inexistente prevalece sobre la falta del nuevo estado.
    @Test
    void cambiarEstadoDeSolicitudInexistenteDevuelve404AntesDeValidarCuerpo() throws Exception {
        mockMvc.perform(put("/solicitudes/99/estado")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No existe la solicitud 99"));
    }

    // RB-04: la solicitud inexistente prevalece sobre la falta del responsableId.
    @Test
    void asignarResponsableDeSolicitudInexistenteDevuelve404AntesDeValidarCuerpo() throws Exception {
        mockMvc.perform(put("/solicitudes/99/responsable")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No existe la solicitud 99"));
    }

    // CP-B03: transición válida de EN_REVISION a RESUELTA.
    @Test
    void resolverSolicitudEnRevision() throws Exception {
        mockMvc.perform(put("/solicitudes/2/estado")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {
                                "estado": "RESUELTA",
                                "comentario": "Trámite completado"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RESUELTA"))
                .andExpect(jsonPath("$.historial", hasSize(4)))
                .andExpect(jsonPath("$.historial[3]",
                        endsWith(": EN_REVISION -> RESUELTA (Trámite completado)")));
    }

    // CP-B04: transición no permitida sin modificar estado ni historial.
    @Test
    void rechazarTransicionNoPermitida() throws Exception {
        mockMvc.perform(put("/solicitudes/4/estado")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {"estado": "PENDIENTE"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error")
                        .value("Transición no permitida: RECHAZADA -> PENDIENTE"));

        mockMvc.perform(get("/solicitudes/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RECHAZADA"))
                .andExpect(jsonPath("$.historial", hasSize(4)));
    }

    // CP-B05: no puede pasar a EN_REVISION sin responsable.
    @Test
    void impedirRevisionSinResponsable() throws Exception {
        mockMvc.perform(put("/solicitudes/1/estado")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {"estado": "EN_REVISION"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error")
                        .value("Para pasar a EN_REVISION la solicitud debe tener un responsable asignado"));
    }

    // CP-B06: el rechazo requiere comentario.
    @Test
    void impedirRechazoSinComentario() throws Exception {
        mockMvc.perform(put("/solicitudes/1/estado")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {"estado": "RECHAZADA"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Para rechazar una solicitud hay que indicar un comentario"));
    }

    // CP-B07: asignar responsable cambia PENDIENTE a EN_REVISION.
    @Test
    void asignarResponsableActivo() throws Exception {
        mockMvc.perform(put("/solicitudes/1/responsable")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {"responsableId": 1}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responsableId").value(1))
                .andExpect(jsonPath("$.estado").value("EN_REVISION"))
                .andExpect(jsonPath("$.historial", hasSize(3)));
    }

    // CP-B08: no se puede asignar un responsable inactivo.
    @Test
    void impedirAsignacionResponsableInactivo() throws Exception {
        mockMvc.perform(put("/solicitudes/5/responsable")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {"responsableId": 4}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error")
                        .value("El responsable Raúl Sosa no está activo"));
    }

    // CP-B09: error al asignar un responsable inexistente.
    @Test
    void impedirAsignacionResponsableInexistente() throws Exception {
        mockMvc.perform(put("/solicitudes/5/responsable")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {"responsableId": 99}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("No existe el responsable 99"));
    }

    // CP-B10: un responsable no admite más de cinco solicitudes abiertas.
    @Test
    void impedirSuperarLimiteDeAsignaciones() throws Exception {
        for (int id = 100; id < 105; id++) {
            agregarSolicitudAbierta(id, 1);
        }

        mockMvc.perform(put("/solicitudes/5/responsable")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {"responsableId": 1}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error")
                        .value("El responsable ya tiene 5 solicitudes abiertas"));
    }

    // CP-B11: No permite cambiar el estado de una solicitud CERRADA.
    @Test
    void impedirCambioDeSolicitudCerrada() throws Exception {

        // Busca la solicitud por su ID, sin depender del orden de la lista.
        Solicitud solicitud = BaseDeDatos.solicitudes.stream()
                .filter(s -> s.getId() == 1)
                .findFirst()
                .orElseThrow();

        solicitud.setEstado("CERRADA");

        mockMvc.perform(put("/solicitudes/1/estado")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {"estado": "EN_REVISION"}
                            """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error")
                        .value("Transición no permitida: CERRADA -> EN_REVISION"));
    }

    // CP-B12: no se asignan responsables a solicitudes RECHAZADAS.
    @Test
    void impedirAsignacionSolicitudRechazada() throws Exception {
        mockMvc.perform(put("/solicitudes/4/responsable")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {"responsableId": 1}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error")
                        .value("No se puede asignar responsable a una solicitud RECHAZADA"));
    }

    // CP-B13: el campo estado es obligatorio.
    @Test
    void impedirCambioSinEstado() throws Exception {
        mockMvc.perform(put("/solicitudes/2/estado")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("El nuevo estado es obligatorio"));
    }

    // CP-B14: una RESUELTA puede volver a EN_REVISION con responsable.
    @Test
    void reabrirSolicitudResueltaConResponsable() throws Exception {
        // El responsable 2 tiene cinco solicitudes abiertas adicionales.
        for (int id = 100; id < 105; id++) {
            agregarSolicitudAbierta(id, 2);
        }

        // Reabrir una solicitud no realiza una nueva asignación.
        mockMvc.perform(put("/solicitudes/3/estado")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {"estado": "EN_REVISION"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_REVISION"))
                .andExpect(jsonPath("$.responsableId").value(2))
                .andExpect(jsonPath("$.historial", hasSize(5)));
    }

    // CP-B15: Rechaza un estado inexistente sin modificar la solicitud.
    @Test
    void impedirCambioAEstadoInexistente() throws Exception {

        mockMvc.perform(put("/solicitudes/1/estado")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {"estado": "FOO"}
                            """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error")
                        .value("Transición no permitida: PENDIENTE -> FOO"));

        // Comprueba que la solicitud sigue en su estado original.
        mockMvc.perform(get("/solicitudes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

// CP-B16: Acepta estados escritos en minúsculas.
    @Test
    void aceptarEstadoEnMinusculas() throws Exception {

        mockMvc.perform(put("/solicitudes/2/estado")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {"estado": "resuelta"}
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RESUELTA"));
    }

    // RB-03: conserva las dos entradas y una misma fecha en la asignación automática.
    @Test
    void asignacionAutomaticaConservaHistorialYFecha() throws Exception {
        String hoy = java.time.LocalDate.now().toString();
        mockMvc.perform(put("/solicitudes/1/responsable")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"responsableId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fechaCreacion").value("2026-09-01"))
                .andExpect(jsonPath("$.fechaUltimoCambio").value(hoy))
                .andExpect(jsonPath("$.historial", hasSize(3)))
                .andExpect(jsonPath("$.historial[0]").value("2026-09-01: solicitud registrada (PENDIENTE)"))
                .andExpect(jsonPath("$.historial[1]").value(hoy + ": responsable asignado Laura Medina (Bedelía)"))
                .andExpect(jsonPath("$.historial[2]").value(hoy + ": PENDIENTE -> EN_REVISION"));
    }

    // RB-03: asignar sobre RESUELTA o EN_REVISION no genera una transición adicional.
    @Test
    void asignacionSinTransicionConservaEstadoYActualizaFecha() throws Exception {
        for (int id : new int[]{2, 3}) {
            Solicitud sol = BaseDeDatos.solicitudes.stream()
                    .filter(s -> s.getId() == id).findFirst().orElseThrow();
            String estado = sol.getEstado();
            int entradas = sol.getHistorial().size();
            mockMvc.perform(put("/solicitudes/" + id + "/responsable")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"responsableId\":1}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.estado").value(estado))
                    .andExpect(jsonPath("$.fechaUltimoCambio").value(java.time.LocalDate.now().toString()))
                    .andExpect(jsonPath("$.historial", hasSize(entradas + 1)));
        }
    }

    // RB-03: round-trip real con el ObjectMapper utilizado por BaseDeDatos.
    @Test
    void persistenciaConservaEstadoFechaEHistorialDeLaTransicion() throws Exception {
        mockMvc.perform(put("/solicitudes/2/estado")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"estado\":\"RESUELTA\",\"comentario\":\"  Trámite completado  \"}"))
                .andExpect(status().isOk());
        Solicitud antes = BaseDeDatos.solicitudes.stream()
                .filter(s -> s.getId() == 2).findFirst().orElseThrow();
        java.util.List<String> historial = java.util.List.copyOf(antes.getHistorial());
        BaseDeDatos.guardar();
        BaseDeDatos.solicitudes = new java.util.ArrayList<>();
        BaseDeDatos.cargar();
        Solicitud despues = BaseDeDatos.solicitudes.stream()
                .filter(s -> s.getId() == 2).findFirst().orElseThrow();
        org.junit.jupiter.api.Assertions.assertNotSame(antes, despues);
        org.junit.jupiter.api.Assertions.assertEquals("RESUELTA", despues.getEstado());
        org.junit.jupiter.api.Assertions.assertEquals(antes.getFechaCreacion(), despues.getFechaCreacion());
        org.junit.jupiter.api.Assertions.assertEquals(antes.getFechaUltimoCambio(), despues.getFechaUltimoCambio());
        org.junit.jupiter.api.Assertions.assertEquals(historial, despues.getHistorial());
    }

    // CP-B17: Rechaza una asignación sin responsableId.
    @Test
    void impedirAsignacionSinResponsableId() throws Exception {

        mockMvc.perform(put("/solicitudes/1/responsable")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("El responsableId es obligatorio"));

        // Comprueba que la solicitud no recibió un responsable.
        mockMvc.perform(get("/solicitudes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responsableId").doesNotExist())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }
}

package ar.edu.utn.tupad.solicitudes;

import ar.edu.utn.tupad.solicitudes.datos.BaseDeDatos;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Pruebas de caracterización de la zona A (registro de solicitudes, POST /solicitudes).
// Registran el comportamiento de la versión inicial: código HTTP, mensaje exacto y datos.
// Tienen que pasar igual antes y después del refactoring de la zona.
@SpringBootTest
@AutoConfigureMockMvc
class SolicitudZonaATest {

    @Autowired
    private MockMvc mockMvc;

    // Archivo real que escribe la persistencia; se restaura para no pisar los datos locales.
    private static final Path ARCHIVO_DATOS = Path.of("datos", "solicitudes.json");

    private static byte[] contenidoOriginal;
    private static boolean archivoExistia;

    private final String hoy = LocalDate.now().toString();

    @BeforeAll
    static void guardarDatosOriginales() throws IOException {
        archivoExistia = Files.exists(ARCHIVO_DATOS);
        if (archivoExistia) {
            contenidoOriginal = Files.readAllBytes(ARCHIVO_DATOS);
        }
    }

    // Cada prueba arranca con los datos de prueba iniciales: 5 solicitudes y 4 estudiantes.
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

    private ResultActions registrar(String json) throws Exception {
        return mockMvc.perform(post("/solicitudes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    // Verifica el formato de error de la API: un objeto con una sola clave "error".
    private void esperarError(ResultActions resultado, int codigo, String mensaje) throws Exception {
        resultado.andExpect(status().is(codigo))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error").value(mensaje));
    }

    // Verifica que un rechazo no dejó datos a medias.
    private void esperarSinCambios() throws Exception {
        mockMvc.perform(get("/solicitudes")).andExpect(jsonPath("$", hasSize(5)));
        mockMvc.perform(get("/estudiantes")).andExpect(jsonPath("$", hasSize(4)));
    }

    // ===================== Registro exitoso =====================

    @Test
    @DisplayName("CP-A01 – Registrar con estudiante existente: 201, PENDIENTE, prioridad por tipo e historial")
    void registrarConEstudianteExistente() throws Exception {
        registrar("""
                {"legajo": "11002", "tipo": "equivalencia",
                 "descripcion": "  Equivalencia de Inglés técnico aprobada en otra institución  "}
                """)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(6))
                .andExpect(jsonPath("$.legajo").value("11002"))
                .andExpect(jsonPath("$.tipo").value("EQUIVALENCIA"))
                .andExpect(jsonPath("$.descripcion").value("Equivalencia de Inglés técnico aprobada en otra institución"))
                .andExpect(jsonPath("$.prioridad").value("MEDIA"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.responsableId").isEmpty())
                .andExpect(jsonPath("$.fechaCreacion").value(hoy))
                .andExpect(jsonPath("$.fechaUltimoCambio").value(hoy))
                .andExpect(jsonPath("$.historial", contains(hoy + ": solicitud registrada (PENDIENTE)")));

        mockMvc.perform(get("/solicitudes/6")).andExpect(status().isOk());
    }

    @ParameterizedTest(name = "CP-A02 – Prioridad por defecto: {0} → {1}")
    @CsvSource({
            "REVISION_NOTA, ALTA",
            "EQUIVALENCIA, MEDIA",
            "CAMBIO_COMISION, MEDIA",
            "CERTIFICADO, BAJA",
            "OTRO, BAJA"
    })
    void prioridadPorDefectoSegunTipo(String tipo, String prioridadEsperada) throws Exception {
        registrar("""
                {"legajo": "11345", "tipo": "%s", "descripcion": "Solicitud para probar la prioridad"}
                """.formatted(tipo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.prioridad").value(prioridadEsperada));
    }

    @Test
    @DisplayName("CP-A03 – Prioridad indicada en minúsculas: se acepta y se guarda en mayúsculas")
    void prioridadIndicadaEnMinusculas() throws Exception {
        registrar("""
                {"legajo": "11002", "tipo": "OTRO", "prioridad": "alta", "descripcion": "Consulta sobre el calendario"}
                """)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.prioridad").value("ALTA"));
    }

    @Test
    @DisplayName("CP-A04 – Estudiante nuevo: se da de alta con el email en minúsculas y la carrera por defecto")
    void registrarConEstudianteNuevo() throws Exception {
        registrar("""
                {"legajo": "12001", "nombre": "  Sofía Castro  ", "email": "  Sofia.Castro@alumnos.example.edu.ar ",
                 "tipo": "CERTIFICADO", "descripcion": "Certificado de materias aprobadas para beca"}
                """)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.legajo").value("12001"));

        mockMvc.perform(get("/estudiantes"))
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[?(@.legajo == '12001')].nombre", contains("Sofía Castro")))
                .andExpect(jsonPath("$[?(@.legajo == '12001')].email", contains("sofia.castro@alumnos.example.edu.ar")))
                .andExpect(jsonPath("$[?(@.legajo == '12001')].carrera", contains("Sin especificar")));
    }

    @Test
    @DisplayName("CP-A05 – Estudiante existente: no hace falta nombre ni email, y los datos enviados se ignoran")
    void estudianteExistenteIgnoraDatosEnviados() throws Exception {
        registrar("""
                {"legajo": "10567", "nombre": "Otro Nombre", "email": "otro@example.edu.ar",
                 "tipo": "OTRO", "descripcion": "Consulta sobre el calendario"}
                """)
                .andExpect(status().isCreated());

        mockMvc.perform(get("/estudiantes"))
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[?(@.legajo == '10567')].nombre", contains("Martín Gómez")))
                .andExpect(jsonPath("$[?(@.legajo == '10567')].email", contains("martin.gomez@alumnos.example.edu.ar")));
    }

    @Test
    @DisplayName("CP-A06 – Mismo tipo que una solicitud cerrada o rechazada: se permite")
    void mismoTipoQueSolicitudRechazada() throws Exception {
        // 11345 tiene una solicitud CAMBIO_COMISION RECHAZADA, que no está abierta
        registrar("""
                {"legajo": "11345", "tipo": "CAMBIO_COMISION", "descripcion": "Pido de nuevo el cambio de comisión"}
                """)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(6));
    }

    @Test
    @DisplayName("CP-A07 – Descripción de exactamente 500 caracteres: se acepta")
    void descripcionEnElLimiteMaximo() throws Exception {
        registrar("""
                {"legajo": "11002", "tipo": "OTRO", "descripcion": "%s"}
                """.formatted("a".repeat(500)))
                .andExpect(status().isCreated());
    }

    // ===================== Validaciones (RN-01 a RN-05) =====================

    @Test
    @DisplayName("CP-A08 – Sin legajo: 400")
    void sinLegajo() throws Exception {
        esperarError(registrar("""
                {"tipo": "OTRO", "descripcion": "Consulta sobre el calendario"}
                """), 400, "El legajo es obligatorio");
        esperarSinCambios();
    }

    @Test
    @DisplayName("CP-A09 – Legajo en blanco: 400")
    void legajoEnBlanco() throws Exception {
        esperarError(registrar("""
                {"legajo": "   ", "tipo": "OTRO", "descripcion": "Consulta sobre el calendario"}
                """), 400, "El legajo es obligatorio");
    }

    @ParameterizedTest(name = "CP-A10 – Legajo con formato inválido: \"{0}\" → 400")
    @CsvSource({"1234", "123456", "12a45"})
    void legajoConFormatoInvalido(String legajo) throws Exception {
        esperarError(registrar("""
                {"legajo": "%s", "tipo": "OTRO", "descripcion": "Consulta sobre el calendario"}
                """.formatted(legajo)), 400, "El legajo debe tener 5 dígitos");
        esperarSinCambios();
    }

    @Test
    @DisplayName("CP-A11 – Sin tipo: 400")
    void sinTipo() throws Exception {
        esperarError(registrar("""
                {"legajo": "11002", "descripcion": "Consulta sobre el calendario"}
                """), 400, "El tipo es obligatorio");
    }

    @Test
    @DisplayName("CP-A12 – Tipo inválido: 400 y el mensaje muestra el valor en mayúsculas")
    void tipoInvalido() throws Exception {
        esperarError(registrar("""
                {"legajo": "11002", "tipo": "beca", "descripcion": "Consulta sobre el calendario"}
                """), 400, "Tipo de solicitud inválido: BECA");
    }

    @Test
    @DisplayName("CP-A13 – Descripción ausente: 400 con el mensaje del mínimo")
    void sinDescripcion() throws Exception {
        esperarError(registrar("""
                {"legajo": "11002", "tipo": "OTRO"}
                """), 400, "La descripción debe tener al menos 10 caracteres");
    }

    @Test
    @DisplayName("CP-A14 – Descripción corta: el mínimo se cuenta sin los espacios de los extremos")
    void descripcionCortaConEspacios() throws Exception {
        // 9 caracteres útiles rodeados de espacios: 15 caracteres en total, pero cuenta 9
        esperarError(registrar("""
                {"legajo": "11002", "tipo": "OTRO", "descripcion": "   123456789   "}
                """), 400, "La descripción debe tener al menos 10 caracteres");
    }

    @Test
    @DisplayName("CP-A15 – Descripción de 501 caracteres: 400")
    void descripcionDemasiadoLarga() throws Exception {
        esperarError(registrar("""
                {"legajo": "11002", "tipo": "OTRO", "descripcion": "%s"}
                """.formatted("a".repeat(501))), 400, "La descripción no puede superar los 500 caracteres");
    }

    @Test
    @DisplayName("CP-A16 – Descripción de 498 caracteres más 3 espacios: el máximo se cuenta con los espacios (400)")
    void maximoCuentaLosEspacios() throws Exception {
        esperarError(registrar("""
                {"legajo": "11002", "tipo": "OTRO", "descripcion": "%s   "}
                """.formatted("a".repeat(498))), 400, "La descripción no puede superar los 500 caracteres");
    }

    @Test
    @DisplayName("CP-A17 – Prioridad inválida: 400")
    void prioridadInvalida() throws Exception {
        esperarError(registrar("""
                {"legajo": "11002", "tipo": "OTRO", "prioridad": "URGENTE", "descripcion": "Consulta sobre el calendario"}
                """), 400, "Prioridad inválida: URGENTE");
    }

    @Test
    @DisplayName("CP-A18 – Prioridad con un espacio adelante: 400 y el mensaje conserva el espacio")
    void prioridadConEspacio() throws Exception {
        esperarError(registrar("""
                {"legajo": "11002", "tipo": "OTRO", "prioridad": " alta", "descripcion": "Consulta sobre el calendario"}
                """), 400, "Prioridad inválida:  ALTA");
    }

    @Test
    @DisplayName("CP-A19 – Estudiante nuevo sin nombre: 400 y no se da de alta")
    void estudianteNuevoSinNombre() throws Exception {
        esperarError(registrar("""
                {"legajo": "12001", "email": "nuevo@alumnos.example.edu.ar",
                 "tipo": "OTRO", "descripcion": "Consulta sobre el calendario"}
                """), 400, "El estudiante no existe: se requieren nombre y email para darlo de alta");
        esperarSinCambios();
    }

    @Test
    @DisplayName("CP-A20 – Estudiante nuevo con email inválido: 400 y no se da de alta")
    void estudianteNuevoConEmailInvalido() throws Exception {
        esperarError(registrar("""
                {"legajo": "12001", "nombre": "Sofía Castro", "email": "sin-arroba",
                 "tipo": "OTRO", "descripcion": "Consulta sobre el calendario"}
                """), 400, "El email no es válido");
        esperarSinCambios();
    }

    // ===================== Regla de duplicados (RN-06) =====================

    @Test
    @DisplayName("CP-A21 – Solicitud abierta del mismo tipo: 409 con el id de la existente, sin cambios")
    void solicitudAbiertaDuplicada() throws Exception {
        // 10234 tiene la solicitud 1, CERTIFICADO, PENDIENTE
        esperarError(registrar("""
                {"legajo": "10234", "tipo": "certificado", "descripcion": "Otro certificado de alumno regular"}
                """), 409, "El estudiante ya tiene una solicitud abierta de tipo CERTIFICADO (id 1)");
        esperarSinCambios();
    }

    // ===================== Orden de las validaciones =====================

    @Test
    @DisplayName("CP-A22 – Orden de las validaciones: con todo inválido, cada corrección revela el error siguiente")
    void ordenDeLasValidaciones() throws Exception {
        esperarError(registrar("""
                {"legajo": "12", "tipo": "BECA", "descripcion": "corta", "prioridad": "URGENTE"}
                """), 400, "El legajo debe tener 5 dígitos");
        esperarError(registrar("""
                {"legajo": "12001", "tipo": "BECA", "descripcion": "corta", "prioridad": "URGENTE"}
                """), 400, "Tipo de solicitud inválido: BECA");
        esperarError(registrar("""
                {"legajo": "12001", "tipo": "OTRO", "descripcion": "corta", "prioridad": "URGENTE"}
                """), 400, "La descripción debe tener al menos 10 caracteres");
        esperarError(registrar("""
                {"legajo": "12001", "tipo": "OTRO", "descripcion": "Consulta sobre el calendario", "prioridad": "URGENTE"}
                """), 400, "Prioridad inválida: URGENTE");
        // Recién después de validar la solicitud se validan los datos del estudiante nuevo
        esperarError(registrar("""
                {"legajo": "12001", "tipo": "OTRO", "descripcion": "Consulta sobre el calendario", "prioridad": "ALTA"}
                """), 400, "El estudiante no existe: se requieren nombre y email para darlo de alta");
        esperarSinCambios();
    }

    @Test
    @DisplayName("CP-A23 – Descripción de 600 espacios: viola el mínimo y el máximo, y gana el mínimo")
    void minimoSeValidaAntesQueElMaximo() throws Exception {
        esperarError(registrar("""
                {"legajo": "11002", "tipo": "OTRO", "descripcion": "%s"}
                """.formatted(" ".repeat(600))), 400, "La descripción debe tener al menos 10 caracteres");
    }

    @Test
    @DisplayName("CP-A24 – Un rechazo no consume id: el siguiente registro válido recibe el 6")
    void rechazoNoConsumeId() throws Exception {
        registrar("""
                {"legajo": "10234", "tipo": "CERTIFICADO", "descripcion": "Otro certificado de alumno regular"}
                """).andExpect(status().isConflict());

        registrar("""
                {"legajo": "11002", "tipo": "OTRO", "descripcion": "Consulta sobre el calendario"}
                """)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(6));
    }

    // ===================== Defecto de la versión inicial =====================

    @Test
    @DisplayName("CP-A25 – Legajo enviado como número: el controlador falla con ClassCastException (500 en el servidor; defecto SA-03)")
    void legajoComoNumeroFallaConClassCastException() throws Exception {
        // MockMvc no convierte la excepción en un 500: la deja salir. En el servidor real la respuesta
        // es 500 (caso 13 de la línea base). Si el refactoring lo corrige, es un cambio intencional
        // y esta prueba se actualiza.
        ServletException error = assertThrows(ServletException.class, () -> registrar("""
                {"legajo": 12345, "tipo": "OTRO", "descripcion": "Prueba con el legajo enviado como número"}
                """));
        assertInstanceOf(ClassCastException.class, error.getRootCause());
        esperarSinCambios();
    }
}

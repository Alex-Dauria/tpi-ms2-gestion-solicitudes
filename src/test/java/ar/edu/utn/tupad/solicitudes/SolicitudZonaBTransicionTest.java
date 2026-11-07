package ar.edu.utn.tupad.solicitudes;

import ar.edu.utn.tupad.solicitudes.modelo.Solicitud;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

class SolicitudZonaBTransicionTest {

    @ParameterizedTest
    @CsvSource(value = {
        "RECHAZADA|PENDIENTE|1|comentario",
        "PENDIENTE|RESUELTA|1|comentario",
        "PENDIENTE|EN_REVISION||comentario",
        "PENDIENTE|RECHAZADA|1|",
        "PENDIENTE|RECHAZADA|1|'   '",
        "PENDIENTE|INEXISTENTE|1|comentario"
    }, delimiter = '|')
    void operacionInvalidaNoModificaLaSolicitud(String estado, String nuevo,
                                               Integer responsable, String comentario) {
        Solicitud sol = new Solicitud();
        sol.setEstado(estado);
        sol.setResponsableId(responsable);
        sol.setFechaCreacion("2026-09-01");
        sol.setFechaUltimoCambio("2026-09-02");
        sol.getHistorial().add("entrada previa");

        assertThrows(RuntimeException.class,
                () -> sol.cambiarEstado(nuevo, comentario, "2026-11-06"));

        assertAll(
                () -> assertEquals(estado, sol.getEstado()),
                () -> assertEquals(responsable, sol.getResponsableId()),
                () -> assertEquals("2026-09-01", sol.getFechaCreacion()),
                () -> assertEquals("2026-09-02", sol.getFechaUltimoCambio()),
                () -> assertEquals(List.of("entrada previa"), sol.getHistorial()));
    }

    @ParameterizedTest
    @CsvSource(value = {
        "EN_REVISION|RESUELTA|'  Trámite completado  '| (Trámite completado)",
        "PENDIENTE|EN_REVISION||",
        "RESUELTA|CERRADA|'   '|",
        "PENDIENTE|RECHAZADA|'  Sin cupo  '| (Sin cupo)"
    }, delimiter = '|', ignoreLeadingAndTrailingWhitespace = false)
    void operacionValidaConservaFormatoYFecha(String estado, String nuevo,
                                             String comentario, String sufijo) {
        Solicitud sol = new Solicitud();
        sol.setEstado(estado);
        sol.setResponsableId(1);
        sol.setFechaCreacion("2026-09-01");
        sol.getHistorial().add("entrada previa");

        sol.cambiarEstado(nuevo, comentario, "2026-11-06");

        assertAll(
                () -> assertEquals(nuevo, sol.getEstado()),
                () -> assertEquals("2026-11-06", sol.getFechaUltimoCambio()),
                () -> assertEquals("2026-09-01", sol.getFechaCreacion()),
                () -> assertEquals(List.of("entrada previa",
                        "2026-11-06: " + estado + " -> " + nuevo + (sufijo == null ? "" : sufijo)),
                        sol.getHistorial()));
    }
}

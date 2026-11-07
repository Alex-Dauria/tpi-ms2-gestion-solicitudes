package ar.edu.utn.tupad.solicitudes.servicio;

import ar.edu.utn.tupad.solicitudes.modelo.Solicitud;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * RN-14: única definición de cuándo vence una solicitud. La usan el filtro y el reporte.
 */
public final class PoliticaVencimiento {

    private static final int DIAS_PRIORIDAD_ALTA = 3;
    private static final int DIAS_PRIORIDAD_MEDIA = 7;
    private static final int DIAS_PRIORIDAD_BAJA = 15;

    private PoliticaVencimiento() {
    }

    public static boolean estaVencida(Solicitud solicitud, LocalDate hoy) {
        if (!solicitud.getEstado().equals("PENDIENTE") && !solicitud.getEstado().equals("EN_REVISION")) {
            return false;
        }
        long dias = ChronoUnit.DAYS.between(LocalDate.parse(solicitud.getFechaCreacion()), hoy);
        return dias > plazoEnDias(solicitud.getPrioridad());
    }

    private static int plazoEnDias(String prioridad) {
        switch (prioridad) {
            case "ALTA":
                return DIAS_PRIORIDAD_ALTA;
            case "MEDIA":
                return DIAS_PRIORIDAD_MEDIA;
            default:
                return DIAS_PRIORIDAD_BAJA;
        }
    }
}

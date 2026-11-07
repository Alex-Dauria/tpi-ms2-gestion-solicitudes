package ar.edu.utn.tupad.solicitudes.servicio;

import ar.edu.utn.tupad.solicitudes.modelo.Solicitud;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Única definición de cuándo vence una solicitud.
 * La usan el filtro (vencidas=true) y el reporte (contador de vencidas).
 */
public final class PoliticaVencimiento {

    // Plazos de vencimiento por prioridad, en días.
    private static final int DIAS_PRIORIDAD_ALTA = 3;
    private static final int DIAS_PRIORIDAD_MEDIA = 7;
    private static final int DIAS_PRIORIDAD_BAJA = 15;

    private PoliticaVencimiento() {
    }

    public static boolean estaVencida(Solicitud solicitud, LocalDate hoy) {
        // Solo las solicitudes abiertas pueden vencer.
        if (!solicitud.getEstado().equals("PENDIENTE") && !solicitud.getEstado().equals("EN_REVISION")) {
            return false;
        }
        // Calcula los días transcurridos desde la creación.
        long dias = ChronoUnit.DAYS.between(LocalDate.parse(solicitud.getFechaCreacion()), hoy);
        // Vence si supera el plazo de su prioridad.
        return dias > plazoEnDias(solicitud.getPrioridad());
    }

    // Devuelve el plazo de vencimiento según la prioridad.
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
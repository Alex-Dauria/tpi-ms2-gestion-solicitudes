package ar.edu.utn.tupad.solicitudes.controlador;

/**
 * Agrupa los criterios opcionales de filtrado del listado de solicitudes.
 * Reemplaza la lista larga de parámetros de {@code filtrar}.
 */
record FiltroSolicitudes(String estado, String tipo, String prioridad, String legajo,
                         Integer responsableId, Boolean vencidas) {
}

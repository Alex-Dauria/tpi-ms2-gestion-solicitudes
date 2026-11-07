package ar.edu.utn.tupad.solicitudes.servicio;

/**
 * Datos que llegan en el pedido de registro de una solicitud, tal como los envió el cliente
 * (sin validar ni normalizar).
 */
public record DatosSolicitud(String legajo, String nombre, String email, String carrera,
                             String tipo, String prioridad, String descripcion) {
}

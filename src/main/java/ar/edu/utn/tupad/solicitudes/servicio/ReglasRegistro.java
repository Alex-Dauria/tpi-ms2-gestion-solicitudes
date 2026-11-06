package ar.edu.utn.tupad.solicitudes.servicio;

import java.util.List;
import java.util.Optional;

/**
 * Reglas de negocio del registro de una solicitud (RN-01 a RN-05).
 * No conoce HTTP ni la persistencia: recibe los datos del pedido y devuelve el resultado.
 */
public final class ReglasRegistro {

    private static final int DIGITOS_LEGAJO = 5;
    private static final int LARGO_MINIMO_DESCRIPCION = 10;
    private static final int LARGO_MAXIMO_DESCRIPCION = 500;
    private static final List<String> TIPOS_VALIDOS =
            List.of("CERTIFICADO", "EQUIVALENCIA", "REVISION_NOTA", "CAMBIO_COMISION", "OTRO");
    private static final List<String> PRIORIDADES_VALIDAS = List.of("ALTA", "MEDIA", "BAJA");

    private ReglasRegistro() {
    }

    /**
     * Valida campo por campo, en este orden, y corta en el primer error.
     *
     * @return el mensaje del primer error, o vacío si los datos son válidos
     */
    public static Optional<String> validarSolicitud(DatosSolicitud datos) {
        return validarLegajo(datos.legajo())
                .or(() -> validarTipo(datos.tipo()))
                .or(() -> validarDescripcion(datos.descripcion()))
                .or(() -> validarPrioridad(datos.prioridad()));
    }

    // RN-05: para dar de alta a un estudiante nuevo hacen falta nombre y un email válido
    public static Optional<String> validarDatosDeAlta(DatosSolicitud datos) {
        if (estaVacio(datos.nombre()) || estaVacio(datos.email())) {
            return Optional.of("El estudiante no existe: se requieren nombre y email para darlo de alta");
        }
        if (!datos.email().contains("@") || !datos.email().contains(".")) {
            return Optional.of("El email no es válido");
        }
        return Optional.empty();
    }

    public static boolean estaVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    // RN-01
    private static Optional<String> validarLegajo(String legajo) {
        if (estaVacio(legajo)) {
            return Optional.of("El legajo es obligatorio");
        }
        if (!legajo.matches("\\d{" + DIGITOS_LEGAJO + "}")) {
            return Optional.of("El legajo debe tener " + DIGITOS_LEGAJO + " dígitos");
        }
        return Optional.empty();
    }

    // RN-02: el tipo no distingue mayúsculas y el mensaje muestra el valor ya pasado a mayúsculas
    private static Optional<String> validarTipo(String tipo) {
        if (estaVacio(tipo)) {
            return Optional.of("El tipo es obligatorio");
        }
        String tipoNormalizado = tipo.toUpperCase();
        if (!TIPOS_VALIDOS.contains(tipoNormalizado)) {
            return Optional.of("Tipo de solicitud inválido: " + tipoNormalizado);
        }
        return Optional.empty();
    }

    // RN-03: el mínimo se mide sin los espacios de los extremos y el máximo con ellos
    private static Optional<String> validarDescripcion(String descripcion) {
        if (descripcion == null || descripcion.trim().length() < LARGO_MINIMO_DESCRIPCION) {
            return Optional.of("La descripción debe tener al menos " + LARGO_MINIMO_DESCRIPCION + " caracteres");
        }
        if (descripcion.length() > LARGO_MAXIMO_DESCRIPCION) {
            return Optional.of("La descripción no puede superar los " + LARGO_MAXIMO_DESCRIPCION + " caracteres");
        }
        return Optional.empty();
    }

    // RN-04: la prioridad es opcional; si viene, tiene que ser válida
    private static Optional<String> validarPrioridad(String prioridad) {
        if (estaVacio(prioridad)) {
            return Optional.empty();
        }
        String prioridadNormalizada = prioridad.toUpperCase();
        if (!PRIORIDADES_VALIDAS.contains(prioridadNormalizada)) {
            return Optional.of("Prioridad inválida: " + prioridadNormalizada);
        }
        return Optional.empty();
    }
}

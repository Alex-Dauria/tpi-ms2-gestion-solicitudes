package ar.edu.utn.tupad.solicitudes.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Solicitud {

    private static final Map<String, Set<String>> TRANSICIONES_PERMITIDAS = Map.of(
            "PENDIENTE", Set.of("EN_REVISION", "RECHAZADA"),
            "EN_REVISION", Set.of("RESUELTA", "RECHAZADA", "PENDIENTE"),
            "RESUELTA", Set.of("CERRADA", "EN_REVISION"),
            "CERRADA", Collections.emptySet(),
            "RECHAZADA", Collections.emptySet());

    private int id;
    private String legajo;
    private String tipo;
    private String descripcion;
    private String prioridad;
    private String estado;
    private Integer responsableId;
    private String fechaCreacion;
    private String fechaUltimoCambio;
    private List<String> historial = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLegajo() {
        return legajo;
    }

    public void setLegajo(String legajo) {
        this.legajo = legajo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean permiteTransicionA(String nuevoEstado) {
        return TRANSICIONES_PERMITIDAS.getOrDefault(estado, Collections.emptySet()).contains(nuevoEstado);
    }

    // S-04 parcialmente resuelto: las transiciones operativas se encapsulan aquí;
    // setEstado se conserva para inicialización y compatibilidad de persistencia.
    public void cambiarEstado(String nuevoEstado, String comentario, String fecha) {
        if (!permiteTransicionA(nuevoEstado)) {
            throw new IllegalStateException("Transición no permitida: " + estado + " -> " + nuevoEstado);
        }
        if (nuevoEstado.equals("EN_REVISION") && responsableId == null) {
            throw new IllegalStateException("Para pasar a EN_REVISION la solicitud debe tener un responsable asignado");
        }
        if (nuevoEstado.equals("RECHAZADA") && (comentario == null || comentario.trim().isEmpty())) {
            throw new IllegalArgumentException("Para rechazar una solicitud hay que indicar un comentario");
        }

        String linea = fecha + ": " + estado + " -> " + nuevoEstado;
        if (comentario != null && !comentario.trim().isEmpty()) {
            linea += " (" + comentario.trim() + ")";
        }
        historial.add(linea);
        estado = nuevoEstado;
        fechaUltimoCambio = fecha;
    }

    public Integer getResponsableId() {
        return responsableId;
    }

    public void setResponsableId(Integer responsableId) {
        this.responsableId = responsableId;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getFechaUltimoCambio() {
        return fechaUltimoCambio;
    }

    public void setFechaUltimoCambio(String fechaUltimoCambio) {
        this.fechaUltimoCambio = fechaUltimoCambio;
    }

    public List<String> getHistorial() {
        return historial;
    }

    public void setHistorial(List<String> historial) {
        this.historial = historial;
    }
}

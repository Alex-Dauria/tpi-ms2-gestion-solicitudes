package ar.edu.utn.tupad.solicitudes.controlador;

import ar.edu.utn.tupad.solicitudes.datos.BaseDeDatos;
import ar.edu.utn.tupad.solicitudes.modelo.Estudiante;
import ar.edu.utn.tupad.solicitudes.modelo.Responsable;
import ar.edu.utn.tupad.solicitudes.modelo.Solicitud;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SolicitudController {

    // ===================== SOLICITUDES =====================

    @PostMapping("/solicitudes")
    public ResponseEntity<Object> crear(@RequestBody Map<String, Object> body) {
        String legajo = (String) body.get("legajo");
        String nombre = (String) body.get("nombre");
        String email = (String) body.get("email");
        String carrera = (String) body.get("carrera");
        String tipo = (String) body.get("tipo");
        String prioridad = (String) body.get("prioridad");
        String descripcion = (String) body.get("descripcion");
        return registrarSolicitud(legajo, nombre, email, carrera, tipo, prioridad, descripcion);
    }

    private ResponseEntity<Object> registrarSolicitud(String legajo, String nombre, String email, String carrera,
                                                      String tipo, String prioridad, String descripcion) {
        Map<String, Object> m = new HashMap<>();

        // validaciones
        if (legajo == null || legajo.trim().isEmpty()) {
            m.put("error", "El legajo es obligatorio");
            return ResponseEntity.badRequest().body(m);
        }
        if (!legajo.matches("\\d{5}")) {
            m.put("error", "El legajo debe tener 5 dígitos");
            return ResponseEntity.badRequest().body(m);
        }
        if (tipo == null || tipo.trim().isEmpty()) {
            m.put("error", "El tipo es obligatorio");
            return ResponseEntity.badRequest().body(m);
        }
        tipo = tipo.toUpperCase();
        if (!tipo.equals("CERTIFICADO") && !tipo.equals("EQUIVALENCIA") && !tipo.equals("REVISION_NOTA")
                && !tipo.equals("CAMBIO_COMISION") && !tipo.equals("OTRO")) {
            m.put("error", "Tipo de solicitud inválido: " + tipo);
            return ResponseEntity.badRequest().body(m);
        }
        if (descripcion == null || descripcion.trim().length() < 10) {
            m.put("error", "La descripción debe tener al menos 10 caracteres");
            return ResponseEntity.badRequest().body(m);
        }
        if (descripcion.length() > 500) {
            m.put("error", "La descripción no puede superar los 500 caracteres");
            return ResponseEntity.badRequest().body(m);
        }
        if (prioridad != null && !prioridad.trim().isEmpty()) {
            prioridad = prioridad.toUpperCase();
            if (!prioridad.equals("ALTA") && !prioridad.equals("MEDIA") && !prioridad.equals("BAJA")) {
                m.put("error", "Prioridad inválida: " + prioridad);
                return ResponseEntity.badRequest().body(m);
            }
        } else {
            // la prioridad se calcula segun el tipo
            switch (tipo) {
                case "REVISION_NOTA":
                    prioridad = "ALTA";
                    break;
                case "EQUIVALENCIA":
                case "CAMBIO_COMISION":
                    prioridad = "MEDIA";
                    break;
                default:
                    prioridad = "BAJA";
            }
        }

        // buscar estudiante
        Estudiante est = null;
        for (Estudiante e : BaseDeDatos.estudiantes) {
            if (e.getLegajo().equals(legajo)) {
                est = e;
                break;
            }
        }
        if (est == null) {
            // si no existe lo damos de alta
            if (nombre == null || nombre.trim().isEmpty() || email == null || email.trim().isEmpty()) {
                m.put("error", "El estudiante no existe: se requieren nombre y email para darlo de alta");
                return ResponseEntity.badRequest().body(m);
            }
            if (!email.contains("@") || !email.contains(".")) {
                m.put("error", "El email no es válido");
                return ResponseEntity.badRequest().body(m);
            }
            est = new Estudiante();
            est.setLegajo(legajo);
            est.setNombre(nombre.trim());
            est.setEmail(email.trim().toLowerCase());
            est.setCarrera(carrera == null ? "Sin especificar" : carrera);
            BaseDeDatos.estudiantes.add(est);
        }

        // no puede tener otra solicitud abierta del mismo tipo
        for (Solicitud s : BaseDeDatos.solicitudes) {
            if (s.getLegajo().equals(legajo) && s.getTipo().equals(tipo)
                    && (s.getEstado().equals("PENDIENTE") || s.getEstado().equals("EN_REVISION"))) {
                m.put("error", "El estudiante ya tiene una solicitud abierta de tipo " + tipo + " (id " + s.getId() + ")");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(m);
            }
        }

        Solicitud s = new Solicitud();
        BaseDeDatos.ultimoId++;
        s.setId(BaseDeDatos.ultimoId);
        s.setLegajo(legajo);
        s.setTipo(tipo);
        s.setDescripcion(descripcion.trim());
        s.setPrioridad(prioridad);
        s.setEstado("PENDIENTE");
        String hoy = LocalDate.now().toString();
        s.setFechaCreacion(hoy);
        s.setFechaUltimoCambio(hoy);
        s.getHistorial().add(hoy + ": solicitud registrada (PENDIENTE)");
        BaseDeDatos.solicitudes.add(s);
        BaseDeDatos.guardar();
        // TODO: avisar por mail al estudiante
        // System.out.println("Se creo la solicitud " + s.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(s);
    }

    @GetMapping("/solicitudes")
    public List<Solicitud> listar(@RequestParam(required = false) String estado,
                                  @RequestParam(required = false) String tipo,
                                  @RequestParam(required = false) String prioridad,
                                  @RequestParam(required = false) String legajo,
                                  @RequestParam(required = false) Integer responsableId,
                                  @RequestParam(required = false) Boolean vencidas) {
        return filtrar(estado, tipo, prioridad, legajo, responsableId, vencidas);
    }

    private List<Solicitud> filtrar(String estado, String tipo, String prioridad, String legajo,
                                    Integer responsableId, Boolean vencidas) {
        List<Solicitud> lst = new ArrayList<>();
        for (Solicitud s : BaseDeDatos.solicitudes) {
            boolean ok = true;
            if (estado != null && !estado.isEmpty()) {
                if (!s.getEstado().equalsIgnoreCase(estado)) {
                    ok = false;
                }
            }
            if (tipo != null && !tipo.isEmpty()) {
                if (!s.getTipo().equalsIgnoreCase(tipo)) {
                    ok = false;
                }
            }
            if (prioridad != null && !prioridad.isEmpty()) {
                if (!s.getPrioridad().equalsIgnoreCase(prioridad)) {
                    ok = false;
                }
            }
            if (legajo != null && !legajo.isEmpty()) {
                if (!s.getLegajo().equals(legajo)) {
                    ok = false;
                }
            }
            if (responsableId != null) {
                if (s.getResponsableId() == null || !s.getResponsableId().equals(responsableId)) {
                    ok = false;
                }
            }
            if (vencidas != null && vencidas) {
                // calcula si esta vencida
                boolean flag = false;
                if (s.getEstado().equals("PENDIENTE") || s.getEstado().equals("EN_REVISION")) {
                    long dias = ChronoUnit.DAYS.between(LocalDate.parse(s.getFechaCreacion()), LocalDate.now());
                    if (s.getPrioridad().equals("ALTA") && dias > 3) {
                        flag = true;
                    }
                    if (s.getPrioridad().equals("MEDIA") && dias > 7) {
                        flag = true;
                    }
                    if (s.getPrioridad().equals("BAJA") && dias > 15) {
                        flag = true;
                    }
                }
                if (!flag) {
                    ok = false;
                }
            }
            if (ok) {
                lst.add(s);
            }
        }
        // ordenar: primero las de prioridad alta
        lst.sort((a, b) -> {
            int pa = a.getPrioridad().equals("ALTA") ? 1 : a.getPrioridad().equals("MEDIA") ? 2 : 3;
            int pb = b.getPrioridad().equals("ALTA") ? 1 : b.getPrioridad().equals("MEDIA") ? 2 : 3;
            if (pa != pb) {
                return pa - pb;
            }
            return a.getId() - b.getId();
        });
        return lst;
    }

    @GetMapping("/solicitudes/{id}")
    public ResponseEntity<Object> obtener(@PathVariable int id) {
        for (Solicitud s : BaseDeDatos.solicitudes) {
            if (s.getId() == id) {
                return ResponseEntity.ok(s);
            }
        }
        Map<String, Object> m = new HashMap<>();
        m.put("error", "No existe la solicitud " + id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(m);
    }

    @PutMapping("/solicitudes/{id}/estado")
    public ResponseEntity<Object> cambiarEstado(@PathVariable int id, @RequestBody Map<String, String> body) {
        Map<String, Object> m = new HashMap<>();
        Solicitud sol = null;
        for (Solicitud s : BaseDeDatos.solicitudes) {
            if (s.getId() == id) {
                sol = s;
                break;
            }
        }
        if (sol == null) {
            m.put("error", "No existe la solicitud " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(m);
        }

        String nuevo = body.get("estado");
        String comentario = body.get("comentario");
        if (nuevo == null || nuevo.trim().isEmpty()) {
            m.put("error", "El nuevo estado es obligatorio");
            return ResponseEntity.badRequest().body(m);
        }
        nuevo = nuevo.toUpperCase();
        String actual = sol.getEstado();

        boolean valido = false;
        if (actual.equals("PENDIENTE")) {
            if (nuevo.equals("EN_REVISION") || nuevo.equals("RECHAZADA")) {
                valido = true;
            }
        } else if (actual.equals("EN_REVISION")) {
            if (nuevo.equals("RESUELTA") || nuevo.equals("RECHAZADA") || nuevo.equals("PENDIENTE")) {
                valido = true;
            }
        } else if (actual.equals("RESUELTA")) {
            if (nuevo.equals("CERRADA") || nuevo.equals("EN_REVISION")) {
                valido = true;
            }
        } else if (actual.equals("CERRADA") || actual.equals("RECHAZADA")) {
            // estados finales, no se pueden cambiar
            valido = false;
        }
        if (!valido) {
            m.put("error", "Transición no permitida: " + actual + " -> " + nuevo);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(m);
        }
        if (nuevo.equals("EN_REVISION") && sol.getResponsableId() == null) {
            m.put("error", "Para pasar a EN_REVISION la solicitud debe tener un responsable asignado");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(m);
        }
        if (nuevo.equals("RECHAZADA") && (comentario == null || comentario.trim().isEmpty())) {
            m.put("error", "Para rechazar una solicitud hay que indicar un comentario");
            return ResponseEntity.badRequest().body(m);
        }

        String hoy = LocalDate.now().toString();
        String linea = hoy + ": " + actual + " -> " + nuevo;
        if (comentario != null && !comentario.trim().isEmpty()) {
            linea += " (" + comentario.trim() + ")";
        }
        sol.getHistorial().add(linea);
        sol.setEstado(nuevo);
        sol.setFechaUltimoCambio(hoy);
        BaseDeDatos.guardar();
        return ResponseEntity.ok(sol);
    }

    @PutMapping("/solicitudes/{id}/responsable")
    public ResponseEntity<Object> asignarResponsable(@PathVariable int id, @RequestBody Map<String, Integer> body) {
        Map<String, Object> m = new HashMap<>();
        Solicitud sol = null;
        for (Solicitud s : BaseDeDatos.solicitudes) {
            if (s.getId() == id) {
                sol = s;
                break;
            }
        }
        if (sol == null) {
            m.put("error", "No existe la solicitud " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(m);
        }
        if (sol.getEstado().equals("CERRADA") || sol.getEstado().equals("RECHAZADA")) {
            m.put("error", "No se puede asignar responsable a una solicitud " + sol.getEstado());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(m);
        }

        Integer rid = body.get("responsableId");
        if (rid == null) {
            m.put("error", "El responsableId es obligatorio");
            return ResponseEntity.badRequest().body(m);
        }
        Responsable r = null;
        for (Responsable x : BaseDeDatos.responsables) {
            if (x.getId() == rid) {
                r = x;
                break;
            }
        }
        if (r == null) {
            m.put("error", "No existe el responsable " + rid);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(m);
        }
        if (!r.isActivo()) {
            m.put("error", "El responsable " + r.getNombre() + " no está activo");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(m);
        }

        int cant = 0;
        for (Solicitud s : BaseDeDatos.solicitudes) {
            if (s.getResponsableId() != null && s.getResponsableId().equals(rid) && s.getId() != sol.getId()
                    && (s.getEstado().equals("PENDIENTE") || s.getEstado().equals("EN_REVISION"))) {
                cant++;
            }
        }
        if (cant >= 5) {
            m.put("error", "El responsable ya tiene 5 solicitudes abiertas");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(m);
        }

        String hoy = LocalDate.now().toString();
        sol.setResponsableId(rid);
        sol.getHistorial().add(hoy + ": responsable asignado " + r.getNombre() + " (" + r.getArea() + ")");
        // si estaba pendiente pasa automaticamente a revision
        if (sol.getEstado().equals("PENDIENTE")) {
            sol.getHistorial().add(hoy + ": PENDIENTE -> EN_REVISION");
            sol.setEstado("EN_REVISION");
        }
        sol.setFechaUltimoCambio(hoy);
        BaseDeDatos.guardar();
        return ResponseEntity.ok(sol);
    }

    // ===================== REPORTES =====================

    @GetMapping("/reportes/resumen")
    public Map<String, Object> resumen() {
        Map<String, Object> r = new LinkedHashMap<>();

        Map<String, Integer> porEstado = new LinkedHashMap<>();
        porEstado.put("PENDIENTE", 0);
        porEstado.put("EN_REVISION", 0);
        porEstado.put("RESUELTA", 0);
        porEstado.put("RECHAZADA", 0);
        porEstado.put("CERRADA", 0);

        Map<String, Integer> porTipo = new LinkedHashMap<>();
        porTipo.put("CERTIFICADO", 0);
        porTipo.put("EQUIVALENCIA", 0);
        porTipo.put("REVISION_NOTA", 0);
        porTipo.put("CAMBIO_COMISION", 0);
        porTipo.put("OTRO", 0);

        Map<String, Integer> porPrioridad = new LinkedHashMap<>();
        porPrioridad.put("ALTA", 0);
        porPrioridad.put("MEDIA", 0);
        porPrioridad.put("BAJA", 0);

        Map<String, Integer> porResp = new LinkedHashMap<>();
        int venc = 0;

        for (Solicitud s : BaseDeDatos.solicitudes) {
            porEstado.put(s.getEstado(), porEstado.get(s.getEstado()) + 1);
            porTipo.put(s.getTipo(), porTipo.get(s.getTipo()) + 1);
            porPrioridad.put(s.getPrioridad(), porPrioridad.get(s.getPrioridad()) + 1);

            if (s.getEstado().equals("PENDIENTE") || s.getEstado().equals("EN_REVISION")) {
                long dias = ChronoUnit.DAYS.between(LocalDate.parse(s.getFechaCreacion()), LocalDate.now());
                int limite = 15;
                if (s.getPrioridad().equals("ALTA")) {
                    limite = 3;
                } else if (s.getPrioridad().equals("MEDIA")) {
                    limite = 7;
                }
                if (dias > limite) {
                    venc++;
                }
                if (s.getResponsableId() != null) {
                    for (Responsable x : BaseDeDatos.responsables) {
                        if (x.getId() == s.getResponsableId()) {
                            String k = x.getNombre() + " (" + x.getArea() + ")";
                            porResp.put(k, porResp.getOrDefault(k, 0) + 1);
                        }
                    }
                }
            }
        }

        r.put("total", BaseDeDatos.solicitudes.size());
        r.put("porEstado", porEstado);
        r.put("porTipo", porTipo);
        r.put("porPrioridad", porPrioridad);
        r.put("vencidas", venc);
        r.put("abiertasPorResponsable", porResp);
        return r;
    }

    // ===================== CATALOGOS =====================

    @GetMapping("/estudiantes")
    public List<Estudiante> estudiantes() {
        return BaseDeDatos.estudiantes;
    }

    @GetMapping("/responsables")
    public List<Responsable> responsables() {
        return BaseDeDatos.responsables;
    }
}

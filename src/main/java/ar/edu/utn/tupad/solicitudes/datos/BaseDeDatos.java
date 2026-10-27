package ar.edu.utn.tupad.solicitudes.datos;

import ar.edu.utn.tupad.solicitudes.modelo.Estudiante;
import ar.edu.utn.tupad.solicitudes.modelo.Responsable;
import ar.edu.utn.tupad.solicitudes.modelo.Solicitud;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Guarda todos los datos del sistema en memoria y los persiste en un archivo JSON.
 */
public class BaseDeDatos {

    public static List<Solicitud> solicitudes = new ArrayList<>();
    public static List<Estudiante> estudiantes = new ArrayList<>();
    public static List<Responsable> responsables = new ArrayList<>();
    public static int ultimoId = 0;

    private static final String ARCHIVO = "datos/solicitudes.json";
    private static ObjectMapper mapper = new ObjectMapper();

    public static void cargar() {
        File f = new File(ARCHIVO);
        if (!f.exists()) {
            cargarDatosDePrueba();
            guardar();
            return;
        }
        try {
            ArchivoDatos a = mapper.readValue(f, ArchivoDatos.class);
            solicitudes = a.solicitudes;
            estudiantes = a.estudiantes;
            responsables = a.responsables;
            ultimoId = a.ultimoId;
        } catch (Exception e) {
            e.printStackTrace();
            // si el archivo esta roto arrancamos con los datos de prueba
            cargarDatosDePrueba();
        }
    }

    public static void guardar() {
        try {
            File f = new File(ARCHIVO);
            f.getParentFile().mkdirs();
            ArchivoDatos a = new ArchivoDatos();
            a.solicitudes = solicitudes;
            a.estudiantes = estudiantes;
            a.responsables = responsables;
            a.ultimoId = ultimoId;
            mapper.writerWithDefaultPrettyPrinter().writeValue(f, a);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // datos sinteticos para probar el sistema
    public static void cargarDatosDePrueba() {
        solicitudes = new ArrayList<>();
        estudiantes = new ArrayList<>();
        responsables = new ArrayList<>();

        estudiantes.add(new Estudiante("10234", "Lucía Fernández", "lucia.fernandez@alumnos.example.edu.ar", "Tecnicatura Universitaria en Programación"));
        estudiantes.add(new Estudiante("10567", "Martín Gómez", "martin.gomez@alumnos.example.edu.ar", "Tecnicatura Universitaria en Programación"));
        estudiantes.add(new Estudiante("11002", "Valentina Ruiz", "valentina.ruiz@alumnos.example.edu.ar", "Tecnicatura Universitaria en Programación"));
        estudiantes.add(new Estudiante("11345", "Tomás Herrera", "tomas.herrera@alumnos.example.edu.ar", "Ingeniería en Sistemas de Información"));

        responsables.add(new Responsable(1, "Laura Medina", "Bedelía", "bedelia@example.edu.ar", true));
        responsables.add(new Responsable(2, "Jorge Paz", "Secretaría Académica", "secretaria.academica@example.edu.ar", true));
        responsables.add(new Responsable(3, "Carla Ibáñez", "Coordinación de Carrera", "coordinacion@example.edu.ar", true));
        responsables.add(new Responsable(4, "Raúl Sosa", "Tesorería", "tesoreria@example.edu.ar", false));

        Solicitud s1 = new Solicitud();
        s1.setId(1);
        s1.setLegajo("10234");
        s1.setTipo("CERTIFICADO");
        s1.setDescripcion("Certificado de alumno regular para presentar en el trabajo");
        s1.setPrioridad("BAJA");
        s1.setEstado("PENDIENTE");
        s1.setFechaCreacion("2026-09-01");
        s1.setFechaUltimoCambio("2026-09-01");
        s1.getHistorial().add("2026-09-01: solicitud registrada (PENDIENTE)");
        solicitudes.add(s1);

        Solicitud s2 = new Solicitud();
        s2.setId(2);
        s2.setLegajo("10567");
        s2.setTipo("EQUIVALENCIA");
        s2.setDescripcion("Solicito equivalencia de Matemática cursada en otra universidad");
        s2.setPrioridad("MEDIA");
        s2.setEstado("EN_REVISION");
        s2.setResponsableId(3);
        s2.setFechaCreacion("2026-09-10");
        s2.setFechaUltimoCambio("2026-09-12");
        s2.getHistorial().add("2026-09-10: solicitud registrada (PENDIENTE)");
        s2.getHistorial().add("2026-09-12: responsable asignado Carla Ibáñez (Coordinación de Carrera)");
        s2.getHistorial().add("2026-09-12: PENDIENTE -> EN_REVISION");
        solicitudes.add(s2);

        Solicitud s3 = new Solicitud();
        s3.setId(3);
        s3.setLegajo("11002");
        s3.setTipo("REVISION_NOTA");
        s3.setDescripcion("Pido revisión de la nota del segundo parcial de Programación II");
        s3.setPrioridad("ALTA");
        s3.setEstado("RESUELTA");
        s3.setResponsableId(2);
        s3.setFechaCreacion("2026-09-15");
        s3.setFechaUltimoCambio("2026-09-17");
        s3.getHistorial().add("2026-09-15: solicitud registrada (PENDIENTE)");
        s3.getHistorial().add("2026-09-15: responsable asignado Jorge Paz (Secretaría Académica)");
        s3.getHistorial().add("2026-09-15: PENDIENTE -> EN_REVISION");
        s3.getHistorial().add("2026-09-17: EN_REVISION -> RESUELTA (Se corrigió la nota en el acta)");
        solicitudes.add(s3);

        Solicitud s4 = new Solicitud();
        s4.setId(4);
        s4.setLegajo("11345");
        s4.setTipo("CAMBIO_COMISION");
        s4.setDescripcion("Necesito cambiar a la comisión de la noche por horario laboral");
        s4.setPrioridad("MEDIA");
        s4.setEstado("RECHAZADA");
        s4.setResponsableId(1);
        s4.setFechaCreacion("2026-09-20");
        s4.setFechaUltimoCambio("2026-09-22");
        s4.getHistorial().add("2026-09-20: solicitud registrada (PENDIENTE)");
        s4.getHistorial().add("2026-09-21: responsable asignado Laura Medina (Bedelía)");
        s4.getHistorial().add("2026-09-21: PENDIENTE -> EN_REVISION");
        s4.getHistorial().add("2026-09-22: EN_REVISION -> RECHAZADA (No hay cupo en la comisión de la noche)");
        solicitudes.add(s4);

        Solicitud s5 = new Solicitud();
        s5.setId(5);
        s5.setLegajo("10234");
        s5.setTipo("REVISION_NOTA");
        s5.setDescripcion("Revisión del recuperatorio de Base de Datos I");
        s5.setPrioridad("ALTA");
        s5.setEstado("PENDIENTE");
        s5.setFechaCreacion("2026-10-01");
        s5.setFechaUltimoCambio("2026-10-01");
        s5.getHistorial().add("2026-10-01: solicitud registrada (PENDIENTE)");
        solicitudes.add(s5);

        ultimoId = 5;
    }

    public static class ArchivoDatos {
        public List<Solicitud> solicitudes;
        public List<Estudiante> estudiantes;
        public List<Responsable> responsables;
        public int ultimoId;
    }
}

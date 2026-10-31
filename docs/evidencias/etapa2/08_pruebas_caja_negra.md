# Pruebas de caja negra – Etapa 2

**Fecha:** 2026-10-22

**Versión probada:** inicial (tag `estado-inicial`)

**Cómo se ejecutó:** aplicación levantada con `./mvnw spring-boot:run` (Java 21) y los pedidos hechos con `curl` contra `http://localhost:8080`.

**Datos de partida:** se borró la carpeta `datos/` antes de arrancar, así que la sesión empieza con los cinco registros sintéticos de prueba.

**Importante:** los casos se ejecutan en orden y algunos modifican el estado del sistema; el número de caso es el orden real de la sesión.

## Resumen

| # | Caso | Respuesta |
|---|---|---|
| 1 | Reporte resumen inicial | 200 |
| 2 | Filtrar por un estado que no existe | 200 |
| 3 | Filtrar vencidas=false | 200 |
| 4 | Consultar solicitud inexistente | 404 |
| 5 | Filtro con tipo de dato inválido | 400 |
| 6 | Registrar con descripción corta | 400 |
| 7 | Registrar con prioridad inexistente | 400 |
| 8 | Registrar duplicada (mismo tipo abierto) | 409 |
| 9 | Registrar con legajo numérico | 500 |
| 10 | Registrar con JSON malformado | 400 |
| 11 | Asignar responsable inactivo | 409 |
| 12 | Asignar responsable inexistente | 404 |
| 13 | Registrar válida estudiante existente (prioridad calculada) | 201 |
| 14 | Registrar válida estudiante nuevo (alta automática) | 201 |
| 15 | Asignar responsable a solicitud PENDIENTE | 200 |
| 16 | Cambiar estado EN_REVISION -> RESUELTA | 200 |
| 17 | Rechazar sin comentario | 400 |
| 18 | Cambiar a un estado inexistente | 409 |
| 19 | Rechazar con comentario | 200 |
| 20 | Reporte resumen final | 200 |
| 21 | Catálogo de responsables | 200 |

## Detalle

### 1. Reporte resumen inicial

`GET /reportes/resumen`

**Respuesta: `200`**

```json
{
  "total" : 5,
  "porEstado" : {
    "PENDIENTE" : 2,
    "EN_REVISION" : 1,
    "RESUELTA" : 1,
    "RECHAZADA" : 1,
    "CERRADA" : 0
  },
  "porTipo" : {
    "CERTIFICADO" : 1,
    "EQUIVALENCIA" : 1,
    "REVISION_NOTA" : 2,
    "CAMBIO_COMISION" : 1,
    "OTRO" : 0
  },
  "porPrioridad" : {
    "ALTA" : 2,
    "MEDIA" : 2,
    "BAJA" : 1
  },
  "vencidas" : 3,
  "abiertasPorResponsable" : {
    "Carla Ibáñez (Coordinación de Carrera)" : 1
  }
}
```

### 2. Filtrar por un estado que no existe

`GET /solicitudes?estado=LOQUESEA`

**Respuesta: `200`**

```json
[ ]
```

### 3. Filtrar vencidas=false

`GET /solicitudes?vencidas=false`

**Respuesta: `200`**

```json
[ {
  "id" : 3,
  "legajo" : "11002",
  "tipo" : "REVISION_NOTA",
  "descripcion" : "Pido revisión de la nota del segundo parcial de Programación II",
  "prioridad" : "ALTA",
  "estado" : "RESUELTA",
  "responsableId" : 2,
  "fechaCreacion" : "2026-09-15",
  "fechaUltimoCambio" : "2026-09-17",
  "historial" : [ "2026-09-15: solicitud registrada (PENDIENTE)", "2026-09-15: responsable asignado Jorge Paz (Secretaría Académica)", "2026-09-15: PENDIENTE -> EN_REVISION", "2026-09-17: EN_REVISION -> RESUELTA (Se corrigió la nota en el acta)" ]
}, {
  "id" : 5,
  "legajo" : "10234",
  "tipo" : "REVISION_NOTA",
  "descripcion" : "Revisión del recuperatorio de Base de Datos I",
  "prioridad" : "ALTA",
  "estado" : "PENDIENTE",
  "responsableId" : null,
  "fechaCreacion" : "2026-10-01",
  "fechaUltimoCambio" : "2026-10-01",
  "historial" : [ "2026-10-01: solicitud registrada (PENDIENTE)" ]
}, {
  "id" : 2,
  "legajo" : "10567",
  "tipo" : "EQUIVALENCIA",
  "descripcion" : "Solicito equivalencia de Matemática cursada en otra universidad",
  "prioridad" : "MEDIA",
  "estado" : "EN_REVISION",
  "responsableId" : 3,
  "fechaCreacion" : "2026-09-10",
  "fechaUltimoCambio" : "2026-09-12",
  "historial" : [ "2026-09-10: solicitud registrada (PENDIENTE)", "2026-09-12: responsable asignado Carla Ibáñez (Coordinación de Carrera)", "2026-09-12: PENDIENTE -> EN_REVISION" ]
}, {
  "id" : 4,
  "legajo" : "11345",
  "tipo" : "CAMBIO_COMISION",
  "descripcion" : "Necesito cambiar a la comisión de la noche por horario laboral",
  "prioridad" : "MEDIA",
  "estado" : "RECHAZADA",
  "responsableId" : 1,
  "fechaCreacion" : "2026-09-20",
  "fechaUltimoCambio" : "2026-09-22",
  "historial" : [ "2026-09-20: solicitud registrada (PENDIENTE)", "2026-09-21: responsable asignado Laura Medina (Bedelía)", "2026-09-21: PENDIENTE -> EN_REVISION", "2026-09-22: EN_REVISION -> RECHAZADA (No hay cupo en la comisión de la noche)" ]
}, {
  "id" : 1,
  "legajo" : "10234",
  "tipo" : "CERTIFICADO",
  "descripcion" : "Certificado de alumno regular para presentar en el trabajo",
  "prioridad" : "BAJA",
  "estado" : "PENDIENTE",
  "responsableId" : null,
  "fechaCreacion" : "2026-09-01",
  "fechaUltimoCambio" : "2026-09-01",
  "historial" : [ "2026-09-01: solicitud registrada (PENDIENTE)" ]
} ]
```

### 4. Consultar solicitud inexistente

`GET /solicitudes/99`

**Respuesta: `404`**

```json
{
  "error" : "No existe la solicitud 99"
}
```

### 5. Filtro con tipo de dato inválido

`GET /solicitudes?responsableId=abc`

**Respuesta: `400`**

```json
{
  "timestamp" : "2026-10-22T22:50:43.201+00:00",
  "status" : 400,
  "error" : "Bad Request",
  "path" : "/solicitudes"
}
```

### 6. Registrar con descripción corta

`POST /solicitudes`

```json
{"legajo":"10567","tipo":"OTRO","descripcion":"corta"}
```

**Respuesta: `400`**

```json
{
  "error" : "La descripción debe tener al menos 10 caracteres"
}
```

### 7. Registrar con prioridad inexistente

`POST /solicitudes`

```json
{"legajo":"10567","tipo":"OTRO","prioridad":"URGENTE","descripcion":"Pedido con prioridad inexistente"}
```

**Respuesta: `400`**

```json
{
  "error" : "Prioridad inválida: URGENTE"
}
```

### 8. Registrar duplicada (mismo tipo abierto)

`POST /solicitudes`

```json
{"legajo":"10234","tipo":"CERTIFICADO","descripcion":"Otro certificado de alumno regular"}
```

**Respuesta: `409`**

```json
{
  "error" : "El estudiante ya tiene una solicitud abierta de tipo CERTIFICADO (id 1)"
}
```

### 9. Registrar con legajo numérico

`POST /solicitudes`

```json
{"legajo":10234,"tipo":"OTRO","descripcion":"Legajo enviado como número"}
```

**Respuesta: `500`**

```json
{
  "timestamp" : "2026-10-22T22:50:43.299+00:00",
  "status" : 500,
  "error" : "Internal Server Error",
  "path" : "/solicitudes"
}
```

### 10. Registrar con JSON malformado

`POST /solicitudes`

```json
{"legajo":"10567",
```

**Respuesta: `400`**

```json
{
  "timestamp" : "2026-10-22T22:50:43.322+00:00",
  "status" : 400,
  "error" : "Bad Request",
  "path" : "/solicitudes"
}
```

### 11. Asignar responsable inactivo

`PUT /solicitudes/5/responsable`

```json
{"responsableId":4}
```

**Respuesta: `409`**

```json
{
  "error" : "El responsable Raúl Sosa no está activo"
}
```

### 12. Asignar responsable inexistente

`PUT /solicitudes/5/responsable`

```json
{"responsableId":99}
```

**Respuesta: `404`**

```json
{
  "error" : "No existe el responsable 99"
}
```

### 13. Registrar válida de estudiante existente (prioridad calculada)

`POST /solicitudes`

```json
{"legajo":"11002","tipo":"equivalencia","descripcion":"Equivalencia de Inglés técnico aprobada en otra institución"}
```

**Respuesta: `201`**

```json
{
  "id" : 6,
  "legajo" : "11002",
  "tipo" : "EQUIVALENCIA",
  "descripcion" : "Equivalencia de Inglés técnico aprobada en otra institución",
  "prioridad" : "MEDIA",
  "estado" : "PENDIENTE",
  "responsableId" : null,
  "fechaCreacion" : "2026-10-22",
  "fechaUltimoCambio" : "2026-10-22",
  "historial" : [ "2026-10-22: solicitud registrada (PENDIENTE)" ]
}
```

### 14. Registrar válida de estudiante nuevo (alta automática)

`POST /solicitudes`

```json
{"legajo":"12001","nombre":"Sofía Castro","email":"Sofia.Castro@alumnos.example.edu.ar","tipo":"CERTIFICADO","descripcion":"Certificado de materias aprobadas para beca"}
```

**Respuesta: `201`**

```json
{
  "id" : 7,
  "legajo" : "12001",
  "tipo" : "CERTIFICADO",
  "descripcion" : "Certificado de materias aprobadas para beca",
  "prioridad" : "BAJA",
  "estado" : "PENDIENTE",
  "responsableId" : null,
  "fechaCreacion" : "2026-10-22",
  "fechaUltimoCambio" : "2026-10-22",
  "historial" : [ "2026-10-22: solicitud registrada (PENDIENTE)" ]
}
```

### 15. Asignar responsable a solicitud PENDIENTE

`PUT /solicitudes/1/responsable`

```json
{"responsableId":1}
```

**Respuesta: `200`**

```json
{
  "id" : 1,
  "legajo" : "10234",
  "tipo" : "CERTIFICADO",
  "descripcion" : "Certificado de alumno regular para presentar en el trabajo",
  "prioridad" : "BAJA",
  "estado" : "EN_REVISION",
  "responsableId" : 1,
  "fechaCreacion" : "2026-09-01",
  "fechaUltimoCambio" : "2026-10-22",
  "historial" : [ "2026-09-01: solicitud registrada (PENDIENTE)", "2026-10-22: responsable asignado Laura Medina (Bedelía)", "2026-10-22: PENDIENTE -> EN_REVISION" ]
}
```

### 16. Cambiar estado EN_REVISION -> RESUELTA

`PUT /solicitudes/1/estado`

```json
{"estado":"RESUELTA","comentario":"Certificado enviado por correo"}
```

**Respuesta: `200`**

```json
{
  "id" : 1,
  "legajo" : "10234",
  "tipo" : "CERTIFICADO",
  "descripcion" : "Certificado de alumno regular para presentar en el trabajo",
  "prioridad" : "BAJA",
  "estado" : "RESUELTA",
  "responsableId" : 1,
  "fechaCreacion" : "2026-09-01",
  "fechaUltimoCambio" : "2026-10-22",
  "historial" : [ "2026-09-01: solicitud registrada (PENDIENTE)", "2026-10-22: responsable asignado Laura Medina (Bedelía)", "2026-10-22: PENDIENTE -> EN_REVISION", "2026-10-22: EN_REVISION -> RESUELTA (Certificado enviado por correo)" ]
}
```

### 17. Rechazar sin comentario

`PUT /solicitudes/2/estado`

```json
{"estado":"RECHAZADA"}
```

**Respuesta: `400`**

```json
{
  "error" : "Para rechazar una solicitud hay que indicar un comentario"
}
```

### 18. Cambiar a un estado inexistente

`PUT /solicitudes/2/estado`

```json
{"estado":"VOLANDO"}
```

**Respuesta: `409`**

```json
{
  "error" : "Transición no permitida: EN_REVISION -> VOLANDO"
}
```

### 19. Rechazar con comentario

`PUT /solicitudes/2/estado`

```json
{"estado":"RECHAZADA","comentario":"La materia no pertenece al plan de estudios"}
```

**Respuesta: `200`**

```json
{
  "id" : 2,
  "legajo" : "10567",
  "tipo" : "EQUIVALENCIA",
  "descripcion" : "Solicito equivalencia de Matemática cursada en otra universidad",
  "prioridad" : "MEDIA",
  "estado" : "RECHAZADA",
  "responsableId" : 3,
  "fechaCreacion" : "2026-09-10",
  "fechaUltimoCambio" : "2026-10-22",
  "historial" : [ "2026-09-10: solicitud registrada (PENDIENTE)", "2026-09-12: responsable asignado Carla Ibáñez (Coordinación de Carrera)", "2026-09-12: PENDIENTE -> EN_REVISION", "2026-10-22: EN_REVISION -> RECHAZADA (La materia no pertenece al plan de estudios)" ]
}
```

### 20. Reporte resumen final

`GET /reportes/resumen`

**Respuesta: `200`**

```json
{
  "total" : 7,
  "porEstado" : {
    "PENDIENTE" : 3,
    "EN_REVISION" : 0,
    "RESUELTA" : 2,
    "RECHAZADA" : 2,
    "CERRADA" : 0
  },
  "porTipo" : {
    "CERTIFICADO" : 2,
    "EQUIVALENCIA" : 2,
    "REVISION_NOTA" : 2,
    "CAMBIO_COMISION" : 1,
    "OTRO" : 0
  },
  "porPrioridad" : {
    "ALTA" : 2,
    "MEDIA" : 3,
    "BAJA" : 2
  },
  "vencidas" : 1,
  "abiertasPorResponsable" : { }
}
```

### 21. Catálogo de responsables

`GET /responsables`

**Respuesta: `200`**

```json
[ {
  "id" : 1,
  "nombre" : "Laura Medina",
  "area" : "Bedelía",
  "email" : "bedelia@example.edu.ar",
  "activo" : true
}, {
  "id" : 2,
  "nombre" : "Jorge Paz",
  "area" : "Secretaría Académica",
  "email" : "secretaria.academica@example.edu.ar",
  "activo" : true
}, {
  "id" : 3,
  "nombre" : "Carla Ibáñez",
  "area" : "Coordinación de Carrera",
  "email" : "coordinacion@example.edu.ar",
  "activo" : true
}, {
  "id" : 4,
  "nombre" : "Raúl Sosa",
  "area" : "Tesorería",
  "email" : "tesoreria@example.edu.ar",
  "activo" : false
} ]
```

## Error 500: rastro del servidor

El caso del legajo enviado como número genera una excepción en el controlador. El registro de la aplicación muestra:

```text
java.lang.ClassCastException: class java.lang.Integer cannot be cast to class java.lang.String
	at ar.edu.utn.tupad.solicitudes.controlador.SolicitudController.crear(SolicitudController.java:32)
```

El cast que falla está en `SolicitudController.crear`: `String legajo = (String) body.get("legajo");`. El `Map<String, Object>` no valida el tipo y el cast falla en tiempo de ejecución.

El número de línea del rastro es parte de la salida original del servidor sobre la versión inicial; después del refactoring puede cambiar y no se usa como referencia en el informe.

## Persistencia: reinicio de la aplicación

Terminada la sesión, se detuvo y volvió a levantar la aplicación **sin borrar** `datos/`. La solicitud creada en el caso del registro válido de un estudiante existente seguía disponible:

`GET /solicitudes/6` → `200`

```json
{
  "id" : 6,
  "legajo" : "11002",
  "tipo" : "EQUIVALENCIA",
  "descripcion" : "Equivalencia de Inglés técnico aprobada en otra institución",
  "prioridad" : "MEDIA",
  "estado" : "PENDIENTE",
  "responsableId" : null,
  "fechaCreacion" : "2026-10-22",
  "fechaUltimoCambio" : "2026-10-22",
  "historial" : [ "2026-10-22: solicitud registrada (PENDIENTE)" ]
}
```

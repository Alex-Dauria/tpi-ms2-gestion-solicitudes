# Línea base del comportamiento externo

- **Versión del código:** `estado-inicial` (commit `ef5f080`)
- **Fecha de ejecución:** 2026-10-27 12:02
- **Generado con:** `scripts/linea_base.ps1`
- **Datos de partida:** datos de prueba sintéticos (se borra `datos/` antes de arrancar)
- La fecha del día de la ejecución se reemplaza por `<HOY>` para poder comparar corridas de días distintos.

| Caso | Pedido | Código |
|---|---|---|
| 1 | `POST /solicitudes` | 201 |
| 2 | `POST /solicitudes` | 201 |
| 3 | `POST /solicitudes` | 400 |
| 4 | `POST /solicitudes` | 409 |
| 5 | `PUT /solicitudes/1/responsable` | 200 |
| 6 | `PUT /solicitudes/5/responsable` | 409 |
| 7 | `PUT /solicitudes/1/estado` | 200 |
| 8 | `PUT /solicitudes/4/estado` | 409 |
| 9 | `GET /solicitudes?prioridad=alta` | 200 |
| 10 | `GET /solicitudes?vencidas=true` | 200 |
| 11 | `GET /solicitudes/99` | 404 |
| 12 | `GET /reportes/resumen` | 200 |

### 1. Registrar solicitud de un estudiante existente (prioridad calculada por tipo)

`POST /solicitudes`

```json
{"legajo":"11002","tipo":"equivalencia","descripcion":"Equivalencia de Inglés técnico aprobada en otra institución"}
```

**Respuesta:** `201`

```json
{
  "id" : 6,
  "legajo" : "11002",
  "tipo" : "EQUIVALENCIA",
  "descripcion" : "Equivalencia de Inglés técnico aprobada en otra institución",
  "prioridad" : "MEDIA",
  "estado" : "PENDIENTE",
  "responsableId" : null,
  "fechaCreacion" : "<HOY>",
  "fechaUltimoCambio" : "<HOY>",
  "historial" : [ "<HOY>: solicitud registrada (PENDIENTE)" ]
}
```

### 2. Registrar solicitud de un estudiante nuevo (alta automática)

`POST /solicitudes`

```json
{"legajo":"12001","nombre":"Sofía Castro","email":"Sofia.Castro@alumnos.example.edu.ar","tipo":"CERTIFICADO","descripcion":"Certificado de materias aprobadas para beca"}
```

**Respuesta:** `201`

```json
{
  "id" : 7,
  "legajo" : "12001",
  "tipo" : "CERTIFICADO",
  "descripcion" : "Certificado de materias aprobadas para beca",
  "prioridad" : "BAJA",
  "estado" : "PENDIENTE",
  "responsableId" : null,
  "fechaCreacion" : "<HOY>",
  "fechaUltimoCambio" : "<HOY>",
  "historial" : [ "<HOY>: solicitud registrada (PENDIENTE)" ]
}
```

### 3. Rechazo por validación (descripción corta)

`POST /solicitudes`

```json
{"legajo":"10567","tipo":"OTRO","descripcion":"corta"}
```

**Respuesta:** `400`

```json
{
  "error" : "La descripción debe tener al menos 10 caracteres"
}
```

### 4. Rechazo por regla de negocio (ya tiene una solicitud abierta del mismo tipo)

`POST /solicitudes`

```json
{"legajo":"10234","tipo":"CERTIFICADO","descripcion":"Otro certificado de alumno regular"}
```

**Respuesta:** `409`

```json
{
  "error" : "El estudiante ya tiene una solicitud abierta de tipo CERTIFICADO (id 1)"
}
```

### 5. Asignar responsable (pasa automáticamente a EN_REVISION)

`PUT /solicitudes/1/responsable`

```json
{"responsableId":1}
```

**Respuesta:** `200`

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
  "fechaUltimoCambio" : "<HOY>",
  "historial" : [ "2026-09-01: solicitud registrada (PENDIENTE)", "<HOY>: responsable asignado Laura Medina (Bedelía)", "<HOY>: PENDIENTE -> EN_REVISION" ]
}
```

### 6. Asignar responsable inactivo

`PUT /solicitudes/5/responsable`

```json
{"responsableId":4}
```

**Respuesta:** `409`

```json
{
  "error" : "El responsable Raúl Sosa no está activo"
}
```

### 7. Cambiar estado EN_REVISION -> RESUELTA

`PUT /solicitudes/1/estado`

```json
{"estado":"RESUELTA","comentario":"Certificado enviado por correo"}
```

**Respuesta:** `200`

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
  "fechaUltimoCambio" : "<HOY>",
  "historial" : [ "2026-09-01: solicitud registrada (PENDIENTE)", "<HOY>: responsable asignado Laura Medina (Bedelía)", "<HOY>: PENDIENTE -> EN_REVISION", "<HOY>: EN_REVISION -> RESUELTA (Certificado enviado por correo)" ]
}
```

### 8. Transición no permitida (RECHAZADA es estado final)

`PUT /solicitudes/4/estado`

```json
{"estado":"PENDIENTE"}
```

**Respuesta:** `409`

```json
{
  "error" : "Transición no permitida: RECHAZADA -> PENDIENTE"
}
```

### 9. Buscar / filtrar: solicitudes de prioridad ALTA

`GET /solicitudes?prioridad=alta`

**Respuesta:** `200`

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
} ]
```

### 10. Buscar / filtrar: solicitudes vencidas

`GET /solicitudes?vencidas=true`

**Respuesta:** `200`

```json
[ {
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
} ]
```

### 11. Consultar una solicitud inexistente

`GET /solicitudes/99`

**Respuesta:** `404`

```json
{
  "error" : "No existe la solicitud 99"
}
```

### 12. Reporte resumen

`GET /reportes/resumen`

**Respuesta:** `200`

```json
{
  "total" : 7,
  "porEstado" : {
    "PENDIENTE" : 3,
    "EN_REVISION" : 1,
    "RESUELTA" : 2,
    "RECHAZADA" : 1,
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
  "vencidas" : 2,
  "abiertasPorResponsable" : {
    "Carla Ibáñez (Coordinación de Carrera)" : 1
  }
}
```

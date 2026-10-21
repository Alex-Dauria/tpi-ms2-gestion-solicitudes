# Registro de ejecución – versión inicial

Registro completo de las operaciones del sistema sobre la versión inicial (tag `estado-inicial`): 12 pedidos HTTP con su respuesta textual y su código. Complementa las capturas de Swagger UI de esta carpeta, que se muestran en la sección 1.10 del informe. Este registro incluye además los casos de error que no se capturaron, y va en el anexo.

- **Fecha de ejecución:** 2026-10-20
- **Cómo se generó:** se levantó la aplicación con `java -jar target/gestion-solicitudes-0.1.0.jar`, partiendo de los datos de prueba sintéticos (se borró `datos/` antes de arrancar), y se ejecutaron los pedidos con `curl`. La ejecución la hizo Claude Code como parte de IA-01 (ver `docs/09_uso_ia.md`); la salida es la respuesta real del sistema.
- Las fechas `2026-10-20` del historial corresponden al día de la ejecución.

| Caso | Operación | Código |
|---|---|---|
| 1, 2 | Registrar solicitud (estudiante existente y estudiante nuevo) | 201 |
| 3 | Registrar con descripción corta | 400 |
| 4 | Registrar una solicitud duplicada | 409 |
| 5 | Asignar responsable | 200 |
| 6 | Asignar responsable inactivo | 409 |
| 7 | Cambiar estado | 200 |
| 8 | Transición no permitida | 409 |
| 9, 10 | Filtrar por prioridad y vencidas | 200 |
| 11 | Consultar solicitud inexistente | 404 |
| 12 | Reporte resumen | 200 |

### 1. Registrar solicitud de un estudiante existente (prioridad calculada por tipo)

```http
POST /solicitudes

{"legajo":"11002","tipo":"equivalencia","descripcion":"Equivalencia de Inglés técnico aprobada en otra institución"}
```

```json
{
  "id" : 6,
  "legajo" : "11002",
  "tipo" : "EQUIVALENCIA",
  "descripcion" : "Equivalencia de Inglés técnico aprobada en otra institución",
  "prioridad" : "MEDIA",
  "estado" : "PENDIENTE",
  "responsableId" : null,
  "fechaCreacion" : "2026-10-20",
  "fechaUltimoCambio" : "2026-10-20",
  "historial" : [ "2026-10-20: solicitud registrada (PENDIENTE)" ]
}
[HTTP 201]
```

### 2. Registrar solicitud de un estudiante nuevo (alta automática)

```http
POST /solicitudes

{"legajo":"12001","nombre":"Sofía Castro","email":"Sofia.Castro@alumnos.example.edu.ar","tipo":"CERTIFICADO","descripcion":"Certificado de materias aprobadas para beca"}
```

```json
{
  "id" : 7,
  "legajo" : "12001",
  "tipo" : "CERTIFICADO",
  "descripcion" : "Certificado de materias aprobadas para beca",
  "prioridad" : "BAJA",
  "estado" : "PENDIENTE",
  "responsableId" : null,
  "fechaCreacion" : "2026-10-20",
  "fechaUltimoCambio" : "2026-10-20",
  "historial" : [ "2026-10-20: solicitud registrada (PENDIENTE)" ]
}
[HTTP 201]
```

### 3. Rechazo por validación (descripción corta)

```http
POST /solicitudes

{"legajo":"10567","tipo":"OTRO","descripcion":"corta"}
```

```json
{
  "error" : "La descripción debe tener al menos 10 caracteres"
}
[HTTP 400]
```

### 4. Rechazo por regla de negocio (ya tiene una solicitud abierta del mismo tipo)

```http
POST /solicitudes

{"legajo":"10234","tipo":"CERTIFICADO","descripcion":"Otro certificado de alumno regular"}
```

```json
{
  "error" : "El estudiante ya tiene una solicitud abierta de tipo CERTIFICADO (id 1)"
}
[HTTP 409]
```

### 5. Asignar responsable (pasa automáticamente a EN_REVISION)

```http
PUT /solicitudes/1/responsable

{"responsableId":1}
```

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
  "fechaUltimoCambio" : "2026-10-20",
  "historial" : [ "2026-09-01: solicitud registrada (PENDIENTE)", "2026-10-20: responsable asignado Laura Medina (Bedelía)", "2026-10-20: PENDIENTE -> EN_REVISION" ]
}
[HTTP 200]
```

### 6. Asignar responsable inactivo

```http
PUT /solicitudes/5/responsable

{"responsableId":4}
```

```json
{
  "error" : "El responsable Raúl Sosa no está activo"
}
[HTTP 409]
```

### 7. Cambiar estado EN_REVISION -> RESUELTA

```http
PUT /solicitudes/1/estado

{"estado":"RESUELTA","comentario":"Certificado enviado por correo"}
```

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
  "fechaUltimoCambio" : "2026-10-20",
  "historial" : [ "2026-09-01: solicitud registrada (PENDIENTE)", "2026-10-20: responsable asignado Laura Medina (Bedelía)", "2026-10-20: PENDIENTE -> EN_REVISION", "2026-10-20: EN_REVISION -> RESUELTA (Certificado enviado por correo)" ]
}
[HTTP 200]
```

### 8. Transición no permitida (RECHAZADA es estado final)

```http
PUT /solicitudes/4/estado

{"estado":"PENDIENTE"}
```

```json
{
  "error" : "Transición no permitida: RECHAZADA -> PENDIENTE"
}
[HTTP 409]
```

### 9. Buscar / filtrar: solicitudes de prioridad ALTA

```http
GET /solicitudes?prioridad=alta
```

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
[HTTP 200]
```

### 10. Buscar / filtrar: solicitudes vencidas

```http
GET /solicitudes?vencidas=true
```

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
[HTTP 200]
```

### 11. Consultar una solicitud inexistente

```http
GET /solicitudes/99
```

```json
{
  "error" : "No existe la solicitud 99"
}
[HTTP 404]
```

### 12. Reporte resumen

```http
GET /reportes/resumen
```

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
[HTTP 200]
```


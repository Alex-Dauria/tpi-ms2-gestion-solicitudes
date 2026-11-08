# 5. Refactoring seguro

**Consolida:** Marina · **Revisión cruzada:** Silvia
**Aportes por zona:** A – Alex (#8) · B – Marina (#9) · C – Silvia (#10)

> La consigna pide **al menos 4** refactorings documentados que incluyan, como mínimo, **Extract Method**, **Move Method** (o reubicación de responsabilidad) e **Introduce Parameter Object** (o simplificación equivalente de parámetros). Cada refactoring tiene que mostrar el antes y el después y la evidencia de que el comportamiento externo no cambió.

## 5.1 Resumen

La numeración definitiva sigue el orden de las fichas de cada zona: R-01 a R-10 corresponden a RA-01 a RA-10, R-11 a R-15 a RB-01 a RB-05, y R-16 a R-18 a RC-01 a RC-03. Se conserva el identificador original para mantener la trazabilidad con commits, pruebas, documentos y registros de IA; no se reescribe el historial. Esta agrupación no supone una secuencia temporal global: B y C trabajaron en paralelo después de A.

Las extracciones de métodos y clases, los objetos de parámetros y la operación de dominio reorganizan responsabilidades o contratos internos. R-07 y R-15 son mejoras acotadas de claridad y consistencia mediante constantes. R-08 es una limpieza menor de dos líneas comentadas: no modifica código ejecutable ni demuestra una reducción importante de complejidad.

Se cumplen los mínimos de la consigna sin depender de esa limpieza: **Extract Method** (por ejemplo, R-01 y R-14), **Move Method o reubicación de responsabilidad** (R-10 y R-11) e **Introduce Parameter Object** (R-03 y R-16), con más de cuatro refactorings estructurales documentados.

| ID | Original | Técnica / cambio | Hallazgo relacionado | Principio / práctica | Zona / autor | Commit |
|---|---|---|---|---|---|---|
| R-01 | RA-01 | Extract Method (`respuestaError`) | S-02 (SA-02) | DRY, Clean Code (manejo de errores) | A – Alex | [9ccf463](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/9ccf463) |
| R-02 | RA-02 | Extract Method (`prioridadPorDefecto`) | S-01 (SA-01), S-08 (SA-04) | SRP, funciones que hacen una sola cosa | A – Alex | [ea76173](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/ea76173) |
| R-03 | RA-03 | Introduce Parameter Object (`DatosSolicitud`) | S-07 (SA-03) | Clean Code (pocos argumentos) | A – Alex | [0e12ea7](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/0e12ea7) |
| R-04 | RA-04 | Extract Method (una validación por campo) | S-01 (SA-01) | SRP, un nivel de abstracción por función | A – Alex | [6342a84](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/6342a84) |
| R-05 | RA-05 | Extract Method (búsqueda, validación y alta del estudiante) | S-15 (SA-05) | SRP, nombres que dicen lo que hacen | A – Alex | [be52cd0](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/be52cd0) |
| R-06 | RA-06 | Extract Method (`buscarSolicitudAbierta`, `construirSolicitud`) | S-01 (SA-01) | SRP, separación consulta/comando | A – Alex | [2bbda70](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/2bbda70) |
| R-07 | RA-07 | Replace Magic Number with Symbolic Constant | S-08 (SA-04) | Clean Code (sin valores mágicos) | A – Alex | [df0dbd9](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/df0dbd9) |
| R-08 | RA-08 | Remove Dead Code | Observaciones complementarias de A: comentarios y código muerto; sin S adicional | Limpieza menor: comentarios obsoletos | A – Alex | [c69ad8f](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/c69ad8f) |
| R-09 | RA-09 | Extract Class (`ReglasRegistro`, con las validaciones) | S-01 (SA-01) | SRP, cohesión | A – Alex | [c6ca13c](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/c6ca13c) |
| R-10 | RA-10 | Move Method (prioridad y creación del estudiante) | S-01 (SA-01), S-15 (SA-05) | SRP, cohesión | A – Alex | [d7996e3](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/d7996e3) |
| R-11 | RB-01 | Move Method / reubicación de responsabilidad (`permiteTransicionA`) | S-03 (SB-01) | SRP parcial, cohesión | B – Marina | [f0e9de8](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/f0e9de8) |
| R-12 | RB-02 | Replace Conditional with Lookup Table | Complementa S-03 (SB-01) | Reglas explícitas, OCP limitado | B – Marina | [daedb7b](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/daedb7b) |
| R-13 | RB-03 | Encapsulación de cambios de estado e historial (`cambiarEstado`) | S-04 (SB-04), parcial | Encapsulación, cohesión | B – Marina | [c9dc80a](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/c9dc80a) |
| R-14 | RB-04 | Extract Method (`buscarSolicitudPorId`) | S-09 (SB-02) | DRY, nombres y funciones | B – Marina | [8e02326](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/8e02326) |
| R-15 | RB-05 | Replace Magic Number with Named Constant | S-11 (SB-05) | Clean Code, constante descriptiva | B – Marina | [4d39d0d](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/4d39d0d) |
| R-16 | RC-01 | Introduce Parameter Object (`FiltroSolicitudes`) | S-12 (SC-01) | Clean Code (pocos argumentos) | C – Silvia | [0624db5](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/0624db5) |
| R-17 | RC-02 | Encapsulate Collection (catálogos) | S-14 (SC-05), alcance sobre la colección devuelta | Encapsulación de la colección devuelta; alcance limitado | C – Silvia | [faf4849](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/faf4849) |
| R-18 | RC-03 | Extract Class (`PoliticaVencimiento`) | S-05 (SC-02), con limitación fuera de prioridades válidas | SRP, DRY, sin valores mágicos | C – Silvia | [63d7b64](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/63d7b64) |

## 5.2 Detalle

Cada ficha conserva su identificador original RA/RB/RC junto al definitivo R y el hash del cambio implementado. Las referencias internas que usan los identificadores originales siguen siendo válidas mediante la correspondencia de 5.1. El hallazgo se identifica con su número S y su referencia original SA/SB/SC; un refactoring puede abordar parte de un hallazgo o complementar otro paso sin crear un problema nuevo.

Los bloques Antes/Después son extractos históricos: salvo indicación expresa, comparan el padre del commit con ese commit, no todo el proceso con el tag `estado-inicial`. Las métricas y verificaciones pertenecen al momento indicado. Se conservan los resultados históricos de 45 pruebas para RA-01 a RA-08; 69 para RA-09, RA-10, RB-01, RB-02 y RC-01 a RC-03; 82 para RB-03; y 84 para RB-04 y RB-05. Esta consolidación documental no constituye una ejecución nueva de la suite integrada ni de la línea base.

Los campos se presentan como hallazgo relacionado, zona/autor, commit, motivación, antes/después, pasos, verificación histórica, comportamiento externo y beneficio/alcance. Las limitaciones técnicas originales se mantienen.

### 5.2.A Zona A – Registro (Alex)

Diez refactorings sobre `SolicitudController.registrarSolicitud` y el armado de las respuestas de error, **uno por commit**, con las pruebas en verde después de cada uno. RA-01 a RA-08 se aplicaron en la rama `zona-a/refactoring`, verificados con las 31 pruebas de la zona A (CP-A01 a CP-A25) y, como red adicional, las 14 de la zona B, porque RA-01 también toca métodos de esa zona. RA-09 y RA-10 se aplicaron después en `zona-a/reglas-registro`, con las 69 pruebas de las tres zonas.

| ID | Técnica | Smell que resuelve | Principio / práctica | Commit |
|---|---|---|---|---|
| R-01 (RA-01) | Extract Method (`respuestaError`) | S-02 (SA-02) | DRY, Clean Code (manejo de errores) | [`9ccf463`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/9ccf463) |
| R-02 (RA-02) | Extract Method (`prioridadPorDefecto`) | S-01 (SA-01), S-08 (SA-04) | SRP, funciones que hacen una sola cosa | [`ea76173`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/ea76173) |
| R-03 (RA-03) | Introduce Parameter Object (`DatosSolicitud`) | S-07 (SA-03) | Clean Code (pocos argumentos) | [`0e12ea7`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/0e12ea7) |
| R-04 (RA-04) | Extract Method (una validación por campo) | S-01 (SA-01) | SRP, un nivel de abstracción por función | [`6342a84`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/6342a84) |
| R-05 (RA-05) | Extract Method (búsqueda, validación y alta del estudiante) | S-15 (SA-05) | SRP, nombres que dicen lo que hacen | [`be52cd0`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/be52cd0) |
| R-06 (RA-06) | Extract Method (`buscarSolicitudAbierta`, `construirSolicitud`) | S-01 (SA-01) | SRP, separación consulta/comando | [`2bbda70`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/2bbda70) |
| R-07 (RA-07) | Replace Magic Number with Symbolic Constant | S-08 (SA-04) | Clean Code (sin valores mágicos) | [`df0dbd9`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/df0dbd9) |
| R-08 (RA-08) | Remove Dead Code | Observaciones complementarias de A: comentarios y código muerto; sin S adicional | Limpieza menor: comentarios obsoletos | [`c69ad8f`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/c69ad8f) |
| R-09 (RA-09) | Extract Class (`ReglasRegistro`, con las validaciones) | S-01 (SA-01) | SRP, cohesión | [`c6ca13c`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/c6ca13c) |
| R-10 (RA-10) | Move Method (prioridad y creación del estudiante) | S-01 (SA-01), S-15 (SA-05) | SRP, cohesión | [`d7996e3`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/d7996e3) |

**Resultado sobre el método principal:**

| | Antes (`estado-inicial`) | Después de RA-08 | Después de RA-10 |
|---|---|---|---|
| Líneas de `registrarSolicitud` | 105 | 39 | 33 |
| Decisiones (`if`, `for`, `case`, `?`) en `registrarSolicitud` | 19 | 5 | 4 |
| Respuestas de error armadas a mano en el controlador | 22 | 0 (un solo método) | 0 |
| Líneas de `SolicitudController` | 441 | 492 | 399 |

La complejidad no desaparece: se reparte en métodos chicos con nombre propio. Lo que baja es la del método principal, que ahora se lee como un índice: validar, dar de alta al estudiante si hace falta, controlar duplicados, construir, persistir y responder.

Después de RA-08 el controlador tenía **más** líneas que al principio: extraer métodos ordena el código, pero lo deja en la misma clase. RA-09 y RA-10 resuelven eso sacando las reglas del registro a una clase propia. Las 399 líneas todavía incluyen el código de las zonas B y C.

#### R-01 (original: RA-01) – Extract Method: una sola forma de armar las respuestas de error

- **Hallazgo relacionado:** SA-02 / S-02 (código duplicado en el armado de las respuestas de error).
- **Zona / autor:** A – Alex.
- **Commit:** [9ccf463](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/9ccf463); estado anterior: `9ccf463^`.
- **Motivación:** el mismo par de líneas (mapa con la clave `"error"` + `ResponseEntity`) estaba escrito 22 veces. Es el primer refactoring porque achica todos los bloques que se extraen después.

**Antes** (`9ccf463^`):

```java
Map<String, Object> m = new HashMap<>();
// ...
if (legajo == null || legajo.trim().isEmpty()) {
    m.put("error", "El legajo es obligatorio");
    return ResponseEntity.badRequest().body(m);
}
```

**Después** (`9ccf463`):

```java
if (legajo == null || legajo.trim().isEmpty()) {
    return respuestaError(HttpStatus.BAD_REQUEST, "El legajo es obligatorio");
}

private ResponseEntity<Object> respuestaError(HttpStatus status, String mensaje) {
    return ResponseEntity.status(status).body(Map.of("error", mensaje));
}
```

- **Pasos seguidos:** se creó `respuestaError(status, mensaje)`, se reemplazaron las 22 apariciones conservando cada código HTTP y cada mensaje, y se eliminaron los cuatro mapas `m` que quedaron sin uso.
- **Verificación histórica:** 45 de 45 pruebas en verde. Las de la zona A verifican en cada error que el cuerpo tiene una sola clave `error` con el mensaje exacto.
- **Comportamiento externo:** sin cambios.
- **Beneficio y alcance:** el formato de error se cambia en un solo lugar. Es el paso previo para unificarlo con los errores del framework, si se decide más adelante (*Replace Error Code with Exception*).

#### R-02 (original: RA-02) – Extract Method: prioridad por defecto según el tipo

- **Hallazgo relacionado:** SA-01 / S-01 y SA-04 / S-08.
- **Zona / autor:** A – Alex.
- **Commit:** [ea76173](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/ea76173); estado anterior: `ea76173^`.
- **Motivación:** el `switch` de RN-04 estaba en el medio de las validaciones. Es una función pura (entra el tipo, sale la prioridad), así que es el paso de menor riesgo.

**Antes** (`ea76173^`):

```java
} else {
    // la prioridad se calcula segun el tipo
    switch (tipo) {
        case "REVISION_NOTA": prioridad = "ALTA"; break;
        case "EQUIVALENCIA":
        case "CAMBIO_COMISION": prioridad = "MEDIA"; break;
        default: prioridad = "BAJA";
    }
}
```

**Después** (`ea76173`):

```java
} else {
    prioridad = prioridadPorDefecto(tipo);
}
```

- **Pasos seguidos:** se movió el `switch` a `prioridadPorDefecto(tipo)`, que devuelve el valor en lugar de asignar una variable.
- **Verificación histórica:** CP-A02 (los cinco tipos) y la suite completa: 45 de 45.
- **Comportamiento externo:** sin cambios.
- **Beneficio y alcance:** el comentario que explicaba el bloque pasó a ser el nombre del método.

#### R-03 (original: RA-03) – Introduce Parameter Object: `DatosSolicitud`

- **Hallazgo relacionado:** SA-03 / S-07 (lista larga de parámetros y *Data Clumps*).
- **Zona / autor:** A – Alex.
- **Commit:** [0e12ea7](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/0e12ea7); estado anterior: `0e12ea7^`.
- **Motivación:** siete `String` seguidos en la firma permiten invertir argumentos sin que el compilador avise.

**Antes** (`0e12ea7^`):

```java
return registrarSolicitud(legajo, nombre, email, carrera, tipo, prioridad, descripcion);

private ResponseEntity<Object> registrarSolicitud(String legajo, String nombre, String email, String carrera,
                                                  String tipo, String prioridad, String descripcion) {
```

**Después** (`0e12ea7`):

```java
DatosSolicitud datos = new DatosSolicitud(
        (String) body.get("legajo"), (String) body.get("nombre"), (String) body.get("email"),
        (String) body.get("carrera"), (String) body.get("tipo"), (String) body.get("prioridad"),
        (String) body.get("descripcion"));
return registrarSolicitud(datos);

record DatosSolicitud(String legajo, String nombre, String email, String carrera,
                      String tipo, String prioridad, String descripcion) { }
```

- **Pasos seguidos:** se creó el `record`, se armó en `crear` y se cambió la firma de `registrarSolicitud`, que al principio copiaba cada campo en una variable local para no tocar el resto del método.
- **Verificación histórica:** 45 de 45. CP-A25 sigue en verde: el legajo numérico sigue fallando igual.
- **Comportamiento externo:** sin cambios, a propósito. `crear` sigue leyendo el mapa con los mismos casts. Recibir directamente un objeto desde el JSON habría convertido el `500` del legajo numérico en otra respuesta, y eso es un cambio de comportamiento que no corresponde a un refactoring.
- **Beneficio y alcance:** un solo parámetro con nombre; los datos del pedido viajan juntos.

#### R-04 (original: RA-04) – Extract Method: una validación por campo

- **Hallazgo relacionado:** SA-01 / S-01 (método largo con responsabilidades mezcladas).
- **Zona / autor:** A – Alex.
- **Commit:** [6342a84](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/6342a84); estado anterior: `6342a84^`.
- **Motivación:** las validaciones ocupaban la mitad del método y mezclaban normalización (`toUpperCase`) con control.

**Antes** (`estado-inicial`, referencia del diagnóstico): cuatro bloques `if` seguidos, con reasignación de `tipo` y `prioridad` en el medio (líneas 46 a 92 de `estado-inicial`).

Fragmento del estado inmediatamente anterior a `6342a84` (`0e12ea7995b833feb0e9439de9e616cd991326d5`), en `SolicitudController.registrarSolicitud`. Solo se reduce la sangría para mostrar el extracto:

```java
// validaciones
if (legajo == null || legajo.trim().isEmpty()) {
    return respuestaError(HttpStatus.BAD_REQUEST, "El legajo es obligatorio");
}
if (!legajo.matches("\\d{5}")) {
    return respuestaError(HttpStatus.BAD_REQUEST, "El legajo debe tener 5 dígitos");
}
if (tipo == null || tipo.trim().isEmpty()) {
    return respuestaError(HttpStatus.BAD_REQUEST, "El tipo es obligatorio");
}
tipo = tipo.toUpperCase();
if (!tipo.equals("CERTIFICADO") && !tipo.equals("EQUIVALENCIA") && !tipo.equals("REVISION_NOTA")
        && !tipo.equals("CAMBIO_COMISION") && !tipo.equals("OTRO")) {
    return respuestaError(HttpStatus.BAD_REQUEST, "Tipo de solicitud inválido: " + tipo);
}
if (descripcion == null || descripcion.trim().length() < 10) {
    return respuestaError(HttpStatus.BAD_REQUEST, "La descripción debe tener al menos 10 caracteres");
}
if (descripcion.length() > 500) {
    return respuestaError(HttpStatus.BAD_REQUEST, "La descripción no puede superar los 500 caracteres");
}
if (prioridad != null && !prioridad.trim().isEmpty()) {
    prioridad = prioridad.toUpperCase();
    if (!prioridad.equals("ALTA") && !prioridad.equals("MEDIA") && !prioridad.equals("BAJA")) {
        return respuestaError(HttpStatus.BAD_REQUEST, "Prioridad inválida: " + prioridad);
    }
} else {
    prioridad = prioridadPorDefecto(tipo);
}
```

**Después** (`6342a84`):

```java
// Se valida campo por campo, en este orden, y se corta en el primer error
Optional<String> error = validarLegajo(datos.legajo())
        .or(() -> validarTipo(datos.tipo()))
        .or(() -> validarDescripcion(datos.descripcion()))
        .or(() -> validarPrioridad(datos.prioridad()));
if (error.isPresent()) {
    return respuestaError(HttpStatus.BAD_REQUEST, error.get());
}
```

- **Pasos seguidos:** cada validación devuelve el mensaje de error o nada. `Optional.or` solo evalúa la siguiente si la anterior no encontró error, así que se conserva el orden y el corte en el primer error. La normalización a mayúsculas se hace dentro de cada validación para armar el mensaje, y una sola vez en el método principal para usar el valor.
- **Verificación histórica:** CP-A22 (cascada de errores en el orden legajo → tipo → descripción → prioridad → estudiante) y CP-A23 (el mínimo de la descripción se valida antes que el máximo); 45 de 45.
- **Comportamiento externo:** sin cambios.
- **Beneficio y alcance:** cada regla (RN-01 a RN-04) tiene su método con su comentario, y el orden de validación se lee en cuatro líneas.

#### R-05 (original: RA-05) – Extract Method: búsqueda, validación y alta del estudiante

- **Hallazgo relacionado:** SA-05 / S-15 (alta implícita del estudiante dentro del registro).
- **Zona / autor:** A – Alex.
- **Commit:** [be52cd0](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/be52cd0); estado anterior: `be52cd0^`.
- **Motivación:** el bloque rotulado `// buscar estudiante` además validaba y creaba. Extraerlo como un solo método con nombre de consulta habría escondido una escritura.

**Antes** (`be52cd0^`, extracto verificable del controlador):

```java
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
        return respuestaError(HttpStatus.BAD_REQUEST, "El estudiante no existe: se requieren nombre y email para darlo de alta");
    }
    if (!email.contains("@") || !email.contains(".")) {
        return respuestaError(HttpStatus.BAD_REQUEST, "El email no es válido");
    }
    est = new Estudiante();
    est.setLegajo(legajo);
    est.setNombre(nombre.trim());
    est.setEmail(email.trim().toLowerCase());
    est.setCarrera(carrera == null ? "Sin especificar" : carrera);
    BaseDeDatos.estudiantes.add(est);
}
```

**Después** (`be52cd0`):

```java
// Si el estudiante no existe, se lo da de alta con los datos del pedido (RN-05)
if (buscarEstudiantePorLegajo(legajo).isEmpty()) {
    Optional<String> errorAlta = validarDatosDeAlta(datos.nombre(), datos.email());
    if (errorAlta.isPresent()) {
        return respuestaError(HttpStatus.BAD_REQUEST, errorAlta.get());
    }
    BaseDeDatos.estudiantes.add(crearEstudiante(datos));
}
```

- **Pasos seguidos:** se separó en tres métodos: `buscarEstudiantePorLegajo` (solo consulta), `validarDatosDeAlta` y `crearEstudiante`. El agregado a la lista queda visible en el método principal.
- **Verificación histórica:** CP-A04, CP-A05, CP-A19 y CP-A20; 45 de 45.
- **Comportamiento externo:** sin cambios: la validación del alta sigue ocurriendo después de la de la solicitud.
- **Beneficio y alcance:** la escritura queda a la vista, y la búsqueda de estudiantes queda lista para moverse a un repositorio en la zona C.

#### R-06 (original: RA-06) – Extract Method: solicitud abierta y construcción de la solicitud

- **Hallazgo relacionado:** SA-01 / S-01.
- **Zona / autor:** A – Alex.
- **Commit:** [2bbda70](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/2bbda70); estado anterior: `2bbda70^`.
- **Motivación:** el control de duplicados recorría la lista dentro del método y la construcción ocupaba doce líneas de setters.

**Antes** (`2bbda70^`, extracto verificable del controlador):

```java
// no puede tener otra solicitud abierta del mismo tipo
for (Solicitud s : BaseDeDatos.solicitudes) {
    if (s.getLegajo().equals(legajo) && s.getTipo().equals(tipo)
            && (s.getEstado().equals("PENDIENTE") || s.getEstado().equals("EN_REVISION"))) {
        return respuestaError(HttpStatus.CONFLICT, "El estudiante ya tiene una solicitud abierta de tipo " + tipo + " (id " + s.getId() + ")");
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
```

**Después** (`2bbda70`):

```java
Optional<Solicitud> abierta = buscarSolicitudAbierta(legajo, tipo);
if (abierta.isPresent()) {
    return respuestaError(HttpStatus.CONFLICT, "El estudiante ya tiene una solicitud abierta de tipo "
            + tipo + " (id " + abierta.get().getId() + ")");
}

BaseDeDatos.ultimoId++;
Solicitud solicitud = construirSolicitud(BaseDeDatos.ultimoId, legajo, tipo, descripcion.trim(), prioridad);
BaseDeDatos.solicitudes.add(solicitud);
BaseDeDatos.guardar();
```

- **Pasos seguidos:** `buscarSolicitudAbierta` devuelve la solicitud en conflicto, no un `boolean`, porque el mensaje del `409` necesita su id. `construirSolicitud` arma la solicitud con su fecha y su primera entrada en el historial. El incremento de `ultimoId`, el agregado y `guardar()` quedan juntos en el método principal, como bloque de persistencia para la zona C.
- **Verificación histórica:** CP-A01, CP-A21 y CP-A24; 45 de 45.
- **Comportamiento externo:** sin cambios.
- **Beneficio y alcance:** la regla "está abierta" queda en un único método, listo para que la zona B la mueva a la entidad.

#### R-07 (original: RA-07) – Replace Magic Number with Symbolic Constant

- **Hallazgo relacionado:** SA-04 / S-08 (tipos, prioridades y límites escritos como valores sueltos).
- **Zona / autor:** A – Alex.
- **Commit:** [df0dbd9](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/df0dbd9); estado anterior: `df0dbd9^`.

- **Motivación:** dar nombre a los límites y valores del registro y compartirlos con los mensajes para evitar desajustes.

**Antes** (`estado-inicial`, referencia del diagnóstico):

```java
if (!tipo.equals("CERTIFICADO") && !tipo.equals("EQUIVALENCIA") && !tipo.equals("REVISION_NOTA")
        && !tipo.equals("CAMBIO_COMISION") && !tipo.equals("OTRO")) {
// ...
if (descripcion.length() > 500) {
    m.put("error", "La descripción no puede superar los 500 caracteres");
```

**Después** (`df0dbd9`):

```java
private static final int LARGO_MAXIMO_DESCRIPCION = 500;
private static final List<String> TIPOS_VALIDOS =
        List.of("CERTIFICADO", "EQUIVALENCIA", "REVISION_NOTA", "CAMBIO_COMISION", "OTRO");
// ...
if (!TIPOS_VALIDOS.contains(tipoNormalizado)) {
// ...
if (descripcion.length() > LARGO_MAXIMO_DESCRIPCION) {
    return Optional.of("La descripción no puede superar los " + LARGO_MAXIMO_DESCRIPCION + " caracteres");
```

- **Pasos seguidos:** se crearon constantes para los dígitos del legajo, los límites de la descripción, los tipos y prioridades válidos y la carrera por defecto. Los mensajes se arman con las mismas constantes, así que el número del mensaje no puede quedar desincronizado con la regla.
- **Verificación histórica:** CP-A07, CP-A10, CP-A15 y CP-A16 (límites exactos); 45 de 45.
- **Comportamiento externo:** sin cambios: los mensajes generados son idénticos.
- **Beneficio y alcance:** cambiar un límite o agregar un tipo se hace en un solo lugar del registro. El paso siguiente sería un `enum` para el tipo y la prioridad (*Replace Type Code with Class*), que queda propuesto porque también lo usa el reporte de la zona C.

#### R-08 (original: RA-08) – Remove Dead Code

- **Hallazgo relacionado:** comentarios y código muerto (notas de la zona A para #6).
- **Zona / autor:** A – Alex.
- **Commit:** [c69ad8f](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/c69ad8f); estado anterior: `c69ad8f^`.
- **Motivación:** retirar comentarios obsoletos que ya no describen una acción mantenida o un requisito asignado. Es limpieza de legibilidad, no una reorganización de responsabilidades.

**Antes** (`c69ad8f^`):

```java
BaseDeDatos.guardar();
// TODO: avisar por mail al estudiante
// System.out.println("Se creo la solicitud " + s.getId());
return ResponseEntity.status(HttpStatus.CREATED).body(solicitud);
```

**Después** (`c69ad8f`):

```java
BaseDeDatos.guardar();
return ResponseEntity.status(HttpStatus.CREATED).body(solicitud);
```

- **Pasos seguidos:** se eliminaron `// TODO: avisar por mail al estudiante`, que no tenía dueño ni issue, y `// System.out.println("Se creo la solicitud " + s.getId());`. Los comentarios que rotulaban bloques ya se habían ido en RA-02 a RA-06, al convertirse en nombres de métodos.
- **Verificación histórica:** 45 de 45.
- **Comportamiento externo:** sin cambios.
- **Beneficio y alcance:** se retiran dos líneas comentadas (TODO y println); no se elimina código ejecutable ni se atribuye una mejora de arquitectura, SRP o complejidad relevante. Las extracciones anteriores explican los demás cambios del método.

#### R-09 (original: RA-09) – Extract Class: las validaciones salen del controlador

- **Hallazgo relacionado:** SA-01 / S-01 (responsabilidades mezcladas en el controlador).
- **Zona / autor:** A – Alex.
- **Commit:** [c6ca13c](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/c6ca13c); estado anterior: `c6ca13c^`.
- **Motivación:** RA-04 separó una validación por método, pero siguieron en `SolicitudController`, que además atiende los pedidos HTTP. Una regla de negocio cambia por motivos distintos que un endpoint.

**Antes** (`c6ca13c^`):

```java
// En SolicitudController
Optional<String> error = validarLegajo(datos.legajo())
        .or(() -> validarTipo(datos.tipo()))
        .or(() -> validarDescripcion(datos.descripcion()))
        .or(() -> validarPrioridad(datos.prioridad()));
```

**Después** (`c6ca13c`):

```java
// En SolicitudController
Optional<String> error = ReglasRegistro.validarSolicitud(datos);

// En servicio/ReglasRegistro
public static Optional<String> validarSolicitud(DatosSolicitud datos) {
    return validarLegajo(datos.legajo())
            .or(() -> validarTipo(datos.tipo()))
            .or(() -> validarDescripcion(datos.descripcion()))
            .or(() -> validarPrioridad(datos.prioridad()));
}
```

- **Pasos seguidos:** se creó `ReglasRegistro` en el paquete nuevo `servicio`, y se movieron las validaciones de RN-01 a RN-05 con sus constantes. `DatosSolicitud` pasó al mismo paquete y se hizo público, para que la clase nueva no dependa del controlador.
- **Verificación histórica:** CP-A08 a CP-A23, en especial CP-A22 (el orden de los errores); 69 de 69.
- **Comportamiento externo:** sin cambios: el orden de las validaciones y los mensajes son los mismos.
- **Beneficio y alcance:** las reglas se pueden leer y cambiar sin mirar nada de HTTP. `ReglasRegistro` no conoce `ResponseEntity` ni `BaseDeDatos`.

#### R-10 (original: RA-10) – Move Method: prioridad y creación del estudiante

- **Hallazgo relacionado:** SA-01 / S-01 y SA-05 / S-15.
- **Zona / autor:** A – Alex.
- **Commit:** [d7996e3](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/d7996e3); estado anterior: `d7996e3^`.
- **Motivación:** el cálculo de la prioridad (RN-04) y las reglas para crear un estudiante (RN-05) también son del registro, y no usan nada del controlador.

**Antes** (`d7996e3^`):

```java
String prioridad = ReglasRegistro.estaVacio(datos.prioridad())
        ? prioridadPorDefecto(tipo)
        : datos.prioridad().toUpperCase();
// ...
BaseDeDatos.estudiantes.add(crearEstudiante(datos));
```

**Después** (`d7996e3`):

```java
String prioridad = ReglasRegistro.prioridadDe(datos);
// ...
BaseDeDatos.estudiantes.add(ReglasRegistro.crearEstudiante(datos));
```

- **Pasos seguidos:** se movieron `prioridadPorDefecto` y `crearEstudiante`, con la constante de la carrera por defecto, y `estaVacio` volvió a ser privado.
- **Verificación histórica:** CP-A02, CP-A03 y CP-A04; 69 de 69.
- **Comportamiento externo:** sin cambios.
- **Beneficio y alcance:** `registrarSolicitud` solo coordina. Las escrituras en `BaseDeDatos` siguen a la vista en ese método, listas para la zona C.

**Qué queda en el controlador de esta zona:** `crear` y `registrarSolicitud`, que coordinan el pedido, y `buscarEstudiantePorLegajo`, `buscarSolicitudAbierta` y `construirSolicitud`, que dependen del acceso a datos (zona C) y de las reglas de estado (zona B).

**Verificación histórica de la zona completa:** integrado con las pruebas finales de las otras zonas, el código refactorizado pasa **69 de 69 pruebas** (31 de la zona A, 17 de la zona B y 21 de la zona C). De caja negra, la línea base da **14 de 14 casos iguales** a la de `estado-inicial`, tanto después de RA-08 ([evidencias/pruebas/zona-a/linea_base_refactoring_zona_a.md](evidencias/pruebas/zona-a/linea_base_refactoring_zona_a.md)) como después de RA-10 ([evidencias/pruebas/zona-a/linea_base_reglas_registro.md](evidencias/pruebas/zona-a/linea_base_reglas_registro.md)). Informes de las pruebas después del refactoring: [evidencias/pruebas/zona-a/despues-refactoring/](evidencias/pruebas/zona-a/despues-refactoring/).

### 5.2.B Zona B – Ciclo de vida (Marina)

Cinco refactorings aplicados en `zona-b/refactoring`, uno por commit, sobre la base `8e81f7c`. Las fichas usan el identificador original SB y su equivalente final S del catálogo de la sección 3. Los ejemplos **Antes** corresponden al padre de cada commit, para mostrar pasos sucesivos y no atribuir al tag `estado-inicial` código incorporado por refactorings anteriores. Los hashes originales están integrados en main y se conservan como referencia; no se enlazan evidencias temporales.

| ID | Técnica | Hallazgo relacionado | Principio / práctica | Commit |
|---|---|---|---|---|
| R-11 (RB-01) | Move Method / reubicación de responsabilidad (`permiteTransicionA`) | S-03 (SB-01) | SRP parcial, cohesión | `f0e9de8` |
| R-12 (RB-02) | Replace Conditional with Lookup Table | Complementa S-03 (SB-01) | Reglas explícitas, OCP limitado | `daedb7b` |
| R-13 (RB-03) | Encapsulación de cambios de estado e historial (`cambiarEstado`) | S-04 (SB-04), parcial | Encapsulación, cohesión | `c9dc80a` |
| R-14 (RB-04) | Extract Method (`buscarSolicitudPorId`) | S-09 (SB-02) | DRY, nombres y funciones | `8e02326` |
| R-15 (RB-05) | Replace Magic Number with Named Constant | S-11 (SB-05) | Clean Code, constante descriptiva | `4d39d0d` |

#### R-11 (original: RB-01) – Move Method: validación de transiciones en `Solicitud`

- **Hallazgo relacionado:** SB-01 / S-03, lógica de transiciones ubicada en el controlador. Se reubica la decisión sobre transiciones; el controlador conserva las validaciones necesarias para las respuestas HTTP.
- **Zona / autor:** B – Marina.
- **Commit:** [f0e9de8](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/f0e9de8); estado anterior: `f0e9de8^`.
- **Motivación:** la entidad debe consultar las transiciones usando su propio estado, sin que el controlador concentre esa decisión de negocio.

**Antes** (`f0e9de8^`, extracto de `SolicitudController.cambiarEstado`):

```java
boolean valido = false;
if (actual.equals("PENDIENTE")) {
    if (nuevo.equals("EN_REVISION") || nuevo.equals("RECHAZADA")) {
        valido = true;
    }
} else if (actual.equals("EN_REVISION")) {
    if (nuevo.equals("RESUELTA") || nuevo.equals("RECHAZADA") || nuevo.equals("PENDIENTE")) {
        valido = true;
    }
}
// ... resto de la cadena condicional
```

**Después** (`f0e9de8`):

```java
// SolicitudController.cambiarEstado
boolean valido = sol.permiteTransicionA(nuevo);

// Solicitud: extracto; la cadena completa se conserva en este paso
public boolean permiteTransicionA(String nuevoEstado) {
    boolean valido = false;
    if (estado.equals("PENDIENTE")) {
        if (nuevoEstado.equals("EN_REVISION") || nuevoEstado.equals("RECHAZADA")) {
            valido = true;
        }
    }
    // ... mismas ramas para los demás estados
    return valido;
}
```

- **Pasos seguidos:** se agregó `permiteTransicionA(String nuevoEstado)` y se trasladó la cadena condicional sustituyendo el estado leído por el atributo de la entidad. El controlador delega la consulta y conserva la obligatoriedad del estado, la conversión a mayúsculas, el `409`, el responsable y el comentario, en el mismo orden.
- **Verificación histórica:** 69 de 69 pruebas: A 31, B 17 y C 21; sin fallos, errores ni omisiones. CP-B03 a CP-B06, CP-B11 y CP-B14 a CP-B16 cubren transiciones, restricciones, estados finales y normalización.
- **Comportamiento externo:** sin cambios en transiciones, códigos HTTP, mensajes, historial, fechas ni persistencia.
- **Beneficio y alcance:** la decisión queda junto a los datos que utiliza. En este paso solo se consulta si la transición está permitida; todavía no se encapsulan sus efectos ni se elimina el setter público.

#### R-12 (original: RB-02) – Replace Conditional with Lookup Table: tabla de transiciones

- **Hallazgo relacionado:** complementa SB-01 / S-03, simplificando la decisión trasladada en RB-01. No resuelve SB-03 / S-10: los estados siguen siendo `String`.
- **Zona / autor:** B – Marina.
- **Commit:** [daedb7b](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/daedb7b); estado anterior: `daedb7b^`.
- **Motivación:** expresar las transiciones como datos inmutables y separar su definición de la consulta.

**Antes** (`daedb7b^`, extracto de `Solicitud.permiteTransicionA`):

```java
boolean valido = false;
if (estado.equals("PENDIENTE")) {
    if (nuevoEstado.equals("EN_REVISION") || nuevoEstado.equals("RECHAZADA")) {
        valido = true;
    }
}
// ... mismas ramas para los demás estados
return valido;
```

**Después** (`daedb7b`, en `Solicitud`):

```java
private static final Map<String, Set<String>> TRANSICIONES_PERMITIDAS = Map.of(
        "PENDIENTE", Set.of("EN_REVISION", "RECHAZADA"),
        "EN_REVISION", Set.of("RESUELTA", "RECHAZADA", "PENDIENTE"),
        "RESUELTA", Set.of("CERRADA", "EN_REVISION"),
        "CERRADA", Collections.emptySet(),
        "RECHAZADA", Collections.emptySet());

public boolean permiteTransicionA(String nuevoEstado) {
    return TRANSICIONES_PERMITIDAS.getOrDefault(estado, Collections.emptySet()).contains(nuevoEstado);
}
```

- **Pasos seguidos:** se creó el mapa estático con conjuntos inmutables, se incluyeron los estados finales sin destinos y se reemplazó la cadena por una consulta. No se modificaron el controlador ni `setEstado()`.
- **Verificación histórica:** 69 de 69 pruebas: A 31, B 17 y C 21; sin fallos, errores ni omisiones. Además se compararon RB-01 y RB-02 en 81 combinaciones: las 25 entre estados válidos y combinaciones con valores desconocidos, vacíos, minúsculas y `null`; coincidieron resultados o tipos de excepción.
- **Comportamiento externo:** sin cambios; se conservan las siete transiciones permitidas y la ausencia de salidas desde `CERRADA` y `RECHAZADA`.
- **Beneficio y alcance:** las reglas se leen en una tabla y la consulta queda estable. Cambiar las reglas exige editar esa tabla; no se afirma cumplimiento completo de OCP ni seguridad de tipos mediante un `enum`.

#### R-13 (original: RB-03) – Encapsulación: estado, historial y fecha en una operación

- **Hallazgo relacionado:** SB-04 / S-04, modificación del estado sin protección en la entidad; resolución **parcial**.
- **Zona / autor:** B – Marina.
- **Commit:** [c9dc80a](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/c9dc80a); estado anterior: `c9dc80a^`.
- **Motivación:** reunir las comprobaciones de una transición operativa y sus efectos para evitar cambios de estado sin el historial o la fecha correspondientes.

**Antes** (`c9dc80a^`, actualización final de `SolicitudController.cambiarEstado`):

```java
String hoy = LocalDate.now().toString();
String linea = hoy + ": " + actual + " -> " + nuevo;
if (comentario != null && !comentario.trim().isEmpty()) {
    linea += " (" + comentario.trim() + ")";
}
sol.getHistorial().add(linea);
sol.setEstado(nuevo);
sol.setFechaUltimoCambio(hoy);
```

**Después** (`c9dc80a`, delegación desde el mismo punto del controlador):

```java
String hoy = LocalDate.now().toString();
sol.cambiarEstado(nuevo, comentario, hoy);
```

La nueva operación en `Solicitud` valida antes de modificar:

```java
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
```

En `asignarResponsable`, después de establecer el responsable y agregar su entrada al historial, el bloque anterior era:

```java
if (sol.getEstado().equals("PENDIENTE")) {
    sol.getHistorial().add(hoy + ": PENDIENTE -> EN_REVISION");
    sol.setEstado("EN_REVISION");
}
sol.setFechaUltimoCambio(hoy);
```

El bloque queda así, sin repetir la actualización de fecha cuando hay transición:

```java
if (sol.getEstado().equals("PENDIENTE")) {
    sol.cambiarEstado("EN_REVISION", null, hoy);
} else {
    sol.setFechaUltimoCambio(hoy);
}
```

- **Pasos seguidos:** se agregó la operación con comprobación de transición, responsable para `EN_REVISION` y comentario para `RECHAZADA`, antes de cualquier modificación. Se delegaron las actualizaciones desde ambos endpoints. El controlador mantiene sus comprobaciones y su orden para conservar códigos y mensajes HTTP; la entidad también se protege ante llamadas directas. La fecha se recibe para compartir el mismo `hoy`, sin leer nuevamente el reloj.
- **Verificación histórica:** 82 de 82 pruebas: A 31, B 30 y C 21; sin fallos, errores ni omisiones. Se conservan las 69 anteriores y se agregan 13 verificaciones de B: seis rechazos sin cambios parciales, cuatro transiciones con comprobación de formato y fecha, asignación automática, asignación sin transición y guardar/cargar con Jackson conservando estado, fechas e historial. Línea base HTTP: **14 de 14 casos iguales** a la referencia original.
- **Comportamiento externo:** sin cambios. En la asignación automática se conservan primero el evento de asignación y después el de transición, con la misma fecha. Creación inicial, datos sintéticos, formato JSON y persistencia permanecen intactos.
- **Beneficio y alcance:** los cambios operativos agrupan sus efectos y rechazan condiciones inválidas sin modificaciones parciales. `setEstado()` sigue público para inicialización y compatibilidad de persistencia; permite eludir esta operación, por lo que SB-04 / S-04 no se considera completamente resuelto. Tampoco se afirma protección completa de todas las invariantes de la entidad.

#### R-14 (original: RB-04) – Extract Method: búsqueda de solicitudes por ID

- **Hallazgo relacionado:** SB-02 / S-09, búsqueda repetida en `obtener`, `cambiarEstado` y `asignarResponsable`.
- **Zona / autor:** B – Marina.
- **Commit:** [8e02326](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/8e02326); estado anterior: `8e02326^`.
- **Motivación:** centralizar el recorrido y conservar una única forma de encontrar la primera solicitud cuyo ID coincide.

**Antes** (`8e02326^`, bloque repetido en los dos endpoints de modificación):

```java
Solicitud sol = null;
for (Solicitud s : BaseDeDatos.solicitudes) {
    if (s.getId() == id) {
        sol = s;
        break;
    }
}
```

**Después** (`8e02326`, helper privado y llamada desde los tres endpoints):

```java
private Solicitud buscarSolicitudPorId(int id) {
    for (Solicitud solicitud : BaseDeDatos.solicitudes) {
        if (solicitud.getId() == id) {
            return solicitud;
        }
    }
    return null;
}

// En cada endpoint, antes de las demás validaciones
Solicitud sol = buscarSolicitudPorId(id);
if (sol == null) {
    return respuestaError(HttpStatus.NOT_FOUND, "No existe la solicitud " + id);
}
```

- **Pasos seguidos:** se extrajo el recorrido a `buscarSolicitudPorId(int id)` y se sustituyeron las tres búsquedas. `obtener` conserva el `200` con la solicitud y el mismo `404` cuando no existe; los endpoints de modificación mantienen todas las comprobaciones posteriores en su orden original.
- **Verificación histórica:** 84 de 84 pruebas: A 31, B 32 y C 21; sin fallos, errores ni omisiones. Dos pruebas nuevas envían `{}` a cambio de estado y asignación de una solicitud inexistente y comprueban que prevalezca el `404` con el mensaje exacto. CP-B03 ya verifica modificar la solicitud 2, fuera de la primera posición. Línea base HTTP: **14 de 14 casos iguales**.
- **Comportamiento externo:** sin cambios en respuestas, precedencia de errores, reglas ni operaciones de RB-03. Se devuelve la instancia original, sin copiarla ni modificarla durante la búsqueda.
- **Beneficio y alcance:** se elimina el recorrido duplicado. La búsqueda sigue siendo lineal y el controlador continúa dependiendo de `BaseDeDatos`; no se introduce un repositorio ni se alteran otras búsquedas.

#### R-15 (original: RB-05) – Replace Magic Number with Named Constant: límite de asignaciones

- **Hallazgo relacionado:** SB-05 / S-11, número mágico para el límite de asignaciones.
- **Zona / autor:** B – Marina.
- **Commit:** [4d39d0d](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/4d39d0d); estado anterior: `4d39d0d^`.
- **Motivación:** nombrar el límite de la RN-12 y evitar que la condición y el mensaje utilicen valores independientes.

**Antes** (`4d39d0d^`, en `SolicitudController.asignarResponsable`):

```java
if (cant >= 5) {
    return respuestaError(HttpStatus.CONFLICT, "El responsable ya tiene 5 solicitudes abiertas");
}
```

**Después** (`4d39d0d`, constante de clase y uso):

```java
private static final int MAX_SOLICITUDES_POR_RESPONSABLE = 5;

// En asignarResponsable
if (cant >= MAX_SOLICITUDES_POR_RESPONSABLE) {
    return respuestaError(HttpStatus.CONFLICT, "El responsable ya tiene " + MAX_SOLICITUDES_POR_RESPONSABLE + " solicitudes abiertas");
}
```

- **Pasos seguidos:** se agregó la constante privada en el controlador, donde permanece la regla, y se sustituyó el literal en la comparación y el mensaje. No se modificaron otras constantes ni pruebas.
- **Verificación histórica:** 84 de 84 pruebas: A 31, B 32 y C 21; sin fallos, errores ni omisiones. CP-B10 verifica el rechazo al alcanzar cinco solicitudes abiertas; CP-B14 conserva la reapertura mediante cambio de estado aunque el responsable tenga cinco solicitudes abiertas adicionales. Línea base HTTP: **14 de 14 casos iguales**.
- **Comportamiento externo:** sin cambios: límite de cinco, mismas validaciones, HTTP `409` y mensaje exacto `El responsable ya tiene 5 solicitudes abiertas`.
- **Beneficio y alcance:** el valor tiene un nombre y se comparte entre condición y mensaje. El conteo y la regla permanecen en el controlador; este paso no reubica responsabilidades adicionales.

#### Decisiones pendientes y alcance de la zona B

- **SB-03 / S-10 permanece pendiente:** se conserva `String` para los estados. Un `enum EstadoSolicitud` requiere revisar consultas, reportes, datos sintéticos y persistencia, además de mantener los valores JSON, el tratamiento de estados inválidos y la normalización de entradas. La compatibilidad con Jackson puede conservarse, pero necesita una intervención específica y coordinación entre zonas. Se posterga para no ampliar estos cinco cambios; RB-02 solo reemplaza la estructura de decisión.
- **SB-04 / S-04 queda parcialmente resuelto:** RB-03 protege las transiciones operativas utilizadas por los endpoints, pero el setter público sigue permitiendo cambios directos. Separar completamente inicialización, rehidratación y modificación del dominio queda fuera de este alcance.
- **Resultados históricos:** RB-01 y RB-02 se verificaron con 69 pruebas; RB-03 agregó 13 verificaciones de B y alcanzó 82; RB-04 agregó otras dos y alcanzó 84; RB-05 conserva ese total. La distribución final es A 31, B 32 y C 21. Las comparaciones HTTP de RB-03, RB-04 y RB-05 dieron 14/14 contra `estado-inicial`; no se atribuye una corrida HTTP independiente a RB-01 o RB-02.

### 5.2.C Zona C – Consultas y persistencia (Silvia)

Tres refactorings sobre `SolicitudController`, aplicados en la rama `zona-c/refactoring`, uno por commit, con las 69 pruebas en verde después de cada uno (31 de zona A, 17 de zona B y 21 de zona C).

| ID | Técnica | Smell que resuelve | Principio / práctica | Commit |
|---|---|---|---|---|
| R-16 (RC-01) | Introduce Parameter Object (`FiltroSolicitudes`) | S-12 (SC-01) | Clean Code (pocos argumentos) | [`0624db5`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/0624db5) |
| R-17 (RC-02) | Encapsulate Collection (catálogos) | S-14 (SC-05), alcance sobre la colección devuelta | Encapsulación de la colección devuelta; alcance limitado | [`faf4849`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/faf4849) |
| R-18 (RC-03) | Extract Class (`PoliticaVencimiento`) | S-05 (SC-02), con limitación fuera de prioridades válidas | SRP, DRY, sin valores mágicos | [`63d7b64`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/63d7b64) |

#### R-16 (original: RC-01) – Introduce Parameter Object: `FiltroSolicitudes`

- **Hallazgo relacionado:** S-12 (SC-01 – lista de parámetros larga en `filtrar`).
- **Zona / autor:** C – Silvia.
- **Commit:** [0624db5](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/0624db5); estado anterior: `0624db5^`.
- **Motivación:** `filtrar` recibía seis parámetros sueltos (cuatro `String`), lo que dificultaba leer la llamada y agregar filtros. Se agrupan en un objeto con nombre.

**Antes** (`0624db5^`):

```java
private List<Solicitud> filtrar(String estado, String tipo, String prioridad, String legajo,
                                Integer responsableId, Boolean vencidas) { ... }
```

**Después** (`0624db5`):

```java
private List<Solicitud> filtrar(FiltroSolicitudes filtro) {
    String estado = filtro.estado();
    // ...
}
```

- **Pasos seguidos:** crear el record `FiltroSolicitudes`; cambiar la firma de `filtrar`; construir el objeto en `listar`. Un commit.
- **Verificación histórica:** 69 pruebas en verde antes y después; CP-C01 a CP-C21 de la zona C.
- **Comportamiento externo:** sin cambios (mismos códigos, mensajes y resultados).
- **Beneficio y alcance:** la llamada se lee como un solo objeto; los criterios viajan juntos en un objeto con nombre y `filtrar` tiene un solo parámetro.

#### R-17 (original: RC-02) – Encapsulate Collection: catálogos inmutables

- **Hallazgo relacionado:** S-14 (SC-05 – los catálogos devuelven las listas internas).
- **Zona / autor:** C – Silvia.
- **Commit:** [faf4849](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/faf4849); estado anterior: `faf4849^`.
- **Motivación:** `estudiantes()` y `responsables()` devolvían la lista interna de `BaseDeDatos`, permitiendo mutar el estado global.

**Antes** (`faf4849^`):

```java
return BaseDeDatos.estudiantes;
```

**Después** (`faf4849`):

```java
return List.copyOf(BaseDeDatos.estudiantes);
```

- **Pasos seguidos:** reemplazar el retorno por `List.copyOf` en ambos endpoints. Un commit.
- **Verificación histórica:** 69 pruebas en verde antes y después; CP-C11 y CP-C12 verifican tamaño y contenido.
- **Comportamiento externo:** sin cambios para lectura; un intento de mutación ahora lanza `UnsupportedOperationException`.
- **Beneficio y alcance:** se impide modificar estructuralmente la colección devuelta. La copia es superficial: sus elementos siguen siendo mutables y las listas públicas de `BaseDeDatos` continúan accesibles. No se afirma encapsulación completa del estado global.

#### R-18 (original: RC-03) – Extract Class: `PoliticaVencimiento`

- **Hallazgo relacionado:** S-05 (SC-02, regla de vencimiento duplicada en `filtrar` y `resumen`).
- **Zona / autor:** C – Silvia.
- **Commit:** [63d7b64](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/63d7b64); estado anterior: `63d7b64^`.
- **Motivación:** la regla de vencimiento (RN-14) estaba duplicada en `filtrar` y en `resumen`, con plazos distintos por prioridad. Se extrae a una sola clase.

**Antes** (`63d7b64^`):

```java
// filtrar
if (s.getPrioridad().equals("ALTA") && dias > 3) { flag = true; }
if (s.getPrioridad().equals("MEDIA") && dias > 7) { flag = true; }
if (s.getPrioridad().equals("BAJA") && dias > 15) { flag = true; }

// resumen
int limite = 15;
if (s.getPrioridad().equals("ALTA")) { limite = 3; }
else if (s.getPrioridad().equals("MEDIA")) { limite = 7; }
if (dias > limite) { venc++; }
```

**Después** (`63d7b64`):

```java
// filtrar
if (vencidas != null && vencidas && !PoliticaVencimiento.estaVencida(s, LocalDate.now())) {
    ok = false;
}

// resumen
if (PoliticaVencimiento.estaVencida(s, LocalDate.now())) {
    venc++;
}
```

- **Pasos seguidos:** crear `PoliticaVencimiento` en `servicio`; reemplazar el cálculo en `filtrar` y `resumen`; quitar `ChronoUnit` del controlador. Un commit.
- **Verificación histórica:** 69 pruebas en verde antes y después; CP-C07 y CP-C21 cubren los límites. Si se cambia el plazo de ALTA a 4 días, CP-C21 falla.
- **Comportamiento externo:** las verificaciones históricas declaradas respaldan los resultados y conteos para las prioridades válidas ALTA, MEDIA y BAJA. No se afirma equivalencia absoluta para prioridades desconocidas.
- **Beneficio y alcance:** la regla de vencimiento vive en un solo lugar; el controlador deja de calcularla.
- **Limitación pendiente de revisión técnica:** la lectura del commit muestra que `plazoEnDias` devuelve 15 días por defecto. Antes, el filtro solo marcaba vencimiento para ALTA, MEDIA o BAJA; una prioridad desconocida no coincidía con ninguna de esas condiciones. La política compartida podría tratarla ahora como BAJA, mientras que el reporte anterior ya usaba un plazo por defecto de 15 días. Debe revisarse con la autora el alcance fuera de los valores válidos, sin dar por verificada su equivalencia. En esta consolidación no se cambia código ni se incorpora una nueva prueba o regla.

#### Lo que no se aborda en esta zona

- **S-06 (estado global y persistencia en `BaseDeDatos`):** atraviesa todos los endpoints y las pruebas de las tres zonas. A un día de la entrega, refactorizarlo requeriría coordinar con las zonas A y B; se deja como mejora pendiente.
- **S-13 (`resumen` con mapas de `String` y `get(...) + 1`):** el `NullPointerException` es latente con los datos actuales; se mantiene la estructura actual para no tocar el contrato del reporte.

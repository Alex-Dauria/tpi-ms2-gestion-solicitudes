# 5. Refactoring seguro

**Consolida:** Marina · **Revisión cruzada:** Silvia
**Aportes por zona:** A – Alex (#8) · B – Marina (#9) · C – Silvia (#10)

> La consigna pide **al menos 4** refactorings documentados que incluyan, como mínimo, **Extract Method**, **Move Method** (o reubicación de responsabilidad) e **Introduce Parameter Object** (o simplificación equivalente de parámetros). Cada refactoring tiene que mostrar el antes y el después y la evidencia de que el comportamiento externo no cambió.

## 5.1 Resumen

| ID | Técnica | Smell que resuelve | Principio | Zona / autora | Commit |
|---|---|---|---|---|---|
| R-01 | Extract Method | | | A – Alex | |
| R-02 | | | | A – Alex | |
| R-03 | Move Method | | | B – Marina | |
| R-04 | | | | B – Marina | |
| R-05 | Introduce Parameter Object (`FiltroSolicitudes`) | S-12 (SC-01 – lista de parámetros larga en `filtrar`) | Clean Code (pocos argumentos) | C – Silvia | [`0624db5`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/0624db5) |
| R-06 | Encapsulate Collection (catálogos) | S-14 (SC-05 – catálogos devuelven las listas internas) | Encapsulamiento, bajo acoplamiento | C – Silvia | [`faf4849`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/faf4849) |

## 5.2 Detalle

_Cada zona carga sus fichas en su apartado, con ids provisorios (RA-01, RB-01, RC-01…). Marina las pasa a la numeración final R-01, R-02… y completa la tabla 5.1 en #12._

### 5.2.A Zona A – Registro (Alex)

Ocho refactorings sobre `SolicitudController.registrarSolicitud` y el armado de las respuestas de error, aplicados en la rama `zona-a/refactoring`, **uno por commit**, con las pruebas en verde después de cada uno: las 31 de la zona A (CP-A01 a CP-A25) y, como red adicional, las 14 de la zona B, porque RA-01 también toca métodos de esa zona.

| ID | Técnica | Smell que resuelve | Principio / práctica | Commit |
|---|---|---|---|---|
| RA-01 | Extract Method (`respuestaError`) | SA-02 | DRY, Clean Code (manejo de errores) | [`9ccf463`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/9ccf463) |
| RA-02 | Extract Method (`prioridadPorDefecto`) | SA-01, SA-04 | SRP, funciones que hacen una sola cosa | [`ea76173`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/ea76173) |
| RA-03 | Introduce Parameter Object (`DatosSolicitud`) | SA-03 | Clean Code (pocos argumentos) | [`0e12ea7`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/0e12ea7) |
| RA-04 | Extract Method (una validación por campo) | SA-01 | SRP, un nivel de abstracción por función | [`6342a84`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/6342a84) |
| RA-05 | Extract Method (búsqueda, validación y alta del estudiante) | SA-05 | SRP, nombres que dicen lo que hacen | [`be52cd0`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/be52cd0) |
| RA-06 | Extract Method (`buscarSolicitudAbierta`, `construirSolicitud`) | SA-01 | SRP, separación consulta/comando | [`2bbda70`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/2bbda70) |
| RA-07 | Replace Magic Number with Symbolic Constant | SA-04 | Clean Code (sin valores mágicos) | [`df0dbd9`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/df0dbd9) |
| RA-08 | Remove Dead Code | Notas para #6 (comentarios y código muerto) | Clean Code (comentarios) | [`c69ad8f`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/c69ad8f) |

**Resultado sobre el método principal:**

| | Antes (`estado-inicial`) | Después |
|---|---|---|
| Líneas de `registrarSolicitud` | 105 | 39 |
| Decisiones (`if`, `for`, `case`, `?`) en `registrarSolicitud` | 19 | 5 |
| Respuestas de error armadas a mano en el controlador | 22 | 0 (un solo método) |

La complejidad no desaparece: se reparte en métodos chicos con nombre propio. Lo que baja es la del método principal, que ahora se lee como un índice: validar, dar de alta al estudiante si hace falta, controlar duplicados, construir, persistir y responder.

#### RA-01 – Extract Method: una sola forma de armar las respuestas de error

- **Smell que resuelve:** SA-02 (código duplicado en el armado de las respuestas de error).
- **Zona / autor:** A – Alex.
- **Commit:** [`9ccf463`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/9ccf463).
- **Motivación:** el mismo par de líneas (mapa con la clave `"error"` + `ResponseEntity`) estaba escrito 22 veces. Es el primer refactoring porque achica todos los bloques que se extraen después.

**Antes:**

```java
Map<String, Object> m = new HashMap<>();
// ...
if (legajo == null || legajo.trim().isEmpty()) {
    m.put("error", "El legajo es obligatorio");
    return ResponseEntity.badRequest().body(m);
}
```

**Después:**

```java
if (legajo == null || legajo.trim().isEmpty()) {
    return respuestaError(HttpStatus.BAD_REQUEST, "El legajo es obligatorio");
}

private ResponseEntity<Object> respuestaError(HttpStatus status, String mensaje) {
    return ResponseEntity.status(status).body(Map.of("error", mensaje));
}
```

- **Pasos seguidos:** se creó `respuestaError(status, mensaje)`, se reemplazaron las 22 apariciones conservando cada código HTTP y cada mensaje, y se eliminaron los cuatro mapas `m` que quedaron sin uso.
- **Verificación:** 45 de 45 pruebas en verde. Las de la zona A verifican en cada error que el cuerpo tiene una sola clave `error` con el mensaje exacto.
- **Comportamiento externo:** sin cambios.
- **Beneficio:** el formato de error se cambia en un solo lugar. Es el paso previo para unificarlo con los errores del framework, si se decide más adelante (*Replace Error Code with Exception*).

#### RA-02 – Extract Method: prioridad por defecto según el tipo

- **Smell que resuelve:** SA-01 y SA-04.
- **Zona / autor:** A – Alex.
- **Commit:** [`ea76173`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/ea76173).
- **Motivación:** el `switch` de RN-04 estaba en el medio de las validaciones. Es una función pura (entra el tipo, sale la prioridad), así que es el paso de menor riesgo.

**Antes:**

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

**Después:**

```java
} else {
    prioridad = prioridadPorDefecto(tipo);
}
```

- **Pasos seguidos:** se movió el `switch` a `prioridadPorDefecto(tipo)`, que devuelve el valor en lugar de asignar una variable.
- **Verificación:** CP-A02 (los cinco tipos) y la suite completa: 45 de 45.
- **Comportamiento externo:** sin cambios.
- **Beneficio:** el comentario que explicaba el bloque pasó a ser el nombre del método.

#### RA-03 – Introduce Parameter Object: `DatosSolicitud`

- **Smell que resuelve:** SA-03 (lista larga de parámetros y *Data Clumps*).
- **Zona / autor:** A – Alex.
- **Commit:** [`0e12ea7`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/0e12ea7).
- **Motivación:** siete `String` seguidos en la firma permiten invertir argumentos sin que el compilador avise.

**Antes:**

```java
return registrarSolicitud(legajo, nombre, email, carrera, tipo, prioridad, descripcion);

private ResponseEntity<Object> registrarSolicitud(String legajo, String nombre, String email, String carrera,
                                                  String tipo, String prioridad, String descripcion) {
```

**Después:**

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
- **Verificación:** 45 de 45. CP-A25 sigue en verde: el legajo numérico sigue fallando igual.
- **Comportamiento externo:** sin cambios, a propósito. `crear` sigue leyendo el mapa con los mismos casts. Recibir directamente un objeto desde el JSON habría convertido el `500` del legajo numérico en otra respuesta, y eso es un cambio de comportamiento que no corresponde a un refactoring.
- **Beneficio:** un solo parámetro con nombre; los datos del pedido viajan juntos.

#### RA-04 – Extract Method: una validación por campo

- **Smell que resuelve:** SA-01 (método largo con responsabilidades mezcladas).
- **Zona / autor:** A – Alex.
- **Commit:** [`6342a84`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/6342a84).
- **Motivación:** las validaciones ocupaban la mitad del método y mezclaban normalización (`toUpperCase`) con control.

**Antes:** cuatro bloques `if` seguidos, con reasignación de `tipo` y `prioridad` en el medio (líneas 46 a 92 de `estado-inicial`).

**Después:**

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
- **Verificación:** CP-A22 (cascada de errores en el orden legajo → tipo → descripción → prioridad → estudiante) y CP-A23 (el mínimo de la descripción se valida antes que el máximo); 45 de 45.
- **Comportamiento externo:** sin cambios.
- **Beneficio:** cada regla (RN-01 a RN-04) tiene su método con su comentario, y el orden de validación se lee en cuatro líneas.

#### RA-05 – Extract Method: búsqueda, validación y alta del estudiante

- **Smell que resuelve:** SA-05 (alta implícita del estudiante dentro del registro).
- **Zona / autor:** A – Alex.
- **Commit:** [`be52cd0`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/be52cd0).
- **Motivación:** el bloque rotulado `// buscar estudiante` además validaba y creaba. Extraerlo como un solo método con nombre de consulta habría escondido una escritura.

**Después:**

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
- **Verificación:** CP-A04, CP-A05, CP-A19 y CP-A20; 45 de 45.
- **Comportamiento externo:** sin cambios: la validación del alta sigue ocurriendo después de la de la solicitud.
- **Beneficio:** la escritura queda a la vista, y la búsqueda de estudiantes queda lista para moverse a un repositorio en la zona C.

#### RA-06 – Extract Method: solicitud abierta y construcción de la solicitud

- **Smell que resuelve:** SA-01.
- **Zona / autor:** A – Alex.
- **Commit:** [`2bbda70`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/2bbda70).
- **Motivación:** el control de duplicados recorría la lista dentro del método y la construcción ocupaba doce líneas de setters.

**Después:**

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
- **Verificación:** CP-A01, CP-A21 y CP-A24; 45 de 45.
- **Comportamiento externo:** sin cambios.
- **Beneficio:** la regla "está abierta" queda en un único método, listo para que la zona B la mueva a la entidad.

#### RA-07 – Replace Magic Number with Symbolic Constant

- **Smell que resuelve:** SA-04 (tipos, prioridades y límites escritos como valores sueltos).
- **Zona / autor:** A – Alex.
- **Commit:** [`df0dbd9`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/df0dbd9).

**Antes:**

```java
if (!tipo.equals("CERTIFICADO") && !tipo.equals("EQUIVALENCIA") && !tipo.equals("REVISION_NOTA")
        && !tipo.equals("CAMBIO_COMISION") && !tipo.equals("OTRO")) {
// ...
if (descripcion.length() > 500) {
    m.put("error", "La descripción no puede superar los 500 caracteres");
```

**Después:**

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
- **Verificación:** CP-A07, CP-A10, CP-A15 y CP-A16 (límites exactos); 45 de 45.
- **Comportamiento externo:** sin cambios: los mensajes generados son idénticos.
- **Beneficio:** cambiar un límite o agregar un tipo se hace en un solo lugar del registro. El paso siguiente sería un `enum` para el tipo y la prioridad (*Replace Type Code with Class*), que queda propuesto porque también lo usa el reporte de la zona C.

#### RA-08 – Remove Dead Code

- **Smell que resuelve:** comentarios y código muerto (notas de la zona A para #6).
- **Zona / autor:** A – Alex.
- **Commit:** [`c69ad8f`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/c69ad8f).
- **Cambio:** se eliminaron `// TODO: avisar por mail al estudiante`, que no tenía dueño ni issue, y `// System.out.println("Se creo la solicitud " + s.getId());`. Los comentarios que rotulaban bloques ya se habían ido en RA-02 a RA-06, al convertirse en nombres de métodos.
- **Verificación:** 45 de 45.
- **Comportamiento externo:** sin cambios.

**Verificación de la zona completa:** integrado con las pruebas finales de las otras zonas, el código refactorizado pasa **69 de 69 pruebas** (31 de la zona A, 17 de la zona B y 21 de la zona C). De caja negra, la línea base da **14 de 14 casos iguales** a la de `estado-inicial` ([evidencias/pruebas/zona-a/linea_base_refactoring_zona_a.md](evidencias/pruebas/zona-a/linea_base_refactoring_zona_a.md)). Informes de las pruebas después del refactoring: [evidencias/pruebas/zona-a/despues-refactoring/](evidencias/pruebas/zona-a/despues-refactoring/).

### Modelo de ficha

_Copiar este bloque en el apartado de la zona._

### R-01 – _Técnica: descripción corta_

- **Smell que resuelve:** S-xx
- **Zona / autora:** _
- **Commit:** _enlace al commit_
- **Motivación:** _por qué este cambio, conectado con el diagnóstico_

**Antes** (`estado-inicial`):

```java
```

**Después:**

```java
```

- **Pasos seguidos:** _la secuencia de pasos chicos, con las pruebas en verde después de cada uno_
- **Verificación:** _qué pruebas cubren este cambio (CP-xx de la sección 6) y su resultado antes y después_
- **Comportamiento externo:** _sin cambios / cambio intencional (explicar cuál y por qué)_
- **Beneficio:** _qué es más fácil ahora que antes_

### 5.2.C Zona C – Consultas y persistencia (Silvia)

Dos refactorings sobre `SolicitudController`, aplicados en la rama `zona-c/refactoring`, uno por commit, con las 69 pruebas en verde después de cada uno (31 de zona A, 17 de zona B y 21 de zona C).

| ID | Técnica | Smell que resuelve | Principio / práctica | Commit |
|---|---|---|---|---|
| RC-01 | Introduce Parameter Object (`FiltroSolicitudes`) | S-12 (SC-01) | Clean Code (pocos argumentos), Long Parameter List | [`0624db5`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/0624db5) |
| RC-02 | Encapsulate Collection (catálogos) | S-14 (SC-05) | Encapsulamiento, bajo acoplamiento | [`faf4849`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/faf4849) |

#### RC-01 – Introduce Parameter Object: `FiltroSolicitudes`

- **Smell que resuelve:** S-12 (SC-01 – lista de parámetros larga en `filtrar`).
- **Zona / autora:** C – Silvia.
- **Commit:** [`0624db5`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/0624db5).
- **Motivación:** `filtrar` recibía seis parámetros sueltos (cuatro `String`), lo que dificultaba leer la llamada y agregar filtros. Se agrupan en un objeto con nombre.

**Antes:**

```java
private List<Solicitud> filtrar(String estado, String tipo, String prioridad, String legajo,
                                Integer responsableId, Boolean vencidas) { ... }
```

**Después:**

```java
private List<Solicitud> filtrar(FiltroSolicitudes filtro) {
    String estado = filtro.estado();
    // ...
}
```

- **Pasos seguidos:** crear el record `FiltroSolicitudes`; cambiar la firma de `filtrar`; construir el objeto en `listar`. Un commit.
- **Verificación:** 69 pruebas en verde antes y después; CP-C01 a CP-C21 de la zona C.
- **Comportamiento externo:** sin cambios (mismos códigos, mensajes y resultados).
- **Beneficio:** la llamada se lee como un solo objeto; agregar un filtro toca un solo lugar.

#### RC-02 – Encapsulate Collection: catálogos inmutables

- **Smell que resuelve:** S-14 (SC-05 – los catálogos devuelven las listas internas).
- **Zona / autora:** C – Silvia.
- **Commit:** [`faf4849`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/commit/faf4849).
- **Motivación:** `estudiantes()` y `responsables()` devolvían la lista interna de `BaseDeDatos`, permitiendo mutar el estado global.

**Antes:**

```java
return BaseDeDatos.estudiantes;
```

**Después:**

```java
return List.copyOf(BaseDeDatos.estudiantes);
```

- **Pasos seguidos:** reemplazar el retorno por `List.copyOf` en ambos endpoints. Un commit.
- **Verificación:** 69 pruebas en verde antes y después; CP-C11 y CP-C12 verifican tamaño y contenido.
- **Comportamiento externo:** sin cambios para lectura; un intento de mutación ahora lanza `UnsupportedOperationException`.
- **Beneficio:** se protege el estado global de mutaciones externas.

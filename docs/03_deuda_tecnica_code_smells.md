# 3. Deuda técnica y code smells

**Consolida:** Marina · **Revisión cruzada:** Silvia y Alex
**Aportes por zona:** A – Alex (#3) · B – Marina (#4) · C – Silvia (#5)

> La consigna pide **al menos 8** code smells o debilidades técnicas, cada uno con evidencia, ubicación, impacto, riesgo de mantenimiento y prioridad de corrección. Las ubicaciones se refieren siempre al tag `estado-inicial`, para que los enlaces no cambien cuando se refactorice.

## 3.1 Criterios de priorización

| Prioridad | Criterio |
|---|---|
| Alta | Afecta varias funcionalidades o hace riesgoso cualquier cambio; bloquea otros refactorings |
| Media | Afecta una funcionalidad o dificulta probar o entender una parte del código |
| Baja | Problema local de legibilidad que no pone en riesgo el comportamiento |

## 3.2 Catálogo resumido

| ID | Smell / debilidad | Ubicación | Impacto | Riesgo de mantenimiento | Prioridad | Zona | Detectado por |
|---|---|---|---|---|---|---|---|
| S-01 | Método largo con responsabilidades mezcladas | `registrarSolicitud` | F1 y cualquier cambio en las reglas del registro | Un cambio en una regla obliga a leer y tocar 105 líneas que hacen siete cosas distintas | Alta | A | Alex |
| S-02 | Código duplicado en el armado de las respuestas de error | `registrarSolicitud` (10 veces) y el resto del controlador (22 en total) | F1, F3, F4 y F5 | Un cambio en el formato de error hay que repetirlo en 22 lugares | Alta | A | Alex |
| S-03 | Lógica de transiciones ubicada en el controlador | `SolicitudController.cambiarEstado()` (líneas 236–299) | F4 – Cambio de estado de solicitudes. | Introducir regresiones en los cambios de estado o respuestas HTTP al modificar la lógica del negocio | Alta | B | Marina |
| S-04 | Modificación del estado sin protección en la entidad | `Solicitud.setEstado()` (líneas 63–65) | F4 – Cambio de estado y consistencia del historial. | Estados e historiales inconsistentes cuando se agreguen nuevos usos de la entidad | Alta | B | Marina |
| S-05 | Regla de vencimiento duplicada | `filtrar` y `resumen` | F2 y F6 | El filtro y el reporte pueden mostrar números distintos | Alta | C | Silvia |
| S-06 | Estado global y persistencia en la misma clase | `BaseDeDatos` | Todos los endpoints, las pruebas y la migración | Las pruebas comparten estado; cambiar la persistencia atraviesa todo | Alta | C | Silvia |
| S-07 | Entrada sin tipos y lista larga de parámetros | `crear` y la firma de `registrarSolicitud` | F1 | Un dato con otro tipo produce un `500`; un argumento en el orden equivocado compila | Media | A | Alex |
| S-08 | Tipos, prioridades y límites escritos como valores sueltos | Validaciones y cálculo de prioridad de `registrarSolicitud` | F1 y, por duplicación, F6 | Agregar un tipo obliga a tocar varios lugares sin ayuda del compilador | Media | A | Alex |
| S-09 | Búsqueda de solicitudes por ID duplicada | `obtener()` (líneas 224–234), `cambiarEstado()` (236–248) y `asignarResponsable()` (302–314) | F3, F4 y F5 – Consulta, cambio de estado y asignación de responsables. | Búsquedas inconsistentes o manejo dispar de solicitudes inexistentes | Media | B | Marina |
| S-10 | Estados representados mediante cadenas de texto | `Solicitud.java` (líneas 13 y 63–65) y comparaciones en `SolicitudController.cambiarEstado()` | F4 – Gestión de estados; también afecta componentes que utilizan esos valores. | Errores de escritura e inconsistencias entre componentes | Media | B | Marina |
| S-11 | Número mágico para el límite de asignaciones | `SolicitudController.asignarResponsable()` (líneas 342–351) | F5 – Asignación de responsables. | Modificar el límite pero conservar un mensaje de error desactualizado | Media | B | Marina |
| S-12 | Lista de parámetros larga en `filtrar` | `SolicitudController.listar` / `filtrar` | F2 y cualquier filtro nuevo | Un argumento en el orden equivocado compila y cambia el resultado | Media | C | Silvia |
| S-13 | `resumen` con mapas de `String` y `get(...) + 1` | `resumen` | F6 y el alta de un tipo o estado | `NullPointerException` latente | Media | C | Silvia |
| S-14 | Los catálogos devuelven las listas internas | `estudiantes` / `responsables` | F7 | Un consumidor puede mutar el estado global | Media | C | Silvia |
| S-15 | Alta implícita del estudiante dentro del registro | Bloque de búsqueda y alta del estudiante | F1 | Una regla nueva sobre estudiantes queda escondida dentro del registro de solicitudes | Baja | A | Alex |

## 3.3 Detalle de cada smell

Las fichas conservan su organización por zona y usan la numeración final del catálogo 3.2. En cada título se indica el identificador original (SA, SB o SC), que mantiene la trazabilidad con documentos y evidencias anteriores. Las referencias originales dentro de los diagnósticos se conservan.

_Si un smell toca dos zonas (por ejemplo, los estados como texto o el chequeo de solicitud duplicada), lo documenta quien lo ve desde su parte y menciona la otra zona en la ficha._

### 3.3.A Zona A – Registro de solicitudes (Alex)

#### Resumen de la zona

| ID | Smell / debilidad | Ubicación | Impacto | Riesgo de mantenimiento | Prioridad | Zona | Detectado por |
|---|---|---|---|---|---|---|---|
| S-01 (SA-01) | Método largo con responsabilidades mezcladas | `registrarSolicitud` | F1 y cualquier cambio en las reglas del registro | Un cambio en una regla obliga a leer y tocar 105 líneas que hacen siete cosas distintas | Alta | A | Alex |
| S-02 (SA-02) | Código duplicado en el armado de las respuestas de error | `registrarSolicitud` (10 veces) y el resto del controlador (22 en total) | F1, F3, F4 y F5 | Un cambio en el formato de error hay que repetirlo en 22 lugares | Alta | A | Alex |
| S-07 (SA-03) | Entrada sin tipos y lista larga de parámetros | `crear` y la firma de `registrarSolicitud` | F1 | Un dato con otro tipo produce un `500`; un argumento en el orden equivocado compila | Media | A | Alex |
| S-08 (SA-04) | Tipos, prioridades y límites escritos como valores sueltos | Validaciones y cálculo de prioridad de `registrarSolicitud` | F1 y, por duplicación, F6 | Agregar un tipo obliga a tocar varios lugares sin ayuda del compilador | Media | A | Alex |
| S-15 (SA-05) | Alta implícita del estudiante dentro del registro | Bloque de búsqueda y alta del estudiante | F1 | Una regla nueva sobre estudiantes queda escondida dentro del registro de solicitudes | Baja | A | Alex |

_El refactoring propuesto para cada smell está en su ficha._

#### S-01 (original: SA-01) – Método largo con responsabilidades mezcladas

- **Ubicación:** [`SolicitudController.registrarSolicitud`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/blob/estado-inicial/src/main/java/ar/edu/utn/tupad/solicitudes/controlador/SolicitudController.java#L42-L146), en `estado-inicial` (105 líneas).
- **Evidencia:** los comentarios que rotulan cada bloque muestran las responsabilidades que conviven en un solo método:

```java
private ResponseEntity<Object> registrarSolicitud(String legajo, String nombre, String email, String carrera,
                                                  String tipo, String prioridad, String descripcion) {
    // validaciones                                         (líneas 46 a 92)
    // la prioridad se calcula segun el tipo                (líneas 80 a 91)
    // buscar estudiante                                    (líneas 94 a 118)
    // no puede tener otra solicitud abierta del mismo tipo (líneas 120 a 127)
    Solicitud s = new Solicitud();                       // (líneas 129 a 140: construcción)
    BaseDeDatos.solicitudes.add(s);                      // (líneas 141 y 142: persistencia)
    BaseDeDatos.guardar();
    return ResponseEntity.status(HttpStatus.CREATED).body(s);
}
```

- **Por qué es un problema:** es un *Long Method* que rompe el principio de responsabilidad única (SRP). En 105 líneas valida la entrada, normaliza datos, calcula la prioridad, busca o da de alta al estudiante, controla duplicados, construye la solicitud, la persiste y arma la respuesta HTTP. Además mezcla niveles de abstracción: reglas de negocio junto a detalles como `matches("\\d{5}")` y `ResponseEntity`. Los comentarios que rotulan cada bloque son el síntoma clásico: cada uno marca un método que todavía no existe.
- **Impacto:** F1 completa. Cualquier cambio en una regla del registro (RN-01 a RN-06) obliga a ubicarla dentro de este método.
- **Riesgo de mantenimiento:** el orden de las validaciones define qué mensaje recibe el usuario cuando hay varios errores, y ese orden solo se ve leyendo el método entero. Mover un bloque puede cambiar el mensaje, o incluso producir un `500` si `matches` queda antes del control de nulos.
- **Prioridad:** Alta, según 3.1: hace riesgoso cualquier cambio en el registro, y como la zona A refactoriza primero, su estructura condiciona los refactorings de las zonas B y C.
- **Refactoring propuesto:** **Extract Method** (el obligatorio de la zona), de menor a mayor riesgo: el cálculo de la prioridad por tipo, el armado de la respuesta de error (SA-02), una validación por campo en el orden original, la validación y el alta del estudiante, la consulta de la solicitud abierta y la construcción de la `Solicitud`. Las validaciones devuelven el error o nada, y el método principal conserva la secuencia y el corte ante el primer error.

#### S-02 (original: SA-02) – Código duplicado en el armado de las respuestas de error

- **Ubicación:** en [`registrarSolicitud`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/blob/estado-inicial/src/main/java/ar/edu/utn/tupad/solicitudes/controlador/SolicitudController.java#L44-L127), líneas 48, 52, 56, 62, 66, 70, 76, 105, 109 y 124. En todo el controlador el patrón aparece 22 veces.
- **Evidencia:**

```java
Map<String, Object> m = new HashMap<>();

if (legajo == null || legajo.trim().isEmpty()) {
    m.put("error", "El legajo es obligatorio");
    return ResponseEntity.badRequest().body(m);
}
if (!legajo.matches("\\d{5}")) {
    m.put("error", "El legajo debe tener 5 dígitos");
    return ResponseEntity.badRequest().body(m);
}
// ... el mismo par de líneas se repite en cada validación
```

- **Por qué es un problema:** es *Duplicated Code*: el formato de la respuesta de error (un mapa con la clave `"error"` y el código HTTP) está escrito a mano en cada validación. La clave `"error"` es un texto suelto repetido, y el tipo de retorno `ResponseEntity<Object>` no deja ver en el contrato si el cuerpo es una solicitud o un error.
- **Impacto:** las respuestas de error de F1, F3, F4 y F5. Es el formato que ve cualquier cliente de la API.
- **Riesgo de mantenimiento:** un cambio en el formato de error (por ejemplo, sumar un código de error) hay que hacerlo en 22 lugares, y si se olvida uno la API queda inconsistente. La sección 2 ya muestra que el contrato es inconsistente con los errores del framework.
- **Prioridad:** Alta, según 3.1: afecta varias funcionalidades.
- **Refactoring propuesto:** **Extract Method** de un método único que arme la respuesta de error a partir del código HTTP y el mensaje, usado en las 22 apariciones. Una mejora posterior es *Replace Error Code with Exception* con un `@ControllerAdvice`, pero cambia el flujo de control: se evalúa en #8 solo si las pruebas verifican el cuerpo completo de cada error.

#### S-07 (original: SA-03) – Entrada sin tipos y lista larga de parámetros

- **Ubicación:** [`SolicitudController.crear`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/blob/estado-inicial/src/main/java/ar/edu/utn/tupad/solicitudes/controlador/SolicitudController.java#L30-L43) y la firma de `registrarSolicitud` (líneas 42 y 43).
- **Evidencia:**

```java
public ResponseEntity<Object> crear(@RequestBody Map<String, Object> body) {
    String legajo = (String) body.get("legajo");
    String nombre = (String) body.get("nombre");
    // ... un cast por cada campo
    return registrarSolicitud(legajo, nombre, email, carrera, tipo, prioridad, descripcion);
}

private ResponseEntity<Object> registrarSolicitud(String legajo, String nombre, String email, String carrera,
                                                  String tipo, String prioridad, String descripcion) {
```

- **Por qué es un problema:** la entrada llega como un `Map<String, Object>` sin tipos y se castea campo por campo, así que un error de tipo aparece recién en tiempo de ejecución. Después los siete valores viajan como siete `String` seguidos: es una *Long Parameter List* con *Data Clumps* (legajo, nombre, email y carrera son los datos del estudiante; tipo, prioridad y descripción, los de la solicitud).
- **Impacto:** F1. Es la causa del `500` que registra la sección 2: si el legajo llega como número, el cast de la línea 32 falla.
- **Riesgo de mantenimiento:** invertir dos argumentos del mismo tipo (por ejemplo, `nombre` y `carrera`) compila sin aviso; cada campo nuevo obliga a tocar el endpoint, la firma y la llamada.
- **Prioridad:** Media, según 3.1: afecta una funcionalidad. El `500` es un defecto real, pero acotado a un dato mal enviado.
- **Refactoring propuesto:** **Introduce Parameter Object**: un objeto de entrada con los campos de la solicitud, que Spring complete directamente desde el JSON, en lugar del mapa y los casts. Si con eso el `500` pasa a responder `400`, es un cambio de comportamiento: se decide en #8 y se documenta como cambio intencional. La línea base ya tiene el caso (caso 13) para detectarlo.

#### S-08 (original: SA-04) – Tipos, prioridades y límites escritos como valores sueltos

- **Ubicación:** en [`registrarSolicitud`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/blob/estado-inicial/src/main/java/ar/edu/utn/tupad/solicitudes/controlador/SolicitudController.java#L51-L91): el formato del legajo (línea 51), los tipos válidos (líneas 60 y 61), los límites de la descripción (65 y 69), las prioridades válidas (75) y el cálculo de prioridad por tipo (81 a 91). También la carrera por defecto (116).
- **Evidencia:**

```java
if (!tipo.equals("CERTIFICADO") && !tipo.equals("EQUIVALENCIA") && !tipo.equals("REVISION_NOTA")
        && !tipo.equals("CAMBIO_COMISION") && !tipo.equals("OTRO")) {
// ...
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
```

- **Por qué es un problema:** es *Primitive Obsession*: tipo y prioridad son conjuntos cerrados de valores representados como `String`, y los límites (5 dígitos, 10 y 500 caracteres) son *Magic Numbers*. El conocimiento sobre cada tipo está repartido entre la validación y el `switch`, y la lista de tipos se repite en el mapa `porTipo` del reporte (zona C, SC-04). Agregar un tipo implica modificar código existente en varios lugares, lo que contradice el principio abierto/cerrado (OCP).
- **Impacto:** F1 y, por la duplicación, F6. Los límites aparecen también dentro de los mensajes de error, escritos aparte.
- **Riesgo de mantenimiento:** un error de tipeo (`"CERTIFICAD0"`) compila; agregar un tipo y olvidarse del `switch` le asigna prioridad BAJA sin aviso; cambiar un límite deja el número viejo en el mensaje.
- **Prioridad:** Media, según 3.1: dificulta entender y cambiar una funcionalidad. El riesgo más grave, el `NullPointerException` del reporte, ya está registrado en SC-04.
- **Refactoring propuesto:** **Replace Type Code with Class** (un `enum` para el tipo, que conozca su prioridad por defecto, y otro para la prioridad) y **Replace Magic Number with Symbolic Constant** para los límites. Como el tipo y la prioridad también se usan en el reporte, se coordina con la zona C; los estados quedan para la zona B.

#### S-15 (original: SA-05) – Alta implícita del estudiante dentro del registro

- **Ubicación:** [`registrarSolicitud`, líneas 94 a 118](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/blob/estado-inicial/src/main/java/ar/edu/utn/tupad/solicitudes/controlador/SolicitudController.java#L94-L118).
- **Evidencia:**

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
    // ... valida nombre y email, crea el Estudiante y lo agrega a BaseDeDatos.estudiantes
}
```

- **Por qué es un problema:** el registro de una solicitud tiene una responsabilidad extra: busca al estudiante y, si no existe, lo da de alta con sus propias validaciones. El comentario `// buscar estudiante` dice menos de lo que el bloque hace. No genera un error hoy (un estudiante recién creado no puede tener una solicitud abierta duplicada), pero esconde una regla de negocio (RN-05) dentro de otra operación.
- **Impacto:** F1. Las validaciones del alta (nombre y email) solo se ejecutan si el legajo no existe, y después de las de la solicitud.
- **Riesgo de mantenimiento:** una regla nueva sobre estudiantes (por ejemplo, validar el email con más rigor) hay que buscarla dentro del registro de solicitudes. Si al refactorizar se extrae con un nombre de consulta, como `buscarEstudiante`, quedaría un método que además escribe: hay que nombrarlo por lo que hace.
- **Prioridad:** Baja, según 3.1: es un problema local de responsabilidades, que hoy no pone en riesgo el comportamiento.
- **Refactoring propuesto:** **Extract Method** en dos partes, la validación de los datos de alta y la creación del `Estudiante`, como parte de SA-01. La búsqueda en la lista es de la zona C.

#### Observaciones complementarias

Problemas que se ven desde la zona A pero atraviesan otras zonas o son transversales:

- **Chequeo de solicitud abierta duplicada** (líneas 120 a 127): la respuesta `409` es de la zona A, la regla "está abierta" (`PENDIENTE` o `EN_REVISION`) es de la zona B y la búsqueda en la lista es de la zona C.
- **Nombres poco expresivos:** `m`, `est`, `e` y `s`. Además, `s` se usa en el `for` del chequeo de duplicados y vuelve a declararse como la `Solicitud` nueva (líneas 121 y 129).
- **Comentarios y código muerto:** comentarios que rotulan bloques (`// validaciones`, `// buscar estudiante`), un `// TODO` sin dueño y un `System.out.println` comentado (líneas 143 y 144).
- **Validación asimétrica de la descripción:** el mínimo se mide sin los espacios de los extremos y el máximo con ellos (líneas 65 y 69). Es comportamiento documentado en RN-03: se preserva y se registra como hallazgo en 6.5.
- **`LocalDate.now()` dentro del registro** (línea 137): la fecha de creación depende del reloj del sistema, lo que dificulta probar con fechas fijas. Aparece también en otros cuatro métodos de las zonas B y C.

### 3.3.B Zona B – Ciclo de vida de la solicitud (Marina)

Las siguientes fichas describen problemas observados en la versión inicial, sin modificar su código. Las ubicaciones enlazan al tag `estado-inicial`.

#### S-03 (original: SB-01) – Lógica de transiciones ubicada en el controlador

- **Ubicación:** [`SolicitudController.cambiarEstado()` (líneas 236–299)](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/blob/estado-inicial/src/main/java/ar/edu/utn/tupad/solicitudes/controlador/SolicitudController.java#L236-L299).
- **Evidencia:** fragmento abreviado; se omitieron otras transiciones por brevedad. El código completo está disponible en el enlace de ubicación.

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
```

- **Por qué es un problema:** el controlador HTTP también decide las transiciones permitidas, mezclando comunicación y reglas de negocio. Afecta la separación de responsabilidades (SRP).
- **Impacto:** cualquier modificación de las transiciones exige intervenir en el controlador y dificulta probar estas reglas por separado.
- **Riesgo de mantenimiento:** introducir regresiones en los cambios de estado o respuestas HTTP al modificar la lógica del negocio.
- **Prioridad:** Alta.
- **Refactoring propuesto:** **Move Method / reubicación de responsabilidad**: trasladar la validación de transiciones a un componente de negocio apropiado, conservando las respuestas de la API.

#### S-09 (original: SB-02) – Búsqueda de solicitudes por ID duplicada

- **Ubicación:** [`obtener()` (líneas 224–234), `cambiarEstado()` (236–248) y `asignarResponsable()` (302–314)](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/blob/estado-inicial/src/main/java/ar/edu/utn/tupad/solicitudes/controlador/SolicitudController.java#L224-L314).
- **Evidencia:** en `cambiarEstado()` y `asignarResponsable()` se repite esta búsqueda; `obtener()` realiza una variante equivalente.

```java
Solicitud sol = null;
for (Solicitud s : BaseDeDatos.solicitudes) {
    if (s.getId() == id) {
        sol = s;
        break;
    }
}
```

- **Por qué es un problema:** existe **Duplicated Code**: varias operaciones implementan por separado la misma búsqueda.
- **Impacto:** más lugares que modificar y mantener si cambia el acceso a los datos.
- **Riesgo de mantenimiento:** búsquedas inconsistentes o manejo dispar de solicitudes inexistentes.
- **Prioridad:** Media.
- **Refactoring propuesto:** **Extract Method** para centralizar la búsqueda y evaluar su reubicación en la capa de acceso a datos.

#### S-10 (original: SB-03) – Estados representados mediante cadenas de texto

- **Ubicación:** [`Solicitud.java` (líneas 13 y 63–65)](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/blob/estado-inicial/src/main/java/ar/edu/utn/tupad/solicitudes/modelo/Solicitud.java#L13-L65) y comparaciones en `SolicitudController.cambiarEstado()`.
- **Evidencia:**

```java
private String estado;

public void setEstado(String estado) {
    this.estado = estado;
}
```

- **Por qué es un problema:** **Primitive Obsession**: un concepto del dominio con valores definidos se representa como `String`, y sus valores se repiten en comparaciones.
- **Impacto:** menor seguridad de tipos y mayor esfuerzo al agregar o modificar estados.
- **Riesgo de mantenimiento:** errores de escritura e inconsistencias entre componentes.
- **Prioridad:** Media.
- **Refactoring propuesto:** introducir un `enum EstadoSolicitud` o tipo de dominio equivalente, conservando la representación externa de los estados y la validación de las entradas.

#### S-04 (original: SB-04) – Modificación del estado sin protección en la entidad

- **Ubicación:** [`Solicitud.setEstado()` (líneas 63–65)](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/blob/estado-inicial/src/main/java/ar/edu/utn/tupad/solicitudes/modelo/Solicitud.java#L63-L65).
- **Evidencia:**

```java
public void setEstado(String estado) {
    this.estado = estado;
}
```

- **Por qué es un problema:** el setter público permite modificar el estado sin verificar la regla RN-07. A diferencia de SB-01 (ubicación de reglas en el controlador), esta ficha señala que la entidad no protege sus invariantes.
- **Impacto:** otros componentes pueden saltarse las reglas de transición y modificar el estado sin actualizar correctamente el historial.
- **Riesgo de mantenimiento:** estados e historiales inconsistentes cuando se agreguen nuevos usos de la entidad.
- **Prioridad:** Alta.
- **Refactoring propuesto:** encapsular la modificación del estado y centralizar una operación de transición válida, coordinada con la capa de negocio.
- **Precaución para la etapa 5:** verificar que Jackson pueda seguir leyendo y escribiendo el JSON persistido; no eliminar el setter sin revisar la deserialización.

#### S-11 (original: SB-05) – Número mágico para el límite de asignaciones

- **Ubicación:** [`SolicitudController.asignarResponsable()` (líneas 342–351)](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/blob/estado-inicial/src/main/java/ar/edu/utn/tupad/solicitudes/controlador/SolicitudController.java#L342-L351).
- **Evidencia:**

```java
if (cant >= 5) {
    m.put("error", "El responsable ya tiene 5 solicitudes abiertas");
    return ResponseEntity.status(HttpStatus.CONFLICT).body(m);
}
```

- **Por qué es un problema:** **Magic Number**: el valor `5` codifica directamente la RN-12 sin un nombre descriptivo.
- **Impacto:** la regla es menos explícita y su cambio exige revisar la condición y el mensaje.
- **Riesgo de mantenimiento:** modificar el límite pero conservar un mensaje de error desactualizado.
- **Prioridad:** Media.
- **Refactoring propuesto:** **Replace Magic Number with Symbolic Constant**, ubicando la constante donde quede la regla de negocio y manteniendo el mensaje coherente.

**Nota de alcance:** estas fichas se refieren a la zona B. Las observaciones de `registrarSolicitud()` pertenecen a la zona A de Alex. Los identificadores originales `SB-*` se conservan como referencia junto a la numeración final del catálogo global de la issue #6.

### 3.3.C Zona C – Consultas y persistencia (Silvia)

#### Resumen de la zona

| ID | Smell / debilidad | Ubicación | Impacto | Riesgo de mantenimiento | Prioridad | Zona | Detectado por |
|---|---|---|---|---|---|---|---|
| S-12 (SC-01) | Lista de parámetros larga en `filtrar` | `SolicitudController.listar` / `filtrar` | F2 y cualquier filtro nuevo | Un argumento en el orden equivocado compila y cambia el resultado | Media | C | Silvia |
| S-05 (SC-02) | Regla de vencimiento duplicada | `filtrar` y `resumen` | F2 y F6 | El filtro y el reporte pueden mostrar números distintos | Alta | C | Silvia |
| S-06 (SC-03) | Estado global y persistencia en `BaseDeDatos` | `BaseDeDatos` | Todos los endpoints, las pruebas y la migración | Las pruebas comparten estado; cambiar la persistencia atraviesa todo | Alta | C | Silvia |
| S-13 (SC-04) | `resumen` con mapas de `String` y `get(...) + 1` | `resumen` | F6 y el alta de un tipo o estado | `NullPointerException` latente | Media | C | Silvia |
| S-14 (SC-05) | Catálogos que devuelven las listas internas | `estudiantes` / `responsables` | F7 | Un consumidor puede mutar el estado global | Media | C | Silvia |

_El refactoring propuesto para cada smell está en su ficha._

#### S-12 (original: SC-01) – Lista de parámetros larga en `filtrar`

- **Ubicación:** [`SolicitudController.listar`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/blob/estado-inicial/src/main/java/ar/edu/utn/tupad/solicitudes/controlador/SolicitudController.java#L149-L156) y [`SolicitudController.filtrar`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/blob/estado-inicial/src/main/java/ar/edu/utn/tupad/solicitudes/controlador/SolicitudController.java#L158-L221), en `estado-inicial`.
- **Evidencia:**

```java
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
```

- **Por qué es un problema:** los seis parámetros forman una *Long Parameter List*: siempre viajan juntos y representan una sola cosa, el filtro. Cuatro son `String`, así que el compilador no distingue un orden equivocado; agregar un filtro obliga a tocar la firma del endpoint, la del método privado y todas las llamadas. Seis parámetros superan el umbral: cuatro ya es el disparador y cinco o más es larga por defecto. Además, el costo se paga en `filtrar`, que no solo recibe los seis sino que los combina en una misma condición, así que un valor en el lugar equivocado no se nota al leer la llamada.
- **Impacto:** F2 y cualquier filtro nuevo; también las pruebas y la documentación de Swagger, que cambian con la firma.
- **Riesgo de mantenimiento:** un `estado` pasado en el lugar de `tipo` compila y devuelve resultados silenciosamente distintos; cada condición nueva agrega otro parámetro a la lista.
- **Prioridad:** Media, según 3.1: afecta una sola funcionalidad (F2) y la firma del endpoint; no bloquea otros refactorings.
- **Refactoring propuesto:** **Introduce Parameter Object**: agrupar los seis valores en `FiltroSolicitudes` y hacer que `filtrar` reciba ese objeto. Los query params HTTP no cambian; `listar` sigue exponiendo los mismos nombres.

#### S-05 (original: SC-02) – Regla de vencimiento duplicada

- **Ubicación:** [`SolicitudController.filtrar`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/blob/estado-inicial/src/main/java/ar/edu/utn/tupad/solicitudes/controlador/SolicitudController.java#L188-L206) y [`SolicitudController.resumen`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/blob/estado-inicial/src/main/java/ar/edu/utn/tupad/solicitudes/controlador/SolicitudController.java#L399-L409).
- **Evidencia:**

```java
// filtrar
if (s.getPrioridad().equals("ALTA") && dias > 3) { flag = true; }
if (s.getPrioridad().equals("MEDIA") && dias > 7) { flag = true; }
if (s.getPrioridad().equals("BAJA") && dias > 15) { flag = true; }
```

```java
// resumen
int limite = 15;
if (s.getPrioridad().equals("ALTA")) { limite = 3; }
else if (s.getPrioridad().equals("MEDIA")) { limite = 7; }
if (dias > limite) { venc++; }
```

- **Por qué es un problema:** es *Duplicated Code* sobre una regla de negocio: RN-14 tiene dos implementaciones con formas distintas. No hay un único lugar que defina los plazos.
- **Impacto:** F2 (`vencidas=true`) y F6 (conteo de `vencidas`); un cambio en RN-14 obliga a editar dos métodos.
- **Riesgo de mantenimiento:** si se cambia una sola copia, el filtro y el reporte muestran números distintos sin avisar.
- **Prioridad:** Alta, según 3.1: afecta dos funcionalidades y hace riesgoso cualquier cambio de plazos.
- **Refactoring propuesto:** **Extract Class** `PoliticaVencimiento`, con `estaVencida(Solicitud, LocalDate)`, llamada desde `filtrar` y `resumen`. Si se decide ubicarla en `Solicitud`, coordinarlo con la zona B (refactoring de ciclo de vida).

#### S-06 (original: SC-03) – Estado global y persistencia en la misma clase

- **Ubicación:** [`BaseDeDatos`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/blob/estado-inicial/src/main/java/ar/edu/utn/tupad/solicitudes/datos/BaseDeDatos.java#L17-L58) y [`cargarDatosDePrueba`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/blob/estado-inicial/src/main/java/ar/edu/utn/tupad/solicitudes/datos/BaseDeDatos.java#L61-L148).
- **Evidencia:**

```java
public static List<Solicitud> solicitudes = new ArrayList<>();
public static List<Estudiante> estudiantes = new ArrayList<>();
public static List<Responsable> responsables = new ArrayList<>();
public static int ultimoId = 0;

private static final String ARCHIVO = "datos/solicitudes.json";
private static ObjectMapper mapper = new ObjectMapper();

public static void cargar() { /* lee el archivo o carga los datos de prueba */ }
public static void guardar() { /* escribe el archivo */ }
```

- **Por qué es un problema:** mezcla *Global State*, persistencia y datos de prueba en una sola clase. El controlador la usa de forma estática veinte veces; no hay una capa que aísle el acceso a datos.
- **Impacto:** todos los endpoints; las pruebas, que comparten el estado; y la migración de JSON a una base de datos.
- **Riesgo de mantenimiento:** cualquier código puede mutar las listas; las pruebas se contaminan entre sí; cambiar la persistencia obliga a tocar la misma clase que guarda los datos y el *seed*.
- **Prioridad:** Alta, según 3.1: es el cambio que más se propaga y bloquea la testabilidad.
- **Refactoring propuesto:** **Extract Class** `AlmacenJson` para `cargar`/`guardar` y `RepositorioSolicitudes`/`RepositorioCatalogos` para las colecciones, más **Encapsulate Collection** para las listas. Conviene hacerlo en dos commits: primero la persistencia, después la encapsulación.

#### S-13 (original: SC-04) – `resumen` con mapas de `String` y `get(...) + 1`

- **Ubicación:** [`SolicitudController.resumen`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/blob/estado-inicial/src/main/java/ar/edu/utn/tupad/solicitudes/controlador/SolicitudController.java#L372-L397).
- **Evidencia:**

```java
porTipo.put("CERTIFICADO", 0);
porTipo.put("EQUIVALENCIA", 0);
// ... una clave por cada tipo válido
porTipo.put(s.getTipo(), porTipo.get(s.getTipo()) + 1);
```

- **Por qué es un problema:** los tipos, estados y prioridades son *Type Codes* representados como `String`. El conjunto de valores válidos está repetido en la validación y el cálculo de prioridad de la zona A, y en el mapa del reporte. `get` devuelve `null` si aparece un valor nuevo.
- **Impacto:** F6 y cualquier alta de un tipo o estado nuevo; el registro puede seguir funcionando y el reporte fallar después.
- **Riesgo de mantenimiento:** `NullPointerException` latente; el compilador no avisa y los datos actuales no lo disparan.
- **Prioridad:** Media, según 3.1: afecta una sola funcionalidad (F6); el `NullPointerException` es latente con los datos actuales.
- **Refactoring propuesto:** **Replace Type Code with Class** (o `enum`) para tipos, estados y prioridades, más **Extract Method** para los conteos y `EnumMap` para evitar el `null`. Toca las zonas A y B; hay que coordinarlo y dejarlo anotado en la ficha.

#### S-14 (original: SC-05) – Los catálogos devuelven las listas internas

- **Ubicación:** [`SolicitudController.estudiantes` y `SolicitudController.responsables`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/blob/estado-inicial/src/main/java/ar/edu/utn/tupad/solicitudes/controlador/SolicitudController.java#L432-L440).
- **Evidencia:**

```java
@GetMapping("/estudiantes")
public List<Estudiante> estudiantes() {
    return BaseDeDatos.estudiantes;
}

@GetMapping("/responsables")
public List<Responsable> responsables() {
    return BaseDeDatos.responsables;
}
```

- **Por qué es un problema:** el endpoint devuelve la colección interna, no una copia. Rompe la encapsulación y expone el estado global mutable.
- **Impacto:** F7; cualquier consumidor puede agregar o quitar elementos de la lista sin pasar por la persistencia.
- **Riesgo de mantenimiento:** un cambio hecho por un consumidor se pierde o se persiste de forma inesperada; las pruebas pueden contaminar los catálogos.
- **Prioridad:** Media, según 3.1: hoy no hay un consumidor que mute la lista, pero el contrato queda abierto.
- **Refactoring propuesto:** **Encapsulate Collection**: devolver `List.copyOf(...)` o una vista no modificable, y encapsular el campo en `BaseDeDatos`.

#### Observaciones complementarias

Estas observaciones se conservan como notas de alcance y coordinación, sin crear fichas adicionales a los 15 hallazgos del catálogo.

- `SolicitudController` concentra los ocho endpoints y la lógica de negocio (441 líneas).
- `crear` recibe `Map<String, Object>` y castea sus valores (zona A).
- Estados, tipos y prioridades se representan como `String` en varias capas (A/B/C); se cruza con SC-04.
- Nombres abreviados: `m`, `lst`, `sol`, `pa`, `pb`.
- Las 22 respuestas de error se arman a mano.
- `LocalDate.now()` aparece en `registrarSolicitud`, `filtrar`, `cambiarEstado`, `asignarResponsable` y `resumen` (5 lugares).
- `// TODO` y código comentado al final de `registrarSolicitud`.
- `cargar` y `guardar` atrapan la excepción y siguen: un archivo JSON corrupto hace que el sistema arranque con los datos de prueba sin avisar, y una escritura fallida se pierde en silencio. Es comportamiento, no solo estructura, y queda registrado como observación complementaria, sin crear una ficha adicional.
- `resumen` calcula los conteos y arma la respuesta en el mismo método (se cruza con SC-04).

### 3.3.D Relaciones transversales

Estas relaciones conectan fichas existentes; no constituyen hallazgos adicionales ni modifican sus diagnósticos:

- **Reglas de negocio en el controlador:** S-01 (SA-01, A) y S-03 (SB-01, B) describen responsabilidades de negocio mezcladas con HTTP. S-02 (SA-02) alcanza también las respuestas de error de F3, F4 y F5. Se relacionan por la estructura del controlador, pero cada ficha conserva su alcance.
- **Tipos, estados y prioridades:** S-08 (SA-04, A), S-10 (SB-03, B) y S-13 (SC-04, C) comparten valores de dominio representados como texto. El registro, las transiciones y los mapas del reporte deben mantener valores compatibles; S-13 ya explicita la coordinación con A y B.
- **Acceso a datos y encapsulación:** S-09 (SB-02, B) repite búsquedas sobre las colecciones que S-06 (SC-03, C) identifica como estado global. S-14 (SC-05, C) describe su exposición desde los catálogos. La búsqueda del estudiante de S-15 (SA-05, A) también cruza con el acceso a datos de C, como indica su ficha.
- **Solicitud abierta y vencimiento:** las notas de A distinguen la respuesta `409` del registro, la regla de estado abierto de B y la búsqueda en listas de C. Esta frontera se vincula con S-10 (SB-03); S-05 (SC-02) también requiere coordinación con B si la política de vencimiento se ubica en `Solicitud`, tal como indica su propuesta. No se agrega una ficha por estas relaciones.
- **Parámetros y reloj:** S-07 (SA-03, A) y S-12 (SC-01, C) comparten listas largas de parámetros, en operaciones distintas. Las notas de A y C registran además `LocalDate.now()` en cinco métodos de A/B/C; se conserva como observación transversal, sin convertirla en un hallazgo nuevo.

## 3.4 Deuda técnica: lectura general

El análisis del sistema original muestra una deuda de diseño y mantenibilidad: responsabilidades de negocio y comunicación HTTP concentradas en el controlador, reglas y búsquedas duplicadas, valores de dominio sin tipos específicos y acceso directo al estado global. Los 15 hallazgos describen distintas manifestaciones de esa deuda; no implican que todas produzcan un fallo con los datos actuales ni que sus refactorings ya estén implementados.

El costo del cambio aumenta cuando una modificación exige revisar varios lugares o comprender responsabilidades mezcladas. S-01 y S-03 dificultan aislar reglas; S-02, S-05 y S-09 obligan a mantener implementaciones repetidas. S-04 permite omitir las reglas de transición, mientras que S-06 y S-14 exponen estado mutable y dificultan el aislamiento de las pruebas. Cambiar una validación puede alterar el primer error informado, modificar una sola copia puede producir resultados inconsistentes y compartir colecciones puede contaminar otras operaciones o pruebas.

Las zonas están relacionadas por los mismos datos y reglas. Los tipos y prioridades utilizados en el registro de A (S-08) y los estados gestionados en B (S-10) son consumidos por el reporte de C (S-13). La búsqueda de estudiantes del registro (S-15) y las búsquedas de solicitudes de B (S-09) dependen del acceso a datos de C (S-06). Las respuestas de error de S-02 atraviesan operaciones de A y B, y S-05 requiere coordinación con B si la política de vencimiento se ubica en la entidad. Por ello, corregir una zona exige conservar los contratos que utilizan las siguientes.

El orden de trabajo previsto es proteger primero el comportamiento mediante pruebas de caracterización y después refactorizar A, B y C, respetando las dependencias del equipo. Las pruebas deben permitir detectar cambios en respuestas, validaciones, transiciones, historial, consultas y persistencia. Este orden organiza las intervenciones; no significa que los hallazgos de C tengan menor prioridad ni que las mejoras estén realizadas.

Dentro de ese orden se aplican los criterios de 3.1. La prioridad **Alta** corresponde a problemas que afectan varias funcionalidades, hacen riesgoso el cambio o bloquean otros refactorings: en A conviene comenzar por S-01 y S-02 para separar responsabilidades y centralizar errores sin alterar la secuencia de validaciones; en B, por S-03 y S-04 para coordinar las reglas de transición con la protección del estado y del historial, preservando la compatibilidad con Jackson; en C, por S-05 y S-06 para unificar el vencimiento y separar persistencia y estado global mediante pasos controlados. Aunque S-06 se intervenga al llegar a C, su efecto sobre las pruebas debe considerarse desde la caracterización mediante aislamiento del estado.

La prioridad **Media** atiende problemas que afectan una funcionalidad o dificultan probarla o entenderla. En A, S-07 y S-08 permiten revisar la entrada y los valores del dominio; cualquier cambio intencional de respuesta debe distinguirse del refactoring. En B, S-09, S-10 y S-11 reducen la repetición de búsquedas, las inconsistencias de estados y la dispersión del límite de asignaciones. En C, S-12, S-13 y S-14 mejoran el contrato interno de filtrado, los conteos y la encapsulación de colecciones. S-08, S-10 y S-13 requieren coordinación para conservar la representación externa de los valores. La prioridad **Baja** de S-15 refleja un problema local que hoy no compromete el comportamiento; su extracción puede acompañar S-01, como propone la ficha, sin desplazar la atención de los riesgos altos.

El [cuadrante de deuda técnica de Martin Fowler](https://martinfowler.com/bliki/TechnicalDebtQuadrant.html) distingue deuda **prudente o imprudente** y **deliberada o inadvertida**. Una decisión consciente con beneficio y costo evaluados puede ser prudente y deliberada; asumir riesgos conocidos sin ponderarlos, imprudente y deliberada. Si la limitación se descubre al aprender más sobre el diseño, puede ser prudente e inadvertida; si proviene de desconocer prácticas de diseño, imprudente e inadvertida. El código permite observar los problemas y sus efectos, pero no demuestra por sí solo las intenciones ni el conocimiento de quienes lo escribieron.

El registro IA-02 de la versión inicial documenta que el prototipo se generó sin cuidar el diseño para disponer de problemas reales que diagnosticar. Esto permite reconocer una simplificación deliberada con finalidad didáctica a nivel del prototipo, pero no atribuir esa intención a cada hallazgo ni concluir que todos sus costos fueron evaluados. Por tanto, el catálogo no asigna un cuadrante definitivo a S-01–S-15: documenta la deuda observable y propone reducirla según su impacto, riesgo y dependencias, sin inferir negligencia, desconocimiento ni decisiones individuales que no están acreditadas.

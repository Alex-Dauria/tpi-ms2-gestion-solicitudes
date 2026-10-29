# 3. Deuda técnica y code smells

**Consolida:** Marina · **Revisión cruzada:** Silvia y Alex
**Aportes por zona:** A – Alex (#3) · B – Marina (#4) · C – Silvia (#5)

> La consigna pide **al menos 8** code smells o debilidades técnicas, cada uno con evidencia, ubicación, impacto, riesgo de mantenimiento y prioridad de corrección. Las ubicaciones se refieren siempre al tag `estado-inicial`, para que los enlaces no cambien cuando se refactorice.

## 3.1 Criterios de priorización

_Definir antes de completar la tabla (lo acuerda el equipo en #6). Propuesta inicial:_

| Prioridad | Criterio |
|---|---|
| Alta | Afecta varias funcionalidades o hace riesgoso cualquier cambio; bloquea otros refactorings |
| Media | Afecta una funcionalidad o dificulta probar o entender una parte del código |
| Baja | Problema local de legibilidad que no pone en riesgo el comportamiento |

## 3.2 Catálogo resumido

| ID | Smell / debilidad | Ubicación | Impacto | Riesgo de mantenimiento | Prioridad | Zona | Detectado por |
|---|---|---|---|---|---|---|---|
| S-01 | | | | | | | |
| S-02 | | | | | | | |
| S-03 | | | | | | | |
| S-04 | | | | | | | |
| S-05 | | | | | | | |
| S-06 | | | | | | | |
| S-07 | | | | | | | |
| S-08 | | | | | | | |

## 3.3 Detalle de cada smell

_Cada zona carga sus fichas en su propio apartado, con un id provisorio (SA-01, SB-01, SC-01…). Así las tres pueden trabajar en paralelo sin pisarse. Al consolidar (#6), Marina pasa todo a la numeración final S-01, S-02… y completa la tabla 3.2._

_Si un smell toca dos zonas (por ejemplo, los estados como texto o el chequeo de solicitud duplicada), lo documenta quien lo ve desde su parte y menciona la otra zona en la ficha._

### 3.3.A Zona A – Registro de solicitudes (Alex)

#### Resumen de la zona

| ID | Smell / debilidad | Ubicación | Impacto | Riesgo de mantenimiento | Prioridad | Zona | Detectado por |
|---|---|---|---|---|---|---|---|
| SA-01 | Método largo con responsabilidades mezcladas | `registrarSolicitud` | F1 y cualquier cambio en las reglas del registro | Un cambio en una regla obliga a leer y tocar 105 líneas que hacen siete cosas distintas | Alta | A | Alex |
| SA-02 | Código duplicado en el armado de las respuestas de error | `registrarSolicitud` (10 veces) y el resto del controlador (22 en total) | F1, F3, F4 y F5 | Un cambio en el formato de error hay que repetirlo en 22 lugares | Alta | A | Alex |
| SA-03 | Entrada sin tipos y lista larga de parámetros | `crear` y la firma de `registrarSolicitud` | F1 | Un dato con otro tipo produce un `500`; un argumento en el orden equivocado compila | Media | A | Alex |
| SA-04 | Tipos, prioridades y límites escritos como valores sueltos | Validaciones y cálculo de prioridad de `registrarSolicitud` | F1 y, por duplicación, F6 | Agregar un tipo obliga a tocar varios lugares sin ayuda del compilador | Media | A | Alex |
| SA-05 | Alta implícita del estudiante dentro del registro | Bloque de búsqueda y alta del estudiante | F1 | Una regla nueva sobre estudiantes queda escondida dentro del registro de solicitudes | Baja | A | Alex |

_El refactoring propuesto para cada smell está en su ficha._

#### SA-01 – Método largo con responsabilidades mezcladas

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

#### SA-02 – Código duplicado en el armado de las respuestas de error

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

#### SA-03 – Entrada sin tipos y lista larga de parámetros

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

#### SA-04 – Tipos, prioridades y límites escritos como valores sueltos

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

#### SA-05 – Alta implícita del estudiante dentro del registro

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

#### Notas para la consolidación (#6)

Problemas que se ven desde la zona A pero atraviesan otras zonas o son transversales:

- **Chequeo de solicitud abierta duplicada** (líneas 120 a 127): la respuesta `409` es de la zona A, la regla "está abierta" (`PENDIENTE` o `EN_REVISION`) es de la zona B y la búsqueda en la lista es de la zona C.
- **Nombres poco expresivos:** `m`, `est`, `e` y `s`. Además, `s` se usa en el `for` del chequeo de duplicados y vuelve a declararse como la `Solicitud` nueva (líneas 121 y 129).
- **Comentarios y código muerto:** comentarios que rotulan bloques (`// validaciones`, `// buscar estudiante`), un `// TODO` sin dueño y un `System.out.println` comentado (líneas 143 y 144).
- **Validación asimétrica de la descripción:** el mínimo se mide sin los espacios de los extremos y el máximo con ellos (líneas 65 y 69). Es comportamiento documentado en RN-03: se preserva y se registra como hallazgo en 6.5.
- **`LocalDate.now()` dentro del registro** (línea 137): la fecha de creación depende del reloj del sistema, lo que dificulta probar con fechas fijas. Aparece también en otros cuatro métodos de las zonas B y C.

### 3.3.B Zona B – Ciclo de vida de la solicitud (Marina)

### 3.3.C Zona C – Consultas y persistencia (Silvia)

### Modelo de ficha

_Copiar este bloque en el apartado de la zona._

#### SA-01 – _Nombre del smell_

- **Ubicación:** [`SolicitudController.java#Lxx-Lyy`](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/blob/estado-inicial/src/main/java/ar/edu/utn/tupad/solicitudes/controlador/SolicitudController.java)
- **Evidencia:**

```java
// fragmento relevante, no la clase entera
```

- **Por qué es un problema:** _qué principio o buena práctica rompe_
- **Impacto:** _qué funcionalidades o cambios afecta_
- **Riesgo de mantenimiento:** _qué puede salir mal si se deja así_
- **Prioridad:** _Alta / Media / Baja, según 3.1_
- **Refactoring propuesto:** _técnica del catálogo de Fowler; se aplica en la sección 5_

## 3.4 Deuda técnica: lectura general

_Clasificar la deuda (por ejemplo con el cuadrante de Fowler: imprudente/prudente, deliberada/inadvertida), explicar cómo eleva el costo del cambio y justificar el orden en que se va a corregir._

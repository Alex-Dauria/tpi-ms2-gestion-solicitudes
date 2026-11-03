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

### 3.3.B Zona B – Ciclo de vida de la solicitud (Marina)

Las siguientes fichas describen problemas observados en la versión inicial, sin modificar su código. Las ubicaciones enlazan al tag `estado-inicial`.

#### SB-01 – Lógica de transiciones ubicada en el controlador

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

#### SB-02 – Búsqueda de solicitudes por ID duplicada

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

#### SB-03 – Estados representados mediante cadenas de texto

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

#### SB-04 – Modificación del estado sin protección en la entidad

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

#### SB-05 – Número mágico para el límite de asignaciones

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

**Nota de alcance:** estas fichas se refieren a la zona B. Las observaciones de `registrarSolicitud()` pertenecen a la zona A de Alex. La numeración `SB-*` es provisional y se consolidará en el catálogo global de la issue #6.

### 3.3.C Zona C – Consultas y persistencia (Silvia)

#### Resumen de la zona

| ID | Smell / debilidad | Ubicación | Impacto | Riesgo de mantenimiento | Prioridad | Zona | Detectado por |
|---|---|---|---|---|---|---|---|
| SC-01 | Lista de parámetros larga en `filtrar` | `SolicitudController.listar` / `filtrar` | F2 y cualquier filtro nuevo | Un argumento en el orden equivocado compila y cambia el resultado | Media | C | Silvia |
| SC-02 | Regla de vencimiento duplicada | `filtrar` y `resumen` | F2 y F6 | El filtro y el reporte pueden mostrar números distintos | Alta | C | Silvia |
| SC-03 | Estado global y persistencia en `BaseDeDatos` | `BaseDeDatos` | Todos los endpoints, las pruebas y la migración | Las pruebas comparten estado; cambiar la persistencia atraviesa todo | Alta | C | Silvia |
| SC-04 | `resumen` con mapas de `String` y `get(...) + 1` | `resumen` | F6 y el alta de un tipo o estado | `NullPointerException` latente | Media | C | Silvia |
| SC-05 | Catálogos que devuelven las listas internas | `estudiantes` / `responsables` | F7 | Un consumidor puede mutar el estado global | Media | C | Silvia |

_El refactoring propuesto para cada smell está en su ficha._

#### SC-01 – Lista de parámetros larga en `filtrar`

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

#### SC-02 – Regla de vencimiento duplicada

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
- **Refactoring propuesto:** **Extract Class** `PoliticaVencimiento`, con `estaVencida(Solicitud, LocalDate)`, llamada desde `filtrar` y `resumen`. Si se decide ubicarla en `Solicitud`, coordinarlo con la zona B (#9).

#### SC-03 – Estado global y persistencia en la misma clase

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

#### SC-04 – `resumen` con mapas de `String` y `get(...) + 1`

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

#### SC-05 – Los catálogos devuelven las listas internas

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

#### Notas para la consolidación (#6)

Estos problemas atraviesan más de una zona. No abro una ficha propia para no duplicar la sección 2; Marina decide en #6 si se convierten en S-xx o quedan como notas.

- `SolicitudController` concentra los ocho endpoints y la lógica de negocio (441 líneas).
- `crear` recibe `Map<String, Object>` y castea sus valores (zona A).
- Estados, tipos y prioridades se representan como `String` en varias capas (A/B/C); se cruza con SC-04.
- Nombres abreviados: `m`, `lst`, `sol`, `pa`, `pb`.
- Las 22 respuestas de error se arman a mano.
- `LocalDate.now()` aparece en `registrarSolicitud`, `filtrar`, `cambiarEstado`, `asignarResponsable` y `resumen` (5 lugares).
- `// TODO` y código comentado al final de `registrarSolicitud`.
- `cargar` y `guardar` atrapan la excepción y siguen: un archivo JSON corrupto hace que el sistema arranque con los datos de prueba sin avisar, y una escritura fallida se pierde en silencio. Es comportamiento, no solo estructura, así que se decide en #6 si entra como hallazgo propio o como cambio intencional.
- `resumen` calcula los conteos y arma la respuesta en el mismo método (se cruza con SC-04).

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

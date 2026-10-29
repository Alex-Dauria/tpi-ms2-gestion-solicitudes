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

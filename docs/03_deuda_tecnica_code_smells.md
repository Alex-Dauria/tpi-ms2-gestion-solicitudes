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

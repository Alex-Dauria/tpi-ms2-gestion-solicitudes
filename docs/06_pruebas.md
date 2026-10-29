# 6. Pruebas y verificación del comportamiento

**Consolida:** Silvia · **Revisión cruzada:** Alex
**Aportes por zona:** A – Alex · B – Marina · C – Silvia

## 6.1 Estrategia

_Explicar el enfoque. Propuesta:_ **pruebas de caracterización**. Antes de refactorizar se escriben pruebas automatizadas que registran cómo se comporta hoy la versión inicial (respuesta, código HTTP y mensaje para cada caso). Esas mismas pruebas se ejecutan después de cada refactoring: si siguen en verde, el comportamiento externo se conservó.

- Herramientas: JUnit 5 + MockMvc (`@SpringBootTest` + `@AutoConfigureMockMvc`).
- Aislamiento: `@BeforeEach` restablece los datos en memoria mediante `BaseDeDatos.cargarDatosDePrueba()`. Además, se conserva una copia del JSON original y se restaura antes y después de las pruebas. Las ejecuciones deben realizarse sin otros procesos utilizando el mismo archivo de datos.
- Ejecución: `.\mvnw.cmd test` (Windows, PowerShell) o `./mvnw test`.

### Línea base del comportamiento externo

Además de las pruebas automatizadas, el script `scripts/linea_base.ps1` verifica el sistema "desde afuera", como lo usaría un cliente. Compila la versión del código que haya en la carpeta, la levanta con los datos de prueba iniciales, ejecuta siempre los mismos 12 pedidos HTTP (operaciones válidas y casos de error) y guarda el código y la respuesta de cada uno.

Como no depende del código interno, sirve igual antes y después del refactoring:

```powershell
# Versión inicial: se genera la referencia
.\scripts\linea_base.ps1 -Salida docs\evidencias\pruebas\linea_base_estado_inicial.md

# Versión final: se ejecuta lo mismo y se compara caso por caso con la referencia
.\scripts\linea_base.ps1 -Salida docs\evidencias\pruebas\linea_base_estado_refactorizado.md -CompararCon docs\evidencias\pruebas\linea_base_estado_inicial.md
```

En macOS o Linux se ejecuta igual con PowerShell 7: `pwsh ./scripts/linea_base.ps1 -Salida …`.

La fecha del día se reemplaza por `<HOY>` para que se puedan comparar corridas de días distintos. El resultado esperado al final es **12 de 12 casos iguales**. Si algún caso da distinto, hay que revisar si es una regresión o un cambio intencional documentado.

| Corrida | Archivo | Resultado |
|---|---|---|
| Versión inicial (`estado-inicial`) | `docs/evidencias/pruebas/linea_base_estado_inicial.md` | Referencia |
| Después del refactoring (`etapa-refactoring`) | `docs/evidencias/pruebas/linea_base_etapa_refactoring.md` | _completar_ |
| Versión final (`estado-refactorizado`) | `docs/evidencias/pruebas/linea_base_estado_refactorizado.md` | _completar_ |

### Qué significa "antes" y "después"

Las pruebas se escriben **después** de crear el tag `estado-inicial`, así que en ese tag no existen. Por eso:

- **Antes** = `main` con los PR de pruebas de las tres zonas ya mergeados y **antes del primer PR de refactoring**. El código del sistema (`src/main`) es idéntico al de `estado-inicial`; solo se agregaron las pruebas. Hay que anotar el commit en el que se corrieron.
- **Después del refactoring** = el tag `etapa-refactoring` (las tres zonas mergeadas). Ahí se corren las pruebas y la línea base para la sección 6 (#13).
- **Versión final** = el tag `estado-refactorizado`, que se crea después del code review final (#14). Como el code review puede generar correcciones, en #14 se vuelven a correr las pruebas y la línea base sobre ese tag.

## 6.2 Casos de prueba

_Un caso por regla o flujo. Tomar como base F1–F7 y RN-01 a RN-15 de la sección 1. Cada zona carga sus casos en su tabla, así se pueden escribir en paralelo sin pisarse. Silvia unifica la numeración en #13._

### 6.2.A Zona A – Registro (Alex)

| ID | Operación | Entrada / situación | Resultado esperado | Regla | Antes | Después |
|---|---|---|---|---|---|---|
| CP-A01 | | | | | | |

### 6.2.B Zona B – Ciclo de vida (Marina)

| ID | Operación | Entrada / situación | Resultado esperado | Regla | Antes | Después |
|---|---|---|---|---|---|---|
| CP-B01 | Obtener solicitud | GET `/solicitudes/1` | HTTP 200. Devuelve la solicitud 1 en estado PENDIENTE. | Consulta por ID | OK | Pendiente |
| CP-B02 | Obtener solicitud inexistente | GET `/solicitudes/99` | HTTP 404. Informa que la solicitud no existe. | Solicitud inexistente | OK | Pendiente |
| CP-B03 | Cambiar estado | EN_REVISION → RESUELTA, con comentario. | HTTP 200. Actualiza el estado y agrega un registro al historial. | Transición válida y trazabilidad | OK | Pendiente |
| CP-B04 | Cambiar estado | RECHAZADA → PENDIENTE. | HTTP 409. Rechaza la transición y conserva estado e historial. | Transición no permitida | OK | Pendiente |
| CP-B05 | Cambiar estado | PENDIENTE → EN_REVISION sin responsable. | HTTP 409. Exige un responsable asignado. | Responsable obligatorio para revisión | OK | Pendiente |
| CP-B06 | Rechazar solicitud | PENDIENTE → RECHAZADA sin comentario. | HTTP 400. Exige un comentario de rechazo. | Comentario obligatorio | OK | Pendiente |
| CP-B07 | Asignar responsable | Asignar responsable activo a solicitud PENDIENTE. | HTTP 200. Asigna responsable, pasa automáticamente a EN_REVISION y registra historial. | Asignación y transición automática | OK | Pendiente |
| CP-B08 | Asignar responsable | Intentar asignar responsable inactivo. | HTTP 409. Rechaza la asignación. | Responsable activo | OK | Pendiente |
| CP-B09 | Asignar responsable | Intentar asignar responsable inexistente. | HTTP 404. Informa que el responsable no existe. | Responsable inexistente | OK | Pendiente |
| CP-B10 | Asignar responsable | Responsable con cinco solicitudes abiertas. | HTTP 409. Impide superar el límite de asignaciones. | Máximo cinco solicitudes abiertas | OK | Pendiente |
| CP-B11 | Cambiar estado | Intentar modificar una solicitud CERRADA. | HTTP 409. Rechaza la transición. | Estado final | OK | Pendiente |
| CP-B12 | Asignar responsable | Intentar asignar responsable a solicitud RECHAZADA. | HTTP 409. Rechaza la asignación. | Estado final | OK | Pendiente |
| CP-B13 | Cambiar estado | Petición sin campo `estado`. | HTTP 400. Informa que el nuevo estado es obligatorio. | Validación de entrada | OK | Pendiente |
| CP-B14 | Reabrir solicitud | RESUELTA → EN_REVISION, con responsable ya asignado y cinco solicitudes abiertas adicionales. | HTTP 200. Permite reabrir sin aplicar nuevamente el límite de asignación. | Reapertura y límite aplicado al asignar | OK | Pendiente |

**Resultado de la ejecución inicial**

- **Herramientas:** JUnit 5 y MockMvc.
- **Clase:** `SolicitudZonaBTest.java`.
- **Comando:** `.\mvnw.cmd -Dtest=SolicitudZonaBTest test`.
- **Fecha:** 29/10/2026.
- **Resultado:** 14 pruebas ejecutadas, 14 correctas, 0 fallidas, 0 errores y 0 omitidas.
- **Estado de Maven:** `BUILD SUCCESS`.
- **Aislamiento:** los datos en memoria se reinician antes de cada prueba; se conserva y restaura el archivo JSON original. Se comprobó que el archivo coincide con su respaldo.
- **Etapa:** caracterización anterior al refactoring. La ejecución posterior queda pendiente.

### 6.2.C Zona C – Consultas y persistencia (Silvia)

| ID | Operación | Entrada / situación | Resultado esperado | Regla | Antes | Después |
|---|---|---|---|---|---|---|
| CP-C01 | | | | | | |

## 6.3 Evidencia antes / después

_Salida de `.\mvnw.cmd test` en cada momento (texto o captura), guardada en `docs/evidencias/pruebas/`. Junto con cada corrida se guarda también la carpeta `target/surefire-reports`, el informe que genera Maven automáticamente al correr las pruebas._

| Momento | Commit / tag | Pruebas ejecutadas | OK | Fallidas | Línea base | Evidencia |
|---|---|---|---|---|---|---|
| Antes (código inicial + pruebas) | _commit_ | | | | Referencia | |
| Después del refactoring | `etapa-refactoring` | | | | _x de 12 iguales_ | |
| Versión final | `estado-refactorizado` | | | | _x de 12 iguales_ | |

## 6.4 Cómo se verificó que el refactoring no alteró el comportamiento

_Explicar el procedimiento: pruebas en verde antes de empezar, un commit por refactoring, pruebas después de cada commit, revisión del PR, y qué hacer si una prueba falla._

## 6.5 Hallazgos

_Comportamientos raros de la versión inicial que aparecieron al escribir las pruebas, y qué se decidió con cada uno (conservarlo o cambiarlo como cambio intencional documentado)._

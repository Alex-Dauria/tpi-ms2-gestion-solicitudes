# 7. Code review profesional

**Checklist y criterios:** Silvia (#7) · **Revisión final del repositorio y registro:** Marina (#14)

> La consigna pide elaborar un checklist técnico y aplicarlo sobre el repositorio, registrando observaciones, feedback profesional, correcciones realizadas y criterios de aceptación técnica.

## 7.1 Proceso

La revisión se hace PR por PR, aplicando el checklist de la sección 7.2. La revisora deja sus observaciones como comentarios en el PR (archivo y línea cuando corresponda). La autora responde punto por punto y sube los ajustes en commits nuevos. Con la aprobación de la revisora, la autora mergea.

Rotación de revisión (según el plan de trabajo):

| Autora del PR | Revisora |
|---|---|
| Alex | Marina |
| Marina | Silvia |
| Silvia | Alex |

Los documentos que consolidan aportes de las tres (#6, #7, #15, #16, #17) los revisan las otras dos. En los PR de código se aplica el checklist completo; en los PR de documentación, las categorías aplicables (nombres, commits y PR, duplicación).

## 7.2 Checklist técnico

Cada ítem se responde con **Sí / No / No aplica**. Un **No** en un ítem bloqueante impide aprobar el PR.

| # | Categoría | Ítem de verificación | Concepto (materia) | Bloqueante |
|---|---|---|---|---|
| 1 | Funcionalidad y pruebas | Las pruebas automatizadas pasan en verde sobre el PR | Pruebas de caracterización, regresión | Sí |
| 2 | Funcionalidad y pruebas | El comportamiento externo (respuestas, códigos HTTP, mensajes) no cambió sin documentarlo: la línea base (`scripts/linea_base.ps1 -CompararCon …`) da 14 de 14 iguales, o los casos distintos están documentados como cambio intencional | Compatibilidad, no regresión | Sí |
| 3 | Funcionalidad y pruebas | Se cubren los casos límite y de error de la zona tocada | Casos de prueba, calidad interna | No |
| 4 | Responsabilidades (SRP) y diseño | Cada clase/método tiene una única razón para cambiar | SRP (SOLID) | No |
| 5 | Responsabilidades (SRP) y diseño | La lógica de negocio no está en el controlador | Separación de responsabilidades | No |
| 6 | Responsabilidades (SRP) y diseño | Las entidades protegen sus invariantes (sin setters públicos que salteen reglas) | Encapsulamiento, invariantes | No |
| 7 | Nombres y legibilidad | Los nombres son descriptivos y no usan abreviaturas mágicas | Clean Code – nombres | No |
| 8 | Nombres y legibilidad | Las funciones usan verbos y las variables, sustantivos | Clean Code – intención | No |
| 9 | Nombres y legibilidad | No hay números ni cadenas mágicas sin constante con nombre | Magic Number / Primitive Obsession | No |
| 10 | Funciones | Cada función hace una sola cosa | Clean Code – funciones | No |
| 11 | Funciones | La lista de parámetros es razonable (≤ 3, o Parameter Object) | Long Parameter List | No |
| 12 | Funciones | Las funciones son cortas y se leen de arriba abajo | Clean Code – tamaño | No |
| 13 | Manejo de errores | Los errores se manejan de forma explícita, sin `null` silencioso | Clean Code – errores | No |
| 14 | Manejo de errores | Los mensajes de error son claros y consistentes | UX de la API, mantenibilidad | No |
| 15 | Duplicación | No hay código duplicado; las reglas de negocio viven en un solo lugar | DRY, Duplicated Code | No |
| 16 | Duplicación | Las búsquedas/accesos a datos están centralizados | Extract Method / Repository | No |
| 17 | Acoplamiento y dependencias | El acoplamiento entre componentes es mínimo y por interfaces | Low coupling, DIP (SOLID) | No |
| 18 | Acoplamiento y dependencias | No se usa estado global compartido sin justificación | Testabilidad, acoplamiento | No |
| 19 | Commits y PR | Los commits son atómicos, en español y con prefijo (docs:, test:, refactor:, feat:, fix:, chore:) | Flujo de trabajo del equipo | No |
| 20 | Commits y PR | El PR tiene descripción, referencia al issue (`Closes #N`) y evidencia | Plantilla de PR | No |
| 21 | Commits y PR | El diff no incluye cambios no relacionados | Alcance del PR | Sí |
| 22 | Commits y PR | En PRs de refactoring: un commit por refactoring con su id (R-xx), pruebas en verde después de cada uno y merge sin squash | Reglas de refactoring seguro (plan §6) | Sí (PRs de refactoring) |

### Versión corta (para pegar como comentario en un PR de código)

```
Checklist de code review (#7):
[ ] 1. Pruebas en verde y sin regresión de comportamiento externo
[ ] 2. SRP: lógica de negocio fuera del controlador; entidades protegen invariantes
[ ] 3. Nombres descriptivos, sin magic numbers/strings
[ ] 4. Funciones cortas, una sola cosa, ≤ 3 parámetros (o Parameter Object)
[ ] 5. Errores explícitos, mensajes claros
[ ] 6. Sin código duplicado ni reglas repetidas
[ ] 7. Bajo acoplamiento, sin estado global injustificado
[ ] 8. Commits atómicos con mensaje en español; PR con descripción, issue y evidencia
[ ] 9. En refactorings: un commit por refactoring (R-xx), pruebas en verde después de cada uno, merge sin squash
Severidad: Bloqueante / Mayor / Menor / Sugerencia
```

## 7.3 Criterios de aceptación técnica

Un PR se aprueba cuando se cumplen todos:

1. Todas las pruebas automatizadas pasan en verde.
2. No hay cambios de comportamiento externo no documentados (línea base: 14 de 14 iguales, o los casos distintos documentados como cambio intencional).
3. Ninguna observación bloqueante queda abierta.
4. Las observaciones mayores están resueltas o aceptadas por la revisora.
5. El diff respeta el alcance declarado en el PR (sin cambios no relacionados).
6. Los commits siguen el formato del equipo y el PR referencia el issue.

## 7.4 Registro de observaciones

| ID | PR | Revisora | Archivo / línea | Categoría | Severidad | Observación | Acción tomada | Estado |
|---|---|---|---|---|---|---|---|---|
| CR-01 | [#20](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/20) | Silvia y Marina | `scripts/linea_base.ps1`, `docs/evidencias/pruebas/linea_base_estado_inicial.md` | Funcionalidad y pruebas | Menor (clasificación posterior) | La línea base no incluía el legajo numérico (`500`) ni `vencidas=false`, así que la comparación no detectaría un cambio en esos casos ([Silvia](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/20#issuecomment-6447312924), [Marina](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/20#issuecomment-6487523728)) | Se agregaron los casos 13 y 14, y el timestamp del `500` se reemplaza por `<TIMESTAMP>` para poder comparar: `b2960a3` ([respuesta](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/20#issuecomment-6508481046)) | Resuelta |
| CR-02 | [#20](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/20) | Marina | `docs/evidencias/pruebas/linea_base_estado_inicial.md` | Funcionalidad y pruebas | Sugerencia (clasificación posterior) | Relacionar cada caso de la línea base con la funcionalidad o regla de negocio que verifica ([comentario](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/20#issuecomment-6487523728)) | Se agregó la columna "Regla" (F1, RN-06, etc.): `b2960a3` | Resuelta |
| CR-03 | [#24](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/24) | Alex | `src/test/.../SolicitudZonaBTest.java` | Funcionalidad y pruebas | Menor (ítem 6: el revisor la etiquetó "menor"; ítems 1 a 3: clasificación posterior, el revisor indicó "no bloquean el merge") | Faltaban casos de la versión inicial que el refactoring de la zona B podía alterar: estado inexistente (`409`), estado en minúsculas, texto del historial y asignación sin `responsableId` ([revisión](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/24#pullrequestreview-5705085537)) | Se agregaron CP-B15, CP-B16 y CP-B17 y se amplió CP-B03 para verificar el texto del historial: `062fbec`, con 17 de 17 en verde ([respuesta](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/24#issuecomment-6595405752)) | Resuelta |
| CR-04 | [#24](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/24) | Alex | `src/test/.../SolicitudZonaBTest.java` (CP-B11) | Duplicación | Menor | CP-B11 usaba `BaseDeDatos.solicitudes.get(0)`: dependía del orden de la lista y de la estructura interna ([revisión](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/24#pullrequestreview-5705085537)) | La solicitud ahora se busca por id: `062fbec` | Resuelta |
| CR-05 | [#24](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/24) | Alex | `docs/06_pruebas.md` (tabla 6.2.B) | Funcionalidad y pruebas | Menor | La columna "Regla" usaba texto libre en lugar de los identificadores F y RN de la sección 1 ([revisión](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/24#pullrequestreview-5705085537)) | Se reemplazó por identificadores F y RN: `062fbec` ([respuesta](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/24#issuecomment-6595405752)) | Resuelta |
| CR-06 | [#24](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/24) | Alex | `src/test/.../SolicitudZonaBTest.java`, línea 1 | Nombres y legibilidad | Menor | El archivo empezaba con una línea en blanco antes de `package` ([revisión](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/24#pullrequestreview-5705085537)) | La respuesta no lo menciona, pero se comprueba en el archivo: en `4e02aeb` la línea 1 está vacía y en `062fbec` empieza con `package` | Resuelta |
| CR-07 | [#28](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/28) | Alex (autor, al aplicar el checklist) | `SolicitudController` (`registrarSolicitud`) | Responsabilidades (SRP) y diseño | Menor (clasificación posterior) | Ítems 4 y 5 del checklist en "Parcial": el registro quedó separado en métodos, pero las reglas seguían dentro del controlador ([checklist aplicado](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/28#issuecomment-6692407996)) | PR [#35](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/35): RA-09 (`c6ca13c`) y RA-10 (`d7996e3`) llevan las reglas a `servicio/ReglasRegistro`. Marina lo aprobó con 69 de 69 y línea base 14 de 14 ([revisión](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/35#pullrequestreview-5809036890)) | Resuelta |
| CR-08 | [#31](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/31) | Alex | `src/test/.../SolicitudZonaCTest.java` | Funcionalidad y pruebas | Mayor (clasificación posterior, por su impacto: las pruebas no detectaban regresiones en el código que se iba a refactorizar; el revisor aprobó el PR y aceptó sumar los casos en otro PR, siempre antes del refactoring) | Prueba de mutación: con seis errores introducidos a propósito (plazos de vencimiento, mayúsculas en filtros, `vencidas=false`, orden, `abiertasPorResponsable` y `guardar()`), las 12 pruebas seguían en verde. La persistencia no tenía pruebas ([revisión](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/31#pullrequestreview-5769291697)) | Se reforzaron las pruebas: persistencia, límites exactos de RN-14, filtros, orden y reporte, hasta 21 casos (`c144187`). Una nueva mutación, de a un error por vez, se detectó 7 de 7 ([respuesta](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/31#issuecomment-6669079154), [verificación](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/31#issuecomment-6692096800)) | Resuelta |
| CR-09 | [#31](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/31) | Alex | `docs/06_pruebas.md` (6.2.C) y `SolicitudZonaCTest.java` | Funcionalidad y pruebas | Menor | Trazabilidad entre la tabla y las pruebas: CP-C02 citaba RN-07 por error, CP-C05 no verificaba los ids y los métodos no indicaban a qué CP-Cxx correspondían ([revisión](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/31#pullrequestreview-5769291697)) | CP-C02 pasó a citar solo F2 y CP-C05 verifica los ids 5 y 1 ([respuesta](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/31#issuecomment-6669079154)). Se agregaron códigos CP-Cxx en `@DisplayName` y en comentarios: `cb8cf16`, `d73cbc8`, `eabfa37` | Resuelta |
| CR-10 | [#38](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/38) | Alex | `SolicitudController` (`filtrar` y `resumen`) | Duplicación | Mayor | S-05, de prioridad Alta, seguía sin resolver: la regla de vencimiento (RN-14) estaba duplicada en `filtrar` y `resumen`, con plazos como números sueltos ([revisión](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/38#pullrequestreview-5813940110)) | RC-03: Extract Class `PoliticaVencimiento`, en un commit propio: `63d7b64` ([respuesta](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/38#issuecomment-6711005366)). Segunda revisión: 69 de 69 en cada commit, 84 de 84 integrado y 14 de 14; si se cambia el plazo de ALTA o de BAJA, falla una prueba ([revisión](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/38#pullrequestreview-5814665657)) | Resuelta |
| CR-11 | [#38](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/38) | Alex | `docs/05_refactoring.md` (5.2.C) | Acoplamiento y dependencias | Mayor | Faltaba dejar escrito qué smells de la zona no se abordaban y por qué: S-06 (estado global en `BaseDeDatos`) y S-13 ([revisión](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/38#pullrequestreview-5813940110)) | Se agregó el apartado "Lo que no se aborda" al final de 5.2.C ([respuesta](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/38#issuecomment-6711005366)). El revisor lo aceptó como justificación en la segunda revisión ([revisión](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/38#pullrequestreview-5814665657)) | Resuelta |
| CR-12 | [#38](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/38) | Alex | `docs/04_solid_clean_code.md`, `docs/05_refactoring.md` (RC-01) | Responsabilidades (SRP) y diseño | Menor | La documentación no coincidía con el código: decía que `filtrar` "ahora coordina", que DIP "no aplica" aunque el controlador depende de `BaseDeDatos`, y que agregar un filtro "toca un solo lugar" ([revisión](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/38#pullrequestreview-5813940110)) | Se corrigieron SRP (con RC-03), DIP (el problema existe y queda pendiente como S-06) y el beneficio de RC-01 ([respuesta](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/38#issuecomment-6711005366)) | Resuelta |
| CR-13 | [#38](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/38) | Alex | `docs/05_refactoring.md` (5.2.C), `docs/04_solid_clean_code.md`, descripción del PR | Nombres y legibilidad | Menor | Formato: 5.2.C quedó después de "Modelo de ficha", faltaba una línea en blanco antes de `## 4.1` y había detalles en el título y la descripción del PR ([revisión](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/38#pullrequestreview-5813940110)). En la segunda revisión, RC-03 y "Lo que no se aborda" seguían fuera de lugar ([revisión](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/38#pullrequestreview-5814665657)) | Silvia movió RC-01 y RC-02 y agregó la línea en blanco ([respuesta](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/38#issuecomment-6711005366)). El revisor completó la ubicación al resolver el conflicto: `f7a6053` y `4645a83` ([comentario](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/38#issuecomment-6712327441)) | Resuelta |
| CR-14 | [#38](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/38) | Alex | `docs/05_refactoring.md` (ficha RC-03) | Nombres y legibilidad | Mayor (clasificación posterior: el revisor lo pidió "antes del merge") | Ficha RC-03 incorrecta o incompleta: técnica "Extract Method" en lugar de Extract Class, commit "(pendiente)" y sin el código real en "Antes" ([revisión](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/38#pullrequestreview-5814665657)) | El revisor completó la ficha en la rama de la autora, sin tocar el código ni los commits de refactoring: `4645a83` ([comentario](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/38#issuecomment-6712327441)). Verificación: 84 de 84 y 14 de 14 | Resuelta |
| CR-15 | [#38](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/38) | Alex | `SolicitudController` (`filtrar`) | Funciones | Sugerencia | Usar `filtro.estado()` y los demás accesos directamente, en lugar de copiarlos a seis variables locales ([revisión](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/38#pullrequestreview-5813940110)) | No se aplicó: la autora justificó que las variables hacen más legibles las condiciones ([respuesta](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/38#issuecomment-6711005366)) y el revisor lo aceptó ([revisión](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/38#pullrequestreview-5814665657)) | No aplicada, con justificación aceptada |
| CR-16 | [#41](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/41) | Silvia | `SolicitudController` (`cambiarEstado`, `asignarResponsable`) | Funcionalidad y pruebas | Menor | Verificar que RB-01 y RB-02 no rompan las pruebas de la zona C, como CP-C02 y CP-C04 ([comentario](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/41#issuecomment-6712084171)) | En el #41 no hay respuesta escrita. Hay verificación posterior con las tres zonas juntas, incluidas las 21 pruebas de la zona C: 84 de 84 y 14 de 14 al combinar las ramas ([revisión del #38](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/38#pullrequestreview-5813940110)), sobre `main` integrado ([revisión del #38](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/38#pullrequestreview-5814665657)) y en la corrida "después" de la Etapa 6 sobre `45f9510` (`docs/evidencias/pruebas/etapa-6-despues/`, PR [#40](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/40)) | Resuelta por verificación posterior |
| CR-17 | [#44](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/44) (documentación) | Alex | `docs/05_refactoring.md` (R-18 / RC-03) | Funcionalidad y pruebas | Sugerencia | Sumar a la ficha R-18 que una prioridad desconocida no puede llegar por la API, porque RN-04 la rechaza al registrar, y que con ese valor `resumen` ya fallaba antes de calcular el vencimiento ([revisión](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/44#pullrequestreview-5815111116)) | No se aplicó. La ficha R-18 ya reconoce la limitación ("No se afirma equivalencia absoluta para prioridades desconocidas" y "Limitación pendiente de revisión técnica"). El caso no corresponde al ingreso normal por API: `ReglasRegistro` rechaza en el alta cualquier prioridad que no sea válida, y ningún endpoint modifica la prioridad, así que solo podría aparecer editando `datos/solicitudes.json` a mano | No aplicada; limitación documentada |

Severidad: **Bloqueante** (impide aprobar) · **Mayor** (corregir en este PR) · **Menor** (puede quedar para después) · **Sugerencia**.

"Clasificación posterior" indica que el revisor original no etiquetó la observación con esta escala y que se la clasificó al consolidar el registro, a partir de lo que el revisor escribió en el comentario y, en CR-08, del impacto de la observación.

## 7.5 Aplicación del checklist sobre el repositorio final

El checklist de 7.2 se aplicó sobre el código integrado en `main` (`26b943e`). El código de producción (`src/` y `pom.xml`) es idéntico al del commit `45f9510`, sobre el que se registró la evidencia "después" de la Etapa 6. Las líneas citadas corresponden a `26b943e`. Las secciones 4 y 5 se citan según su versión integrada en `main` (PR #42 y #44); esa integración no modificó el código de producción. Esta revisión no modificó código ni agregó refactorings.

**Estado:** los cuatro criterios bloqueantes (1, 2, 21 y 22) dan Sí. Los criterios 1 y 2 se verificaron sobre el commit `50bac22`; el tag `estado-refactorizado` todavía está pendiente de creación y de verificación definitiva sobre `main`. La aprobación de esta sección corresponde a la revisión del PR.

| # | Criterio | Bloqueante | Resultado | Evidencia | Observación y tratamiento |
|---|---|---|---|---|---|
| 1 | Pruebas automatizadas en verde | Sí | Sí | 84 de 84 sobre `50bac22` (7/11): 31 de la zona A, 22 + 10 de la zona B y 21 de la zona C, sin fallas, errores ni omisiones (`docs/evidencias/pruebas/estado-refactorizado/`, tabla 6.3). Coincide con la evidencia histórica de 84 de 84 sobre `45f9510` (PR #40); el código de producción es el mismo | Verificado sobre `50bac22`, no sobre el tag `estado-refactorizado`, que todavía no existe |
| 2 | Comportamiento externo sin cambios no documentados | Sí | Sí | 14 de 14 casos iguales a `linea_base_estado_inicial.md` sobre `50bac22` (`docs/evidencias/pruebas/linea_base_estado_refactorizado.md`), con las mismas respuestas que la corrida histórica sobre `45f9510`. El `500` del legajo numérico (CP-25) se conserva a propósito y está documentado en 6.5 | Verificado sobre `50bac22`. Al terminar, el script dejó el servidor en ejecución y hubo que detenerlo manualmente (nota de 6.3) |
| 3 | Casos límite y de error cubiertos | No | Sí | CP-01 a CP-70 (6.2): límites de RN-03 y de RN-14 (3/4, 7/8 y 15/16 días), límite de asignaciones, recursos inexistentes, entradas inválidas y persistencia (CP-60 a CP-62 y CP-68). Mutación de la zona C: 7 de 7 detectadas (CR-08) | Sin prueba para una falla de escritura en `guardar()`: ver HF-01 |
| 4 | Una única razón de cambio por clase o método | No | No (mejora parcial) | Salieron del controlador las reglas del registro (`ReglasRegistro`, R-09 y R-10), las transiciones (`Solicitud`, R-11 a R-13) y el vencimiento (`PoliticaVencimiento`, R-18). `SolicitudController` todavía filtra, ordena, arma el reporte, asigna y persiste; `BaseDeDatos` mezcla estado y persistencia | Deuda documentada: SRP y OCP como mejora parcial en 4.1, "Lo que no se aborda" en 5.2.C y S-06 |
| 5 | La lógica de negocio no está en el controlador | No | No (mejora parcial) | `SolicitudController.cambiarEstado` repite RN-08 y RN-09 (líneas 220–225), que también valida `Solicitud.cambiarEstado` (líneas 87–91). Las reglas de asignación siguen en `asignarResponsable` (líneas 239–270) | La repetición de RN-08 y RN-09 está documentada en 4.1 y 4.3. Reglas de asignación: ver HF-04 |
| 6 | Las entidades protegen sus invariantes | No | No | `Solicitud.setEstado()` es público. `getHistorial()` devuelve la lista interna y el controlador le agrega entradas (líneas 104 y 274); también usa `setResponsableId` y `setFechaUltimoCambio` (líneas 273 y 279) | `setEstado()`: S-04 parcial, documentado en R-13 y 4.3. Historial y setters: ver HF-02 |
| 7 | Nombres descriptivos, sin abreviaturas | No | No (menor) | Quedan `lst`, `ok`, `venc`, `porResp`, `x` y `k` en `filtrar` y `resumen`, y `sol`, `rid`, `cant` y `r` en `asignarResponsable`. Las clases nuevas usan nombres descriptivos | Deuda documentada en 4.2, que registra los de `filtrar` y `resumen` |
| 8 | Funciones con verbo, variables con sustantivo | No | Sí | `validarSolicitud`, `buscarSolicitudAbierta`, `construirSolicitud`, `permiteTransicionA`, `estaVencida`, `prioridadDe` | — |
| 9 | Sin números ni cadenas mágicas | No | No | Constantes en el registro (R-07), el límite de asignaciones (R-15) y los plazos (R-18). Los estados siguen como texto en el controlador, la entidad y `PoliticaVencimiento`; el orden del listado usa 1, 2 y 3 según la prioridad (líneas 173–174), y `resumen` repite las claves de estados, tipos y prioridades (líneas 291–308) | Deuda documentada: estados como `String` (S-10; el `enum` se postergó en "Decisiones pendientes" de 5.2.B) y `resumen` (S-13). Ver HF-03 |
| 10 | Cada función hace una sola cosa | No | No (mejora parcial) | `registrarSolicitud` coordina pasos con nombre. `filtrar` filtra y ordena; `asignarResponsable` valida, busca, cuenta, asigna, registra el historial y persiste; `resumen` hace cuatro conteos | Deuda documentada en 4.2 |
| 11 | Hasta 3 parámetros o Parameter Object | No | No (menor) | `filtrar` y `registrarSolicitud` reciben un objeto (R-16 y R-03). `construirSolicitud` recibe 5 parámetros (línea 93) | Ver HF-05 |
| 12 | Funciones cortas | No | No | `filtrar` y `resumen`, 53 líneas cada una; `asignarResponsable`, 50. `registrarSolicitud` bajó de 105 a 33 | Deuda documentada en 4.2 |
| 13 | Errores explícitos, sin `null` silencioso | No | No | `BaseDeDatos.guardar()` atrapa `Exception` y solo imprime la traza (líneas 55–57). `buscarSolicitudPorId` devuelve `null`, y los tres métodos que la usan lo convierten en 404. `cargar()` con un archivo corrupto arranca con los datos de prueba (CP-62, se conserva) | Ver HF-01. El `500` del legajo numérico (CP-25) y el `NullPointerException` latente de `resumen` (S-13) están documentados |
| 14 | Mensajes de error claros y consistentes | No | Sí | Un único `respuestaError` con el formato `{"error": "..."}` (R-01); mensajes verificados por CP-01 a CP-70 | La excepción documentada es el `500` del legajo numérico (CP-25) |
| 15 | Sin duplicación; reglas en un solo lugar | No | No (mejora parcial) | Se resolvieron S-02 (R-01), S-05 (R-18) y S-09 (R-14). Siguen repetidas RN-08 y RN-09, la regla de solicitud abierta, la búsqueda de responsable por id y la lista de tipos entre `ReglasRegistro` y `resumen` | RN-08 y RN-09: documentadas en 4.1. Ver HF-03 y HF-04 |
| 16 | Búsquedas y acceso a datos centralizados | No | No (mejora parcial) | `buscarSolicitudPorId` centraliza la búsqueda de solicitudes (R-14). La búsqueda de responsables se hace en línea dos veces y el controlador accede directamente a las listas de `BaseDeDatos` | Deuda documentada: S-06. Ver HF-04 |
| 17 | Acoplamiento mínimo y por interfaces | No | No | El controlador depende de las clases concretas y estáticas `BaseDeDatos`, `ReglasRegistro` y `PoliticaVencimiento`; el proyecto no define interfaces | Deuda documentada: DIP "no se mejoró" en 4.1 y S-06 |
| 18 | Sin estado global injustificado | No | No (justificado) | `BaseDeDatos` expone listas `public static` y `ultimoId`, que el controlador incrementa (línea 74). R-17 solo copia las listas de los catálogos | Justificado en "Lo que no se aborda" de 5.2.C (CR-11); S-06 |
| 19 | Commits atómicos, en español y con prefijo | No | Sí, con observaciones | Los commits de `main` que no son merges usan prefijo, salvo `5de5ac7 Initial commit`, generado por GitHub al crear el repositorio. Los merges automáticos (`Merge remote-tracking branch…`) no llevan prefijo | Observaciones registradas; no se reescribe el historial |
| 20 | PR con descripción, issue y evidencia | No | Sí | #28 y #35 cierran #8, #38 cierra #10 y #41 cierra #9; los PR de pruebas (#24, #26 y #31) usan `Refs`. Todos declaran resultados y evidencia | — |
| 21 | El diff no incluye cambios no relacionados | Sí | Sí | Los PR de refactoring tocan su zona y su documentación | — |
| 22 | Refactoring: un commit por R-xx, pruebas después de cada uno, sin squash | Sí (PR de refactoring) | Sí, con observaciones | 18 commits `refactor(…)`, uno por refactoring; la tabla 5.1 hace corresponder RA, RB y RC con R-01 a R-18. Los merges de #28 (`142f937`), #35 (`264aeb5`), #38 (`35e29c4`) y #41 (`4426062`) tienen dos padres. Pruebas por commit registradas en las fichas, en las revisiones de #35 y #38 y en IA-16 | Los commits usan el identificador original (RA, RB o RC); la correspondencia con R-xx está en 5.1. `63d7b64` incluye RC-03 y su documentación. Los desvíos de revisión de #28 y #41 se tratan en 7.6 |

**Resumen:** de los 22 criterios, 9 se evaluaron positivamente y 13 dieron negativo o parcial.

- **Positivos (9):** 1, 2, 3, 8, 14, 20 y 21 (Sí), y 19 y 22 (Sí, con observaciones).
- **Negativos o parciales (13):** 4, 5, 6, 7, 9, 10, 11, 12, 13, 15, 16, 17 y 18. Ninguno es bloqueante y todos quedan documentados como deuda técnica.

### Hallazgos de la revisión final

Hallazgos que no estaban registrados en las secciones anteriores. Se documentan como deuda técnica, sin cambios de código: corregir HF-01 modificaría el comportamiento externo, y el resto requeriría nuevos refactorings con sus pruebas y revisión.

| ID | Hallazgo | Ubicación | Criterios | Motivo para no corregirlo ahora |
|---|---|---|---|---|
| HF-01 | Si falla la escritura del archivo, el error se descarta: la API responde con éxito y el cambio se pierde al reiniciar | `BaseDeDatos.guardar()` (líneas 55–57) | 3, 13 | Informar el error al cliente cambiaría el comportamiento externo; no es un refactoring |
| HF-02 | El historial se puede modificar desde fuera de la entidad, y el controlador usa setters públicos | `Solicitud.getHistorial()`; `SolicitudController` (líneas 104, 273, 274 y 279) | 6 | Encapsularlo afecta la persistencia con Jackson y la asignación; es el mismo frente que S-04 |
| HF-03 | La regla de solicitud abierta (`PENDIENTE` o `EN_REVISION`) se repite en cuatro lugares | `SolicitudController` (líneas 85, 264 y 321) y `PoliticaVencimiento.estaVencida` (línea 24) | 9, 15 | Depende de la representación de los estados (S-10) |
| HF-04 | La búsqueda de un responsable por id está duplicada, y las reglas de asignación siguen en el controlador | `asignarResponsable` (líneas 239–270) y `resumen` (líneas 323–328) | 5, 15, 16 | Mismo alcance que S-06 y que la mejora parcial de SRP |
| HF-05 | `construirSolicitud` recibe cinco parámetros | `SolicitudController.construirSolicitud` (línea 93) | 11 | Bajo impacto: método privado de construcción |

### Deuda técnica documentada que se mantiene

- SRP y OCP como mejora parcial: 4.1.
- `setEstado()` público, S-04 parcial: R-13 y 4.3.
- Estados representados como `String` (S-10); el `enum` se postergó: "Decisiones pendientes" de 5.2.B y 4.2.
- Estado global y persistencia en `BaseDeDatos` (S-06), sin mejora de DIP: 4.1 y "Lo que no se aborda" de 5.2.C.
- `resumen` con mapas de `String` y `NullPointerException` latente (S-13): 5.2.C.
- Métodos largos y nombres abreviados en `filtrar`, `resumen` y `asignarResponsable`: 4.2.
- RN-08 y RN-09 repetidas en el controlador y en `Solicitud`: 4.1 y 4.3.
- `500` del legajo numérico, conservado a propósito: CP-25 y 6.5.

### Verificación pendiente

1. Con este PR integrado, crear el tag `estado-refactorizado` sobre `main` (y `estadorefactorizado` sobre el mismo commit).
2. Verificar que el código de producción del tag coincida con el de `50bac22` (`git diff 50bac22 estado-refactorizado -- src pom.xml` sin diferencias). Si hubiera diferencias, repetir las pruebas y la línea base sobre el tag.

## 7.6 Feedback y aprendizajes

### Feedback objetivo que cambió el trabajo

Las observaciones con más efecto se apoyaron en evidencia verificable y propusieron una acción concreta:

- **Medir la calidad de las pruebas (PR #31, CR-08).** El revisor introdujo errores a propósito para comprobar si las pruebas los detectaban, en lugar de limitarse a verificar que pasaran. Con los casos agregados por la autora, la zona C se refactorizó con una red de seguridad que antes no tenía. Aprendizaje: una prueba en verde no demuestra que proteja el comportamiento.
- **Anticipar lo que el propio refactoring podía romper (PR #24, CR-03).** El revisor pidió fijar, antes del refactoring de la zona B, comportamientos fáciles de alterar sin querer. Esos casos quedaron en la suite durante R-12 y R-13. Aprendizaje: la caracterización se diseña pensando en el cambio que viene.
- **Revisar la prioridad, no solo la corrección (PR #38, CR-10 y CR-11).** El código funcionaba, pero dejaba sin tratar un smell de prioridad alta según 3.4. El pedido de cambios llevó a resolverlo (R-18) y a justificar por escrito lo que quedaba fuera del alcance. Aprendizaje: el diagnóstico priorizado también es un criterio de revisión.
- **Ampliar la referencia de comparación (PR #20, CR-01).** Dos revisoras detectaron, por separado, casos que la línea base no cubría. Aprendizaje: la referencia contra la que se compara también se revisa.

### Sugerencias no aplicadas, con justificación

No todas las observaciones terminaron en un cambio. En CR-15 la autora explicó por qué conservaba su versión y el revisor aceptó el argumento. En CR-17 la sugerencia no se aplicó porque la documentación ya reconocía la limitación. Una sugerencia es una propuesta, no una obligación: corresponde responderla con una razón verificable.

### Desvíos del proceso de revisión

Según 7.1, la autora sube los ajustes y mergea con la aprobación de la revisora, y los documentos que consolidan aportes de las tres los revisan las otras dos. En cinco PR la integración se apartó de ese proceso:

| PR | Desvío verificado | Motivo registrado en el PR |
|---|---|---|
| [#27](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/27) | La issue #7 pedía dos aprobaciones y hubo una, emitida sobre un commit anterior a los ajustes; los ajustes se verificaron después en un comentario. El PR lo integró el revisor | "estamos con los tiempos justos" ([comentario](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/27#issuecomment-6692083823)) |
| [#28](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/28) | La revisión de la revisora asignada fue de compatibilidad y sin aprobación. El autor integró el PR y dejó registrada como pendiente la aprobación integral | "las zonas B y C dependen de esto y estamos con los tiempos justos" ([comentario](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/28#issuecomment-6692407996)) |
| [#38](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/38) | Después de aprobar, el revisor agregó dos commits en la rama de la autora (`f7a6053` y `4645a83`) e integró el PR. Esos commits no tuvieron revisión de otra integrante | "Para ganar tiempo" ([comentario](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/38#issuecomment-6712327441)) |
| [#40](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/40) | El revisor agregó `fb2181d` en la rama de la autora, aprobó ese mismo commit e integró el PR | "Para ganar tiempo" ([comentario](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/40#issuecomment-6713274997)) |
| [#41](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/41) | La aprobación quedó como comentario, sin revisión formal de GitHub (la revisora sigue figurando como revisión solicitada), y el PR lo integró la revisora | No hay un motivo registrado |

Las pruebas funcionales y la línea base posteriores (84 de 84 y 14 de 14 sobre `45f9510`) no mostraron regresiones. Esos resultados aportan evidencia de que se conservó el comportamiento externo en los casos verificados, pero no garantizan la ausencia de otros defectos ni reemplazan las revisiones independientes del código. También en los PR #39 y #40 se pidió una nueva revisión antes de que los cambios estuvieran en la rama remota, y el revisor lo detectó al no encontrarlos ([PR #39](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/39#issuecomment-6712286753), [PR #40](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/40#pullrequestreview-5814665729)).

### Aprendizajes

- **Pruebas.** Las pruebas de caracterización tienen que fijar valores exactos (códigos, mensajes, orden y límites), y conviene medir su capacidad de detección antes de refactorizar.
- **Refactoring seguro.** Un commit por refactoring, con pruebas y línea base después de cada uno y merge sin squash, permitió revisar cada cambio por separado. Conservar los defectos encontrados, como el `500` del legajo numérico, fue una decisión explícita: corregirlos habría sido cambiar el comportamiento.
- **Trazabilidad.** Los identificadores (S, RA/RB/RC, R-xx, CP-xx, CR-xx) vincularon hallazgos, cambios, pruebas y revisiones. Cada renumeración (CP-01 a CP-70 y R-01 a R-18) necesitó una tabla de correspondencia para conservar las referencias anteriores.
- **Colaboración.** El feedback fue más útil cuando incluyó evidencia y una propuesta concreta, y cuando la autora pudo discutirlo con argumentos. Cuando quien revisa también escribe el cambio, ese cambio queda sin una revisión independiente.

### Prácticas para próximos proyectos

**Mantener:**
- Aplicar el checklist como comentario en cada PR de código, indicando las verificaciones y el commit sobre el que se hicieron.
- Medir las pruebas con errores introducidos antes de refactorizar.
- Usar `Refs #N` en los PR parciales y `Closes #N` solo en el que completa la issue.
- Registrar la justificación cuando una sugerencia no se aplica.

**Mejorar:**
- Aprobar con una revisión formal de GitHub sobre el último commit del PR, no con un comentario.
- Que la revisora proponga los cambios (por ejemplo, con sugerencias en el PR) en lugar de subirlos a la rama de la autora, y que la autora integre el PR.
- Configurar la protección de `main` para exigir una aprobación y descartar las aprobaciones anteriores a un commit nuevo.
- Comprobar que los cambios estén en la rama remota antes de pedir una nueva revisión.
- Reservar tiempo de revisión en el cronograma.
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

_Resultado del checklist sobre `estado-refactorizado`._

## 7.6 Feedback y aprendizajes

_Qué se aprendió de revisar y ser revisada: ejemplos de feedback objetivo y cómo cambió el código._
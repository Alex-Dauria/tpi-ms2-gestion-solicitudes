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
| CR-01 | | | | | | | | |

Severidad: **Bloqueante** (impide aprobar) · **Mayor** (corregir en este PR) · **Menor** (puede quedar para después) · **Sugerencia**.

## 7.5 Aplicación del checklist sobre el repositorio final

_Resultado del checklist sobre `estado-refactorizado`._

## 7.6 Feedback y aprendizajes

_Qué se aprendió de revisar y ser revisada: ejemplos de feedback objetivo y cómo cambió el código._
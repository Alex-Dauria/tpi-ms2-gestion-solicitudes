# IA-16 – Refactoring y SOLID/Clean Code de la zona B

**Integrante:** Marina Giselle Cordero

**Herramientas:** ChatGPT (OpenAI) y Codex

**Etapa:** 5 – Refactoring y 4 – SOLID/Clean Code

**Período de trabajo:** 06/11/2026 al 07/11/2026

**Rama:** `zona-b/refactoring`

**Issue:** #9

**Grado de intervención:** Alto

Este archivo es una síntesis de los pedidos principales, aportes de las herramientas, decisiones humanas y verificaciones realizadas. No es una transcripción literal ni completa y no se publica un enlace a la conversación. Se mantiene la modalidad de registro documentado utilizada en IA-05 e IA-11.

El identificador IA-16 se eligió tras revisar los registros locales y las referencias remotas actualizadas: IA-15 ya estaba utilizado por Silvia en `origin/zona-c/evidencia-ia-refactoring`. No se incorpora ni modifica ese aporte desde esta rama.

## Finalidad y grado de intervención

Usar ChatGPT y Codex como apoyo para analizar alternativas, implementar y revisar los refactorings RB-01 a RB-05, generar pruebas adicionales de zona B y redactar su documentación en los documentos 04 y 05.

Se declara intervención **Alta** según la escala del proyecto: las herramientas generaron código de producción, pruebas y borradores de documentación que se revisaron, verificaron y adoptaron bajo las instrucciones de Marina. La implementación asistida y la ejecución de comandos las realizó Codex; Marina definió el alcance, revisó los resultados presentados y autorizó por separado los commits. La intervención de IA se declara como tal y no se presenta como código escrito íntegramente por la integrante.

## Registro resumido de intercambios

Los pedidos de esta tabla son síntesis, no citas textuales. El intercambio con ChatGPT y el trabajo de Codex se registran conjuntamente; no se atribuyen a una herramienta propuestas separadas que no puedan distinguirse en el intercambio.

| Pedido | Aporte de las herramientas | Decisión o revisión humana | Verificación realizada |
|---|---|---|---|
| Preparar el entorno y analizar la reubicación de las transiciones | Revisión del código y propuesta de mover la decisión a `Solicitud.permiteTransicionA`; implementación de RB-01 | Marina acotó el cambio a la validación de transiciones, manteniendo las restantes validaciones en el controlador | 69 pruebas correctas; comparación de la lógica trasladada con la original |
| Sustituir los condicionales por una tabla | Implementación de RB-02 con `Map<String, Set<String>>` estático e inmutable | Marina indicó las transiciones exactas y conservar la firma pública, sin introducir estados nuevos ni un enum | 69 pruebas correctas; comparación de 81 combinaciones con RB-01, incluidas las 25 entre estados válidos |
| Analizar la protección del estado y la compatibilidad con Jackson antes de implementar | Localización de usos de `setEstado`, análisis de inicialización, carga JSON y flujos operativos; propuesta de encapsular estado, historial y fecha | Marina decidió conservar el setter y reconocer que SB-04 / S-04 queda parcialmente resuelto | Revisión del código y posterior prueba de guardar/cargar con el `ObjectMapper` real de `BaseDeDatos` |
| Implementar y revisar RB-03 | Nueva operación `Solicitud.cambiarEstado`, delegación desde cambio de estado y asignación automática, y generación de 13 verificaciones adicionales de B | Marina solicitó una revisión final y luego el ajuste para evitar la actualización duplicada de fecha en la asignación automática | 82 pruebas correctas; rechazos sin cambios parciales, historial y fechas, persistencia JSON y línea base HTTP 14/14 |
| Centralizar las búsquedas por ID | Implementación de RB-04 y dos pruebas de solicitud inexistente con cuerpo `{}` | Marina restringió la extracción a tres endpoints y exigió mantener el 404 y la precedencia de validaciones | 84 pruebas correctas; los nuevos casos comprobaron el 404 y CP-B03 cubrió una solicitud fuera de la primera posición; línea base 14/14 |
| Nombrar el límite de asignaciones | Implementación de RB-05 con una constante compartida por condición y mensaje | Marina exigió conservar el valor 5, los mensajes, códigos HTTP y las demás reglas | 84 pruebas correctas, incluidos los casos de límite de asignaciones; línea base 14/14 |
| Documentar los cinco refactorings y su relación con SOLID/Clean Code | Borradores y edición de los aportes de B, ejemplos antes/después, referencias SB/S, commits y resultados históricos | Marina revisó y aprobó la propuesta y autorizó registrar exclusivamente los documentos 04 y 05 | Referencias contrastadas con los commits y sus padres; preservación de aportes A/C y tablas generales; `git diff --check` sin errores |

## Decisiones humanas y límites del trabajo

- Aplicar cambios pequeños, revisar cada resultado y autorizar un commit independiente por refactoring, seguido de un commit separado de documentación.
- Mantener el comportamiento externo: transiciones, orden de validaciones, códigos y mensajes HTTP, formato y orden del historial, fechas y persistencia.
- Conservar `setEstado()` para inicialización y compatibilidad de persistencia. RB-03 protege las transiciones operativas, pero el setter público permite eludirlas: SB-04 / S-04 queda parcialmente resuelto.
- Postergar el `enum EstadoSolicitud` de SB-03 / S-10 para una intervención coordinada sobre consumidores del estado, entradas inválidas y JSON. La compatibilidad puede preservarse, pero requiere verificaciones específicas y excede estos cinco cambios. RB-02 conserva los estados `String`.
- No atribuir cumplimiento completo de SRP u OCP ni mejoras de LSP, ISP o DIP que no se implementaron.
- Incorporar pruebas adicionales solo en zona B y conservar las pruebas de A y C. Guardar los resultados de las líneas base fuera del repositorio, sin sobrescribir evidencias existentes.

## Verificaciones y trazabilidad

Las verificaciones siguientes fueron ejecutadas por Codex durante la implementación, bajo las instrucciones de Marina, y sus resultados se presentaron para revisión antes de autorizar los commits. Son resultados de ejecuciones reales, no resultados supuestos de las propuestas.

**Suite automatizada:** `.\mvnw.cmd test`.

| Refactoring | Hallazgo | Commit | Zona A | Zona B | Zona C | Total correcto | Línea base HTTP |
|---|---|---|---|---|---|---|---|
| RB-01 – Move Method / reubicación de responsabilidad | SB-01 / S-03 | `f0e9de8` | 31 | 17 | 21 | 69/69 | No se registró una corrida independiente en este paso |
| RB-02 – Replace Conditional with Lookup Table | Complementa SB-01 / S-03 | `daedb7b` | 31 | 17 | 21 | 69/69 | No se registró una corrida independiente en este paso |
| RB-03 – Encapsulación de estado e historial | SB-04 / S-04, parcial | `c9dc80a` | 31 | 30 | 21 | 82/82 | 14/14 iguales |
| RB-04 – Extract Method | SB-02 / S-09 | `8e02326` | 31 | 32 | 21 | 84/84 | 14/14 iguales |
| RB-05 – Replace Magic Number with Named Constant | SB-05 / S-11 | `4d39d0d` | 31 | 32 | 21 | 84/84 | 14/14 iguales |

Todas las ejecuciones de la tabla terminaron con `BUILD SUCCESS`, cero fallos, cero errores y cero omisiones. Las 13 verificaciones añadidas en RB-03 son diez casos parametrizados de entidad y tres pruebas de integración; RB-04 añadió otras dos pruebas. No se confunden las 32 pruebas finales de B con las 17 pruebas de caracterización anteriores al refactoring documentadas en IA-11.

La línea base se ejecutó con `scripts/linea_base.ps1`, usando `-Salida` hacia un archivo nuevo fuera del repositorio y `-CompararCon docs/evidencias/pruebas/linea_base_estado_inicial.md`. En RB-03, RB-04 y RB-05 los 14 casos coincidieron con la referencia original. Se respaldaron y restauraron los datos locales y los archivos generados; las evidencias existentes se conservaron. Este registro no crea enlaces a archivos temporales ni presenta esas salidas como evidencias versionadas.

## Resultado y documentación relacionada

El resultado es cinco refactorings independientes, documentación revisada y decisiones técnicas justificadas, con comportamiento externo conservado en las verificaciones realizadas. La aceptación de los cambios y la responsabilidad de explicar el trabajo corresponden a Marina; la generación de código, pruebas y borradores por las herramientas queda declarada con grado Alto. Esta revisión no equivale a aprobación cruzada ni a integración del trabajo en `main`.

- [Diagnóstico original IA-05](IA-05_registro_zona_b_marina.md).
- [Pruebas de caracterización IA-11](IA-11_pruebas_zona_b.md).
- [Aporte de zona B a SOLID/Clean Code](../../04_solid_clean_code.md).
- [Fichas RB-01 a RB-05](../../05_refactoring.md), documentadas en el commit `ccd25e2`.
- Los commits indicados identifican los cambios locales; no se inventan enlaces públicos a conversaciones ni se atribuyen a las herramientas autorías o coautorías de los commits.

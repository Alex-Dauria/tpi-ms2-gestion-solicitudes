# Registro de uso de IA – IA-04, IA-08, IA-09, IA-10, IA-13 e IA-14

**Integrante:** Alex Pedro Dauria · **Herramienta:** Claude Code

Registro resumido de los pedidos principales, lo que devolvió la herramienta y cómo se verificó. Es un resumen: las sesiones fueron locales y no se publica la conversación completa. Cada resultado se puede comprobar en el PR o el commit indicado.

## IA-04 – Auditoría externa de la organización y de la Etapa 1

**Herramienta de esta consulta:** Claude Opus 5.5.

| Pedido | Qué devolvió la herramienta | Qué decidió el equipo |
|---|---|---|
| Revisar la organización, los issues, el tablero y la Etapa 1 contra la consigna | Informe de hallazgos | Se tomaron solo los necesarios: un archivo sin versionar (`.gitignore` excluía `BaseDeDatos.java`), el orden de las mediciones de la etapa 6, la visibilidad del tablero y detalles de redacción. Se descartaron las fechas límite y los comandos exactos en los issues |

## IA-08 – Diagnóstico de la zona A

| Pedido | Qué devolvió la herramienta | Cómo se verificó |
|---|---|---|
| Redactar las fichas de los smells de la zona A a partir del análisis de la consulta IA-03 | Fichas SA-01 a SA-05, con ubicación, evidencia, impacto, riesgo, prioridad y refactoring propuesto | Ubicaciones y conteos contrastados con el tag `estado-inicial`. Marina aprobó el PR #25 y Silvia también lo revisó |

## IA-09 – Pruebas de la zona A

| Pedido | Qué devolvió la herramienta | Cómo se verificó |
|---|---|---|
| Escribir las pruebas de caracterización de la zona A a partir de las reglas RN-01 a RN-06 | `SolicitudZonaATest` (CP-A01 a CP-A25), la tabla 6.2.A y los hallazgos de 6.5 | 31 de 31 en verde sobre `estado-inicial`. Marina aprobó el PR #26 y Silvia lo revisó sin observaciones |

## IA-10 – Refactoring de la zona A

| Pedido | Qué devolvió la herramienta | Cómo se verificó |
|---|---|---|
| Aplicar los refactorings de la zona A, uno por commit, en el orden de la consulta IA-03 | RA-01 a RA-08, las fichas de 5.2.A y el aporte de la zona A a la sección 4 | 45 de 45 pruebas después de cada commit, 69 de 69 integrado con las otras zonas y línea base 14 de 14 (PR #28). Marina verificó la compatibilidad con la zona B (17 de 17) y Silvia lo revisó sin observaciones bloqueantes. Marina no llegó a aprobarlo con el checklist completo antes del merge |

## IA-13 – Revisión de PR y auditorías del repositorio

| Pedido | Qué devolvió la herramienta | Qué se hizo |
|---|---|---|
| Revisar los PR que me tocaban o que estaban pendientes (#21, #22, #24, #27, #31 y #33) | Observaciones sobre cada PR, con su verificación | Se publicaron como revisiones en cada PR. Las autoras aplicaron lo que correspondía |
| Comprobar si las pruebas de la zona C detectaban errores (PR #31) | Introdujo errores a propósito en una copia temporal del código: las primeras 12 pruebas no detectaron ninguno de 6 | Silvia reforzó las pruebas hasta 21, que detectan 7 de 7 |
| Destrabar la integración del 6/11 | Resolvió los conflictos de merge, restauró la fila IA-08 que el PR #32 borraba y propuso B y C en paralelo (DT-08) | Se aceptó el cambio de plan y se mergearon #27, #28, #31, #32 y #33 |
| Auditar el repositorio contra la consigna (6/11) | Lista de errores y pendientes | Se corrigieron los errores de documentación y se decidió hacer RA-09 y RA-10 |
| Redactar la sección 1 y el README a partir del sistema base | Textos de esas secciones | Se revisaron en el PR #19, con las observaciones de Silvia y Marina aplicadas |

## IA-14 – Reglas del registro fuera del controlador

| Pedido | Qué devolvió la herramienta | Cómo se verificó |
|---|---|---|
| Sacar del controlador las reglas del registro, uno por commit | RA-09 (`c6ca13c`), RA-10 (`d7996e3`), sus fichas en la sección 5 y la actualización de la sección 4 | 69 de 69 pruebas después de cada commit y línea base 14 de 14 (PR #35) |

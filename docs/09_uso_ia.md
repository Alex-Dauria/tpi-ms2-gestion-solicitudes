# Uso de inteligencia artificial

**Coordina:** Alex · **Aportan:** las tres

> La consigna permite usar IA como apoyo para comprender conceptos, revisar alternativas o mejorar la redacción. Hay que declarar la herramienta, la finalidad y el grado de intervención, y adjuntar evidencia del intercambio. La IA no reemplaza la autoría: el equipo tiene que poder justificar todo lo entregado.

## Escala de intervención

| Grado | Significado |
|---|---|
| Bajo | Consulta de conceptos o revisión de redacción; el contenido es propio |
| Medio | La IA propuso alternativas o borradores que el equipo evaluó, eligió y modificó |
| Alto | La IA generó el contenido o el código, y el equipo lo revisó, lo probó y lo adoptó |

## Uso en la organización y en la versión inicial

| # | Integrante | Herramienta | Finalidad | Grado | Qué hizo el equipo |
|---|---|---|---|---|---|
| IA-01 | Alex | Claude Code | Analizar la consigna y proponer la organización del trabajo: división del código en zonas, reparto de tareas, orden del refactoring, estructura del repositorio y plantillas de las secciones, issues y milestones del tablero | Alto | Revisó y ajustó el plan, que se aprueba con la revisión de las tres integrantes en el PR de la Etapa 1 |
| IA-02 | Alex | Claude Code | Generar el prototipo de la versión inicial (en estilo "prototipo rápido", sin cuidar el diseño, para contar con problemas reales que diagnosticar) y el script de verificación `scripts/linea_base.ps1` | Alto | Ejecutó y probó el sistema, sacó las capturas de evidencia, verificó los requisitos mínimos de la consigna y revisó que las reglas de negocio documentadas coincidieran con el comportamiento |

**Evidencia:** registro de los pedidos principales y de las decisiones tomadas en `docs/evidencias/ia/IA-01_IA-02_registro.md`.

## Consultas por etapa

Consultas puntuales hechas durante el trabajo, con el enlace a la conversación o el registro de cada una. Cada integrante agrega las suyas.

| # | Etapa | Integrante | Herramienta | Consulta | Grado | Enlace |
|---|---|---|---|---|---|---|
| IA-03 | 3 – Code smells y 5 – Refactoring | Alex | Claude (chat) | Diagnóstico de code smells del método `registrarSolicitud` (zona A): identificación según Fowler y Clean Code, priorización para la zona y reparto de los smells de frontera con las otras zonas. Después, criterio para aplicar Extract Method de forma segura: orden de extracción, cuidados con el orden de las validaciones y comparación con Replace Error Code with Exception. El integrante corrigió un hallazgo que no correspondía al código original (indentación alterada al pegar) | Bajo | [Conversación](https://claude.ai/share/b1753be3-72ba-442b-b159-9dc406abb747) |
| IA-04 | Organización (todas las etapas) | Alex | Claude Opus 5.5 | Auditoría externa de la organización y de la Etapa 1 contra la consigna. El equipo tomó solo los hallazgos que consideró necesarios: se corrigió un archivo que había quedado sin versionar, el orden de las mediciones de la etapa 6, la visibilidad del tablero y detalles de redacción | Bajo | [Registro](evidencias/ia/IA-04_IA-08_IA-09_IA-10_IA-13_IA-14_registro.md) |
| IA-05 | 3 – Deuda técnica y code smells (zona B) | Marina Giselle Cordero | ChatGPT (OpenAI) | Análisis guiado de cambiarEstado, asignarResponsable, obtener y Solicitud: cinco debilidades, riesgos, prioridades y propuestas de refactoring; revisión de compatibilidad con Jackson. | Medio | [Registro](evidencias/ia/IA-05_registro_zona_b_marina.md) · [Evidencia seleccionada](evidencias/ia/evidencia_ia_zona_b_marina.md) |
| IA-06 | 3 – Code smells (zona C) | Silvia Giardini | OpenCode (DeepSeek V4.1 Flash) | Diagnóstico de code smells de la zona C: orientación conceptual y revisión de los cinco hallazgos; regla de conteo de parámetros | Medio | [Registro](evidencias/ia/IA-06_registro.md) |
| IA-07 | 2 – Calidad externa/interna | Silvia Giardini | OpenCode y Gemini | Resumen de lo solicitado: estructura y organización posible para cumplir con todos los puntos solicitados, evidencia en el código, solicitud de estructura útil, revisión de referencias, medición de eficiencia y reajuste final. | Medio | OpenCode: Sin enlace público (sesión local), Gemini: [Conversación](https://share.gemini.google/epkkZgawv19N) · [Registro](evidencias/ia/IA-07_registro.md) |
| IA-08 | 3 – Code smells (zona A) | Alex | Claude Code | Redacción de las fichas SA-01 a SA-05 a partir del análisis de la consulta IA-03: ubicación, evidencia, impacto, riesgo, prioridad según 3.1 y refactoring propuesto. Las ubicaciones y los conteos se verificaron contra el código del tag `estado-inicial` | Alto | [Registro](evidencias/ia/IA-04_IA-08_IA-09_IA-10_IA-13_IA-14_registro.md) |
| IA-09 | 6 – Pruebas (zona A) | Alex | Claude Code | Diseño y escritura de las pruebas de caracterización de la zona A (`SolicitudZonaATest`, CP-A01 a CP-A25) a partir de las reglas RN-01 a RN-06 y de los cuidados sobre el orden de las validaciones de la consulta IA-03; tabla 6.2.A y hallazgos de 6.5. Las pruebas se ejecutaron sobre el código de `estado-inicial` (31 de 31 en verde) | Alto | [Registro](evidencias/ia/IA-04_IA-08_IA-09_IA-10_IA-13_IA-14_registro.md) |
| IA-10 | 5 – Refactoring y 4 – SOLID (zona A) | Alex | Claude Code | Aplicación de los refactorings RA-01 a RA-08 sobre `registrarSolicitud` siguiendo el orden de extracción de la consulta IA-03 (un commit por refactoring, con las 45 pruebas en verde después de cada uno), verificación con la línea base (14 de 14 iguales) y redacción de las fichas de 5.2.A y del aporte de la zona A en la sección 4 | Alto | [Registro](evidencias/ia/IA-04_IA-08_IA-09_IA-10_IA-13_IA-14_registro.md) |
| IA-11 | 6 – Pruebas de caracterización (zona B) | Marina Giselle Cordero | ChatGPT (OpenAI) | Generación de pruebas automatizadas con JUnit 5 y MockMvc, aislamiento de datos y apoyo en la documentación. Se implementaron y verificaron 17 casos antes del refactoring. | Alto | [Registro](evidencias/ia/IA-11_pruebas_zona_b.md) |
| IA-12 | 6 – Pruebas (zona C) | Silvia Giardini | Gemini (Google) | Diseño y escritura de las pruebas de caracterización de la zona C (`SolicitudZonaCTest`, CP-C01 a CP-C21, casos de la tabla 6.2.C): configuración de MockMvc, restauración del archivo de datos, filtros, vencimiento, persistencia, orden y reporte. Orientación y revisión de los casos propuestos. La implementación siguió el patrón de las pruebas de las zonas A y B. | Medio | [Conversación](https://share.gemini.google/vsEiNBYxAM61) · [Registro](evidencias/ia/IA-12_registro.md) |
| IA-13 | 7 – Code review y organización | Alex | Claude Code | Revisión de los PR #21, #22, #24, #27, #31 y #33, comprobación de las pruebas de la zona C introduciendo errores a propósito, resolución de conflictos de integración, propuesta de B y C en paralelo (DT-08), auditoría del repositorio contra la consigna, y redacción de la sección 1 y del README | Alto | [Registro](evidencias/ia/IA-04_IA-08_IA-09_IA-10_IA-13_IA-14_registro.md) |
| IA-14 | 5 – Refactoring y 4 – SOLID (zona A) | Alex | Claude Code | Aplicación de RA-09 y RA-10 (reglas del registro fuera del controlador), con 69 de 69 pruebas después de cada commit y línea base 14 de 14; fichas y actualización de la sección 4 | Alto | [Registro](evidencias/ia/IA-04_IA-08_IA-09_IA-10_IA-13_IA-14_registro.md) |
| IA-15 | 5 – Refactoring y 4 – SOLID (zona C) | Silvia Giardini | OpenCode | Orientación y revisión del refactoring de la zona C: Introduce Parameter Object y Encapsular colección: cuidados, falsos positivos, verificación de comportamiento externo y afirmaciones que requieren evidencia. La implementación siguió el patrón del proyecto. | Alto | Sin enlace (sesión local) · [Registro](evidencias/ia/IA-15_registro.md) |
| IA-16 | 5 – Refactoring y 4 – SOLID/Clean Code (zona B) | Marina Giselle Cordero | ChatGPT (OpenAI) y Codex | Análisis de alternativas, implementación y revisión de RB-01 a RB-05, generación de pruebas adicionales y apoyo en la documentación. Marina definió el alcance y revisó los cambios antes de autorizar cada commit; se mantuvo el comportamiento externo, se postergó el enum y se reconoció la resolución parcial de SB-04. Se verificaron 69, 69, 82, 84 y 84 pruebas, y línea base HTTP 14/14 para RB-03 a RB-05. | Alto | [Registro](evidencias/ia/IA-16_refactoring_zona_b_marina.md) |

**Aclaración sobre IA-05:** por privacidad, se adjunta evidencia seleccionada con extractos y síntesis identificados como tales, sin enlace público a la conversación. No se presenta como una transcripción literal completa. La consigna pide adjuntar evidencia del intercambio relevante, sin exigir la conversación completa, y el equipo acepta esta modalidad.

**Aclaración sobre IA-11:** se presenta un registro de los pedidos principales, aportes de la IA y verificaciones realizadas, sin publicar la conversación completa, conforme a la modalidad de evidencia documentada aceptada por el equipo.

_Para cada uso se adjunta un enlace público a la conversación o, si no lo hay, un registro de los pedidos y las decisiones en `docs/evidencias/ia/`. Los enlaces tienen que estar accesibles al momento de la corrección._

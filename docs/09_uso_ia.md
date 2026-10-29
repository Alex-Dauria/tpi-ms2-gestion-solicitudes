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

Consultas puntuales hechas durante el trabajo, con el enlace a cada conversación. Cada integrante agrega las suyas.

| # | Etapa | Integrante | Herramienta | Consulta | Grado | Enlace |
|---|---|---|---|---|---|---|
| IA-03 | 3 – Code smells y 5 – Refactoring | Alex | Claude (chat) | Diagnóstico de code smells del método `registrarSolicitud` (zona A): identificación según Fowler y Clean Code, priorización para la zona y reparto de los smells de frontera con las otras zonas. Después, criterio para aplicar Extract Method de forma segura: orden de extracción, cuidados con el orden de las validaciones y comparación con Replace Error Code with Exception. El integrante corrigió un hallazgo que no correspondía al código original (indentación alterada al pegar) | Bajo | [Conversación](https://claude.ai/share/b1753be3-72ba-442b-b159-9dc406abb747) |
| IA-04 | Organización (todas las etapas) | Alex | Claude Code | Auditoría externa de la organización y de la Etapa 1 contra la consigna. El equipo tomó solo los hallazgos que consideró necesarios: se corrigió un archivo que había quedado sin versionar, el orden de las mediciones de la etapa 6, la visibilidad del tablero y detalles de redacción | Bajo | Sin enlace (sesión local) |
| IA-05 | | | | | | |
| IA-09 | 6 – Pruebas (zona A) | Alex | Claude Code | Diseño y escritura de las pruebas de caracterización de la zona A (`SolicitudZonaATest`, CP-A01 a CP-A25) a partir de las reglas RN-01 a RN-06 y de los cuidados sobre el orden de las validaciones de la consulta IA-03; tabla 6.2.A y hallazgos de 6.5. Las pruebas se ejecutaron sobre el código de `estado-inicial` (31 de 31 en verde) | Alto | Sin enlace (sesión local) |

_Los enlaces deben ser públicos (enlace compartido de la conversación) y estar accesibles al momento de la corrección. Además, conviene exportar cada conversación a `docs/evidencias/ia/` por si el enlace deja de funcionar._

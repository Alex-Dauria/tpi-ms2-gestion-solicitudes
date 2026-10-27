# Registro de uso de IA – IA-01 e IA-02

**Integrante:** Alex Pedro Dauria · **Herramienta:** Claude Code · **Fecha:** 2026-10-20

Registro resumido de los pedidos principales, lo que devolvió la herramienta y lo que decidió el equipo.

## IA-01 – Organización del trabajo

| Pedido | Qué devolvió la herramienta | Qué decidió el equipo |
|---|---|---|
| Analizar la consigna, el modelo de presentación de la cátedra y nuestra forma de trabajo en otros TPs (issues, revisión cruzada, Project) | Resumen de las etapas, los entregables, los tags obligatorios y la rúbrica | Mantener la metodología que ya usamos: un issue por tarea, responsable y revisión cruzada, un PR por entrega |
| Proponer una tecnología a nuestro alcance | Spring Boot (API REST), justificado por lo visto en Programación y en el Trabajo Final | Se adoptó (DT-02) |
| Proponer cómo repartir el trabajo de forma pareja | División del código en tres zonas, con una responsable de punta a punta y revisión rotativa | Se adoptó (DT-04); zona A Alex, B Marina, C Silvia |
| Proponer el orden de trabajo | Pruebas de caracterización primero; refactoring en secuencia A → B → C | Se adoptó (DT-05) |
| Estructurar el repositorio y los documentos | Estructura de `docs/`, plantillas de las secciones, issues y milestones | Se ajustaron los textos de los issues y del plan |

## IA-02 – Prototipo de la versión inicial

| Pedido | Qué devolvió la herramienta | Qué hizo el equipo |
|---|---|---|
| Generar un prototipo rápido del caso sugerido, con las funcionalidades mínimas y sin cuidar el diseño, para tener problemas reales que diagnosticar | Prototipo en Spring Boot con 3 entidades, 6 operaciones y persistencia en JSON | Lo ejecutó y probó desde Swagger UI, y sacó las 7 capturas de la sección 1 |
| Documentar las reglas de negocio que implementa el prototipo | Reglas RN-01 a RN-15 de la sección 1 | Verificó que las reglas coincidieran con el comportamiento observado |
| Proponer una forma de verificar el comportamiento externo antes y después del refactoring | Script `scripts/linea_base.ps1` (12 pedidos HTTP con comparación caso por caso) | Decidió usarlo como evidencia de la Etapa 6 y descartar el registro manual de pedidos que había antes |

## Ajustes hechos por el equipo

- Corrección del comando del Maven Wrapper para PowerShell.
- Selección y orden de las capturas de evidencia: se ordenaron según cómo se tomaron, para que el reporte no contradiga los cambios de estado posteriores.
- La línea base del comportamiento se genera recién con el tag `estado-inicial` creado, y se dejó como condición para empezar a refactorizar.

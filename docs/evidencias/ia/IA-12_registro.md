# Registro de uso de IA – IA-12

**Integrante:** Silvia Giardini · **Herramienta:** Gemini (Google) · **Fecha:** 2026-11-05
**Rama:** `zona-c/pruebas` · **Issue:** #10 – Zona C: pruebas de caracterización y refactoring
**Enlace:** https://gemini.google.com/app/7afc99f59ca3e1af

> **Grado de intervención: Medio.** La herramienta aportó el marco conceptual y una revisión de los casos propuestos. La integrante evaluó cada observación, la verificó contra el código del tag `estado-inicial` y decidió qué se mantiene, qué se ajusta y qué se descarta.

## 1. Qué se pidió

| # | Pedido | Qué devolvió | Qué hizo la integrante |
|---|---|---|---|
| 1 | Guía de orientación para escribir pruebas de caracterización de la zona C | Definición y propósito de las pruebas de caracterización; configuración de MockMvc; restauración del archivo de datos; casos a cubrir; límites con fechas; persistencia; falsos positivos | Usó la guía para diseñar las 12 pruebas (CP-C01 a CP-C12) y las de refuerzo (persistencia, límites, filtros, orden) |
| 2 | Revisión de los casos propuestos | Tabla con veredicto por caso y lista de posibles faltantes (CP-C13 a CP-C15 y persistencia con lista vacía / caracteres especiales) | Verificó los casos contra el código y descartó los que no aplicaban a la versión inicial |

## 2. Resultado de la revisión

Todos los casos propuestos (CP-C01 a CP-C12, persistencia, límites de vencimiento, filtros y orden) fueron considerados válidos. Se identificaron tres posibles faltantes (CP-C13 a CP-C15), que se evaluaron contra el código y no se incorporaron por no aplicar a la versión inicial.

## 3. Qué no aportó la herramienta

- No leyó el código: no podía decir qué señal aplica en esta zona, y lo dijo.
- Las observaciones sobre los casos son orientativas; cada una se verificó contra el tag `estado-inicial`.

## 4. Evidencia

Este registro es la evidencia resumida. La conversación completa está disponible en el enlace indicado arriba.
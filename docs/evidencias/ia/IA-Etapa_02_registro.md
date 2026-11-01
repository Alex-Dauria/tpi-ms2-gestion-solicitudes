# Registro de uso de IA – IA-05

**Integrante:** Silvia Giardini · **Herramienta:** OpenCode y Gemini (asistentes de IA) · **Fecha:** 2026-10-23

Registro resumido de los pedidos principales, lo que devolvió la herramienta y lo que decidió el equipo.

## Calidad externa e interna

| Pedido | Qué devolvió la herramienta | Qué decidió el equipo |
|---|---|---|
| Analizar cómo separar calidad externa e interna y la plantilla de la sección | Estructura separada en items con la posible organización | Se adoptaron algunos items de la estructura sugerida |
| Ubicar evidencia concreta en el código | 22 bloques de respuesta de error repetidos, cálculo de vencimiento duplicado en `filtrar` y `resumen`, 20 referencias a `BaseDeDatos`, el `ClassCastException` de `crear` con legajo numérico y el `NullPointerException` latente de `resumen` | Se incluyeron como ejemplos en las tablas de calidad interna y de cambios probables |
| Redactar un borrador de la sección | La organización de la sección, con las tablas y estructura para completar  | Se tomó como base y se ajustó la redacción |
| Revisar las referencias del documento | Señaló que los números de línea y de apartados se rompen al refactorizar | Se reemplazaron los números de línea por nombres de métodos; las referencias a secciones se mantuvieron |
| Verificar la medición de eficiencia | Tres alternativas (pestaña Network de DevTools, `curl -w` y consola local) | Se adoptó DevTools: tres recargas de `GET /solicitudes` dieron 20, 7 y 8 ms con una respuesta de 2,5 kB y se citaron las capturas `09` y `10` en la tabla de calidad externa |

## Evidencia

- `docs/evidencias/etapa2/01` a `07`: capturas de Swagger UI y de las respuestas de la API.
- `docs/evidencias/etapa2/08_pruebas_caja_negra.md`: los 21 pedidos, el rastro del error 500 y la prueba de persistencia.
- `docs/evidencias/etapa2/09_tiempos_respuesta.png` y `10_url_pedido.png`: medición de tiempos en la pestaña Network de DevTools y headers del pedido.
- Conversación completa: Gemini: [Conversación](https://share.gemini.google/epkkZgawv19N), OpenCode: Sin enlace público (sesión local) 
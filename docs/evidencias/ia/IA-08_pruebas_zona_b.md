
# IA-08 – Pruebas de caracterización de la Zona B

**Integrante:** Marina Giselle Cordero  
**Herramienta:** ChatGPT (OpenAI)  
**Fecha:** 29/10/2026  
**Grado de intervención:** Alto  
**Issue:** #9  
**Pull Request:** #24

## Finalidad

Utilizar IA como apoyo para comprender, implementar y documentar las pruebas de caracterización del ciclo de vida de las solicitudes antes del refactoring.

## Registro de intercambios

| Pedido a la IA | Aporte recibido | Verificación realizada |
|---|---|---|
| Explicar JUnit 5 y MockMvc. | Explicaciones y configuración de pruebas en Spring Boot. | Se ejecutó una prueba inicial correctamente. |
| Preparar datos independientes para las pruebas. | Código para restablecer los datos en memoria y proteger el archivo JSON. | Se comparó el JSON con su respaldo después de las pruebas. |
| Generar y ampliar las pruebas de caracterización. | Código inicial de 14 pruebas para consultas, estados, responsables y validaciones. Posteriormente se agregaron 3 casos y se mejoraron las verificaciones a partir del code review. | Se ejecutaron las 17 pruebas correctamente con JUnit 5 y MockMvc. |
| Explicar los comandos de Maven. | Explicación de Maven Wrapper y ejecución de pruebas. | Se ejecutaron los comandos en VS Code. |
| Documentar los resultados. | Propuesta para completar `docs/06_pruebas.md`. | Se revisó la documentación y se incorporaron los resultados. |

## Validación

**Comando ejecutado:**

`.\mvnw.cmd -Dtest=SolicitudZonaBTest test`

**Resultados:**

- 17 pruebas ejecutadas.
- 17 correctas.
- 0 fallos y 0 errores.
- `BUILD SUCCESS`.

Se verificó que el archivo `datos/solicitudes.json` conservara su contenido original y que no se modificara `src/main/`.

## Intervención humana

La IA generó propuestas de código, explicaciones y documentación.

La integrante incorporó el código al proyecto, ejecutó las pruebas, verificó los resultados y comprobó que los datos originales no fueran alterados.

## Evidencias relacionadas

- `docs/06_pruebas.md`
- `docs/evidencias/pruebas/zona-b/`
- [Pull Request #24](https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/pull/24)
- Evidencia del intercambio: registro de los pedidos principales, aportes de la IA y verificaciones realizadas, documentado en este archivo.

## Conclusión

La IA permitió preparar las pruebas de caracterización. Los resultados fueron comprobados mediante su ejecución real antes de comenzar el refactoring.

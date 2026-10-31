# Registro de uso de IA – IA-06

**Integrante:** Silvia Giardini · **Herramienta:** OpenCode, agente con modelo DeepSeek V4.1 Flash · **Fecha:** 2026-10-27
**Rama:** `zona-c/diagnostico` · **Issue:** #5 – Diagnóstico de la zona C

> **Grado de intervención: Alto.** El borrador de las fichas se generó con el mismo agente en una sesión anterior (sin registro propio) y la integrante revisó, verificó contra el código y adoptó o ajustó cada hallazgo. La consulta de esta sesión (orientación + revisión) aportó el marco conceptual y observaciones adicionales, que la integrante evaluó y decidió.

**Alcance de esta declaración.** El borrador de las fichas se generó con el mismo agente en una sesión anterior, que no tiene registro propio. Esta consulta se hizo sobre ese borrador ya escrito, por lo que el grado Alto cubre el conjunto (generación + revisión). Además, el agente conserva notas del proyecto en su memoria y lo advirtió al cerrar la revisión ("tengo apuntes previos de esta zona"): se declara acá para que la evidencia no se lea como una consulta a ciegas.

## 1. Qué se pidió

| # | Pedido | Qué devolvió | Qué hizo la integrante |
|---|---|---|---|
| 1 | Guía de orientación para diagnosticar la zona, sin diagnóstico del código ni solución | Mapa de 14 smells de Fowler y 11 criterios de Clean Code con su señal; preguntas guía; tabla de 11 técnicas con cuándo aplican y cuándo no; 12 falsos positivos; tres preguntas de priorización; tabla de evidencia mínima por afirmación | Usó las señales y los falsos positivos para revisar las cinco fichas ya escritas |
| 2 | Revisión de los cinco hallazgos y regla de conteo de parámetros | Veredicto por hallazgo, siete candidatos faltantes planteados como preguntas y regla de conteo (4 parámetros es el disparador, 5 o más es larga) | Verificó los siete candidatos contra el código y ajustó las fichas: ver secciones 3 y 4 |

## 2. Los prompts

Los prompts utilizados se omiten en este registro por privacidad. El detalle de las consultas y sus resultados se resume en la sección 1 y en las secciones 3 a 5. El export completo de la sesión se conserva como respaldo local.

## 3. Qué devolvió la revisión, hallazgo por hallazgo

| Ficha | Observación de la herramienta | Decisión |
|---|---|---|
| SC-01 | Correcto pero impreciso: no dice de qué firma habla y el costo se paga al reenviar los seis parámetros | Se sostiene. Se explicita que la firma es la de `listar`/`filtrar` y que el problema aparece al reenviar y combinar los filtros |
| SC-02 | Correcto, condicionado a probar que las dos copias cambian juntas | Se sostiene. La ficha ya documenta que un cambio de RN-14 obliga a editar los dos métodos |
| SC-03 | Parcial: son dos problemas metidos en un solo hallazgo | Se mantiene con un ID y dos síntomas, con nota cruzada a SC-05 para la consolidación (#6); el refactoring ya preveía dos commits |
| SC-04 | El más débil: mezcla el `get(...) + 1`, que es un defecto, con las claves fijas, que son un esquema legítimo | Se separa el `NullPointerException` latente como riesgo de la ficha, y el hallazgo se apoya en el uso del mapa como estructura de datos |
| SC-05 | Condicionado al `return`: si ya devuelve copia o vista inmutable, no hay exposición | Se verificó: devuelve la lista interna. Se sostiene |

## 4. Los siete candidatos faltantes

El agente los planteó como preguntas, sin afirmar que existieran. Cada uno se verificó en el código del tag `estado-inicial`.

| Candidato | Verificación | Resultado |
|---|---|---|
| Orden del listado | `filtrar` ordena explícitamente por prioridad e id (`lst.sort`) | No aplica |
| Borde del JSON dentro de las consultas | La lectura y la escritura están en `BaseDeDatos`, no en los métodos de consulta | No aplica |
| Atomicidad memoria/archivo | `guardar` escribe un único archivo y no hay coordinador entre memoria y disco | Se solapa con SC-03 |
| Arranque con archivo ausente o corrupto | `cargar` cae a los datos de prueba si el archivo está roto | Candidato nuevo: se anota para #6 |
| Concurrencia del archivo | No hay lock y el controlador es singleton | Se solapa con SC-03 |
| Cálculo y formato en el resumen | `resumen` calcula y arma la respuesta en el mismo método | Se solapa con SC-04 |
| Errores silenciosos | `cargar` y `guardar` atrapan la excepción y siguen con `printStackTrace` | Candidato nuevo: se anota para #6 |

## 5. Regla de conteo que se adoptó para SC-01

- Se cuentan solo los parámetros que el llamador elige; no cuentan el receptor ni las dependencias que el framework completa.
- Cuatro parámetros es el disparador y cinco o más es larga por defecto. Un segundo criterio, independiente del número: si no se puede repetir el orden de los argumentos sin mirar la firma, ya es larga.
- En un endpoint HTTP la firma larga es aceptable si cada parámetro es parte del contrato. Deja de serlo cuando se reenvían en el mismo orden a otro método o cuando se combinan entre sí.

## 6. Qué no aportó la herramienta

- No leyó el código: no podía decir qué señal aplica en esta zona, y lo dijo.
- La regla de conteo es orientativa, no una norma; se usa como criterio de justificación.
- Los siete candidatos son preguntas, no hallazgos: ninguno se incorporó sin verificarlo.

## 7. Evidencia

Este registro es la evidencia curada. El export completo de la sesión se conserva como respaldo: incluye el razonamiento interno del agente y sus llamadas a herramientas, que no aportan a la consulta pero permiten verificar que lo declarado coincide con lo que pasó.

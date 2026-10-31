# Registro de uso de IA – IA-06

**Integrante:** Silvia Giardini · **Herramienta:** OpenCode, agente con modelo DeepSeek V4.1 Flash · **Fecha:** 2026-10-27
**Rama:** `zona-c/diagnostico` · **Issue:** #5 – Diagnóstico de la zona C

> **Grado de intervención: Medio.** El borrador previo no fue generado por la integrante: ya estaba en el repositorio, por lo que no existe registro de esa sesión. La intervención declarada cubre la consulta de esta sesión (orientación + revisión), que aportó el marco conceptual y observaciones adicionales; cada hallazgo se evaluó, verificó contra el código y se ajustó.

## 1. Qué se pidió

| # | Pedido | Qué devolvió | Qué hizo la integrante |
|---|---|---|---|
| 1 | Guía de orientación para diagnosticar la zona, sin diagnóstico del código ni solución | Mapa de 14 smells de Fowler y 11 criterios de Clean Code con su señal; preguntas guía; tabla de 11 técnicas con cuándo aplican y cuándo no; 12 falsos positivos; tres preguntas de priorización; tabla de evidencia mínima por afirmación | Usó las señales y los falsos positivos para revisar las cinco fichas ya escritas |
| 2 | Revisión de los cinco hallazgos y regla de conteo de parámetros | Veredicto por hallazgo, siete candidatos faltantes planteados como preguntas y regla de conteo (4 parámetros es el disparador, 5 o más es larga) | Verificó los siete candidatos contra el código y ajustó las fichas: ver secciones 3 y 4 |

## 2. Los prompts

### Prompt 1 — orientación

```text
ROL: Actuás como tutor de ingeniería de software. Tu función es orientarme, no
resolverme el trabajo: me das el marco conceptual y las preguntas que debería
hacerme, y el diagnóstico lo hago yo.

CONTEXTO: TP final de Metodología de Sistemas II. Tenemos una API REST en Spring
Boot hecha en estilo "prototipo rápido": funciona, pero no está diseñada. La
versión quedó congelada con el tag estado-inicial y no se puede cambiar el
comportamiento externo. Mi zona es "Consultas y persistencia": los endpoints de
listado y filtrado, el reporte resumen, los catálogos y la clase que guarda los
datos en memoria y en un archivo JSON.

TAREA: dame una guía de orientación para diagnosticar esa zona por mi cuenta.
Necesito:
1. Un mapa de conceptos: los nombres del catálogo de Fowler y de Clean Code que
   se aplican típicamente a este tipo de código, con una definición de una línea
   y la señal concreta que delata cada uno.
2. Preguntas guía: qué debería preguntarme al leer cada método, del tipo "¿este
   valor viaja siempre con estos otros?", sin responderme.
3. Una tabla comparativa de técnicas de refactoring candidatas para esos
   problemas: técnica | cuándo aplica | cuándo NO conviene | qué riesgo trae.
4. Los falsos positivos más comunes en este tipo de código, para no forzar
   hallazgos que no corresponden.
5. Tres preguntas para asignar prioridad Alta / Media / Baja.
6. Qué afirmaciones NO puedo hacer sin evidencia y qué debería mostrar para
   respaldarlas.

FORMATO: seis secciones con esos títulos, en viñetas o tablas. Nada de párrafos
largos.

RESTRICCIONES: no me digas qué problemas tiene mi código, no nombres smells
concretos de mi proyecto, no escribas fichas y no escribas código. Si necesitás
el código para responder algo, decime que no podés en vez de suponerlo.
```

El agente aclaró en la primera línea de su respuesta que no había abierto el repositorio, y cerró con dos cosas que no podía responder sin verlo. Esa restricción se respetó.

### Prompt 2 — revisión de los hallazgos

_Segundo mensaje, enviado en la misma conversación._

```text
TAREA: te paso los cinco hallazgos que detecté. Revisalos, sin buscar por mí.
1. ¿Alguno está mal fundamentado o no es realmente un smell?
2. ¿Me falta algún caso en mi zona? Si agregás uno, mostrame solo el método y la
   firma o el fragmento, sin explicación extendida.
3. Prestá atención especial a las listas de parámetros largas: ¿me quedó alguna
   sin detectar? Y dame la regla explícita con la que lo decidís: cuándo una firma
   cuenta como larga, cuántos parámetros es el umbral, y cuándo una firma larga es
   aceptable en un endpoint HTTP.

FORMATO: (a) tabla con mi hallazgo | ¿está bien? | observación en una línea;
(b) lista de posibles faltantes, si hay; (c) la regla de conteo en tres viñetas.

RESTRICCIONES: no reescribas mis fichas, no escribas refactorings ni código. Si
para responder necesitás algo que no te di, preguntámelo en vez de suponerlo.

MIS HALLAZGOS:
SC-01 Lista de parámetros larga en listar/filtrar (seis parámetros) — Alta
SC-02 Regla de vencimiento duplicada en filtrar y resumen — Alta
SC-03 Estado global y persistencia en la misma clase — Alta
SC-04 resumen con claves fijas y get(...) + 1 — Alta
SC-05 Catálogos que devuelven las listas internas — Media
```

## 3. Qué devolvió la revisión, hallazgo por hallazgo

| Ficha | Observación de la herramienta | Decisión |
|---|---|---|
| SC-01 | Correcto pero impreciso: no dice de qué firma habla y el costo se paga al reenviar los seis parámetros | Se sostiene. Se explicita que la firma es la de `listar`/`filtrar` y que el problema aparece al reenviar y combinar los filtros |
| SC-02 | Correcto, condicionado a probar que las dos copias cambian juntas | Se sostiene. La ficha ya documenta que un cambio de RN-14 obliga a editar los dos métodos |
| SC-03 | Parcial: son dos problemas metidos en un solo hallazgo | Se mantiene con un ID y dos síntomas; el refactoring ya preveía dos commits |
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
- Nada se incorporó sin verificar.
Este registro es una evidencia resumida.

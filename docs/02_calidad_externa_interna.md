# 2. Calidad externa vs. calidad interna

**Responsable:** Silvia · **Revisión cruzada:** Alex

Para separar las dos miradas usamos dos métodos distintos sobre la misma versión (tag `estado-inicial`):

- **Desde afuera:** probamos el sistema como usuario, con Swagger UI, sin mirar el código. La aplicación se levantó con `./mvnw spring-boot:run` y la sesión partió de los datos sintéticos, sin la carpeta `datos/`. Ejecutamos 21 pedidos: los caminos principales de F1, F4, F5 y F6, casos de F2 con filtros de borde, el catálogo de responsables (F7) y una selección de casos de error de las reglas RN-01 a RN-15. F3 se cubrió en su camino de error dentro de la sesión y en su camino exitoso en la prueba de persistencia. La sesión completa está en [`evidencias/etapa2/08_pruebas_caja_negra.md`](evidencias/etapa2/08_pruebas_caja_negra.md) y las capturas en [`evidencias/etapa2/`](evidencias/etapa2/).
- **Desde adentro:** leímos el código de la versión inicial y lo evaluamos con las subcaracterísticas de **mantenibilidad** de ISO/IEC 25010.

La selección cubre los caminos principales y los errores más visibles, no todos los bordes: quedaron sin caso el legajo vacío o de cuatro dígitos (RN-01), el tipo inválido (RN-02), el máximo de 500 caracteres (RN-03), el cálculo de prioridad de `REVISION_NOTA` y `CAMBIO_COMISION` (RN-04), el alta fallida de estudiante (RN-05), la transición a `EN_REVISION` sin responsable (RN-08), la asignación a una solicitud `RECHAZADA` o `CERRADA` (RN-10) y el límite de cinco solicitudes abiertas por responsable (RN-12). Tampoco se ejercitaron todos los filtros: la sesión usó `estado` y `vencidas`, y solo probó `responsableId` con un valor inválido. Es una decisión de alcance de esta etapa: la sesión prioriza los flujos principales y los errores de contrato.

El catálogo detallado de problemas va en la sección 3 (deuda técnica y code smells). Acá buscamos la mirada general: qué se ve bien desde afuera y qué cuesta mantener desde adentro.

## 2.1 Calidad externa: lo que percibe el usuario

De los 21 pedidos, 10 devolvieron una respuesta `2xx` y 11 fueron rechazados: 10 con un código previsto por las reglas (`400`, `404` o `409`) y 1 con un `500`. Que una respuesta sea `2xx` no siempre significa que la operación haya hecho lo esperado: el filtro por estado inexistente devolvió `200` con una lista vacía y `vencidas=false` devolvió `200` ignorando el filtro (se detallan en la fila de usabilidad).

| Característica (ISO 25010) | Qué se observó | Evidencia |
|---|---|---|
| **Adecuación funcional** | Las funcionalidades F1–F6 responden y de F7 se probó el catálogo de responsables. El registro devuelve `201` con id, prioridad calculada por tipo y estado `PENDIENTE`; la lista se ordena por prioridad; el cambio de estado respeta el flujo; asignar responsable a una solicitud pendiente la pasa a `EN_REVISION`. Los datos sobreviven al reinicio: después de reiniciar la aplicación, una solicitud creada antes seguía disponible. | [02](evidencias/etapa2/02_registrar_solicitud_201.png), [06](evidencias/etapa2/06_reporte_resumen.png); los casos de registro, asignación, cambio de estado, reporte y catálogo de responsables, más la prueba de persistencia, en [08](evidencias/etapa2/08_pruebas_caja_negra.md) |
| **Fiabilidad / manejo de errores** | Las reglas de negocio devuelven el código correcto y un mensaje claro en español: descripción corta `400`, prioridad inválida `400`, duplicada `409`, responsable inactivo `409`, solicitud inexistente `404`, rechazo sin comentario `400`. **Pero hay casos que rompen el contrato:** si el legajo llega como número, el sistema responde `500` en lugar de `400`, y el cuerpo del error es el formato por defecto de Spring, no el `{"error": "..."}` del resto de la API. Con un JSON malformado o un filtro de tipo inválido el código es `400`, pero el cuerpo usa el mismo formato por defecto. Un estado inexistente al cambiar de estado también rompe la semántica: devuelve `409` con el texto "Transición no permitida", como si fuera una transición prohibida y no un dato inválido. | [03](evidencias/etapa2/03_error_400_prioridad.png), [04](evidencias/etapa2/04_error_409_duplicada.png), [05](evidencias/etapa2/05_error_500_legajo_numerico.png); los casos de prioridad inválida, filtro inválido, legajo numérico, JSON malformado y estado inexistente en [08](evidencias/etapa2/08_pruebas_caja_negra.md) |
| **Usabilidad de la API** | Swagger UI está disponible y los nombres de las operaciones son claros. Sin embargo, la documentación generada muestra **solo la respuesta `200`**: el `201` del registro y todos los errores aparecen como *Undocumented* (se ve en la captura del registro). Además, un filtro con un valor desconocido devuelve `200` con lista vacía sin avisar, y `vencidas=false` no filtra: devuelve las cinco solicitudes, incluidas las tres que el reporte marca como vencidas. | [01](evidencias/etapa2/01_swagger_endpoints.png), [02](evidencias/etapa2/02_registrar_solicitud_201.png), [07](evidencias/etapa2/07_filtro_estado_inexistente.png) (solo el cuerpo vacío de la respuesta); el pedido completo del filtro inexistente y el caso de `vencidas=false` en [08](evidencias/etapa2/08_pruebas_caja_negra.md) |
| **Eficiencia de desempeño** | Con los datos sintéticos, `GET /solicitudes` respondió en **7 y 8 ms** en dos de las tres mediciones; la primera marcó **20 ms**. La respuesta pesa **2,5 kB**. Es una medición indicativa desde el navegador: incluye el tiempo tal como lo mide el navegador, no solo el del servidor. El costo no preocupa a esta escala, pero conviene registrar dos límites: las listas se recorren completas en cada filtro y `GET /solicitudes` no tiene paginación, así que el tiempo y el tamaño de la respuesta crecen con la cantidad de solicitudes. | Medición con la pestaña Network de Chrome DevTools, tres recargas del mismo pedido · [09](evidencias/etapa2/09_tiempos_respuesta.png), [10](evidencias/etapa2/10_url_pedido.png) |

No se evaluaron **compatibilidad**, **seguridad** ni **portabilidad**: el alcance de la versión inicial no incluye autenticación ni integraciones con otros sistemas, y no se probó el despliegue en otros entornos. Quedan fuera de esta etapa.

**Conclusión parcial:** desde afuera el sistema **cumple lo que promete** en el uso corriente. Las reglas están implementadas y los errores de negocio se explican bien. El problema visible no es la falta de funcionalidad, sino la **inconsistencia del contrato**: tres casos usan el formato de error por defecto de Spring —uno de ellos, además, responde `500`— y la documentación de Swagger no describe lo que realmente devuelve la API.

## 2.2 Calidad interna: lo que ve el equipo técnico

Los defectos externos no son casos aislados: cada uno tiene una causa en la estructura del código. Esta tabla los conecta antes de entrar en las subcaracterísticas.

| Síntoma externo (2.1) | Causa interna |
|---|---|
| `500` con el legajo numérico | `crear` recibe un `Map<String, Object>` y hace `(String) body.get("legajo")`; no hay DTO ni validación de tipo, así que el error aparece en tiempo de ejecución |
| Tres casos con el formato de error por defecto | No hay un manejador global (`@ControllerAdvice`) que unifique el contrato: las 22 respuestas de error de negocio se arman a mano y las excepciones del framework salen con su propio formato |
| Swagger muestra solo `200` | Los endpoints no declaran las respuestas posibles ni modelos de error |
| Filtros que no avisan | `filtrar` recibe `String` libres sin validar y solo aplica el filtro de vencidas cuando el valor es `true`; un estado o tipo desconocido no coincide con nada y `vencidas=false` se ignora |

Esa cadena es la hipótesis de trabajo: lo que el usuario ve como inconsistencia es el resultado de decisiones internas. Las subcaracterísticas de mantenibilidad explican por qué esas decisiones son caras de cambiar.

De las cinco subcaracterísticas de mantenibilidad de ISO/IEC 25010, la checklist del issue pide cuatro: modularidad, analizabilidad, modificabilidad y capacidad de ser probado. Sumamos **reusabilidad** porque el código la expone con claridad: la misma solución está copiada en varios lugares.

| Subcaracterística de mantenibilidad | Qué se observó | Ejemplo concreto | Qué encarece al cambiar |
|---|---|---|---|
| **Modularidad** | Toda la lógica de la API vive en un controlador que atiende los ocho endpoints y además concentra las reglas de negocio. No hay capa de servicio ni de repositorio, y la persistencia se invoca de forma estática desde el controlador. | `SolicitudController.registrarSolicitud` hace validaciones, alta del estudiante, cálculo de prioridad, control de duplicados, construcción de la solicitud y persistencia, todo en un mismo método | Cada cambio de una regla obliga a ubicarla dentro del controlador y a distinguirla del manejo HTTP |
| **Reusabilidad** | La misma respuesta de error está escrita 22 veces. El cálculo de vencimiento está duplicado y la búsqueda de responsable también. | Errores: `m.put("error", ...)` repetido en 22 lugares del controlador. Vencimiento: la misma regla aparece en `filtrar` y en `resumen`. Responsable: la búsqueda se repite en `asignarResponsable` y en `resumen` | Una corrección en el formato de error hay que repetirla en cada uno de los 22 lugares |
| **Analizabilidad (legibilidad)** | Los métodos son largos, hay valores escritos como texto suelto y los nombres son abreviaturas. El `Map<String, Object>` de entrada borra el tipo de los datos: por eso el legajo numérico explota recién en tiempo de ejecución. | `registrarSolicitud` es un método de más de 100 líneas que hace todo el registro de punta a punta. `"PENDIENTE"` aparece 11 veces en todo `src/main/java` (9 en el controlador), `"EN_REVISION"` 11 (10 en el controlador) y `"ALTA"` 9 (7 en el controlador). Variables `m`, `lst`, `sol`, `pa`, `pb`. `// TODO` y código comentado. | Leer un método de más de 100 líneas para responder una pregunta puntual, y descubrir los tipos recién al ejecutar |
| **Modificabilidad** | Un cambio de dominio obliga a tocar varios lugares que no están conectados entre sí. El controlador conoce directamente la estructura de datos global. | 20 referencias directas a `BaseDeDatos` dentro del controlador. Agregar un tipo obliga a tocar la validación del tipo y el `switch` de prioridad en `registrarSolicitud`, y el mapa `porTipo` en `resumen` | Agregar un dato del dominio exige buscar todas las referencias dispersas y sin que el compilador ayude |
| **Capacidad de ser probado** | El estado es global y mutable, la fecha se toma del reloj del sistema y cada cambio escribe un archivo. Eso hace difícil aislar un caso de prueba. | `BaseDeDatos` tiene listas `static` y `ultimoId` se incrementa a mano desde `registrarSolicitud`; `LocalDate.now()` se usa en `registrarSolicitud`, `filtrar` y `resumen`; `guardar()` escribe `datos/solicitudes.json` al registrar, al cambiar de estado y al asignar responsable; `SolicitudesApplication.main` carga los datos al arrancar | Cada prueba comparte el estado global y escribe un archivo; aislar un caso exige preparar y limpiar el entorno |

La versión inicial tampoco tiene pruebas automatizadas: el `pom.xml` ya incluye JUnit 5 y MockMvc, pero no hay carpeta `src/test`. Esa es una decisión del alcance del hito 1, no un olvido: las pruebas se escriben en el hito 3, antes de refactorizar. Sin esa red, la única forma de saber si un cambio rompió el contrato es repetir la sesión de caja negra a mano.

## 2.3 Contraste y relación con la evolución futura

Que el sistema funcione desde afuera no significa que sea barato de cambiar. Estos son tres cambios probables, de distinta escala: los dos primeros son cambios de dominio, del tipo que aparece en el día a día del sistema; el tercero es un cambio estructural. Para cada uno, qué habría que tocar hoy:

| Cambio probable | Eje que castiga | Partes del código afectadas hoy | Riesgo |
|---|---|---|---|
| **Agregar un tipo de solicitud** (por ejemplo, `CAMBIO_CARRERA`) | Modificabilidad y analizabilidad | Validación del tipo y cálculo de prioridad en `registrarSolicitud` · mapa `porTipo` en `resumen` · documentación: la lista de tipos de la sección 1 | **Alto.** El registro puede quedar funcionando y el error aparecer recién al pedir el reporte: en `resumen`, `porTipo.get(tipo) + 1` recibe `null` para el tipo nuevo y lanza `NullPointerException`. El compilador no avisa nada |
| **Cambiar los plazos de vencimiento de RN-14** (3, 7 y 15 días) | Reusabilidad y modificabilidad | Cálculo de vencimiento en `filtrar` y en `resumen` | **Medio.** Son dos copias de la misma regla. Si se cambia una sola, el filtro y el reporte empiezan a mostrar números distintos |
| **Pasar la persistencia de JSON a una base de datos** | Modularidad y capacidad de ser probado | La clase `BaseDeDatos` completa (listas `static`, `cargar`, `guardar`, `ArchivoDatos`) · `SolicitudesApplication.main` · las 20 referencias del controlador · las tres llamadas a `BaseDeDatos.guardar()` en `registrarSolicitud`, `cambiarEstado` y `asignarResponsable` · las clases del modelo (anotaciones y repositorios) | **Alto.** Es el cambio que más se propaga: no queda ninguna capa que aísle el acceso a datos |

El primer cambio se ve en `resumen`:

```java
porTipo.put("CERTIFICADO", 0);
porTipo.put("EQUIVALENCIA", 0);
// ... una clave por cada tipo válido
porTipo.put(s.getTipo(), porTipo.get(s.getTipo()) + 1);
```

Si el tipo no está en el mapa, `get` devuelve `null` y la suma falla.

Con los estados pasa lo mismo: `resumen` inicializa `porEstado` con cinco claves y después hace `porEstado.get(s.getEstado()) + 1`. Agregar un estado nuevo sin tocar ese mapa produce el mismo `NullPointerException`.

El caso muestra el problema de fondo: **el sistema compila, pero no avisa qué otras partes dependen del mismo dato**. La lista de tipos válidos y el mapa del reporte son dos copias de la misma idea, sin nada que las mantenga sincronizadas. Eso es deuda técnica: no se paga al escribir, se paga al cambiar.

## 2.4 Conclusión

La versión inicial **funciona desde la mirada del usuario**: las operaciones responden, las reglas se respetan y los errores de negocio se explican con códigos y mensajes razonables. Su calidad externa tiene un defecto concreto —el `500` con legajo numérico— y varias inconsistencias de contrato: formato de error, Swagger incompleto y filtros que no avisan.

La calidad interna es otra historia: el controlador concentra responsabilidades, el estado es global, las reglas están repetidas y los datos circulan sin tipo. Eso no se nota al usar la aplicación, pero encarece cada cambio. Esa diferencia es la que ordena la **sección 3**: el catálogo toma estos problemas, los ubica por archivo y método y les asigna impacto, riesgo y prioridad; los refactorings salen de ahí.

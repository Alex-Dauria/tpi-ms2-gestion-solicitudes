# 2. Calidad externa vs. calidad interna

**Responsable:** Silvia · **Revisión cruzada:** Alex

Para separar las dos miradas usamos dos métodos distintos sobre la misma versión (tag `estado-inicial`):

- **Desde afuera:** probamos el sistema como usuario, con Swagger UI y con `curl`, sin mirar el código. Ejecutamos 21 pedidos: los caminos felices de F1–F7 y los casos de error de las reglas RN-01 a RN-15. La sesión completa está en [`evidencias/etapa2/08_pruebas_caja_negra.md`](evidencias/etapa2/08_pruebas_caja_negra.md) y las capturas en [`evidencias/etapa2/`](evidencias/etapa2/).
- **Desde adentro:** leímos el código de la versión inicial y lo evaluamos con las subcaracterísticas de **mantenibilidad** de ISO/IEC 25010.

El catálogo detallado de problemas va en la sección 3. Acá buscamos la mirada general: qué se ve bien desde afuera y qué cuesta mantener desde adentro.

## 2.1 Calidad externa: lo que percibe el usuario

De los 21 pedidos, 10 terminaron en una respuesta exitosa (`200` o `201`) y 11 fueron rechazados: 10 con un código previsto por las reglas (`400`, `404` o `409`) y 1 con un `500`.

| Característica (ISO 25010) | Qué se observó | Evidencia |
|---|---|---|
| **Adecuación funcional** | Las siete funcionalidades responden. El registro devuelve `201` con id, prioridad calculada por tipo y estado `PENDIENTE`; los filtros se combinan y ordenan por prioridad; el cambio de estado respeta el flujo; asignar responsable a una solicitud pendiente la pasa a `EN_REVISION`. Los datos sobreviven al reinicio: después de reiniciar la aplicación, una solicitud creada antes seguía disponible. | [02](evidencias/etapa2/02_registrar_solicitud_201.png), [06](evidencias/etapa2/06_reporte_resumen.png); casos 13 a 16, 20 a 21 y prueba de persistencia en [08](evidencias/etapa2/08_pruebas_caja_negra.md) |
| **Fiabilidad / manejo de errores** | Las reglas de negocio devuelven el código correcto y un mensaje claro en español: descripción corta `400`, duplicada `409`, responsable inactivo `409`, solicitud inexistente `404`, rechazo sin comentario `400`. **Pero hay un caso que rompe el contrato:** si el legajo llega como número, el sistema responde `500` en lugar de `400`, y el cuerpo del error es el formato por defecto de Spring, no el `{"error": "..."}` del resto de la API. Lo mismo pasa con un JSON malformado y con un filtro de tipo inválido. | [05](evidencias/etapa2/05_error_500_legajo_numerico.png); casos 5, 9 y 10 |
| **Usabilidad de la API** | Swagger UI está disponible y los nombres de las operaciones son claros. Sin embargo, la documentación generada muestra **solo la respuesta `200`**: el `201` del registro y todos los errores aparecen como *Undocumented* (captura 02). Además, un estado inexistente al cambiar de estado devuelve `409` con el texto "Transición no permitida", y un filtro con un valor desconocido devuelve `200` con lista vacía sin avisar. `vencidas=false` no filtra: devuelve las cinco solicitudes, incluidas las tres que el reporte marca como vencidas. | [01](evidencias/etapa2/01_swagger_endpoints.png), [02](evidencias/etapa2/02_registrar_solicitud_201.png), [07](evidencias/etapa2/07_filtro_estado_inexistente.png); casos 2, 3 y 18 |
| **Eficiencia de desempeño** | Con los datos sintéticos, las consultas responden **por debajo de 2 ms** en régimen (la primera corrida, en frío, puede llegar a 6,5 ms). El costo no preocupa a esta escala, pero conviene registrar dos límites: las listas se recorren completas en cada filtro y `GET /solicitudes` no tiene paginación, así que el tiempo y el tamaño de la respuesta crecen con la cantidad de solicitudes. | Medición con `curl -w '%{time_total}'`, tres corridas |

**Conclusión parcial:** desde afuera el sistema **cumple lo que promete**. Las reglas están implementadas y los errores de negocio se explican bien. El problema visible no es la falta de funcionalidad, sino la **inconsistencia del contrato**: un caso responde `500`, tres casos usan un formato de error distinto y la documentación de Swagger no describe lo que realmente devuelve la API.

## 2.2 Calidad interna: lo que ve el equipo técnico

| Subcaracterística de mantenibilidad | Qué se observó | Ejemplo concreto |
|---|---|---|
| **Modularidad** | Todo el sistema vive en un controlador de 441 líneas que atiende los ocho endpoints y además concentra las reglas de negocio. No hay capa de servicio ni de repositorio, y la persistencia se invoca de forma estática desde el controlador. | `SolicitudController.registrarSolicitud` (líneas 42 a 146) hace validaciones, alta del estudiante, cálculo de prioridad, control de duplicados, construcción de la solicitud y persistencia |
| **Reusabilidad** | La misma respuesta de error está escrita 22 veces. El cálculo de vencimiento está duplicado y la búsqueda de responsable también. | Errores: `m.put("error", ...)` repetido en 22 lugares. Vencimiento: `filtrar` (188-202) y `resumen` (399-409). Responsable: 326-331 y 411-416 |
| **Analizabilidad (legibilidad)** | Los métodos son largos, hay valores escritos como texto suelto y los nombres son abreviaturas. El `Map<String, Object>` de entrada borra el tipo de los datos: por eso el legajo numérico explota recién en tiempo de ejecución. | `registrarSolicitud` tiene 105 líneas. `"PENDIENTE"` aparece 11 veces, `"EN_REVISION"` 11 y `"ALTA"` 9. Variables `m`, `lst`, `sol`, `pa`, `pb`. `// TODO` y código comentado en 143-144 |
| **Modificabilidad** | Un cambio de dominio obliga a tocar varios lugares que no están conectados entre sí. El controlador conoce directamente la estructura de datos global. | 20 referencias directas a `BaseDeDatos` dentro del controlador. Agregar un tipo obliga a tocar la validación (60-64), el cálculo de prioridad (81-92) y el mapa del reporte (379-384) |
| **Capacidad de ser probado** | El estado es global y mutable, la fecha se toma del reloj del sistema y cada cambio escribe un archivo. Eso hace difícil aislar un caso de prueba. | `BaseDeDatos` tiene listas `static` (17-20); `LocalDate.now()` se usa en 137, 192 y 400; `guardar()` escribe `datos/solicitudes.json` en 142, 297 y 362; `SolicitudesApplication.main` carga los datos al arrancar |

La versión inicial tampoco tiene pruebas automatizadas: el `pom.xml` ya incluye JUnit 5 y MockMvc, pero no hay carpeta `src/test`. Esa es una decisión del alcance del hito 1, no un olvido: las pruebas se escriben en el hito 3, antes de refactorizar.

## 2.3 Contraste y relación con la evolución futura

Que el sistema funcione desde afuera no significa que sea barato de cambiar. Estos son tres cambios probables y lo que habría que tocar hoy:

| Cambio probable | Partes del código afectadas hoy | Riesgo |
|---|---|---|
| **Agregar un tipo de solicitud** (por ejemplo, `CAMBIO_CARRERA`) | Validación del tipo (60-64) · cálculo de prioridad (81-92) · mapa `porTipo` del reporte (379-384) · sección 1 del informe | **Alto.** El registro puede quedar funcionando y el error aparecer recién al pedir el reporte: en la línea 396, `porTipo.get(tipo) + 1` recibe `null` para el tipo nuevo y lanza `NullPointerException`. El compilador no avisa nada |
| **Cambiar los plazos de vencimiento de RN-14** (3, 7 y 15 días) | `filtrar` (193-201) · `resumen` (401-406) | **Medio.** Son dos copias de la misma regla. Si se cambia una sola, el filtro y el reporte empiezan a mostrar números distintos |
| **Pasar la persistencia de JSON a una base de datos** | `BaseDeDatos` completa (15-156: listas estáticas, `cargar`, `guardar`, `ArchivoDatos`) · arranque en `SolicitudesApplication` (11) · las 20 referencias del controlador · las clases del modelo (anotaciones y repositorios) | **Alto.** Es el cambio que más se propaga: no queda ninguna capa que aísle el acceso a datos |

El primer caso muestra el problema de fondo: **el sistema compila, pero no avisa qué otras partes dependen del mismo dato**. La lista de tipos válidos y el mapa del reporte son dos copias de la misma idea, sin nada que las mantenga sincronizadas. Eso es deuda técnica: no se paga al escribir, se paga al cambiar.

## 2.4 Conclusión

La versión inicial **funciona bien desde la mirada del usuario**: las operaciones responden, las reglas de negocio se respetan y los errores de negocio se explican con códigos y mensajes razonables. Su calidad externa tiene un defecto concreto (el `500` con legajo numérico) y varias inconsistencias de contrato (formato de error, documentación de Swagger y filtros que no avisan), pero ninguna impide usar el sistema.

La calidad interna es otra historia. El controlador concentra demasiadas responsabilidades, el estado es global, las reglas están repetidas y los datos circulan sin tipo. Eso no se nota al usar la aplicación, pero se nota al cambiarla: agregar un tipo puede romper el reporte, cambiar un plazo obliga a editar dos lugares y migrar la persistencia atraviesa todo el proyecto.

Esa diferencia es justamente lo que vamos a ordenar en la sección 3: el catálogo de deuda técnica y code smells toma estos problemas, los ubica con archivo y línea, y les asigna impacto, riesgo y prioridad. Los refactorings de la sección 5 salen de ese catálogo, no de una lista genérica.

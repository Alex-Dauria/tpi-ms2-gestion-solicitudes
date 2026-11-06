# 6. Pruebas y verificación del comportamiento

**Consolida:** Silvia · **Revisión cruzada:** Alex
**Aportes por zona:** A – Alex · B – Marina · C – Silvia

## 6.1 Estrategia

**Pruebas de caracterización**. Antes de refactorizar se escriben pruebas automatizadas que registran cómo se comporta hoy la versión inicial (respuesta, código HTTP y mensaje para cada caso). Esas mismas pruebas se ejecutan después de cada refactoring: si siguen en verde, el comportamiento externo se conservó.

- Herramientas: JUnit 5 + MockMvc (`@SpringBootTest` + `@AutoConfigureMockMvc`).
- Aislamiento: `@BeforeEach` restablece los datos en memoria mediante `BaseDeDatos.cargarDatosDePrueba()`. Además, se conserva una copia del JSON original y se restaura antes y después de las pruebas. Las ejecuciones deben realizarse sin otros procesos utilizando el mismo archivo de datos.
- Ejecución: `.\mvnw.cmd test` (Windows, PowerShell) o `./mvnw test`.

### Línea base del comportamiento externo

Además de las pruebas automatizadas, el script `scripts/linea_base.ps1` verifica el sistema "desde afuera", como lo usaría un cliente. Compila la versión del código que haya en la carpeta, la levanta con los datos de prueba iniciales, ejecuta siempre los mismos 14 pedidos HTTP (operaciones válidas y casos de error) y guarda el código y la respuesta de cada uno. Cada caso indica qué funcionalidad (F) y qué regla de negocio (RN) de la sección 1 verifica, para mantener la trazabilidad.

Como no depende del código interno, sirve igual antes y después del refactoring:

```powershell
# Versión inicial: se genera la referencia
.\scripts\linea_base.ps1 -Salida docs\evidencias\pruebas\linea_base_estado_inicial.md

# Versión final: se ejecuta lo mismo y se compara caso por caso con la referencia
.\scripts\linea_base.ps1 -Salida docs\evidencias\pruebas\linea_base_estado_refactorizado.md -CompararCon docs\evidencias\pruebas\linea_base_estado_inicial.md
```

En macOS o Linux se ejecuta igual con PowerShell 7: `pwsh ./scripts/linea_base.ps1 -Salida …`.

La fecha del día se reemplaza por `<HOY>` para que se puedan comparar corridas de días distintos. El resultado esperado al final es **14 de 14 casos iguales**. Si algún caso da distinto, hay que revisar si es una regresión o un cambio intencional documentado.

| Corrida | Archivo | Resultado |
|---|---|---|
| Versión inicial (`estado-inicial`) | `docs/evidencias/pruebas/linea_base_estado_inicial.md` | Referencia |
| Después del refactoring (`etapa-refactoring`) | `docs/evidencias/pruebas/linea_base_etapa_refactoring.md` | _completar_ |
| Versión final (`estado-refactorizado`) | `docs/evidencias/pruebas/linea_base_estado_refactorizado.md` | _completar_ |

### Qué significa "antes" y "después"

Las pruebas se escriben **después** de crear el tag `estado-inicial`, así que en ese tag no existen. Por eso:

- **Antes** = `main` con los PR de pruebas de las tres zonas ya mergeados y **antes del primer PR de refactoring**. El código del sistema (`src/main`) es idéntico al de `estado-inicial`; solo se agregaron las pruebas. Hay que anotar el commit en el que se corrieron.
- **Después del refactoring** = el tag `etapa-refactoring` (las tres zonas mergeadas). Ahí se corren las pruebas y la línea base para la sección 6 (#13).
- **Versión final** = el tag `estado-refactorizado`, que se crea después del code review final (#14). Como el code review puede generar correcciones, en #14 se vuelven a correr las pruebas y la línea base sobre ese tag.

## 6.2 Casos de prueba

_Un caso por regla o flujo. Tomar como base F1–F7 y RN-01 a RN-15 de la sección 1. Cada zona carga sus casos en su tabla, así se pueden escribir en paralelo sin pisarse. Silvia unifica la numeración en #13._

### 6.2.A Zona A – Registro (Alex)

Todas las pruebas son sobre `POST /solicitudes`. En los errores se verifica el código HTTP, el mensaje exacto y que el cuerpo tenga una sola clave `error`. En los rechazos se verifica además que no quedaron solicitudes ni estudiantes a medias.

| ID | Operación | Entrada / situación | Resultado esperado | Regla | Antes | Después |
|---|---|---|---|---|---|---|
| CP-01 | Registrar | Estudiante existente, tipo en minúsculas, sin prioridad, descripción con espacios en los extremos | `201`, id 6, tipo en mayúsculas, prioridad MEDIA, estado PENDIENTE, sin responsable, descripción recortada, fechas de hoy y una entrada en el historial | F1, RN-02, RN-04, RN-15 | OK | Pendiente |
| CP-02 | Registrar | Sin prioridad, para cada tipo (5 casos) | `201` con REVISION_NOTA → ALTA, EQUIVALENCIA y CAMBIO_COMISION → MEDIA, CERTIFICADO y OTRO → BAJA | RN-04 | OK | Pendiente |
| CP-03 | Registrar | Prioridad `"alta"` | `201` con prioridad ALTA | RN-04 | OK | Pendiente |
| CP-04 | Registrar | Estudiante nuevo con nombre y email con espacios y mayúsculas, sin carrera | `201`; el estudiante queda con el nombre recortado, el email en minúsculas y la carrera "Sin especificar" | RN-05 | OK | Pendiente |
| CP-05 | Registrar | Estudiante existente con otro nombre y email | `201`; los datos enviados se ignoran y el estudiante no cambia | RN-05 | OK | Pendiente |
| CP-06 | Registrar | Mismo tipo que una solicitud RECHAZADA del estudiante | `201` | RN-06 | OK | Pendiente |
| CP-07 | Registrar | Descripción de exactamente 500 caracteres | `201` | RN-03 | OK | Pendiente |
| CP-08 | Registrar | Sin legajo | `400` "El legajo es obligatorio", sin cambios | RN-01 | OK | Pendiente |
| CP-09 | Registrar | Legajo en blanco | `400` "El legajo es obligatorio" | RN-01 | OK | Pendiente |
| CP-10 | Registrar | Legajo `1234`, `123456` o `12a45` (3 casos) | `400` "El legajo debe tener 5 dígitos", sin cambios | RN-01 | OK | Pendiente |
| CP-11 | Registrar | Sin tipo | `400` "El tipo es obligatorio" | RN-02 | OK | Pendiente |
| CP-12 | Registrar | Tipo `"beca"` | `400` "Tipo de solicitud inválido: BECA" | RN-02 | OK | Pendiente |
| CP-13 | Registrar | Sin descripción | `400` "La descripción debe tener al menos 10 caracteres" | RN-03 | OK | Pendiente |
| CP-14 | Registrar | 9 caracteres rodeados de espacios (15 en total) | `400` con el mensaje del mínimo: el mínimo no cuenta los espacios | RN-03 | OK | Pendiente |
| CP-15 | Registrar | Descripción de 501 caracteres | `400` "La descripción no puede superar los 500 caracteres" | RN-03 | OK | Pendiente |
| CP-16 | Registrar | 498 caracteres más 3 espacios al final | `400` con el mensaje del máximo: el máximo sí cuenta los espacios | RN-03 | OK | Pendiente |
| CP-17 | Registrar | Prioridad `"URGENTE"` | `400` "Prioridad inválida: URGENTE" | RN-04 | OK | Pendiente |
| CP-18 | Registrar | Prioridad `" alta"` (con un espacio adelante) | `400` "Prioridad inválida:  ALTA" (el mensaje conserva el espacio) | RN-04 | OK | Pendiente |
| CP-19 | Registrar | Estudiante nuevo sin nombre | `400` "El estudiante no existe: se requieren nombre y email para darlo de alta", sin alta | RN-05 | OK | Pendiente |
| CP-20 | Registrar | Estudiante nuevo con email `"sin-arroba"` | `400` "El email no es válido", sin alta | RN-05 | OK | Pendiente |
| CP-21 | Registrar | Estudiante con una CERTIFICADO abierta, tipo en minúsculas | `409` "El estudiante ya tiene una solicitud abierta de tipo CERTIFICADO (id 1)", sin cambios | RN-06 | OK | Pendiente |
| CP-22 | Registrar | Todos los campos inválidos, corrigiendo uno por vez | El error aparece en este orden: legajo → tipo → descripción → prioridad → datos del estudiante nuevo | RN-01 a RN-05 | OK | Pendiente |
| CP-23 | Registrar | Descripción de 600 espacios (viola el mínimo y el máximo) | `400` con el mensaje del mínimo, que se valida primero | RN-03 | OK | Pendiente |
| CP-24 | Registrar | Un `409` seguido de un registro válido | El registro válido recibe el id 6: un rechazo no consume id | F1 | OK | Pendiente |
| CP-25 | Registrar | Legajo enviado como número | El controlador falla con `ClassCastException`; en el servidor es un `500` (caso 13 de la línea base). Defecto SA-03 | F1, RN-01 | OK | Pendiente |

**Resultado de la ejecución inicial**

- **Clase:** `SolicitudZonaATest.java`, con JUnit 5 y MockMvc.
- **Comando:** `.\mvnw.cmd clean test`.
- **Fecha:** 29/10/2026, sobre el código de `estado-inicial` (`src/main` sin cambios).
- **Resultado:** 31 ejecuciones (25 casos; CP-02 y CP-10 se repiten con varios valores): 31 correctas, 0 fallidas, 0 errores.
- **Aislamiento:** los datos en memoria se reinician antes de cada prueba y el archivo `datos/solicitudes.json` se restaura antes y después. Se comprobó con su huella que queda idéntico.
- **Evidencia:** `docs/evidencias/pruebas/zona-a/` (informes de Maven Surefire).
- **CP-25 (antes CP-A25)** fija un defecto. Si el refactoring lo corrige (por ejemplo, a `400`), es un cambio intencional: se documenta en 6.5 y se actualiza la prueba en un commit aparte.

### 6.2.B Zona B – Ciclo de vida (Marina)

| ID | Operación | Entrada / situación | Resultado esperado | Regla | Antes | Después |
|---|---|---|---|---|---|---|
| CP-26 | Obtener solicitud | GET `/solicitudes/1` | HTTP 200. Devuelve la solicitud 1 en estado PENDIENTE. | F3 | OK | Pendiente |
| CP-27 | Obtener solicitud inexistente | GET `/solicitudes/99` | HTTP 404. Informa que la solicitud no existe. | F3 | OK | Pendiente |
| CP-28 | Cambiar estado | EN_REVISION → RESUELTA, con comentario. | HTTP 200. Actualiza el estado y agrega un registro al historial. | F4, RN-07, RN-15 | OK | Pendiente |
| CP-29 | Cambiar estado | RECHAZADA → PENDIENTE. | HTTP 409. Rechaza la transición y conserva estado e historial. | RN-07 | OK | Pendiente |
| CP-30 | Cambiar estado | PENDIENTE → EN_REVISION sin responsable. | HTTP 409. Exige un responsable asignado. | RN-08 | OK | Pendiente |
| CP-31 | Rechazar solicitud | PENDIENTE → RECHAZADA sin comentario. | HTTP 400. Exige un comentario de rechazo. | RN-09 | OK | Pendiente |
| CP-32 | Asignar responsable | Asignar responsable activo a solicitud PENDIENTE. | HTTP 200. Asigna responsable, pasa automáticamente a EN_REVISION y registra historial. | RN-13, RN-15 | OK | Pendiente |
| CP-33 | Asignar responsable | Intentar asignar responsable inactivo. | HTTP 409. Rechaza la asignación. | RN-11 | OK | Pendiente |
| CP-34 | Asignar responsable | Intentar asignar responsable inexistente. | HTTP 404. Informa que el responsable no existe. | RN-11 | OK | Pendiente |
| CP-35 | Asignar responsable | Responsable con cinco solicitudes abiertas. | HTTP 409. Impide superar el límite de asignaciones. | RN-12 | OK | Pendiente |
| CP-36 | Cambiar estado | Intentar modificar una solicitud CERRADA. | HTTP 409. Rechaza la transición. | RN-07 | OK | Pendiente |
| CP-37 | Asignar responsable | Intentar asignar responsable a solicitud RECHAZADA. | HTTP 409. Rechaza la asignación. | RN-10 | OK | Pendiente |
| CP-38 | Cambiar estado | Petición sin campo `estado`. | HTTP 400. Informa que el nuevo estado es obligatorio. | F4 – estado obligatorio | OK | Pendiente |
| CP-39 | Reabrir solicitud | RESUELTA → EN_REVISION, con responsable ya asignado y cinco solicitudes abiertas adicionales. | HTTP 200. Permite reabrir sin aplicar nuevamente el límite de asignación. | RN-07, RN-08, RN-12 | OK | Pendiente |
| CP-40 | Cambiar estado | PENDIENTE → FOO (estado inexistente). | HTTP 409. Rechaza el cambio y conserva el estado original. | RN-07 | OK | Pendiente |
| CP-41 | Cambiar estado | EN_REVISION → resuelta (minúsculas). | HTTP 200. Acepta el valor y devuelve RESUELTA. | F4 – normalización de estado | OK | Pendiente |
| CP-42 | Asignar responsable | Petición sin `responsableId`. | HTTP 400. Informa que el responsable es obligatorio y conserva la solicitud. | F5 – responsable obligatorio | OK | Pendiente |

**Resultado de la ejecución inicial**

- **Herramientas:** JUnit 5 y MockMvc.
- **Clase:** `SolicitudZonaBTest.java`.
- **Comando:** `.\mvnw.cmd -Dtest=SolicitudZonaBTest test`.
- **Fecha:** 29/10/2026.
- **Resultado:** 17 pruebas ejecutadas, 17 correctas, 0 fallidas, 0 errores y 0 omitidas.
- **Estado de Maven:** `BUILD SUCCESS`.
- **Aislamiento:** los datos en memoria se reinician antes de cada prueba; se conserva y restaura el archivo JSON original. Se comprobó que el archivo coincide con su respaldo.
- **Etapa:** caracterización anterior al refactoring. La ejecución posterior queda pendiente.

### 6.2.C Zona C – Consultas y persistencia (Silvia)

_Los identificadores CP-43 a CP-63 corresponden a los casos de la zona C._

| ID | Operación | Entrada / situación | Resultado esperado | Regla | Antes | Después |
|---|---|---|---|---|---|---|
| CP-43 | Listar solicitudes | GET `/solicitudes` sin filtros | HTTP 200. Devuelve las 5 solicitudes de prueba. | F2 | OK | Pendiente |
| CP-44 | Filtrar por estado | GET `/solicitudes?estado=PENDIENTE` | HTTP 200. Devuelve las solicitudes 5 y 1 (ordenadas por prioridad). | F2 | OK | Pendiente |
| CP-45 | Filtrar por tipo | GET `/solicitudes?tipo=CERTIFICADO` | HTTP 200. Devuelve la solicitud 1. | F2 | OK | Pendiente |
| CP-46 | Filtrar por prioridad | GET `/solicitudes?prioridad=ALTA` | HTTP 200. Devuelve las solicitudes 3 y 5. | F2 | OK | Pendiente |
| CP-47 | Filtrar por legajo | GET `/solicitudes?legajo=10234` | HTTP 200. Devuelve las solicitudes 1 y 5. | F2 | OK | Pendiente |
| CP-48 | Filtrar por responsable | GET `/solicitudes?responsableId=3` | HTTP 200. Devuelve la solicitud 2. | F2 | OK | Pendiente |
| CP-49 | Filtrar vencidas | GET `/solicitudes?vencidas=true` | HTTP 200. Devuelve las 3 solicitudes abiertas vencidas. | F2, RN-14 | OK | Pendiente |
| CP-50 | Filtrar combinado | GET `/solicitudes?estado=PENDIENTE&prioridad=ALTA` | HTTP 200. Devuelve la solicitud 5. | F2 | OK | Pendiente |
| CP-51 | Filtrar sin resultados | GET `/solicitudes?estado=CERRADA&tipo=CERTIFICADO` | HTTP 200. Devuelve lista vacía. | F2 | OK | Pendiente |
| CP-52 | Reporte resumen | GET `/reportes/resumen` | HTTP 200. Totales, conteos por estado/tipo/prioridad, 3 vencidas y abiertas por responsable. | F6 | OK | Pendiente |
| CP-53 | Catálogo estudiantes | GET `/estudiantes` | HTTP 200. Devuelve los 4 estudiantes de prueba. | F7 | OK | Pendiente |
| CP-54 | Catálogo responsables | GET `/responsables` | HTTP 200. Devuelve los 4 responsables de prueba. | F7 | OK | Pendiente |
| CP-55 | Orden del listado | GET `/solicitudes` sin filtros | HTTP 200. Devuelve los ids en el orden 3, 5, 2, 4, 1. | F2 | OK | Pendiente |
| CP-56 | Filtrar vencidas=false | GET `/solicitudes?vencidas=false` | HTTP 200. Devuelve las 5 solicitudes. | F2 | OK | Pendiente |
| CP-57 | Filtro con valor vacío | GET `/solicitudes?estado=` | HTTP 200. No filtra; devuelve las 5 solicitudes. | F2 | OK | Pendiente |
| CP-58 | Filtros en minúsculas | GET `/solicitudes?tipo=revision_nota&estado=pendiente&prioridad=alta` | HTTP 200. Devuelve la solicitud 5 (igual que en mayúsculas). | F2 | OK | Pendiente |
| CP-59 | responsableId no numérico | GET `/solicitudes?responsableId=abc` | HTTP 400. | F2 | OK | Pendiente |
| CP-60 | Persistencia sin archivo | Eliminar `datos/solicitudes.json` y llamar `cargar()` | Carga los datos de prueba (5 solicitudes) y crea el archivo. | SC-03 | OK | Pendiente |
| CP-61 | Persistencia ida y vuelta | `guardar()` y luego `cargar()` | Recupera las 5 solicitudes y `ultimoId`. | SC-03 | OK | Pendiente |
| CP-62 | Persistencia archivo corrupto | Escribir basura en el JSON y llamar `cargar()` | Arranca con los datos de prueba (5 solicitudes). | SC-03 | OK | Pendiente |
| CP-63 | Límites de vencimiento | Solicitudes abiertas con 3/7/15 y 4/8/16 días; una RESUELTA vieja | `vencidas=true` devuelve 3; el reporte cuenta 3 vencidas. | SC-02, RN-14 | OK | Pendiente |

## 6.3 Evidencia antes / después

_Salida de `.\mvnw.cmd test` en cada momento (texto o captura), guardada en `docs/evidencias/pruebas/`. Junto con cada corrida se guarda también la carpeta `target/surefire-reports`, el informe que genera Maven automáticamente al correr las pruebas._

| Momento | Commit / tag | Pruebas ejecutadas | OK | Fallidas | Línea base | Evidencia |
|---|---|---|---|---|---|---|
| Antes (código inicial + pruebas) | `8d97555` (pruebas de las 3 zonas mergeadas, antes del primer refactoring) | 69 | 69 | 0 | Referencia (`linea_base_estado_inicial.md`) | `docs/evidencias/pruebas/etapa-6/` |
| Después del refactoring | `etapa-refactoring` | | | | _x de 14 iguales_ | |
| Versión final | `estado-refactorizado` | | | | _x de 14 iguales_ | |

## 6.4 Cómo se verificó que el refactoring no alteró el comportamiento

Procedimiento aplicado:

1. **Red de seguridad:** antes de refactorizar se escribieron las pruebas de caracterización de las tres zonas (Fase 1 de #8, #9 y #10), todas en verde sobre el código original.
2. **Un commit por refactoring:** cada refactoring se aplicó en un commit separado, con id `R-xx` (o `RA-xx`, `RC-xx` provisorio), y las pruebas se corrieron después de cada commit.
3. **Pruebas antes y después:** se ejecutó `.\mvnw.cmd test` antes de empezar y después de cada refactoring; todas las zonas debían quedar en verde.
4. **Línea base:** se ejecutó `scripts/linea_base.ps1` comparando la salida con `linea_base_estado_inicial.md`; el resultado esperado es "14 de 14 casos iguales".
5. **Revisión del PR:** el PR de refactoring se revisó con el checklist de #7. Si una prueba falla, se revisa si es una regresión (se corrige el refactoring) o un cambio intencional (se documenta y se actualiza la prueba en un commit aparte).
6. **Merge sin squash:** para conservar un commit por refactoring en el historial.

## 6.5 Hallazgos

_Comportamientos raros de la versión inicial que aparecieron al escribir las pruebas, y qué se decidió con cada uno (conservarlo o cambiarlo como cambio intencional documentado)._

**Zona A (registro):**

| Hallazgo | Prueba | Decisión |
|---|---|---|
| El mínimo de la descripción no cuenta los espacios de los extremos, pero el máximo sí | CP-14, CP-16 | Se conserva: es RN-03 tal como está documentada |
| Una prioridad con espacios (`" alta"`) se rechaza y el mensaje muestra el espacio | CP-18 | Se conserva |
| Si el estudiante ya existe, el nombre y el email enviados se ignoran sin aviso | CP-05 | Se conserva |
| Los datos del estudiante nuevo se validan recién después de toda la solicitud | CP-22 | Se conserva: el refactoring no puede cambiar el orden de las validaciones |
| El legajo enviado como número produce un `500` con el formato de error de Spring | CP-25 (antes CP-A25), caso 13 de la línea base | Se conserva: corregirlo sería un cambio de comportamiento, no un refactoring. RA-03 mantiene los casts a propósito. Queda como mejora pendiente |

Los hallazgos de las zonas B y C se registran en sus PR de pruebas y en sus diagnósticos. Las decisiones tomadas (conservar comportamiento, documentar cambios intencionales) se reflejan en las tablas 6.2.B y 6.2.C (columna "Antes/Después").

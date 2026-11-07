# 4. Aplicación de SOLID y Clean Code

**Consolida:** Alex · **Revisión cruzada:** Marina
**Aportes por zona:** A – Alex · B – Marina · C – Silvia

> La consigna pide mejoras vinculadas a SRP, OCP, LSP, ISP y DIP **cuando corresponda**, y mejoras de nombres, funciones, manejo de errores, cohesión, acoplamiento y límites entre componentes. Si un principio no aplica, hay que justificar por qué; no se fuerza.

## Aportes por zona

_Cada una escribe acá, al terminar su refactoring (fase 3 de #8, #9 y #10), un párrafo corto: qué principios y prácticas de Clean Code aplicó en su zona, sobre qué smell y con qué refactoring. Con esos aportes, Alex completa las tablas de abajo en #11._

### Zona A – Registro (Alex)

- **SRP:** `registrarSolicitud` hacía siete cosas en 105 líneas (SA-01). Ahora coordina: cada responsabilidad tiene su método (validar cada campo, validar y crear al estudiante, buscar la solicitud abierta, construir la solicitud) y el método principal se lee como una secuencia de pasos (RA-04, RA-05 y RA-06). Además, las reglas RN-01 a RN-05 salieron del controlador a `ReglasRegistro`, que no conoce HTTP ni la persistencia (RA-09 y RA-10). De esta zona quedan en el controlador la coordinación del pedido y las búsquedas y la construcción de la solicitud, que dependen del acceso a datos (zona C) y de las reglas de estado (zona B).
- **OCP:** parcial. Con las listas `TIPOS_VALIDOS` y `PRIORIDADES_VALIDAS` (RA-07), agregar un tipo ya no obliga a tocar la condición de validación, pero sí `prioridadPorDefecto` y el reporte. Cerrarlo del todo requiere un `enum` de tipo que conozca su prioridad (*Replace Type Code with Class*), que queda propuesto para coordinar con la zona C.
- **LSP, ISP y DIP:** no aplican a esta zona. No hay jerarquías ni interfaces en el registro, y la dependencia con `BaseDeDatos` es de la zona C. El controlador usa `ReglasRegistro` directamente: son reglas sin acceso a datos ni a servicios externos, así que una interfaz no agregaría nada que hoy haga falta reemplazar.
- **Clean Code:**
  - **Funciones:** el método principal pasó de 105 a 33 líneas y de 19 decisiones a 4, en métodos que hacen una sola cosa.
  - **Argumentos:** de siete `String` a un `DatosSolicitud` (RA-03).
  - **Manejo de errores:** un solo `respuestaError` para las 22 respuestas (RA-01), y las validaciones devuelven `Optional` en lugar de cortar el flujo con `return` en el medio del método.
  - **Nombres:** los comentarios que rotulaban bloques (`// validaciones`, `// buscar estudiante`) pasaron a ser nombres de métodos, y desaparecieron `m`, `est`, `e` y la `s` reutilizada.
  - **Valores mágicos:** constantes con nombre (RA-07).
  - **Código muerto:** se eliminaron el `TODO` y el `println` comentado (RA-08).

### Zona B – Ciclo de vida (Marina)

- **SRP y cohesión:** RB-01 reubica la decisión sobre las transiciones permitidas desde `SolicitudController` hacia `Solicitud.permiteTransicionA` (SB-01 / S-03). RB-03 reúne en `Solicitud.cambiarEstado` las comprobaciones de la transición, el responsable y el comentario, y la actualización del estado, historial y fecha (SB-04 / S-04). La entidad usa su propio estado y el controlador delega la operación. La mejora de SRP es parcial: el controlador conserva validaciones para producir las respuestas HTTP y sigue coordinando el acceso a `BaseDeDatos` y la persistencia.
- **Encapsulación:** RB-03 comprueba las condiciones antes de modificar la solicitud; una operación rechazada no cambia estado, historial ni fecha. La asignación automática conserva primero la entrada de asignación del responsable y después la de transición a `EN_REVISION`, con una misma fecha. SB-04 / S-04 queda **parcialmente resuelto**: `setEstado()` sigue siendo público para inicialización y compatibilidad con la persistencia JSON, por lo que todavía puede eludirse la operación de dominio. No se afirma que todas las invariantes estén protegidas.
- **OCP:** mejora limitada. RB-02 reemplaza la cadena condicional por una tabla estática e inmutable de transiciones; la consulta no cambia al ajustar sus entradas, pero modificar las reglas todavía requiere editar la tabla. Los estados continúan como `String`: RB-02 no resuelve SB-03 / S-10 ni implica cumplimiento completo de OCP.
- **LSP, ISP y DIP:** no fueron objeto de mejoras en esta zona. No se introdujeron jerarquías, interfaces ni inversión de dependencias; se conserva la dependencia del controlador con `BaseDeDatos`.
- **Clean Code:** RB-04 extrae `buscarSolicitudPorId` para las tres operaciones que repetían el recorrido de solicitudes (SB-02 / S-09), conservando la primera instancia coincidente y la respuesta `404`. RB-05 introduce `MAX_SOLICITUDES_POR_RESPONSABLE = 5` y utiliza el mismo valor en la condición y el mensaje (SB-05 / S-11). Los nombres expresan la intención, la búsqueda queda en un solo lugar y la operación de cambio de estado concentra sus efectos. Se mantienen el orden de las validaciones, los códigos HTTP y los mensajes existentes.
- **Decisión pendiente:** se posterga el `enum EstadoSolicitud` propuesto en SB-03 / S-10. El estado también interviene en consultas, reportes, datos sintéticos y JSON persistido; cambiar su tipo requiere coordinar esos consumidores y verificar la representación externa, las entradas inválidas y su orden de validación. No es una incompatibilidad inevitable con Jackson, sino un cambio de mayor alcance que se separa de RB-01 a RB-05.

### Zona C – Consultas y persistencia (Silvia)

## 4.1 Principios SOLID

| Principio | Problema en la versión inicial (smell S-xx) | Mejora aplicada | Refactoring (R-xx) | Zona |
|---|---|---|---|---|
| SRP – Responsabilidad única | | | | |
| OCP – Abierto/cerrado | | | | |
| LSP – Sustitución de Liskov | | | | |
| ISP – Segregación de interfaces | | | | |
| DIP – Inversión de dependencias | | | | |

_Para cada principio, un párrafo corto con el antes y el después y por qué la mejora reduce el costo del cambio._

## 4.2 Clean Code

| Aspecto | Antes | Después | Zona |
|---|---|---|---|
| Nombres | | | |
| Funciones (tamaño, un solo nivel de abstracción) | | | |
| Manejo de errores | | | |
| Comentarios y código muerto | | | |
| Números y textos mágicos | | | |

## 4.3 Cohesión, acoplamiento y límites entre componentes

_Diagrama de clases antes y después (Mermaid) y explicación de qué responsabilidad quedó en cada componente y de qué depende cada uno._

```mermaid
classDiagram
    %% Antes: estructura de la versión inicial
```

```mermaid
classDiagram
    %% Después: estructura de la versión refactorizada
```

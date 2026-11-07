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

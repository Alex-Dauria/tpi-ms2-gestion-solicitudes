# 6. Pruebas y verificación del comportamiento

**Consolida:** Silvia · **Revisión cruzada:** Alex
**Aportes por zona:** A – Alex · B – Marina · C – Silvia

## 6.1 Estrategia

_Explicar el enfoque. Propuesta:_ **pruebas de caracterización**. Antes de refactorizar se escriben pruebas automatizadas que registran cómo se comporta hoy la versión inicial (respuesta, código HTTP y mensaje para cada caso). Esas mismas pruebas se ejecutan después de cada refactoring: si siguen en verde, el comportamiento externo se conservó.

- Herramientas: JUnit 5 + MockMvc (`@SpringBootTest` + `@AutoConfigureMockMvc`).
- Aislamiento: en `@BeforeEach` llamar a `BaseDeDatos.cargarDatosDePrueba()` para que cada prueba empiece con los mismos datos.
- Ejecución: `mvnw.cmd test` (Windows) o `./mvnw test`.

## 6.2 Casos de prueba

_Un caso por regla o flujo. Tomar como base F1–F7 y RN-01 a RN-15 de la sección 1._

| ID | Zona | Operación | Entrada / situación | Resultado esperado | Regla | Antes (`estado-inicial`) | Después (`estado-refactorizado`) |
|---|---|---|---|---|---|---|---|
| CP-01 | A | Registrar | | | | | |
| CP-02 | | | | | | | |

## 6.3 Evidencia antes / después

_Salida de `mvnw test` sobre `estado-inicial` y sobre `estado-refactorizado` (texto o captura), guardada en `docs/evidencias/pruebas/`._

| Versión | Pruebas ejecutadas | OK | Fallidas | Evidencia |
|---|---|---|---|---|
| `estado-inicial` | | | | |
| `estado-refactorizado` | | | | |

## 6.4 Cómo se verificó que el refactoring no alteró el comportamiento

_Explicar el procedimiento: pruebas en verde antes de empezar, un commit por refactoring, pruebas después de cada commit, revisión del PR, y qué hacer si una prueba falla._

## 6.5 Hallazgos

_Comportamientos raros de la versión inicial que aparecieron al escribir las pruebas, y qué se decidió con cada uno (conservarlo o cambiarlo como cambio intencional documentado)._

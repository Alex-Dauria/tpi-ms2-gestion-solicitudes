# 6. Pruebas y verificación del comportamiento

**Consolida:** Silvia · **Revisión cruzada:** Alex
**Aportes por zona:** A – Alex · B – Marina · C – Silvia

## 6.1 Estrategia

_Explicar el enfoque. Propuesta:_ **pruebas de caracterización**. Antes de refactorizar se escriben pruebas automatizadas que registran cómo se comporta hoy la versión inicial (respuesta, código HTTP y mensaje para cada caso). Esas mismas pruebas se ejecutan después de cada refactoring: si siguen en verde, el comportamiento externo se conservó.

- Herramientas: JUnit 5 + MockMvc (`@SpringBootTest` + `@AutoConfigureMockMvc`).
- Aislamiento: en `@BeforeEach` llamar a `BaseDeDatos.cargarDatosDePrueba()` para que cada prueba empiece con los mismos datos.
- Ejecución: `.\mvnw.cmd test` (Windows, PowerShell) o `./mvnw test`.

### Línea base del comportamiento externo

Además de las pruebas automatizadas, el script `scripts/linea_base.ps1` verifica el sistema "desde afuera", como lo usaría un cliente. Compila la versión del código que haya en la carpeta, la levanta con los datos de prueba iniciales, ejecuta siempre los mismos 12 pedidos HTTP (operaciones válidas y casos de error) y guarda el código y la respuesta de cada uno.

Como no depende del código interno, sirve igual antes y después del refactoring:

```powershell
# Versión inicial: se genera la referencia
.\scripts\linea_base.ps1 -Salida docs\evidencias\pruebas\linea_base_estado_inicial.md

# Versión final: se ejecuta lo mismo y se compara caso por caso con la referencia
.\scripts\linea_base.ps1 -Salida docs\evidencias\pruebas\linea_base_estado_refactorizado.md -CompararCon docs\evidencias\pruebas\linea_base_estado_inicial.md
```

La fecha del día se reemplaza por `<HOY>` para que se puedan comparar corridas de días distintos. El resultado esperado al final es **12 de 12 casos iguales**. Si algún caso da distinto, hay que revisar si es una regresión o un cambio intencional documentado.

| Corrida | Archivo | Resultado |
|---|---|---|
| Versión inicial | `docs/evidencias/pruebas/linea_base_estado_inicial.md` | Referencia |
| Versión final | `docs/evidencias/pruebas/linea_base_estado_refactorizado.md` | _completar_ |

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

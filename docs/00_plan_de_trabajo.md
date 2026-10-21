# Plan de trabajo y metodología del equipo

Este documento organiza el trabajo: quién hace qué, en qué orden y cómo nos revisamos. No forma parte del informe final tal cual, pero de acá sale el apartado de participación de cada integrante.

## 1. Producto a entregar

| Entregable | Dónde se arma |
|---|---|
| Informe PDF único | Se escribe en Markdown en `docs/` y se convierte a PDF al final (issue #17) |
| Repositorio GitHub | Este repositorio, con los tags obligatorios |
| Anexo ZIP | Capturas, informes de herramientas, pruebas y conversaciones con IA (issue #16) |
| Video 10–15 min | Todos con cámara y voz propia (issue #18) |

## 2. Zonas del código

Para repartir el diagnóstico y el refactoring de forma pareja, el código de la versión inicial se divide en tres zonas. Cada integrante es responsable de una zona de punta a punta: diagnostica sus smells, escribe sus pruebas de caracterización, aplica sus refactorings y los documenta. Así cada una puede defender su parte completa: problema, mejora y verificación.

| Zona | Alcance en la versión inicial | Responsable | Refactoring obligatorio asignado |
|---|---|---|---|
| **A – Registro de solicitudes** | `SolicitudController.crear` y `registrarSolicitud` (validaciones, alta de estudiante, cálculo de prioridad, regla de duplicados) y la forma en que el controlador arma las respuestas de error | Alex | Extract Method |
| **B – Ciclo de vida de la solicitud** | `cambiarEstado`, `asignarResponsable`, `obtener` y la clase `Solicitud` (estados e historial) | Marina | Move Method / reubicación de responsabilidad |
| **C – Consultas y persistencia** | `listar`/`filtrar`, `resumen`, catálogos y la clase `BaseDeDatos` | Silvia | Introduce Parameter Object |

Los problemas transversales (tamaño y responsabilidades del controlador, nombres, comentarios) los registra quien los detecte y los ordena Marina al consolidar el catálogo (#6).

Los demás refactorings de cada zona se eligen **a partir del diagnóstico**, no antes: primero se justifica el problema y después la técnica.

## 3. Reparto de tareas

| Integrante | Responsable de | Revisa a |
|---|---|---|
| **Alex** | #1 Sistema base · #3 Diagnóstico zona A · #8 Refactoring zona A · #11 SOLID y Clean Code · #16 IA y ZIP · #18 Video | Silvia |
| **Silvia** | #2 Calidad externa/interna · #5 Diagnóstico zona C · #7 Checklist de code review · #10 Refactoring zona C · #13 Pruebas · #15 Conclusiones | Marina |
| **Marina** | #4 Diagnóstico zona B · #6 Catálogo de deuda técnica · #9 Refactoring zona B · #12 Documento de refactorings · #14 Code review final · #17 Revisión integral y PDF | Alex |

**Revisión cruzada rotativa:** Marina revisa lo de Alex, Silvia revisa lo de Marina y Alex revisa lo de Silvia. Los documentos que consolidan aportes de las tres (#6, #7, #15, #16, #17) los revisan las otras dos.

## 4. Hitos y tags

| Hito | Issues | Tag al cerrar |
|---|---|---|
| 1. Estado inicial | #1 | `estado-inicial` |
| 2. Diagnóstico | #2 a #7 | `etapa-diagnostico` |
| 3. Refactoring y pruebas | #8 a #13 | `etapa-refactoring` y `etapa-pruebas` |
| 4. Code review y documentación | #14 a #16 | `estado-refactorizado` (código final) y `etapa-docs` |
| 5. Entrega | #17 y #18 | — |

Fechas: _completar con las de la pestaña Evaluaciones del aula._

## 5. Orden del refactoring

Las tres zonas tocan `SolicitudController`. Para no pisarnos:

1. **Pruebas de caracterización primero, en paralelo.** Cada una escribe las pruebas de su zona sobre la versión inicial, en un PR solo de pruebas (`src/test`), sin tocar `src/main`. Tienen que pasar en verde sobre el código original: son la red de seguridad.
2. **Refactorings en secuencia: A → B → C.** Cada PR de refactoring se abre desde `main` actualizado, después de mergear el anterior.
   - A va primero porque ordena el registro y el manejo de errores, que usan todos los endpoints.
   - B trabaja sobre esa base y reubica la lógica de estados.
   - C cierra con las consultas y la persistencia, que atraviesan todo lo anterior.
3. Mientras una zona refactoriza, las otras avanzan con su documentación.

## 6. Reglas de refactoring seguro

- Antes de empezar: `mvnw.cmd test` en verde.
- Pasos chicos: **un commit por refactoring**, con las pruebas en verde después de cada uno.
- Mensaje de commit con el identificador del refactoring: `refactor(zona-a): extraigo la validación del registro (R-01)`.
- No se cambia el comportamiento externo (respuestas, códigos HTTP, mensajes). Si al refactorizar aparece un comportamiento que conviene corregir, se discute en el issue y, si se acepta, se hace en un commit aparte y se documenta como **cambio intencional**.
- Los PR de refactoring se mergean con **merge commit, no squash**, para conservar un commit por refactoring en el historial.

## 7. Flujo de trabajo en GitHub

1. Tomar el issue y moverlo a **In progress** en el Project.
2. Crear la rama desde `main`: `etapa-N/tema` para documentos o `zona-x/tema` para código (ej.: `zona-a/pruebas`, `zona-a/refactoring`).
3. Abrir el PR con la plantilla, con `Closes #N`, y mover el issue a **In review**.
4. La revisora deja sus observaciones en el PR. En los PR de código se aplica el checklist de code review (#7).
5. La autora responde punto por punto y sube los ajustes.
6. Con la aprobación, la autora mergea y el issue pasa a **Done**.

**Commits:** en español, con prefijo: `docs:`, `test:`, `refactor:`, `feat:`, `fix:`, `chore:`.

## 8. Uso de IA

La consigna permite usar IA como apoyo si se declara. Cada integrante registra en [09_uso_ia.md](09_uso_ia.md) la herramienta, para qué la usó, el grado de intervención y el enlace a la evidencia, y guarda las conversaciones relevantes en `docs/evidencias/ia/`, que después van al ZIP.

## 9. Armado del PDF

Se escribe y corrige todo en Markdown. Recién cuando las secciones estén aprobadas se arma el PDF único (portada, resumen ejecutivo, secciones 01 a 10, enlaces al repo y al video, participación de cada integrante). Eso se hace en el issue #17.

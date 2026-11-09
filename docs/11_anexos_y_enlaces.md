# 11. Anexos y enlaces

**Completa:** Marina (#17)

## 11.1 Enlaces

| Recurso | Enlace |
|---|---|
| Repositorio | <https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes> |
| Video de defensa | <https://youtu.be/LkxOHGBwQAU> |
| Tablero de trabajo (GitHub Project) | <https://github.com/users/Alex-Dauria/projects/1> |

## 11.2 Tags del repositorio

| Tag | Hito | Commit | Enlace |
|---|---|---|---|
| `estado-inicial` | Versión base del sistema | `ef5f080` | <https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/tree/estado-inicial> |
| `etapa-diagnostico` | Diagnóstico y catálogo de deuda técnica | `85377f3` | <https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/tree/etapa-diagnostico> |
| `etapa-refactoring` | Refactorings aplicados | `afc85e5` | <https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/tree/etapa-refactoring> |
| `etapa-pruebas` | Pruebas y evidencia antes/después | `26b943e` | <https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/tree/etapa-pruebas> |
| `estado-refactorizado` (también `estadorefactorizado`, como lo escribe la consigna) | Versión final del código | `f8c4e30` | <https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/tree/estado-refactorizado> |
| `etapa-docs` | Documentación completa | _Pendiente: se crea al cerrar #17_ | — |

Comparación completa del código: <https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes/compare/estado-inicial...estado-refactorizado>

## 11.3 Contenido del anexo ZIP

El ZIP se entrega en la plataforma junto con el informe. Se arma desde `docs/evidencias/` y suma los diagramas del informe exportados como imagen; su índice está en `LEEME.txt`, en la raíz del ZIP.

| Carpeta | Contenido |
|---|---|
| `LEEME.txt` | Índice del contenido |
| `etapa1/` | 7 capturas de Swagger UI del sistema base |
| `etapa2/` | 9 capturas del análisis de calidad externa y el registro de pruebas de caja negra |
| `pruebas/` | Líneas base de la versión inicial, del refactoring y de la versión final (sobre `50bac22` y sobre el tag `estado-refactorizado`), e informes de Surefire de cada corrida: `zona-a/`, `zona-b/`, `zona-c/`, `etapa-6/` (antes), `etapa-6-despues/` (después), `estado-refactorizado/` y `estado-refactorizado-tag/` (versión final) |
| `code-review/` | 9 capturas de las revisiones de los PR #24, #28, #31, #38 y #41, con su índice |
| `diagramas/` | 4 diagramas del informe exportados como imagen: los dos de la sección 1 y los de clases antes y después de la sección 4 |
| `ia/` | La declaración completa de uso de IA (`09_uso_ia.md`) y los registros de los usos declarados en la sección 9 |

En los informes de pruebas, los nombres de usuario y las rutas de las computadoras se reemplazaron por `[USUARIO]`. Los resultados de las pruebas no se modificaron.

## 11.4 Decisiones técnicas

Ver la sección de decisiones técnicas (`docs/10_decisiones_tecnicas.md`), que se incluye a continuación en el PDF.

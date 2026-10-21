# Sistema de Gestión de Solicitudes Académicas

Trabajo Práctico Integrador de **Metodología de Sistemas II**, Tecnicatura Universitaria en Programación a Distancia (UTN).

**Tema:** evolución profesional de un sistema existente: calidad, deuda técnica, refactoring y revisión técnica.

El sistema permite que los estudiantes registren solicitudes académicas (certificados, equivalencias, revisiones de nota, cambios de comisión), clasificarlas por tipo, prioridad y estado, asignarles un responsable, buscarlas con filtros y obtener un reporte resumen.

El objetivo del trabajo no es sumar funcionalidades, sino **diagnosticar y mejorar la calidad interna** de una versión inicial, sin alterar su comportamiento externo.

## Integrantes

| Integrante | GitHub |
|---|---|
| Alex Pedro Dauria | [@Alex-Dauria](https://github.com/Alex-Dauria) |
| Silvia Giardini | [@SilviaGiardini](https://github.com/SilviaGiardini) |
| Marina Giselle Cordero | [@Marigi84](https://github.com/Marigi84) |

## Versiones del sistema

| Tag | Qué contiene |
|---|---|
| `estado-inicial` | Versión base del sistema, con los problemas de diseño a diagnosticar |
| `etapa-diagnostico` | Diagnóstico de calidad y catálogo de deuda técnica y code smells |
| `etapa-refactoring` | Refactorings aplicados |
| `etapa-pruebas` | Pruebas y evidencia antes/después |
| `etapa-docs` | Documentación completa del trabajo |
| `estado-refactorizado` | Versión final del código |

Para comparar las dos versiones: `git diff estado-inicial estado-refactorizado -- src/`

## Funcionalidades

| Operación | Endpoint |
|---|---|
| Registrar solicitud (da de alta al estudiante si no existe) | `POST /solicitudes` |
| Buscar y filtrar solicitudes (estado, tipo, prioridad, legajo, responsable, vencidas) | `GET /solicitudes` |
| Consultar una solicitud | `GET /solicitudes/{id}` |
| Cambiar el estado | `PUT /solicitudes/{id}/estado` |
| Asignar responsable | `PUT /solicitudes/{id}/responsable` |
| Reporte resumen | `GET /reportes/resumen` |
| Listar estudiantes y responsables | `GET /estudiantes`, `GET /responsables` |

Los datos se guardan en `datos/solicitudes.json`. La primera vez que se ejecuta, el sistema carga datos de prueba **sintéticos** (no hay datos personales reales).

## Tecnologías

- Java 17
- Spring Boot 3.3 (Spring Web)
- Maven (incluye Maven Wrapper, no hace falta instalarlo)
- springdoc-openapi (Swagger UI)
- JUnit 5 y MockMvc para las pruebas

## Instalación y uso

Requisito: tener instalado un JDK 17 o superior.

Desde la carpeta del proyecto:

```powershell
# Windows (PowerShell)
.\mvnw.cmd spring-boot:run
```

```bash
# Linux / macOS
./mvnw spring-boot:run
```

La primera vez tarda porque descarga Maven y las dependencias. La aplicación está lista cuando aparece `Started SolicitudesApplication`. Para frenarla: `Ctrl+C`.

Con la aplicación levantada:

- Swagger UI: <http://localhost:8080/swagger-ui.html>
- Ejemplo: `curl http://localhost:8080/solicitudes?prioridad=ALTA`

Para volver a los datos de prueba iniciales, borrar la carpeta `datos/` antes de arrancar.

Ejecutar las pruebas:

```powershell
.\mvnw.cmd test      # Windows (PowerShell)
```

```bash
./mvnw test          # Linux / macOS
```

## Estructura del repositorio

```text
src/main/java/...        Código del sistema
src/test/java/...        Pruebas automatizadas (se agregan en el hito 3)
scripts/linea_base.ps1   Verificación del comportamiento externo antes/después
docs/                    Informe del TPI en Markdown (una sección por etapa)
docs/evidencias/         Capturas y salidas de ejecución
docs/07_code_review.md   Checklist y registro del code review
entrega/                 Informe PDF final y cómo se arma la entrega
```

## Documentación del trabajo

| Sección | Documento |
|---|---|
| Plan de trabajo y metodología | [docs/00_plan_de_trabajo.md](docs/00_plan_de_trabajo.md) |
| Portada y resumen ejecutivo | [docs/portada_y_resumen.md](docs/portada_y_resumen.md) |
| 1. Sistema base | [docs/01_sistema_base.md](docs/01_sistema_base.md) |
| 2. Calidad externa vs. calidad interna | [docs/02_calidad_externa_interna.md](docs/02_calidad_externa_interna.md) |
| 3. Deuda técnica y code smells | [docs/03_deuda_tecnica_code_smells.md](docs/03_deuda_tecnica_code_smells.md) |
| 4. SOLID y Clean Code | [docs/04_solid_clean_code.md](docs/04_solid_clean_code.md) |
| 5. Refactoring seguro | [docs/05_refactoring.md](docs/05_refactoring.md) |
| 6. Pruebas y verificación | [docs/06_pruebas.md](docs/06_pruebas.md) |
| 7. Code review | [docs/07_code_review.md](docs/07_code_review.md) |
| 8. Conclusiones | [docs/08_conclusiones.md](docs/08_conclusiones.md) |
| Uso de inteligencia artificial | [docs/09_uso_ia.md](docs/09_uso_ia.md) |
| Decisiones técnicas | [docs/10_decisiones_tecnicas.md](docs/10_decisiones_tecnicas.md) |
| 11. Anexos y enlaces | [docs/11_anexos_y_enlaces.md](docs/11_anexos_y_enlaces.md) |
| Armado del PDF y del ZIP | [entrega/README.md](entrega/README.md) |

## Enlaces

- Tablero de trabajo: <https://github.com/users/Alex-Dauria/projects/1>
- Video de defensa: _pendiente_
- Informe PDF: _pendiente_ (quedará en `entrega/`)

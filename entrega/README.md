# Armado de la entrega final

Esta carpeta guarda el **informe PDF final**. Las instrucciones de abajo explican cómo se arman el PDF y el ZIP a partir de los Markdown de `docs/`. Se hace una sola vez, al final, en el issue #17, cuando todas las secciones estén aprobadas.

La consigna considera entregado el TPI cuando están cargados los cuatro recursos:

| Recurso | Dónde queda | Issue |
|---|---|---|
| Informe PDF único | `entrega/TPI_MSII_Gestion_Solicitudes_Academicas.pdf` (en el repo) y en la plataforma | #17 |
| Repositorio GitHub | <https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes>, con los seis tags | #17 |
| Anexo ZIP | `Anexo_TPI_MSII_Gestion_Solicitudes_Academicas.zip`, solo en la plataforma (se genera desde `docs/evidencias/` y no se versiona) | #16 |
| Video | Enlace en el PDF, en el README y en la plataforma | #18 |

## 1. Contenido y orden del PDF

El PDF sigue la estructura sugerida por la consigna. Cada parte sale de un archivo de `docs/`:

| # | Parte del informe | Archivo de origen |
|---|---|---|
| 1 | Portada | `docs/portada_y_resumen.md` |
| 2 | Resumen ejecutivo (5 a 7 líneas) | `docs/portada_y_resumen.md` |
| 3 | Índice | Se genera al armar el PDF |
| 4 | Descripción del sistema base | `docs/01_sistema_base.md` |
| 5 | Calidad externa e interna | `docs/02_calidad_externa_interna.md` |
| 6 | Deuda técnica y code smells | `docs/03_deuda_tecnica_code_smells.md` |
| 7 | SOLID y Clean Code | `docs/04_solid_clean_code.md` |
| 8 | Refactorings con evidencia antes/después | `docs/05_refactoring.md` |
| 9 | Pruebas y verificación | `docs/06_pruebas.md` |
| 10 | Code review y checklist técnico | `docs/07_code_review.md` |
| 11 | Conclusiones (grupales, individuales y participación de cada integrante) | `docs/08_conclusiones.md` |
| 12 | Uso de IA y registro de prompts | `docs/09_uso_ia.md` |
| 13 | Anexos y enlaces (incluye las decisiones técnicas) | `docs/11_anexos_y_enlaces.md` y `docs/10_decisiones_tecnicas.md` |

`docs/00_plan_de_trabajo.md` **no va en el PDF**: es la organización interna del equipo. La participación de cada integrante se informa en la sección 8.3.

## 2. Cómo se convierte

Propuesta, a confirmar con una prueba en #17: la extensión **Markdown Preview Enhanced** de VS Code, que dibuja los diagramas Mermaid y exporta a PDF ("Chrome (Puppeteer)").

1. Unir los archivos en el orden de la tabla en un solo `entrega/informe.md`, con un salto de página entre secciones.
2. Revisar que se vean las imágenes, las tablas, los diagramas y los bloques de código.
3. Exportar a `entrega/TPI_MSII_Gestion_Solicitudes_Academicas.pdf`.
4. Revisar el PDF completo: portada, índice, enlaces que funcionen, nada cortado.

Las imágenes se referencian con rutas relativas a `docs/` (por ejemplo, `evidencias/etapa1/...`). Si `informe.md` se arma en `entrega/`, hay que ajustarlas a `../docs/evidencias/...`.

## 3. Contenido del ZIP

El ZIP se arma copiando `docs/evidencias/` y sumando lo que no está en el repo:

```text
Anexo_TPI_MSII_Gestion_Solicitudes_Academicas.zip
├── LEEME.txt                ← índice del contenido
├── etapa1/                  ← capturas del sistema base
├── etapa2/                  ← capturas del análisis de calidad externa
├── pruebas/                 ← líneas base y salidas de mvnw test, antes y después
├── code-review/             ← capturas de las revisiones en los PR
├── diagramas/               ← diagramas exportados, si hay
└── ia/                      ← evidencia de uso de IA: registros curados o conversaciones (IA-01, IA-02…)
```

Antes de subirlo: verificar que no haya datos personales reales.

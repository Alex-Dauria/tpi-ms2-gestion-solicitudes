# Armado de la entrega final

Esta carpeta explica cómo se armaron el informe PDF y el anexo ZIP a partir de los Markdown de `docs/`. Se hizo una sola vez, al final (issues #16 y #17), con todas las secciones aprobadas.

La consigna considera entregado el TPI cuando están cargados los cuatro recursos:

| Recurso | Dónde queda | Issue |
|---|---|---|
| Informe PDF único | En la plataforma | #17 |
| Repositorio GitHub | <https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes>, con los seis tags (más el alias `estadorefactorizado`) | #17 |
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
| 12 | Uso de IA y registro de prompts | `docs/09_uso_ia.md`, en forma resumida (herramienta, finalidad y grado de cada uso); la tabla completa y los registros van en el ZIP, carpeta `ia/` |
| 13 | Anexos y enlaces (incluye las decisiones técnicas) | `docs/11_anexos_y_enlaces.md` y `docs/10_decisiones_tecnicas.md` |

`docs/00_plan_de_trabajo.md` **no va en el PDF**: es la organización interna del equipo. La participación de cada integrante se informa en la sección 8.3.

## 2. Cómo se armó

1. Los archivos se unieron en el orden de la tabla en un documento Word, con los diagramas Mermaid exportados como imagen.
2. Para que el informe quede breve, cada sección resume su archivo y remite al repositorio para el detalle completo; las evidencias van en el ZIP.
3. El documento se terminó de editar fuera del repositorio y se exportó a PDF.
4. Se revisó el PDF completo contra la consigna: portada, resumen ejecutivo, índice, enlaces, tablas e imágenes.

## 3. Contenido del ZIP

El ZIP se arma al final, cuando ya están las capturas del code review (#14), copiando `docs/evidencias/` y sumando lo que no está en el repo:

```text
Anexo_TPI_MSII_Gestion_Solicitudes_Academicas.zip
├── LEEME.txt                ← índice del contenido
├── etapa1/                  ← capturas del sistema base
├── etapa2/                  ← capturas del análisis de calidad externa
├── pruebas/                 ← líneas base, salidas de mvnw test e informes surefire-reports, antes y después
├── code-review/             ← capturas de las revisiones en los PR
├── diagramas/               ← diagramas del informe exportados como imagen
└── ia/                      ← 09_uso_ia.md completo y los registros de los usos de IA
```

En los informes de pruebas, los nombres de usuario y las rutas de las computadoras se reemplazan por `[USUARIO]`; los resultados no cambian. Los enlaces de `ia/` se ajustan para que funcionen dentro del ZIP.

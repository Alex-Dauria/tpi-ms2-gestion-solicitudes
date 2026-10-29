# Evidencia de uso de IA — Diagnóstico de zona B

**Trabajo:** TPI Metodología de Sistemas II — Gestión de Solicitudes Académicas\
**Integrante:** Marina Giselle Cordero\
**Herramienta:** ChatGPT (OpenAI)\
**Etapa:** 3 — Deuda técnica y code smells (issue #4)\
**Fecha de trabajo:** 26/10/2026\
**Tipo de evidencia:** extracto curado de intercambios relevantes y síntesis identificadas como tales. **No es una transcripción íntegra del chat.**

## Objetivo y alcance

Analizar exclusivamente `SolicitudController.cambiarEstado`, `asignarResponsable`, `obtener` y `modelo/Solicitud` (zona B), reconocer debilidades de diseño, contrastar refactorings posibles y preparar fichas de diagnóstico sin modificar el código.

## Interacciones relevantes

### E-01 — Separación de responsabilidades
- **Solicitud realizada (resumen, no cita literal):** comenzar el diagnóstico de code smells de la zona B.
- **Orientación de la IA (síntesis):** señaló la lógica de transición de estados dentro del controlador como responsabilidad mal ubicada y propuso evaluar Move Method o reubicación en una capa adecuada.
- **Intervención de la estudiante (cita textual):** “porque tendriamos responsabiliadades mezcladas, deberiamos moverlo al service que se encargue de la logica del negocio y en el conrtoller dejar solo la comunicacion”.
- **Decisión:** registrar SB-01; considerar SRP como principio principal y OCP como una posible consecuencia del diseño, no algo garantizado por mover un método.

### E-02 — Duplicación de búsqueda por ID
- **Solicitud realizada (resumen):** continuar el diagnóstico con el siguiente problema de zona B.
- **Orientación de la IA (síntesis):** identificó búsquedas similares por ID en `obtener`, `cambiarEstado` y `asignarResponsable`, y contrastó Extract Method con reubicación en Repository.
- **Intervención de la estudiante (cita textual):** “no la mezcla de responsabilidades aun seguiria”.
- **Decisión:** registrar SB-02 y diferenciar eliminación de duplicación de separación de responsabilidades.

### E-03 — Estados como String y Parameter Object
- **Solicitud realizada (resumen):** analizar el uso de tipos primitivos para representar estados.
- **Orientación de la IA (síntesis):** propuso un enum para los estados y explicó que Introduce Parameter Object aborda listas de parámetros relacionados, no la representación de un estado.
- **Intervención de la estudiante (cita textual):** “eliminariamos la posibilidad de que si se ecribe de distintas maneras haya consecuencias como que no se detecte el estado o tener innumerables estados dificiles de filtrar. introduce parameter objets no corresponderia tambien?”.
- **Decisión:** registrar SB-03; evaluar enum en un refactoring posterior preservando API y JSON.

### E-04 — Encapsulamiento y compatibilidad de JSON
- **Solicitud realizada (resumen):** revisar si trasladar reglas al Service solucionaría la modificación arbitraria del estado.
- **Orientación de la IA (síntesis):** advirtió que dejar `setEstado` público permite omitir validaciones y que hay que preservar la lectura de JSON mediante Jackson.
- **Intervención de la estudiante (cita textual):** “no , porque se poria modificar el estado arbitrariamnete si tener en cuenta las reglas de negocio”.
- **Decisión:** registrar SB-04 y anotar compatibilidad de deserialización como precaución para el refactoring, no como cambio a realizar durante el diagnóstico.

### E-05 — Número mágico y revisión conjunta
- **Solicitud realizada (resumen):** continuar el análisis de la zona B y revisar las fichas antes de incorporarlas.
- **Orientación de la IA (síntesis):** señaló el límite `cant >= 5` en `asignarResponsable` y propuso una constante con nombre descriptivo. También revisó el posible solapamiento entre SB-01 y SB-04.
- **Decisión:** registrar SB-05; conservar cinco candidatos diferenciados y someterlos a revisión cruzada antes de incorporarlos al catálogo.

## Resultado del intercambio

Se prepararon borradores de cinco fichas: SB-01 responsabilidades mezcladas; SB-02 búsqueda duplicada; SB-03 Primitive Obsession; SB-04 encapsulamiento insuficiente; SB-05 número mágico. Se propusieron técnicas de refactoring pero **no se modificó el repositorio ni se ejecutaron refactorings**. La redacción de las fichas tuvo asistencia de IA; su validez requiere revisión del código y revisión cruzada por el equipo.

## Declaración de intervención

**Grado propuesto: medio** para el análisis y la selección conjunta de alternativas. La IA tuvo una intervención sustantiva en la redacción de las fichas; esto se declara expresamente y debe considerarse al completar la tabla del informe. La estudiante contrastó conceptos, formuló objeciones y tomó decisiones durante la revisión.

## Privacidad y alcance de la evidencia

Este documento contiene solamente contenido relacionado con el TPI y fragmentos seleccionados de las intervenciones. No contiene un enlace público a la conversación completa. Antes de subirlo al repositorio o al ZIP, revisar el texto y confirmar que se desea compartir cada cita. Las solicitudes identificadas como «resumen» no son citas literales.

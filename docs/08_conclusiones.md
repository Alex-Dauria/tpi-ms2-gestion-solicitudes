# 8. Conclusiones y aprendizaje

**Coordina:** Silvia · **Aportan:** las tres

## 8.1 Conclusiones grupales

Partimos de un prototipo que funcionaba, pero con casi todas las reglas concentradas en un controlador de 441 líneas. Detectamos 15 code smells, aplicamos 18 refactorings en tres zonas, incluidos los tres obligatorios, y verificamos que el comportamiento externo se conservó: 84 de 84 pruebas en verde y 14 de 14 casos de la línea base iguales. El sistema es más fácil de leer y de cambiar, aunque sigue teniendo deuda técnica que identificamos y dejamos documentada.

### Decisiones tomadas

- **Dividir el código en tres zonas**, con una responsable de punta a punta y revisión rotativa (DT-04). Así todas diagnosticamos, probamos y refactorizamos una parte real del sistema.
- **Escribir las pruebas antes de refactorizar** y comparar contra una línea base de caja negra (DT-05 y DT-06). Nos permitió cambiar la estructura con evidencia de que el comportamiento seguía igual.
- **Hacer un commit por refactoring y mergear sin squash**, para que cada cambio se pueda seguir en el historial.
- **Conservar los defectos encontrados**, como el `500` del legajo numérico, y registrarlos como hallazgos: corregirlos habría sido cambiar el comportamiento, no refactorizar.
- **Trabajar las zonas B y C en paralelo** cuando el orden A → B → C ya no entraba en los tiempos (DT-08), repitiendo las pruebas y la línea base al integrar.

### Dificultades

- **Los tiempos:** el cronograma se atrasó, las revisiones cruzadas se acumularon y una tarea bloqueaba a la siguiente. Tuvimos que replanificar sin reducir el alcance.
- **La integración:** las tres zonas tocaban el mismo controlador y los mismos documentos, lo que generó conflictos que hubo que resolver y volver a verificar.
- **Probar de verdad:** que las pruebas pasen no alcanza. Al introducir errores a propósito en la zona C, las primeras 12 pruebas no detectaron ninguno de 6. Las reforzamos hasta llegar a 21, que detectan 7 de 7.
- **No cambiar nada sin querer:** había que conservar el orden de las validaciones, los códigos HTTP y los mensajes, incluso donde el código original era discutible.

### Límites del trabajo

- El sistema es un prototipo: API REST sin interfaz gráfica ni autenticación, persistencia en un archivo JSON y datos sintéticos.
- Las pruebas verifican el comportamiento funcional de la API. No medimos rendimiento ni concurrencia.
- El refactoring mejoró la estructura sin eliminar el problema de fondo: el controlador bajó de 441 a 361 líneas, pero todavía concentra el filtrado y el reporte.
- Parte del trabajo se hizo con apoyo de IA, declarado en la sección 9 con su grado de intervención.

### Mejoras pendientes

- Separar el estado y la persistencia de `BaseDeDatos` detrás de un repositorio con interfaz (S-06), la deuda más importante y la que permitiría mejorar DIP.
- Representar estados, tipos y prioridades con `enum` en lugar de texto (S-08 y S-10).
- Terminar de proteger el estado de `Solicitud`, que todavía tiene `setEstado()` público (S-04), y dejar RN-08 y RN-09 en un solo lugar.
- Dividir los métodos que siguen siendo largos: `filtrar`, `resumen` y `asignarResponsable`.
- Corregir el `500` del legajo numérico como cambio intencional de comportamiento.

### Relación con los contenidos de la asignatura

Trabajamos la diferencia entre calidad externa e interna con ISO/IEC 25010: el sistema podía responder bien y aun así ser caro de mantener. Usamos el catálogo de code smells y el cuadrante de deuda técnica para diagnosticar y priorizar con criterio. Aplicamos refactoring seguro con pruebas de caracterización, y evaluamos SOLID y Clean Code indicando también dónde la mejora fue parcial o no correspondía. Por último, el code review con checklist nos dio criterios comunes para aceptar o pedir cambios.

## 8.2 Conclusiones individuales

### Alex Pedro Dauria

Me ocupé de la zona de registro, de coordinar el repositorio, del sistema base y de consolidar la sección de SOLID. Lo que más me sirvió fue aprender a refactorizar sin romper nada: primero preparé pruebas que fijaran cómo se comportaba el código, y después fui cambiando de a un paso hasta reducir `registrarSolicitud` de 105 a 33 líneas, sin alterar lo que hacía por fuera. También entendí que a veces lo correcto es dejar un defecto como está, como el `500` que aparece con el legajo numérico, porque arreglarlo ya no es refactorizar, es cambiar el comportamiento. Lo más difícil fue coordinar los tiempos y juntar el trabajo de las tres zonas. Usé la IA como apoyo en gran parte de mi trabajo, y aprendí que cada resultado hay que revisarlo y entenderlo antes de darlo por bueno.

### Silvia Giardini

Me ocupé del diagnóstico, las pruebas y el refactoring de la zona C (consultas y persistencia), del checklist de code review y de consolidar la sección de pruebas. En este proceso aprendí que refactorizar no es solo ordenar el código, sino cambiarlo basándose en evidencia: usar pruebas de caracterización y una línea base me dio la seguridad para saber qué optimizar y qué dejar intacto sin romper nada. También entendí lo importante que es dejar registrada la deuda técnica que queda pendiente —como la de BaseDeDatos— para que el próximo que trabaje en el código sepa por dónde empezar. La IA me ayudó a orientarme, a revisar alternativas y a destrabar puntos en los que me encontraba bloqueada.

### Marina Giselle Cordero

En este trabajo me ocupé del diagnóstico, las pruebas y los refactorings de la zona B, además de participar en la documentación y las revisiones del equipo. Aprendí que mejorar el código no significa solamente hacerlo más legible, sino también asegurar que siga funcionando de la misma manera. Las pruebas de caracterización me permitieron realizar cambios pequeños y comprobar sus resultados. También comprendí que no todos los code smells detectados deben refactorizarse: es necesario evaluar su impacto, prioridad, riesgos y costo antes de decidir qué mejorar y qué dejar documentado como deuda técnica pendiente. El trabajo colaborativo me enseñó la importancia de las ramas, los commits y las revisiones cruzadas, así como la necesidad de coordinar los cambios y los tiempos. Por último, la IA me ayudó a analizar alternativas y desarrollar soluciones, pero aprendí que sus propuestas deben verificarse y que las decisiones finales son responsabilidad nuestra.

## 8.3 Participación de cada integrante

| Integrante | Etapas y tareas a cargo | Revisiones realizadas | Commits / PR principales |
|---|---|---|---|
| Alex Pedro Dauria | Sistema base y plan (#1), diagnóstico y refactoring de la zona A (#3 y #8), SOLID y Clean Code (#11), uso de IA y ZIP (#16), revisión integral y PDF (#17), coordinación del video (#18) | PR #21, #22, #24, #27, #30, #31, #32, #33, #38, #39, #40 y #44 | PR #19, #20, #25, #26, #28, #35, #42, #43 y #45 |
| Silvia Giardini | Calidad externa e interna (#2), diagnóstico y refactoring de la zona C (#5 y #10), checklist de code review (#7), pruebas (#13), conclusiones (#15) | PR #19, #20, #23, #25, #26, #28, #30, #33, #41, #43, #44, #45, #46 y #47 | PR #21, #22, #27, #31, #38, #39 y #40 |
| Marina Giselle Cordero | Diagnóstico y refactoring de la zona B (#4 y #9), catálogo de deuda técnica (#6), documento de refactorings (#12), code review final (#14), verificación final y anexos en la revisión integral (#17) | PR #19, #20, #25, #26, #28, #35, #42 y #43 | PR #23, #24, #30, #33, #41, #44, #46 y #47; commits propios en el PR #45 |

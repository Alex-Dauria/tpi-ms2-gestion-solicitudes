# Decisiones técnicas

Registro breve de las decisiones que condicionan el trabajo. Cada una indica el contexto, lo que se decidió y por qué. Si una decisión cambia, se agrega una nueva entrada que la reemplaza, en lugar de borrar la anterior.

## DT-01 – Caso de trabajo: Gestión de Solicitudes Académicas

- **Contexto:** la consigna sugiere este caso y admite otros dominios equivalentes.
- **Decisión:** usar el caso sugerido.
- **Motivo:** es un dominio que conocemos como estudiantes, y sus reglas (estados, prioridades, vencimientos) dan lugar a problemas de diseño concretos. Además evita tener que justificar ante la cátedra un dominio alternativo.

## DT-02 – Tecnología: Java 17 + Spring Boot, API REST sin interfaz gráfica

- **Contexto:** la consigna no exige una tecnología. Buscamos una que las tres manejemos y que permita mostrar SOLID con claridad.
- **Decisión:** Spring Boot 3.3 con Java 17 y Maven; API REST probada con Swagger UI; sin frontend.
- **Motivo:** Spring Boot es lo que vimos en Programación (unidades 1 a 3) y lo que usamos en el Trabajo Final. Las capas controlador/servicio/repositorio y la inyección de dependencias hacen visibles SRP y DIP. Un frontend agregaría trabajo sin sumar a lo que se evalúa: la consigna aclara que la funcionalidad visible puede ser simple.

## DT-03 – Persistencia en archivo JSON

- **Contexto:** la consigna pide persistir o simular datos (archivo, base de datos o memoria).
- **Decisión:** archivo `datos/solicitudes.json`, leído y escrito con Jackson.
- **Motivo:** no requiere instalar nada, se puede inspeccionar a mano y reiniciar borrando la carpeta.

## DT-04 – División del código en tres zonas con dueña

- **Contexto:** hay que repartir el trabajo de forma pareja y la cátedra puede pedir defensa individual.
- **Decisión:** zonas A (registro), B (ciclo de vida) y C (consultas y persistencia), cada una con una responsable de punta a punta (ver el plan de trabajo).
- **Motivo:** cada integrante puede defender un recorrido completo: diagnóstico, pruebas, refactoring y documentación.

## DT-05 – Pruebas de caracterización antes de refactorizar y refactorings en secuencia

- **Contexto:** el refactoring tiene que ser seguro y las tres zonas modifican el mismo controlador.
- **Decisión:** primero se escriben las pruebas de cada zona sobre la versión inicial (pueden hacerse en paralelo); después se refactoriza en orden A → B → C, con un commit por refactoring y merge sin squash.
- **Motivo:** las pruebas son la red de seguridad que demuestra que no cambió el comportamiento, y la secuencia evita conflictos de merge.

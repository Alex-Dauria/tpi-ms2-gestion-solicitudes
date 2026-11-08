<!-- Portada y resumen ejecutivo del informe. Se completa en el issue #17. -->

# Evolución profesional de un sistema existente: Sistema de Gestión de Solicitudes Académicas

**Trabajo Práctico Integrador – Metodología de Sistemas II**

Tecnicatura Universitaria en Programación a Distancia – Universidad Tecnológica Nacional

| Integrante | Comisión |
|---|---|
| Alex Pedro Dauria | 07 |
| Silvia Giardini | 12 |
| Marina Giselle Cordero | 07 |

| | |
|---|---|
| **Fecha** | 8 de noviembre de 2026 |
| **Repositorio** | <https://github.com/Alex-Dauria/tpi-ms2-gestion-solicitudes> |
| **Video** | _completar_ |

---

## Resumen ejecutivo

Analizamos un Sistema de Gestión de Solicitudes Académicas, una API REST en Java 17 con Spring Boot y persistencia en JSON. Funcionaba, pero su calidad interna era baja: un controlador de 441 líneas concentraba validaciones, transiciones de estado, vencimientos y reportes, sobre un estado global estático. Detectamos 15 code smells; los más importantes fueron el método largo del registro, las respuestas de error duplicadas, la lógica de transiciones en el controlador y la regla de vencimiento duplicada. Aplicamos 18 refactorings, entre ellos Extract Method, Move Method e Introduce Parameter Object, orientados a SRP, DRY y Clean Code. Verificamos que el comportamiento externo no cambiara con pruebas de caracterización (69 de 69 antes y 84 de 84 después) y con una línea base de caja negra: 14 de 14 casos iguales. El sistema quedó más fácil de leer y de cambiar con el mismo comportamiento, y la deuda que queda, sobre todo el estado global de `BaseDeDatos`, está identificada.

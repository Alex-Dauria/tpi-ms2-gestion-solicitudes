# 2. Calidad externa vs. calidad interna

**Responsable:** Silvia · **Revisión cruzada:** Alex

> Plantilla: reemplazar las indicaciones en cursiva por el contenido. Como marco se puede usar el modelo de calidad ISO/IEC 25010, que separa las características que percibe el usuario de las que solo ve el equipo técnico.

## 2.1 Calidad externa: lo que percibe el usuario

_Recorrer el sistema como usuario (Swagger UI o `curl`) y registrar qué funciona correctamente. Usar como guía las funcionalidades F1–F7 y las reglas RN-01 a RN-15 de la sección 1._

| Característica (ISO 25010) | Qué se observó | Evidencia |
|---|---|---|
| Adecuación funcional | | |
| Fiabilidad / manejo de errores | | |
| Usabilidad de la API (mensajes, códigos HTTP) | | |
| Eficiencia | | |

_Conclusión parcial: ¿el sistema cumple lo que promete desde afuera?_

## 2.2 Calidad interna: lo que ve el equipo técnico

_Leer el código de la versión inicial (tag `estado-inicial`) y describir su estructura desde la mantenibilidad. No hace falta catalogar todos los smells: eso es la sección 3. Acá se busca la mirada general._

| Subcaracterística de mantenibilidad | Qué se observó | Ejemplo concreto (archivo / método) |
|---|---|---|
| Modularidad | | |
| Reusabilidad | | |
| Analizabilidad (legibilidad) | | |
| Modificabilidad | | |
| Capacidad de ser probado | | |

## 2.3 Contraste y relación con la evolución futura

_Explicar por qué un sistema puede funcionar bien desde afuera y aun así ser costoso de mantener. Tomar 2 o 3 cambios probables (por ejemplo: agregar un tipo de solicitud, cambiar el plazo de vencimiento de una prioridad, pasar de archivo JSON a base de datos) y estimar qué partes del código habría que tocar hoy para cada uno._

| Cambio probable | Partes del código afectadas hoy | Riesgo |
|---|---|---|
| | | |

## 2.4 Conclusión

_5 a 8 líneas que conecten con la sección 3 (deuda técnica)._

# Plan de Implementación: Sistema Militar (Java 21 / Spring Boot)

## 🎯 Objetivo General del Proyecto

Modelar el dominio de los ejércitos militares para permitir su creación, entrenamiento, transformación y simulación en batallas. El sistema debe adherirse a la arquitectura hexagonal y operar sin persistencia de datos, exponiendo sus funcionalidades únicamente vía una API REST bien documentada (Swagger/OpenAPI).

## 🛠️ Stack Tecnológico Requerido

- Lenguaje: Java 21
- Framework: Spring Boot
- Testing: JUnit
- Documentación API: Swagger/OpenAPI
- Containerización: Docker

## � Alcance y Alineación con el PRD

Este plan está orientado a cubrir de forma incremental todos los requisitos funcionales y no funcionales del PRD, manteniendo una separación estricta entre dominio, puertos y adaptadores. Dado que el PRD habla de un sistema orientado a API REST sin persistencia, el trabajo se concentra en un microservicio hexagonal modular, con posibilidad de extenderlo posteriormente a almacenamiento real o interfaces adicionales.

### Cobertura prevista frente al PRD

- Crear ejército con civilización: cubierto en las Fases 0, 1 y 2.
- Consultar estado del ejército: cubierto en la Fase 2 y expuesto en la Fase 3.
- Entrenar y transformar unidades: cubierto en las Fases 1 y 2.
- Simular batallas y registrar historial: cubierto en las Fases 1, 2 y 3.
- Arquitectura hexagonal: cubierto en las Fases 0, 2 y 3.
- Documentación Swagger: cubierto en la Fase 3.
- Tests unitarios, integración y funcionales: cubierto en la Fase 4.

## 🗺️ Estructura del Plan de Trabajo (Fases)

### Fase 0: Configuración y Estructura Inicial (Setup)

**Objetivo:** Establecer el entorno de desarrollo y la estructura de paquetes que refleje la Arquitectura Hexagonal.
**Dependencias:** Ninguna.
**Salida Esperada (Milestone):** Proyecto Spring Boot inicializado con las capas de arquitectura definidas.

| Paso    | Tarea Detallada                         | Descripción Técnica                                                                                                                 | Criterio de Aceptación                                                           |
| :------ | :-------------------------------------- | :---------------------------------------------------------------------------------------------------------------------------------- | :------------------------------------------------------------------------------- |
| **0.1** | Inicializar Proyecto Spring Boot        | Crear el proyecto utilizando Java 21 y añadir dependencias clave (Spring Web, Lombok, JUnit, Springdoc OpenAPI, etc.).              | `pom.xml` o `build.gradle` configurado correctamente.                            |
| **0.2** | Definición de la Arquitectura Hexagonal | Crear los paquetes lógicos: `domain`, `ports`, `adapters/in`, `adapters/out`. Esto es crucial para la separación de preocupaciones. | Estructura de directorios limpia y lógica (Core → Ports → Adapters).             |
| **0.3** | Modelo Base Inicial                     | Crear clases básicas `Army` y `Unit` en el paquete `domain`, sin implementar reglas aún, solo atributos.                            | Las entidades principales existen y están listas para recibir lógica de negocio. |

---

### Fase 1: Modelado del Dominio (Core Domain)

**Objetivo:** Implementar las estructuras de datos completas, constantes de negocio y la lógica inmutable que define el estado de un ejército y sus unidades. Esta es la base fundamental.
**Dependencias:** Estructura de proyecto de la Fase 0.
**Salida Esperada (Milestone):** Dominio modelado con todas las constancias de reglas (puntos, costes iniciales).

#### Tareas Específicas:

1. **Constantes y Configuraciones:**
   - Definir `CIVILIZATION_STATS`: Mapa o clase que contenga los valores iniciales para Chinos, Ingleses y Bizantinos.
   - Definir `UNIT_POINTS` y `INITIAL_GOLD`: Constantes (Piquero 5 pts, etc.; Oro inicial = 1000).
   - Definir `TRAINING_COSTS` y `BENEFITS`: Mapa de unidad → (Costo Gold, Puntos Ganados).
   - Definir `TRANSFORMATION_RULES`: Mapa o tabla que mapee (Origen, Destino) a Costo (Ej: Piquero → Arquero = 30g).

2. **Clase Unidad (`Unit`):**
   - Implementar el cálculo de puntos base y puntos potenciados por entrenamiento.
   - Métodos para aplicar entrenamientos y obtener nuevos puntos/costos.

3. **Clase Ejército (`Army`):**
   - Atributos: ID, Civilización, Lista de Unidades (o un contador por tipo), Oro actual, Historial de Batallas (Lista<String>).
   - Método `create()`: Debe calcular el estado inicial basándose en la civilización proporcionada y establecer 1000 monedas de oro.

4. **Gestión del Estado:** Implementar las utilidades para manipular listas de unidades, asegurando que cualquier modificación mantenga la integridad del dominio (ej. verificar que el dinero no sea negativo).

---

### Fase 2: Casos de Uso y Lógica de Negocio (Use Cases / Ports)

**Objetivo:** Implementar los puertos (`ports`) como interfaces en Java, definiendo cómo interactúan las capas superiores con el dominio sin saber la implementación específica. Se simulan los servicios de negocio.
**Dependencias:** Modelo de Dominio completamente funcional (Fase 1).
**Salida Esperada (Milestone):** Interfaces de servicio definidas, contratos claros y pruebas unitarias para cada caso de uso críticos pasadas.

| Paso    | Caso de Uso (Puerto)                    | Lógica a Implementar                                                                                                                 | Dependencias Clave                        | Validación / Pruebas Unitarias                                                        |
| :------ | :-------------------------------------- | :----------------------------------------------------------------------------------------------------------------------------------- | :---------------------------------------- | :------------------------------------------------------------------------------------ |
| **2.1** | `ArmyServicePort.trainUnit(...)`        | Verificar costo de entrenamiento, descontar oro, actualizar puntos y estado de la unidad.                                            | `TRAINING_COSTS`, `Army` (oro).           | Test con saldo insuficiente; Test exitoso de aumento de puntos.                       |
| **2.2** | `ArmyServicePort.transformUnit(...)`    | Verificar costo de transformación, descontar oro y reemplazar la unidad por el tipo destino según reglas.                            | `TRANSFORMATION_RULES`, `Army` (oro).     | Test con regla inexistente; Test exitoso de conversión de Piquero → Arquero.          |
| **2.3** | `BattleServicePort.simulateBattle(...)` | Calcular puntaje total, determinar ganador/perdedor/empate, aplicar consecuencias (oro y pérdida de unidades) y registrar historial. | `Unit` (Puntos), `Army` (Oro, Historial). | Test de victoria clara; Test de empate (criterio programador); Test de derrota total. |
| **2.4** | `ArmyServicePort.getArmyState(...)`     | Exponer el estado actual del ejército incluyendo oro, unidades y historial.                                                          | Ninguna lógica compleja, solo lectura.    | Verificación de que todos los atributos se recuperan correctamente.                   |

#### Contratos de entrada/salida recomendados

- DTOs de request: `CreateArmyRequest`, `TrainUnitRequest`, `TransformUnitRequest`, `BattleRequest`.
- DTOs de response: `ArmyResponse`, `BattleResultResponse`.
- Los puertos deben trabajar con estos DTOs o con modelos de dominio ya adaptados, sin depender de Spring o HTTP.

---

### Fase 3: Adaptadores y Exposición de API (Adapters In & Out)

**Objetivo:** Crear la capa REST (`adapters/in`) que consuma los puertos definidos en la Fase 2 y exponer todas las funcionalidades a través de endpoints HTTP. Implementar el adaptador de almacenamiento en memoria.
**Dependencias:** Puertos de servicio implementados (Fase 2).
**Salida Esperada (Milestone):** Endpoints REST funcionando, documentados con Swagger.

| Paso    | Componente                    | Tarea Detallada                                                                                                                                                            | Detalles Técnicos                                                                                | Criterios de Aceptación                                                                      |
| :------ | :---------------------------- | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | :----------------------------------------------------------------------------------------------- | :------------------------------------------------------------------------------------------- |
| **3.1** | `ArmyRepositoryAdapter` (Out) | Implementar el almacenamiento en memoria. Usar un `Map<String, Army>` para simular la persistencia y permitir que los servicios lean/escriban estados de ejércitos por ID. | Debe ser fácilmente reemplazable por una implementación JPA más adelante.                        | Crear, buscar y actualizar un objeto `Army` sin fallos transaccionales simulados.            |
| **3.2** | `ArmyController` (In)         | Implementar el controlador REST principal (`@RestController`). Mapear los endpoints requeridos.                                                                            | Utilizar `@RequestMapping`, `@PathVariable`, `@RequestBody` y manejar errores HTTP (400/404).    | Endpoints para GET /armies/{id}, POST /armies/{id}/train, etc., están activos.               |
| **3.3** | Documentación OpenAPI/Swagger | Integrar la documentación completa de la API usando `@Operation`, `@ApiResponses` y anotaciones de esquema.                                                                | Debe documentar parámetros, body y respuestas esperadas para todos los casos de uso principales. | Al acceder a `/swagger-ui.html`, se visualizan todos los endpoints con ejemplos funcionales. |

#### Endpoints REST esperados

| Método | Endpoint                 | Descripción                              |
| :----- | :----------------------- | :--------------------------------------- |
| `POST` | `/armies`                | Crear un ejército con civilización.      |
| `GET`  | `/armies/{id}`           | Consultar estado del ejército.           |
| `POST` | `/armies/{id}/train`     | Entrenar una unidad.                     |
| `POST` | `/armies/{id}/transform` | Transformar una unidad.                  |
| `POST` | `/battle`                | Simular una batalla entre dos ejércitos. |

---

### Fase 4: Pruebas, Documentación Final y Despliegue

**Objetivo:** Asegurar la calidad del código, completar las pruebas de integración/funcional y preparar el artefacto ejecutable para un entorno contenedorizado.
**Dependencias:** Fases 1, 2 y 3 completadas y funcionando.
**Salida Esperada (Milestone):** Código base estable, cubierto por tests unitarios, integración y funcionales, listo para Dockerización.

| Paso    | Tipo de Prueba              | Objetivo                                                                                          | Acción Principal                                                                    | Entregable/Resultado                                                          |
| :------ | :-------------------------- | :------------------------------------------------------------------------------------------------ | :---------------------------------------------------------------------------------- | :---------------------------------------------------------------------------- |
| **4.1** | Test Unitarios (JUnit)      | Validar la lógica pura del dominio (`Unit`, `Army`).                                              | Crear tests para cada método complejo dentro del paquete `domain`.                  | Cobertura unitaria mínima requerida (e.g., 85%) en el dominio.                |
| **4.2** | Test de Integración (JUnit) | Verificar la comunicación entre la capa Service y la capa Adapter.                                | Simular peticiones completas desde el servicio hasta el adaptador en memoria.       | La ejecución completa de un caso de uso (ej. Batalla) se replica sin errores. |
| **4.3** | Test Funcionales            | Validar el comportamiento completo de la API REST desde el controlador hasta las respuestas HTTP. | Ejecutar pruebas contra los endpoints reales con datos de ejemplo.                  | Se valida que los endpoints cumplen el flujo esperado del PRD.                |
| **4.4** | Revisión de Código y Estilo | Asegurar la adhesión a los estándares de Java 21, clean code y consistencia de nomenclatura.      | Refactorización y revisión manual por pares.                                        | Código base limpio, con comentarios detallados sobre decisiones de diseño.    |
| **4.5** | Dockerización               | Crear el archivo `Dockerfile` necesario para empaquetar la aplicación Spring Boot.                | Compilar un JAR ejecutable y definir el comando de ejecución dentro del contenedor. | El servicio se levanta exitosamente con `docker run ...`.                     |

---

## ⚠️ Consideraciones, Riesgos y Asunciones

### A. Suposiciones (Assumptions)

1. **Implementación de Desempate:** Se asume que la regla de empate en batalla será decidida por el programador, y se debe implementar una lógica simple (ej. ambos pierden 1 unidad aleatoria o vuelven a un estado predeterminado). Debe ser justificado.
2. **Atomicidad:** Dado que es un sistema sin persistencia, todas las operaciones de negocio deben tratarse como transacciones atómicas dentro del `AdapterOut` (Ej: el descuento de oro y la pérdida de unidades en una batalla ocurren o no ocurren juntas).

### B. Riesgos Identificados (Risks)

1. **Riesgo Arquitectónico:** Mezclar lógica de negocio con detalles de infraestructura REST.
   - Mitigación: Adherirse rigurosamente a que el paquete `domain` y `ports` sean completamente independientes de Spring/HTTP.
2. **Complejidad del Estado:** Manejar la mutabilidad (entrenamiento, transformación) puede llevar a inconsistencias si no se gestiona cuidadosamente qué unidad es modificada.
   - Mitigación: Implementar el concepto de valor inmutable o patrones de dominio en las unidades; cualquier cambio debe generar una nueva versión del estado (`Army` → `NewArmyState`).

### C. Checkpoints y Métrica de Éxito

- **Check 1 (Fase 1):** ¿Puedo crear un ejército con los datos iniciales correctos usando solo clases POJO? (Verificación de la lógica de civilización).
- **Check 2 (Fase 2):** Si ejecuto `simulateBattle`, ¿el estado final del dinero y las unidades es matemáticamente correcto según las reglas definidas, sin consultar la capa REST? (Validación del dominio puro).
- **Check 3 (Fase 4):** ¿Puedo invocar todos los casos de uso principales mediante el endpoint HTTP sin escribir código en el servicio o dominio para manejar esa llamada específica? (Prueba de integración exitosa).

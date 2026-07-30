# 🛡️ Sistema de Simulación Militar (Military War Simulation System)

[![Java Version](https://img.shields.io/badge/java-21-blue)](https://www.oracle.com/java/technologies/javase/jdk-21-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-green)](https://spring.io/projects/spring-boot)
[![Swagger](https://img.shields.io/badge/Swagger-OpenAPI-orange)](http://localhost:8080/swagger-ui/index.html)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## 📜 Descripción del Proyecto

Este proyecto implementa una simulación militar en memoria basada en una arquitectura hexagonal. Su propósito es modelar el dominio de un ejército, cubrir reglas de negocio como entrenamiento, transformación de unidades, cálculo de puntos y simulación de batallas, todo expuesto a través de una API REST documentada con OpenAPI/Swagger.

El sistema funciona sin base de datos y guarda los estados de los ejércitos en memoria, lo que lo hace ideal como base para evolucionar hacia persistencia real, servicios externos o más reglas de negocio.

## 🚀 Características Principales

- Modelado del dominio con clases de negocio para ejércitos y unidades, sin dependencias de Spring.
- Reglas de entrenamiento y transformación de unidades con costos en oro.
- Simulación de batalla con cálculo de victoria, derrota o empate, incluyendo recompensas y bajas.
- Historial de batallas por ejército.
- Arquitectura hexagonal con puertos de entrada y salida, y cableado explícito de beans.
- API REST documentada y lista para ejecutarse localmente.
- Soporte para despliegue local con Docker y Docker Compose.

## 🛠️ Stack Tecnológico

| Componente        | Tecnología                   | Propósito                              |
| :---------------- | :--------------------------- | :------------------------------------- |
| Backend           | Java 21                      | Lenguaje principal                     |
| Framework         | Spring Boot 4.1.0            | Desarrollo de la API REST              |
| Arquitectura      | Hexagonal (Ports & Adapters) | Separación entre dominio y adaptadores |
| Logging           | Log4j2                       | Trazas de aplicación y dominio         |
| Testing           | JUnit 5 / Spring Boot Test   | Pruebas unitarias y de integración     |
| Documentación API | springdoc-openapi 2.8.0      | Swagger UI / OpenAPI                   |
| Build             | Maven + Maven Wrapper        | Compilación y empaquetado              |
| Contenerización   | Docker / Docker Compose      | Despliegue local reproducible          |

## 🏗️ Arquitectura del Proyecto

El proyecto organiza su código en capas para mantener el dominio independiente de la infraestructura:

- **Domain**: contiene las reglas y entidades del negocio (`Army`, `Unit`). No conoce Spring ni HTTP.
- **Application**: implementa los casos de uso, define los puertos de entrada/salida y expone los DTO del contrato.
- **Adapters**: incluye el adaptador REST de entrada y el repositorio en memoria de salida.
- **Configuration**: declara los beans y conecta los servicios de aplicación con el adaptador de persistencia.

### Estructura principal

```text
src/main/java/com/kala/military/
├── MilitaryApplication.java
├── adapters/
│   ├── in/rest/
│   │   ├── ArmyController.java
│   │   └── GlobalExceptionHandler.java
│   └── out/inmemory/
│       └── InMemoryArmyRepository.java
├── application/
│   ├── dto/
│   │   ├── ArmyResponse.java
│   │   ├── BattleRequest.java
│   │   ├── BattleResultResponse.java
│   │   ├── CreateArmyRequest.java
│   │   ├── TrainUnitRequest.java
│   │   ├── TransformUnitRequest.java
│   │   └── UnitResponse.java
│   ├── ports/
│   │   ├── in/
│   │   │   ├── ArmyUseCasePort.java
│   │   │   └── BattleUseCasePort.java
│   │   └── out/
│   │       └── ArmyRepositoryPort.java
│   └── services/
│       ├── ArmyApplicationService.java
│       └── BattleApplicationService.java
├── configuration/
│   └── BeanConfiguration.java
└── domain/
    ├── Army.java
    └── Unit.java

src/main/resources/
├── application.properties
├── application-docker.properties
└── log4j2.xml
```

## 📐 Reglas de Negocio

### Civilizaciones soportadas

El campo `civilization` acepta el nombre en inglés o en español y se normaliza al valor en inglés:

| Entrada aceptada        | Valor normalizado |
| :---------------------- | :---------------- |
| `china`, `chinos`       | `china`           |
| `english`, `ingleses`   | `english`         |
| `byzantine`, `bizantinos` | `byzantine`     |

Cualquier otro valor (o vacío) produce un error `400`. Todo ejército nuevo inicia con **1000 de oro** y tres unidades: Piquero (5 pts), Arquero (8 pts) y Caballero (12 pts).

### Entrenamiento

Entrenar una unidad suma **+5 puntos** e incrementa su contador de entrenamientos. El costo depende del tipo:

| Unidad     | Costo de entrenamiento |
| :--------- | :--------------------- |
| Piquero    | 30                     |
| Arquero    | 40                     |
| Caballero  | 50                     |

Si el ejército no tiene la unidad o no tiene oro suficiente, la operación se rechaza con `400`.

### Transformación

La transformación cuesta **30 de oro** y solo admite el ciclo definido:

```text
Piquero → Arquero → Caballero → Piquero
```

La unidad resultante conserva el mayor valor entre sus puntos actuales y los puntos base del tipo destino, y su contador de entrenamientos se reinicia. Cualquier otra combinación se rechaza con `400`.

### Batalla

Se comparan los puntos totales de ambos ejércitos:

- **Victoria / derrota**: el ganador recibe **+100 de oro**; el perdedor pierde su unidad más débil y **-50 de oro**.
- **Empate**: ambos ejércitos pierden su unidad más débil y no hay cambios de oro.

En todos los casos el resultado queda registrado en el `battleHistory` de ambos ejércitos.

## ⚙️ Requisitos Previos

- Java 21
- Maven 3.9+ (opcional: el repositorio incluye `mvnw` / `mvnw.cmd`)
- Docker Desktop o Docker Engine (solo para el despliegue con contenedores)
- Docker Compose

## ▶️ Ejecutar Localmente sin Docker

### 1. Clonar el repositorio

```bash
git clone <repository-url>
cd military-war
```

### 2. Compilar y ejecutar

```bash
mvn clean package -DskipTests
java -jar target/military-0.0.1-SNAPSHOT.jar
```

O directamente con el wrapper, sin empaquetar:

```bash
./mvnw spring-boot:run     # Linux / macOS
mvnw.cmd spring-boot:run   # Windows
```

### 3. Acceder a la API

- API base: http://localhost:8080/api/v1
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## 🐳 Despliegue Local con Docker

> El `Dockerfile` copia el JAR ya construido desde `target/`, por lo que **es obligatorio empaquetar antes** de construir la imagen.

### Construir y levantar el contenedor

```bash
mvn clean package -DskipTests
docker compose up --build
```

### Detener el contenedor

```bash
docker compose down
```

### Verificar que el servicio esté activo

```bash
curl -X POST http://localhost:8080/api/v1/armies \
  -H "Content-Type: application/json" \
  -d '{"civilization":"chinos"}'
```

> El contenedor expone el puerto 8080 y activa el perfil `docker` mediante la variable `SPRING_PROFILES_ACTIVE` definida en `docker-compose.yml`.

## 🌐 Endpoints de la API

Todos los endpoints están bajo el prefijo:

```text
/api/v1
```

| Método | Ruta                          | Descripción                 | Éxito |
| :----- | :---------------------------- | :-------------------------- | :---- |
| POST   | `/api/v1/armies`              | Crear ejército              | `201` |
| GET    | `/api/v1/armies/{id}`         | Consultar estado            | `200` |
| POST   | `/api/v1/armies/{id}/train`   | Entrenar una unidad         | `200` |
| POST   | `/api/v1/armies/{id}/transform` | Transformar una unidad    | `200` |
| POST   | `/api/v1/battle`              | Simular una batalla         | `200` |

### 1. Crear ejército

- Método: POST
- Ruta: `/api/v1/armies`
- Descripción: crea un ejército nuevo con las unidades iniciales según la civilización.
- Respuesta: `201 Created`

#### Request example

```json
{
  "civilization": "chinos"
}
```

#### Response example

```json
{
  "id": "5f10db6e-3f79-4e4c-8ac6-58a1db7a6b8b",
  "civilization": "china",
  "gold": 1000,
  "units": [
    {
      "type": "Piquero",
      "points": 5,
      "trainingCount": 0
    },
    {
      "type": "Arquero",
      "points": 8,
      "trainingCount": 0
    },
    {
      "type": "Caballero",
      "points": 12,
      "trainingCount": 0
    }
  ],
  "battleHistory": []
}
```

### 2. Consultar estado del ejército

- Método: GET
- Ruta: `/api/v1/armies/{id}`
- Descripción: devuelve el estado actual del ejército, incluyendo oro, unidades y historial.

#### Request example

```text
GET /api/v1/armies/5f10db6e-3f79-4e4c-8ac6-58a1db7a6b8b
```

#### Response example

```json
{
  "id": "5f10db6e-3f79-4e4c-8ac6-58a1db7a6b8b",
  "civilization": "china",
  "gold": 1000,
  "units": [
    {
      "type": "Piquero",
      "points": 5,
      "trainingCount": 0
    }
  ],
  "battleHistory": []
}
```

### 3. Entrenar una unidad

- Método: POST
- Ruta: `/api/v1/armies/{id}/train`
- Descripción: entrena una unidad existente, suma 5 puntos y descuenta el costo en oro.

#### Request example

```json
{
  "unitType": "Piquero"
}
```

> El identificador del ejército se toma siempre del path; si el cuerpo incluye `armyId`, se ignora.

#### Response example

```json
{
  "id": "5f10db6e-3f79-4e4c-8ac6-58a1db7a6b8b",
  "civilization": "china",
  "gold": 970,
  "units": [
    {
      "type": "Piquero",
      "points": 10,
      "trainingCount": 1
    }
  ],
  "battleHistory": []
}
```

### 4. Transformar una unidad

- Método: POST
- Ruta: `/api/v1/armies/{id}/transform`
- Descripción: transforma una unidad siguiendo el ciclo permitido y descuenta 30 de oro.

#### Request example

```json
{
  "sourceType": "Piquero",
  "targetType": "Arquero"
}
```

#### Response example

```json
{
  "id": "5f10db6e-3f79-4e4c-8ac6-58a1db7a6b8b",
  "civilization": "china",
  "gold": 970,
  "units": [
    {
      "type": "Arquero",
      "points": 8,
      "trainingCount": 0
    }
  ],
  "battleHistory": []
}
```

### 5. Simular una batalla

- Método: POST
- Ruta: `/api/v1/battle`
- Descripción: compara los puntos totales de dos ejércitos y aplica el resultado a ambos.

#### Request example

```json
{
  "firstArmyId": "5f10db6e-3f79-4e4c-8ac6-58a1db7a6b8b",
  "secondArmyId": "6ac91bb8-2e4c-4dab-a6b8-5d3e7f121a02"
}
```

#### Response example

```json
{
  "result": "victory",
  "winnerId": "5f10db6e-3f79-4e4c-8ac6-58a1db7a6b8b",
  "loserId": "6ac91bb8-2e4c-4dab-a6b8-5d3e7f121a02",
  "summary": "Battle simulated successfully"
}
```

El campo `result` toma los valores `victory`, `defeat` o `draw`, siempre desde la perspectiva del `firstArmyId`. En caso de empate, `winnerId` y `loserId` se devuelven como `null`.

## ⚠️ Manejo de Errores

Los errores de negocio se traducen a `400 Bad Request` mediante `GlobalExceptionHandler`, con el siguiente formato:

```json
{
  "message": "Oro insuficiente"
}
```

Mensajes posibles: `Ejército no encontrado`, `Unidad no encontrada`, `Oro insuficiente`, `Civilización no soportada`, `La civilización es obligatoria`, `Regla de transformación no soportada`.

## 🧪 Pruebas

El proyecto incluye 13 pruebas repartidas en dominio, aplicación e integración REST:

| Clase                          | Nivel        |
| :----------------------------- | :----------- |
| `ArmyTest`, `UnitTest`         | Dominio      |
| `ArmyServiceTest`, `BattleServiceTest` | Aplicación |
| `ArmyControllerIntegrationTest` | Integración REST |
| `MilitaryApplicationTests`     | Contexto Spring |

```bash
mvn test
```

## 🔎 Observaciones de Diseño

- El estado del ejército se mantiene en memoria (`HashMap`) y se pierde al reiniciar el proceso.
- El repositorio en memoria no es thread-safe; es suficiente para el alcance actual pero debe revisarse ante concurrencia real.
- La capa de dominio no depende de Spring ni de HTTP, y los servicios de aplicación se instancian manualmente en `BeanConfiguration`.
- Los errores de negocio se traducen a respuestas HTTP 400 mediante un manejador global.
- El proyecto está preparado para evolucionar hacia persistencia real o integración con otros servicios sustituyendo el adaptador de salida.

## 📚 Documentación Adicional

La carpeta [docs/](docs/) contiene el enunciado del ejercicio, el PRD y el plan de trabajo del proyecto.

## 📌 Notas Finales

Este README documenta la implementación actual del proyecto, incluyendo las reglas de negocio, el flujo local, el despliegue con Docker y los contratos de entrada/salida de los endpoints.

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

- Modelado del dominio con clases de negocio para ejércitos y unidades.
- Reglas de entrenamiento y transformación de unidades.
- Simulación de batalla con cálculo de victoria, derrota o empate.
- Arquitectura hexagonal con separación entre dominio, aplicaciones y adaptadores.
- API REST documentada y lista para ejecutarse localmente.
- Soporte para despliegue local con Docker y Docker Compose.

## 🛠️ Stack Tecnológico

| Componente        | Tecnología                   | Propósito                              |
| :---------------- | :--------------------------- | :------------------------------------- |
| Backend           | Java 21                      | Lenguaje principal                     |
| Framework         | Spring Boot 4.1.0            | Desarrollo de la API REST              |
| Arquitectura      | Hexagonal (Ports & Adapters) | Separación entre dominio y adaptadores |
| Testing           | JUnit 5                      | Pruebas unitarias y de integración     |
| Documentación API | springdoc-openapi            | Swagger UI / OpenAPI                   |
| Contenerización   | Docker / Docker Compose      | Despliegue local reproducible          |

## 🏗️ Arquitectura del Proyecto

El proyecto organiza su código en capas para mantener el dominio independiente de la infraestructura:

- Domain: contiene las reglas y entidades del negocio.
- Application: implementa los casos de uso y expone los contratos de negocio.
- Adapters: incluye el adaptador REST y el repositorio en memoria.
- Ports: define los puertos para la persistencia y dependencias externas.

### Estructura principal

```text
src/main/java/com/kala/military/
├── adapters/
│   ├── in/rest/
│   │   ├── ArmyController.java
│   │   └── GlobalExceptionHandler.java
│   └── out/inmemory/
│       └── InMemoryArmyRepository.java
├── application/
│   ├── ArmyApplicationService.java
│   ├── BattleApplicationService.java
│   └── dto/
├── configuration/
│   └── BeanConfiguration.java
├── domain/
│   ├── Army.java
│   └── Unit.java
└── ports/
    └── out/
        └── ArmyRepositoryPort.java
```

## ⚙️ Requisitos Previos

- Java 21
- Maven 3.9+
- Docker Desktop o Docker Engine
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

### 3. Acceder a la API

- API base: http://localhost:8080/api/v1
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## 🐳 Despliegue Local con Docker

### Construir y levantar el contenedor

```bash
docker compose up --build
```

### Detener el contenedor

```bash
docker compose down
```

### Verificar que el servicio esté activo

```bash
curl http://localhost:8080/api/v1/armies
```

> El contenedor expone el puerto 8080 y usa la configuración de perfil docker definida en los recursos de la aplicación.

## 🌐 Endpoints de la API

Todos los endpoints están bajo el prefijo:

```text
/api/v1
```

### 1. Crear ejército

- Método: POST
- Ruta: /api/v1/armies
- Descripción: crea un ejército nuevo con las unidades iniciales según la civilización.

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
- Ruta: /api/v1/armies/{id}
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
- Ruta: /api/v1/armies/{id}/train
- Descripción: entrena una unidad existente y consume oro según la regla configurada.

#### Request example

```json
{
  "unitType": "Piquero"
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
- Ruta: /api/v1/armies/{id}/transform
- Descripción: transforma una unidad de un tipo a otro cuando la regla está definida.

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
- Ruta: /api/v1/battle
- Descripción: compara dos ejércitos y devuelve el resultado de la simulación.

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

## 🧪 Pruebas

El proyecto incluye pruebas de dominio y de integración para cubrir la lógica principal.

```bash
mvn test
```

## 🔎 Observaciones de Diseño

- El estado del ejército se mantiene en memoria y se comparte dentro del proceso.
- La capa de dominio no depende de Spring ni de HTTP.
- Los errores de negocio se traducen a respuestas HTTP 400 mediante un manejador global.
- El proyecto está preparado para evolucionar hacia persistencia real o integración con otros servicios.

## 📌 Notas Finales

Este README documenta la implementación actual del proyecto, incluyendo el flujo local, el despliegue con Docker y los contratos de entrada/salida de los endpoints principales.

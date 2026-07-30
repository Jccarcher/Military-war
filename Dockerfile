# syntax=docker/dockerfile:1

# --- Etapa 1: compilar el JAR dentro del contenedor ---------------------------
# Evita depender de que exista target/ en la máquina: "docker compose up --build"
# funciona sobre un clon recién bajado, sin ejecutar Maven antes.
FROM eclipse-temurin:21-jdk AS build

WORKDIR /build

# El wrapper y el pom se copian primero: mientras no cambien, Docker reutiliza la
# capa de dependencias aunque cambie el código fuente.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw

COPY src/ src/

# La caché de ~/.m2 se monta como caché de BuildKit: acelera reconstrucciones sin
# engordar la imagen final.
RUN --mount=type=cache,target=/root/.m2 ./mvnw -B -DskipTests clean package

# --- Etapa 2: imagen de ejecución --------------------------------------------
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# Colección de Postman — Military War Simulation API

Colección lista para importar con las 25 peticiones de la API, agrupadas por caso de uso.

| Archivo | Qué es |
| :------ | :----- |
| `military-war.postman_collection.json` | Colección con todas las peticiones y sus pruebas |
| `military-war-local.postman_environment.json` | Entorno con `host`, `baseUrl`, `armyId` y `rivalArmyId` |

## Importar

1. Levanta la aplicación:

   ```bash
   mvn spring-boot:run
   # o
   mvn clean package -DskipTests && docker compose up --build
   ```

2. En Postman: **Import → Files** y selecciona los dos archivos de esta carpeta.
3. Arriba a la derecha, selecciona el entorno **Military War - Local**. (Es opcional: la
   colección ya trae los mismos valores por defecto.)

## Cómo usarla

La colección se encadena sola. Ejecuta primero, en este orden:

1. `1. Ejércitos → Crear ejército (chinos)` — guarda el id en la variable `armyId`.
2. `1. Ejércitos → Crear ejército rival (ingleses)` — guarda el id en `rivalArmyId`.

A partir de ahí, el resto de peticiones ya apuntan al ejército correcto sin que copies ningún id.

También puedes pulsar **Run collection** en el Collection Runner: las carpetas están numeradas para
que el flujo completo (crear → entrenar → transformar → batallar → consultar historial) se ejecute
de principio a fin, incluidos los casos de error.

## Contenido

| Carpeta | Peticiones |
| :------ | :--------- |
| 1. Ejércitos | Crear (chinos, ingleses, bizantinos) y consultar estado |
| 2. Entrenamiento | Entrenar Piquero (30), Arquero (40) y Caballero (50) |
| 3. Transformación | Ciclo Piquero → Arquero → Caballero → Piquero |
| 4. Batalla | Simular batalla y consultar el historial resultante |
| 5. Casos de error (400) | Civilización no soportada o ausente, tipo de unidad ausente o inexistente, transformación no soportada, ejército no encontrado y JSON malformado |
| 6. Health Checks | Probes de liveness y readiness, health agregado y verificación de que el resto de Actuator sigue cerrado |
| 7. Documentación | OpenAPI JSON y Swagger UI |

Cada petición trae pruebas (`pm.test`) que verifican el código de estado y el contrato de la
respuesta, así que el Runner sirve como humo de la API además de como cliente manual.

## Variables

| Variable | Valor por defecto | Notas |
| :------- | :---------------- | :---- |
| `host` | `http://localhost:8080` | Cámbialo si expones otro puerto |
| `baseUrl` | `{{host}}/api/v1` | Prefijo de todos los endpoints de negocio |
| `armyId` | (vacío) | Lo rellena el script de «Crear ejército (chinos)» |
| `rivalArmyId` | (vacío) | Lo rellena el script de «Crear ejército rival (ingleses)» |

> El estado vive en memoria: al reiniciar la aplicación, los ids guardados dejan de existir y las
> peticiones responderán `400 Ejército no encontrado`. Vuelve a ejecutar la carpeta 1.

## Ejecutar desde la línea de comandos

Con [Newman](https://github.com/postmanlabs/newman) puedes correr la colección en CI:

```bash
npm install -g newman
newman run military-war.postman_collection.json -e military-war-local.postman_environment.json
```

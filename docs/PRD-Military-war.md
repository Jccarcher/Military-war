## PRD (Product Requeriment Document) - Military-war (microservices)

## 1. Contexto

El sistema debe modelar ejércitos de distintas civilizaciones, con unidades, entrenamiento, transformaciones y batallas. El objetivo es capturar el dominio sin persistencia ni interfaz gráfica, aplicando arquitectura hexagonal para separar el núcleo de negocio de los adaptadores.

## 2. Objetivos
- Representar ejércitos con sus unidades iniciales según civilización.
- Permitir entrenamiento y transformación de unidades.
- Simular batallas entre ejércitos con reglas claras de ganador/perdedor.
- Mantener historial de batallas por ejército.
- Aplicar arquitectura hexagonal: Dominio (Core) + Puertos (Interfaces) + Adaptadores (In/Out).

## 3. Actores
- Usuario/Cliente externo → interactúa vía API REST (Adapter In).
- Sistema externo (ej. almacenamiento, APIs de terceros) → no requerido en este ejercicio, pero se modela como Adapter Out para extensibilidad futura.

## 4. Requisitos funcionales
- Crear ejército con civilización (Chinos, Ingleses, Bizantinos).
- Consultar estado de un ejército (unidades, oro, historial).
- Entrenar unidades (según tabla de costos/beneficios).
- Transformar unidades (según reglas de conversión).
- Ejecutar batallas entre ejércitos:
- Determinar ganador por puntos.
- Aplicar consecuencias (pérdida de unidades, ganancia de oro).
- Registrar historial.

## 5. Requisitos no funcionales
- Código en Java 21 + Spring Boot + JUNIT + Swagger (openApi) + docker
- Arquitectura hexagonal (separación clara de dominio y adaptadores).
- Sin persistencia (objetos en memoria).
- Sin interfaz gráfica (solo API REST).
- Extensible para agregar almacenamiento o UI en el futuro.
- Documentacion implementando swagger
- Dockerizada para poder ser implementada en cualquier entorno.
- Test unitarios, Test de integracion y Test funcionales. 

## 6. Puertos y Adaptadores
- Inbound Ports (IN): Interfaces de casos de uso (ej. BattleService, ArmyService).
- Inbound Adapters: Controladores REST que exponen endpoints.
- Outbound Ports (OUT): Interfaces para dependencias externas (ej. ArmyRepository).
- Outbound Adapters: Implementaciones en memoria (Map/List), fácilmente reemplazables por DB.

## 7. Casos de uso principales
- Crear ejército → POST /armies
- Entrenar unidad → POST /armies/{id}/train
- Transformar unidad → POST /armies/{id}/transform
- Batalla → POST /battle
- Consultar ejército → GET /armies/{id}

## 8. Métricas de éxito
- Correcta separación de dominio y adaptadores.
- Cumplimiento de reglas de negocio.
- Facilidad para extender con persistencia o UI.
- Código limpio y testeable.
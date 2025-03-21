# 🏗️ Arquitectura

## Visión General de la Arquitectura

El Sistema de Solicitudes Distribuido implementa una arquitectura de microservicios moderna, diseñada para ofrecer escalabilidad, mantenibilidad y resistencia a fallos. Esta arquitectura se compone de varios componentes independientes que se comunican entre sí a través de mensajes asíncronos y APIs REST.

## Diagrama de Arquitectura

```
┌───────────────┐     ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   Cliente     │     │  Frontend   │     │    INGR     │     │    CTRL     │
│   (Browser)   │◄───►│   Service   │◄───►│   Service   │◄───►│   Service   │
└───────────────┘     └──────┬──────┘     └──────┬──────┘     └──────┬──────┘
                             │                   │                   │
                             ▼                   ▼                   ▼
                      ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
                      │  WebSocket  │     │    Kafka    │     │   MongoDB   │
                      │   Server    │     │   Broker    │     │  Database   │
                      └─────────────┘     └─────────────┘     └─────────────┘
```

## Componentes Principales

### Frontend Service

Este componente sirve como punto de entrada para los usuarios, proporcionando:

- **Interfaz web** para la creación y visualización de solicitudes
- **API REST** para operaciones sobre solicitudes
- **Servidor WebSocket** para actualizaciones en tiempo real
- **Productor Kafka** para enviar solicitudes nuevas
- **Consumidor Kafka** para recibir actualizaciones de estado

### INGR Service (Ingesta)

Responsable de la validación inicial y el procesamiento de solicitudes:

- **Consumidor Kafka** para recibir solicitudes nuevas
- **Lógica de validación** para verificar la integridad de las solicitudes
- **Productor Kafka** para enviar solicitudes validadas o rechazadas

### CTRL Service (Control)

Encargado del procesamiento final y el almacenamiento:

- **Consumidor Kafka** para recibir solicitudes validadas
- **Lógica de procesamiento** para finalizar las solicitudes
- **Conexión a MongoDB** para el almacenamiento persistente
- **API REST** para consultas históricas
- **Productor Kafka** para notificar sobre solicitudes procesadas

### Infraestructura Compartida

#### Apache Kafka

Plataforma de mensajería distribuida que:

- Actúa como **bus de eventos** entre los servicios
- Proporciona **persistencia de mensajes** para tolerancia a fallos
- Permite **escalabilidad horizontal** de productores y consumidores
- Garantiza **orden de mensajes** dentro de particiones

#### MongoDB

Base de datos NoSQL que ofrece:

- **Almacenamiento persistente** de solicitudes
- **Esquema flexible** para adaptarse a cambios en el modelo
- **Escalabilidad horizontal** mediante sharding y replicación
- **Alto rendimiento** para operaciones de lectura y escritura

## Flujo de Datos

1. **Cliente → Frontend**:
   - El usuario envía una solicitud a través de la interfaz web
   - El Frontend asigna un ID único y registra la fecha de creación

2. **Frontend → Kafka (Topic: solicitudes-entrada)**:
   - El Frontend publica la solicitud como mensaje en Kafka

3. **Kafka → INGR Service**:
   - El INGR Service consume el mensaje
   - Valida la solicitud según reglas de negocio

4. **INGR Service → Kafka (Topic: solicitudes-procesadas)**:
   - Publica la solicitud validada/rechazada como mensaje

5. **Kafka → CTRL Service**:
   - El CTRL Service consume el mensaje
   - Procesa la solicitud y la almacena en MongoDB

6. **CTRL Service → Kafka (Topic: solicitudes-finalizadas)**:
   - Publica una notificación de finalización

7. **Kafka → Frontend**:
   - El Frontend consume la notificación
   - Actualiza el estado interno

8. **Frontend → Clientes**:
   - Envía actualizaciones en tiempo real a los clientes conectados vía WebSocket

## Patrones de Diseño Arquitectónicos

### Patrón de Mensajería (Messaging Pattern)

- Los servicios se comunican a través de mensajes, sin dependencias directas
- Permite operaciones asíncronas y procesamiento concurrente

### Patrón de Base de Datos por Servicio (Database per Service)

- El CTRL Service tiene su propia base de datos MongoDB
- Encapsula la estructura de datos y proporciona acceso solo a través de API

### Patrón API Gateway

- El Frontend Service actúa como puerta de enlace para los clientes
- Centraliza las solicitudes y oculta la complejidad interna

### Patrón Pub-Sub

- Implementado con Kafka para la distribución de eventos
- Permite múltiples suscriptores para cada tipo de evento

### Patrón CQRS (Command Query Responsibility Segregation)

- Separación de operaciones de escritura (comandos) y lectura (consultas)
- Frontend envía comandos para crear solicitudes
- CTRL Service maneja consultas sobre el histórico

## Consideraciones Arquitectónicas

### Escalabilidad

- Cada servicio puede escalarse horizontalmente de forma independiente
- Kafka permite particionamiento para distribuir la carga
- MongoDB puede escalarse mediante sharding

### Disponibilidad

- Los servicios no tienen dependencias directas entre sí
- La persistencia de mensajes en Kafka permite recuperación ante fallos
- MongoDB puede configurarse con replicación para alta disponibilidad

### Consistencia

- Eventual consistency es el modelo predominante
- Las solicitudes se procesan en orden dentro de cada partición de Kafka
- MongoDB proporciona consistencia a nivel de documento

### Seguridad

- Comunicación entre servicios puede asegurarse con TLS
- Kafka admite autenticación y autorización
- MongoDB implementa control de acceso basado en roles

## Evolución de la Arquitectura

La arquitectura está diseñada para evolucionar con el tiempo:

- Nuevos servicios pueden añadirse sin modificar los existentes
- Nuevos canales de entrada pueden integrarse con el Frontend
- Servicios adicionales de análisis pueden consumir eventos de Kafka
- Sistemas externos pueden integrarse a través de adaptadores específicos

---

*[Volver al índice principal](WIKI.md)* 
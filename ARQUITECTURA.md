# 🏗️ Arquitectura del Sistema

## Diseño de Microservicios

El sistema está diseñado siguiendo los principios de la arquitectura de microservicios, donde cada componente es independiente, con responsabilidades bien definidas y comunicación a través de mensajes y APIs REST.

### Microservicios Principales

| Microservicio | Puerto | Responsabilidad |
|---------------|--------|-----------------|
| **Frontend Service** | 8080 | Interfaz de usuario y comunicación con el cliente |
| **INGR Service** | 8081 | Validación de solicitudes entrantes |
| **CTRL Service** | 8082 | Procesamiento y almacenamiento de solicitudes |

## Diagrama de Arquitectura

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   Cliente   │────▶│  Frontend   │────▶│    INGR     │
│  (Navegador)│◀────│   Service   │◀────│   Service   │
└─────────────┘     └─────────────┘     └─────────────┘
                           ▲                   ▼
                           │             ┌─────────────┐
                           └─────────────│    CTRL     │
                                         │   Service   │
                                         └─────────────┘
                                                ▼
┌─────────────┐      ┌─────────────┐    ┌─────────────┐
│   Apache    │◀────▶│   MongoDB   │◀───│  Solicitudes │
│    Kafka    │      │             │    │ Almacenadas  │
└─────────────┘      └─────────────┘    └─────────────┘
```

## Servicios de Infraestructura

### Apache Kafka
- **Propósito**: Sistema de mensajería distribuido para comunicación asíncrona entre microservicios
- **Puerto**: 9092
- **Funciones**: 
  - Intermediario de mensajes entre servicios
  - Almacenamiento temporal de solicitudes en proceso
  - Garantía de entrega de mensajes incluso ante fallos

### MongoDB
- **Propósito**: Base de datos NoSQL para almacenamiento persistente de solicitudes
- **Puerto**: 27017
- **Funciones**:
  - Almacenamiento de solicitudes completadas
  - Consulta de histórico de solicitudes
  - Persistencia de datos en formato JSON

### Zookeeper
- **Propósito**: Coordinación de servicios distribuidos para Kafka
- **Puerto**: 2181
- **Funciones**:
  - Gestión de configuración para Kafka
  - Elección de líder y detección de fallos
  - Sincronización distribuida

## Comunicación entre Servicios

### Comunicación Síncrona (REST)
- **Endpoint Frontend → CTRL**: Consulta de histórico de solicitudes
- **Formato**: JSON sobre HTTP
- **Métodos**: GET, POST

### Comunicación Asíncrona (Kafka)
- **Topics**:
  - `solicitudes-entrada`: Solicitudes creadas por el usuario (Frontend → INGR)
  - `solicitudes-procesadas`: Solicitudes validadas (INGR → CTRL)
  - `solicitudes-finalizadas`: Confirmaciones de procesamiento (CTRL → Frontend)

### Comunicación en Tiempo Real
- **WebSockets**:
  - Protocolo: STOMP sobre SockJS
  - Destino: `/topic/solicitudes`
  - Cliente JavaScript se suscribe para recibir actualizaciones

## Patrones de Diseño Aplicados

1. **Event Sourcing**: Registro de eventos para mantener historial completo de acciones
2. **Publish-Subscribe**: Comunicación mediante publicación y suscripción a eventos
3. **Circuit Breaker**: Detección de fallos en la comunicación entre servicios
4. **CQRS**: Separación de operaciones de lectura y escritura

## Consideraciones de Seguridad

- **CORS**: Configurado para permitir solicitudes desde orígenes específicos
- **Validación de Entrada**: Implementada en el INGR Service antes del procesamiento
- **Limpieza de Datos**: Sanitización antes de almacenar en MongoDB

## Escalabilidad

El sistema puede escalar horizontalmente:
- Añadiendo más instancias de cada microservicio
- Incrementando particiones en los topics de Kafka
- Configurando réplicas de MongoDB

---

*[Volver al índice principal](WIKI.md)* 
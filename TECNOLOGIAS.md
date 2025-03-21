# 💻 Tecnologías Utilizadas

Este proyecto utiliza un stack tecnológico moderno orientado a microservicios. A continuación se detallan las tecnologías principales y sus roles en el sistema.

## Backend

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **Java** | 11 | Lenguaje de programación principal |
| **Spring Boot** | 2.7.9 | Framework para desarrollo de microservicios |
| **Spring Web** | | APIs REST y controladores web |
| **Spring WebSocket** | | Comunicación en tiempo real |
| **Spring Kafka** | | Integración con Apache Kafka |
| **Spring Data MongoDB** | | Acceso a datos en MongoDB |

### Dependencias Importantes

```xml
<dependencies>
    <!-- Spring Boot -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-websocket</artifactId>
    </dependency>
    
    <!-- Kafka -->
    <dependency>
        <groupId>org.springframework.kafka</groupId>
        <artifactId>spring-kafka</artifactId>
    </dependency>
    
    <!-- MongoDB -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-mongodb</artifactId>
    </dependency>
    
    <!-- JSON -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.datatype</groupId>
        <artifactId>jackson-datatype-jsr310</artifactId>
    </dependency>
</dependencies>
```

## Frontend

| Tecnología | Propósito |
|------------|-----------|
| **HTML5** | Estructura del documento web |
| **CSS3** | Estilos y presentación |
| **JavaScript** | Lógica del cliente y manipulación del DOM |
| **SockJS** | Biblioteca cliente para WebSockets |
| **STOMP.js** | Protocolo de mensajería sobre WebSockets |

## Infraestructura

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **Apache Kafka** | 2.13-3.1.0 | Sistema de mensajería |
| **Zookeeper** | 3.8 | Coordinación de servicios distribuidos |
| **MongoDB** | 4.4 | Base de datos NoSQL |
| **Docker** | | Contenedores para servicios |
| **Docker Compose** | | Orquestación de contenedores |

### Configuración Docker

```yaml
version: '3'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      
  kafka:
    image: confluentinc/cp-kafka:latest
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      
  mongodb:
    image: mongo:4.4
    ports:
      - "27017:27017"
    volumes:
      - mongodb_data:/data/db

volumes:
  mongodb_data:
```

## Herramientas de Desarrollo

| Herramienta | Propósito |
|-------------|-----------|
| **Maven** | Gestión de dependencias y build |
| **Git** | Control de versiones |
| **VSCode/IntelliJ** | IDEs recomendados |
| **Postman** | Pruebas de API REST |
| **MongoDB Compass** | Cliente visual para MongoDB |
| **Conduktor** | Herramienta para monitoreo de Kafka |

## Protocolos y Estándares

- **HTTP/1.1**: Comunicación REST entre servicios
- **WebSocket**: Comunicación bidireccional en tiempo real
- **STOMP**: Protocolo simple de mensajería orientado a texto
- **JSON**: Formato de intercambio de datos
- **OAuth 2.0**: Autorización (previsto para futuras versiones)

## Requisitos del Sistema

### Desarrollo
- Java JDK 11 o superior
- Maven 3.6+
- Docker y Docker Compose
- Git
- Mínimo 8GB de RAM

### Producción
- 2 núcleos CPU mínimo por servicio
- 2GB RAM mínimo por servicio
- 10GB almacenamiento para MongoDB
- Sistema operativo: Linux (recomendado) o Windows Server

## Compatibilidad

### Navegadores Soportados
- Chrome 80+
- Firefox 72+
- Safari 13+
- Edge 80+

---

*[Volver al índice principal](WIKI.md)* 
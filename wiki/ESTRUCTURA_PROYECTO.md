# 📂 Estructura del Proyecto

Este documento describe la organización del código fuente y los archivos del Sistema de Solicitudes Distribuido.

## Visión General

El proyecto sigue una estructura de múltiples módulos Maven, donde cada microservicio y el módulo común constituyen un módulo independiente:

```
DEMO KAFKA/                        # Directorio raíz del proyecto
│
├── common/                        # Módulo común compartido entre servicios
├── frontend-service/              # Servicio de frontend y UI
├── ingr-service/                  # Servicio de ingesta y validación
├── ctrl-service/                  # Servicio de control y almacenamiento
├── docker-compose.yml             # Configuración de servicios Docker
├── start-services.bat             # Script para iniciar servicios (Windows)
├── start-services.sh              # Script para iniciar servicios (Linux/Mac)
├── pom.xml                        # Archivo POM principal (parent)
└── README.md                      # Documentación principal
```

## Estructura Detallada

### Módulo Common

```
common/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── solicitudes/
│                   └── model/
│                       └── Solicitud.java    # Modelo de datos compartido
├── pom.xml                                   # Configuración Maven del módulo
└── .gitignore
```

### Frontend Service

```
frontend-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── solicitudes/
│   │   │           └── frontend/
│   │   │               ├── FrontendServiceApplication.java   # Clase principal
│   │   │               ├── controller/
│   │   │               │   └── SolicitudController.java      # Controlador REST
│   │   │               ├── service/
│   │   │               │   ├── SolicitudService.java         # Servicio de gestión
│   │   │               │   └── NotificacionService.java      # Servicio WebSocket
│   │   │               └── config/
│   │   │                   ├── WebSocketConfig.java          # Config WebSocket
│   │   │                   └── AppConfig.java                # Config general
│   │   └── resources/
│   │       ├── static/                       # Recursos web estáticos
│   │       │   ├── index.html                # Página principal
│   │       │   ├── js/
│   │       │   │   └── app.js                # Lógica JavaScript
│   │       │   ├── css/
│   │       │   │   └── styles.css            # Estilos CSS
│   │       │   └── img/                      # Imágenes
│   │       └── application.properties        # Configuración del servicio
│   └── test/                                 # Pruebas unitarias e integración
├── pom.xml                                   # Configuración Maven del módulo
└── .gitignore
```

### INGR Service

```
ingr-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── solicitudes/
│   │   │           └── ingr/
│   │   │               ├── IngrServiceApplication.java    # Clase principal
│   │   │               ├── service/
│   │   │               │   └── SolicitudValidatorService.java  # Validación
│   │   │               └── config/
│   │   │                   └── KafkaConfig.java           # Config Kafka
│   │   └── resources/
│   │       └── application.properties                     # Configuración
│   └── test/                                              # Pruebas
├── pom.xml                                                # Config Maven
└── .gitignore
```

### CTRL Service

```
ctrl-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── solicitudes/
│   │   │           └── ctrl/
│   │   │               ├── CtrlServiceApplication.java    # Clase principal
│   │   │               ├── controller/
│   │   │               │   └── SolicitudController.java   # Controlador REST
│   │   │               ├── service/
│   │   │               │   └── SolicitudProcessorService.java  # Procesamiento
│   │   │               └── repository/
│   │   │                   └── SolicitudRepository.java   # Acceso MongoDB
│   │   └── resources/
│   │       └── application.properties                     # Configuración
│   └── test/                                              # Pruebas
├── pom.xml                                                # Config Maven
└── .gitignore
```

## Archivos de Configuración

### POM Principal

El archivo `pom.xml` en la raíz del proyecto define:

- Información del proyecto (groupId, version)
- Módulos incluidos
- Propiedades comunes (versiones de Java, Spring Boot)
- Dependencias compartidas
- Plugins de compilación

```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.solicitudes</groupId>
    <artifactId>sistema-solicitudes</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>pom</packaging>
    
    <modules>
        <module>common</module>
        <module>frontend-service</module>
        <module>ingr-service</module>
        <module>ctrl-service</module>
    </modules>
    
    <properties>
        <java.version>11</java.version>
        <spring-boot.version>2.7.9</spring-boot.version>
        <!-- Otras propiedades -->
    </properties>
    
    <!-- Dependencias y plugins comunes -->
</project>
```

### Docker Compose

El archivo `docker-compose.yml` define los servicios de infraestructura:

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

### Scripts de Inicio

El proyecto incluye scripts para iniciar todos los servicios:

**start-services.bat** (Windows):
```batch
@echo off
echo NOTA: Este script iniciará los servicios Java, pero necesitarás Kafka y MongoDB para un funcionamiento completo.

echo.
echo Iniciando INGR Service...
start "INGR-SERVICE" java -jar ingr-service/target/ingr-service-1.0-SNAPSHOT.jar

echo.
echo Iniciando CTRL Service...
start "CTRL-SERVICE" java -jar ctrl-service/target/ctrl-service-1.0-SNAPSHOT.jar

echo.
echo Iniciando Frontend Service...
start "FRONTEND-SERVICE" java -jar frontend-service/target/frontend-service-1.0-SNAPSHOT.jar

echo.
echo Todos los servicios iniciados! Accede a http://localhost:8080
```

## Configuración de Propiedades

### Frontend Service (application.properties)

```properties
# Puerto del servidor
server.port=8080

# Configuración de Kafka
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=frontend-group
spring.kafka.consumer.auto-offset-reset=earliest

# Configuración del servicio CTRL
ctrl.service.url=http://localhost:8082

# Configuración de logs
logging.level.com.solicitudes=DEBUG
```

### INGR Service (application.properties)

```properties
# Puerto del servidor
server.port=8081

# Configuración de Kafka
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=ingr-group
spring.kafka.consumer.auto-offset-reset=earliest

# Configuración de logs
logging.level.com.solicitudes=DEBUG
```

### CTRL Service (application.properties)

```properties
# Puerto del servidor
server.port=8082

# Configuración de Kafka
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=ctrl-group
spring.kafka.consumer.auto-offset-reset=earliest

# Configuración de MongoDB
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=solicitudes_db

# Configuración de logs
logging.level.com.solicitudes=DEBUG
```

## Organización del Código

El proyecto sigue una estructura de capas estándar para cada microservicio:

1. **Capa de Controlador**: Maneja las peticiones HTTP
2. **Capa de Servicio**: Contiene la lógica de negocio
3. **Capa de Repositorio**: Acceso a datos (MongoDB)
4. **Capa de Modelo**: Entidades y DTOs compartidos

Cada microservicio sigue los principios de arquitectura hexagonal, separando:
- **Core**: Lógica de negocio
- **Adaptadores de Entrada**: Controllers, Listeners
- **Adaptadores de Salida**: Repositories, Producers

## Convenciones de Nomenclatura

- **Paquetes**: Nombres en minúsculas, siguiendo convención Java (`com.solicitudes.modulo.subcategoria`)
- **Clases**: PascalCase, nombre descriptivo con sufijo indicando el tipo (`SolicitudController`, `KafkaConfig`)
- **Métodos**: camelCase, verbo descriptivo (`enviarSolicitud`, `procesarSolicitud`)
- **Constantes**: SNAKE_CASE_MAYÚSCULAS (`KAFKA_TOPIC_ENTRADA`)
- **Properties**: kebab-case (`spring.kafka.bootstrap-servers`)

---

*[Volver al índice principal](WIKI.md)* 
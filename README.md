# Sistema de Gestión de Solicitudes Asíncrono con Kafka

Este proyecto implementa un sistema de gestión de solicitudes distribuido utilizando Apache Kafka para la comunicación asíncrona entre microservicios.

## Arquitectura del Sistema

El sistema consta de tres microservicios:

1. **Frontend Service (Puerto 8080)**: Proporciona una interfaz de usuario y API REST para enviar solicitudes. Implementa WebSockets para actualizaciones en tiempo real.
2. **INGR Service (Puerto 8081)**: Valida y transforma las solicitudes recibidas, aplicando reglas de negocio.
3. **CTRL Service (Puerto 8082)**: Almacena las solicitudes en MongoDB y confirma su procesamiento.

### Módulo Común
- **common**: Contiene los modelos de datos y utilidades compartidas entre todos los microservicios.

## Flujo de Datos

1. El usuario envía una solicitud a través de la interfaz web.
2. El Frontend Service publica la solicitud en el topic `solicitudes-entrada` de Kafka.
3. El INGR Service consume la solicitud, la valida y la publica en el topic `solicitudes-procesadas`.
4. El CTRL Service consume la solicitud procesada, la almacena en MongoDB y publica una confirmación en el topic `solicitudes-finalizadas`.
5. El Frontend Service recibe la confirmación y actualiza la interfaz de usuario en tiempo real mediante WebSocket.

## Interfaz de Usuario

El sistema proporciona una interfaz web sencilla con los siguientes elementos:

1. **Formulario de creación de solicitudes** con los campos:
   - **Solicitud**: Nombre o identificador de la solicitud (internamente manejado como `nombreCliente`)
   - **Descripción**: Detalles de la solicitud
   - **ID Petición**: Número identificador de la petición (internamente manejado como `cantidad`)

2. **Lista de solicitudes recientes** que muestra:
   - Solicitud
   - ID único del sistema
   - Descripción
   - ID Petición
   - Estado (CREADA, VALIDADA, PROCESADA, RECHAZADA)

3. **Botón para ver el histórico** de solicitudes procesadas.

## Tecnologías Utilizadas

- **Backend**: Java 11, Spring Boot 2.7.9
- **Mensajería**: Apache Kafka 3.6.1
- **Base de datos**: MongoDB
- **Frontend**: HTML, CSS, JavaScript
- **Comunicación en tiempo real**: WebSockets (STOMP)
- **Dependencias principales**:
  - spring-kafka 2.9.11
  - kafka-clients 3.6.1
  - spring-boot-starter-websocket
  - jackson-databind 2.15.3

## Requisitos Previos

- Java 11 o superior
- Maven 3.6 o superior
- Docker Desktop

## Instalación y Ejecución

### 1. Iniciar Kafka, Zookeeper y MongoDB

```bash
docker compose -f docker-compose-simple.yml up -d
```

### 2. Compilar el proyecto

```bash
# Actualizar dependencias y compilar
mvn clean install -DskipTests -U
```

### 3. Iniciar los microservicios

**Opción 1 (Recomendada)**: Usar el script de inicio
```bash
.\start-services.bat
```

**Opción 2**: Iniciar manualmente (cada uno en una terminal diferente)
```bash
# Terminal 1 - INGR Service
java -jar ingr-service/target/ingr-service-1.0-SNAPSHOT.jar

# Terminal 2 - CTRL Service
java -jar ctrl-service/target/ctrl-service-1.0-SNAPSHOT.jar

# Terminal 3 - Frontend Service
java -jar frontend-service/target/frontend-service-1.0-SNAPSHOT.jar
```

### 4. Acceder a la aplicación

Abre tu navegador en [http://localhost:8080](http://localhost:8080)

## Estructura del Proyecto

```
├── common/                       # Módulo con clases compartidas
│   ├── src/main/java/com/solicitudes/model/
│   └── pom.xml                   # Dependencias: jackson-databind, jackson-datatype-jsr310
│
├── frontend-service/             # Servicio de interfaz de usuario
│   ├── src/main/java/com/solicitudes/frontend/
│   │   ├── config/               # Configuración de WebSockets y Kafka
│   │   ├── controller/           # Controladores REST
│   │   └── service/              # Servicios de negocio
│   ├── src/main/resources/
│   │   ├── static/               # Recursos estáticos (HTML, CSS, JS)
│   │   └── application.properties # Configuración de la aplicación
│   └── pom.xml                   # Dependencias: spring-boot, spring-kafka, websocket
│
├── ingr-service/                 # Servicio de ingestión y validación
│   ├── src/main/java/com/solicitudes/ingr/
│   │   ├── config/               # Configuración de Kafka
│   │   ├── controller/           # Controladores REST
│   │   └── service/              # Validación y procesamiento
│   ├── src/main/resources/
│   │   └── application.properties # Configuración de la aplicación
│   └── pom.xml                   # Dependencias: spring-boot, spring-kafka
│
├── ctrl-service/                 # Servicio de procesamiento y almacenamiento
│   ├── src/main/java/com/solicitudes/ctrl/
│   │   ├── config/               # Configuración de Kafka y MongoDB
│   │   ├── controller/           # Controladores REST
│   │   └── service/              # Servicios de procesamiento
│   ├── src/main/resources/
│   │   └── application.properties # Configuración de la aplicación
│   └── pom.xml                   # Dependencias: spring-boot, spring-kafka, mongodb
│
├── docker-compose-simple.yml     # Configuración para iniciar Kafka, Zookeeper y MongoDB
├── docker-compose.yml            # Configuración Docker alternativa
├── start-services.bat            # Script para iniciar los servicios
├── pom.xml                       # POM raíz del proyecto
├── DIAGRAMA_FLUJO.md             # Diagramas de componentes y secuencia
├── INSTRUCCIONES_DE_EJECUCION.md # Guía detallada de ejecución
└── README.md                     # Este archivo
```

## Configuración de Seguridad

Para mitigar las vulnerabilidades conocidas, se han implementado las siguientes medidas:

1. **CVE-2024-31141 en kafka-clients**: 
   - Se ha actualizado a la versión 3.6.1 compatible con spring-kafka 2.9.11
   - Se ha agregado la configuración `org.apache.kafka.automatic.config.providers=none` en todos los servicios

2. **Versiones de jackson-databind y jackson-datatype-jsr310**:
   - Actualizadas a la versión 2.15.3 para mitigar vulnerabilidades

## Detener el Sistema

1. Cierra las ventanas de los microservicios Java
2. Detén los contenedores de Docker:
```bash
docker compose -f docker-compose-simple.yml down
```

## Solución de Problemas

### Errores de Conexión a Kafka
- Verifica que el contenedor de Kafka esté en ejecución: `docker ps`
- Comprueba que el puerto 9092 esté abierto: `netstat -an | findstr "9092"`

### Errores de Conexión a MongoDB
- Verifica que el contenedor de MongoDB esté en ejecución: `docker ps`
- Comprueba que el puerto 27017 esté abierto: `netstat -an | findstr "27017"`

### Problemas con la Compilación
- Limpia los directorios target: `mvn clean`
- Fuerza la actualización de dependencias: `mvn -U clean install -DskipTests`

### Reiniciar la Aplicación
Si has realizado cambios o hay problemas con la aplicación:
1. Detén todos los servicios Java (cierra las ventanas o usa `taskkill /f /im java.exe` en Windows)
2. Compila el proyecto: `mvn clean install -DskipTests`
3. Inicia los servicios nuevamente con `.\start-services.bat`

### Puertos Ocupados
- Verifica que los puertos 8080, 8081, 8082, 9092, 2181 y 27017 estén disponibles

## Diagrama de Flujo

Consulta el archivo `DIAGRAMA_FLUJO.md` para ver los diagramas de componentes, secuencia y flujo de ejecución del sistema.

## Instrucciones Detalladas

Para instrucciones más detalladas sobre la ejecución, consulta el archivo `INSTRUCCIONES_DE_EJECUCION.md`. 

# Configuración de MongoDB
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=solicitudes_db 

## Últimas Mejoras

### Mejoras de Documentación y Organización del Código

Se han realizado las siguientes mejoras en el código:

1. **Documentación mejorada**: Se han agregado comentarios detallados en español a todas las clases Java del proyecto, explicando el propósito de cada componente y su funcionamiento.

2. **Renombrado de métodos**: Se han renombrado métodos en los servicios para utilizar nomenclatura en español más descriptiva, facilitando la comprensión del flujo de datos.

3. **Mejora en manejo de errores**: Se ha implementado mejor manejo de errores en las comunicaciones entre los servicios, especialmente para la obtención del histórico de solicitudes.

4. **Configuración más explícita**: Se han mejorado los archivos de configuración para hacer más explícitos los parámetros y facilitar cambios futuros.

5. **Organización del código**: Se ha reorganizado el código siguiendo mejores prácticas para microservicios en Spring Boot.

Estas mejoras hacen que el código sea más mantenible, comprensible y mejor documentado para futuros desarrolladores que trabajen con el sistema. 

## Consulta de la Base de Datos MongoDB

Para ver y gestionar el contenido de la base de datos MongoDB, puedes utilizar los siguientes comandos:

### 1. Ver todas las solicitudes almacenadas

```powershell
docker exec -it mongodb mongosh --eval "db.getSiblingDB('solicitudes_db').solicitud.find().pretty()"
```

### 2. Buscar solicitudes por cliente

```powershell
docker exec -it mongodb mongosh --eval "db.getSiblingDB('solicitudes_db').solicitud.find({nombreCliente: 'TRY-UIK'}).pretty()"
```

### 3. Contar el total de solicitudes

```powershell
docker exec -it mongodb mongosh --eval "db.getSiblingDB('solicitudes_db').solicitud.countDocuments()"
```

### 4. Buscar solicitudes por estado

```powershell
docker exec -it mongodb mongosh --eval "db.getSiblingDB('solicitudes_db').solicitud.find({estado: 'PROCESADA'}).pretty()"
```

### 5. Insertar una solicitud de prueba

```powershell
docker exec -it mongodb mongosh --eval "db.getSiblingDB('solicitudes_db').solicitud.insertOne({id: 'test-id', nombreCliente: 'Cliente Prueba', descripcion: 'Solicitud de prueba', cantidad: 1, estado: 'CREADA', fechaCreacion: new Date()})"
```

Estos comandos te permiten verificar el correcto funcionamiento del sistema comprobando que las solicitudes se están almacenando correctamente en MongoDB. 
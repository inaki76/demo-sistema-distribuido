# 🧩 Componentes del Sistema

Este documento detalla los componentes principales que conforman el Sistema de Solicitudes Distribuido, explicando su propósito, funcionalidades y relaciones entre ellos.

## 📋 Índice

- [Visión General](#visión-general)
- [Frontend Service](#frontend-service)
- [INGR Service](#ingr-service)
- [CTRL Service](#ctrl-service)
- [Módulo Común](#módulo-común)
- [Servicios de Infraestructura](#servicios-de-infraestructura)
- [Interacción entre Componentes](#interacción-entre-componentes)

## Visión General

El sistema está compuesto por tres microservicios principales, un módulo común compartido y servicios de infraestructura para mensajería y persistencia. Esta estructura modular permite la escalabilidad y mantenibilidad independiente de cada componente.

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│                 │     │                 │     │                 │
│ Frontend Service│     │   INGR Service  │     │   CTRL Service  │
│                 │     │                 │     │                 │
└────────┬────────┘     └────────┬────────┘     └────────┬────────┘
         │                       │                       │
         └───────────┬───────────┴───────────┬───────────┘
                     │                       │
         ┌───────────▼───────────┐ ┌─────────▼───────────┐
         │                       │ │                      │
         │     Apache Kafka      │ │       MongoDB        │
         │                       │ │                      │
         └───────────────────────┘ └──────────────────────┘
```

## Frontend Service

El Frontend Service es el punto de entrada para los usuarios del sistema, ofreciendo una interfaz web y gestionando la comunicación en tiempo real.

### Propósito Principal

- Proporcionar una interfaz de usuario para la creación y visualización de solicitudes
- Servir como gateway de API para los clientes
- Facilitar actualizaciones en tiempo real mediante WebSockets
- Iniciar el proceso de solicitudes enviando mensajes a Kafka

### Componentes Internos

#### ✨ Interfaz Web (UI)

- **Tecnologías**: HTML5, CSS3, JavaScript, Bootstrap
- **Archivos clave**: 
  - `index.html`: Página principal del sistema
  - `js/app.js`: Lógica del cliente para gestión de solicitudes
  - `css/styles.css`: Estilos de la aplicación

#### 🛠️ API REST

- **Tecnologías**: Spring Boot, Spring Web
- **Clases clave**:
  - `SolicitudController`: Maneja peticiones HTTP para creación y consulta
  - `SolicitudService`: Lógica de negocio para gestión de solicitudes

#### 📡 Servidor WebSocket

- **Tecnologías**: Spring WebSocket, STOMP
- **Clases clave**:
  - `WebSocketConfig`: Configuración del servidor WebSocket
  - `NotificacionService`: Envío de notificaciones en tiempo real

#### 🔄 Integración con Kafka

- **Tecnologías**: Spring Kafka
- **Clases clave**:
  - `KafkaProducer`: Envío de solicitudes a Kafka
  - `KafkaConsumer`: Recepción de actualizaciones de estado

### Endpoints Principales

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/` | Página principal del sistema |
| POST | `/api/solicitudes` | Crea una nueva solicitud |
| GET | `/api/solicitudes/historico` | Obtiene el histórico de solicitudes |
| WS | `/ws` | Endpoint de conexión WebSocket |
| WS | `/topic/solicitudes` | Canal de suscripción a actualizaciones |

## INGR Service

El INGR Service (Ingesta) se encarga de la validación y procesamiento inicial de solicitudes recibidas.

### Propósito Principal

- Validar las solicitudes según reglas de negocio
- Determinar si una solicitud debe ser procesada o rechazada
- Enriquecer las solicitudes con información adicional
- Asignar estados apropiados a las solicitudes

### Componentes Internos

#### 🔍 Módulo de Validación

- **Tecnologías**: Spring Boot, Java Bean Validation
- **Clases clave**:
  - `SolicitudValidatorService`: Implementa la lógica de validación
  - `ValidationRules`: Define las reglas de negocio

#### 📊 Procesador de Estados

- **Tecnologías**: Spring State Machine
- **Clases clave**:
  - `SolicitudStateManager`: Gestiona las transiciones de estado
  - `StateConfig`: Configura los estados y transiciones posibles

#### 🔄 Integración con Kafka

- **Tecnologías**: Spring Kafka
- **Clases clave**:
  - `KafkaConfig`: Configuración de conexión a Kafka
  - `KafkaConsumer`: Recepción de solicitudes nuevas
  - `KafkaProducer`: Envío de solicitudes validadas

### Reglas de Validación

El INGR Service aplica varias reglas de validación:

- Campos obligatorios (nombreCliente, descripcion, cantidad)
- Longitud mínima y máxima para campos de texto
- Valores numéricos dentro de rangos aceptables
- Validaciones específicas del dominio

## CTRL Service

El CTRL Service (Control) es responsable del procesamiento final y almacenamiento de solicitudes.

### Propósito Principal

- Procesar solicitudes validadas
- Almacenar solicitudes en la base de datos MongoDB
- Proporcionar acceso al histórico de solicitudes
- Notificar sobre el procesamiento completado

### Componentes Internos

#### 💾 Módulo de Persistencia

- **Tecnologías**: Spring Data MongoDB
- **Clases clave**:
  - `SolicitudRepository`: Interfaz para operaciones CRUD
  - `MongoConfig`: Configuración de conexión a MongoDB

#### 🧮 Procesador de Solicitudes

- **Tecnologías**: Spring Boot
- **Clases clave**:
  - `SolicitudProcessorService`: Lógica de procesamiento final
  - `ProcessingStrategy`: Estrategias para diferentes tipos de solicitudes

#### 🔄 Integración con Kafka

- **Tecnologías**: Spring Kafka
- **Clases clave**:
  - `KafkaConfig`: Configuración de conexión a Kafka
  - `KafkaConsumer`: Recepción de solicitudes validadas
  - `KafkaProducer`: Envío de notificaciones de finalización

#### 🛠️ API REST

- **Tecnologías**: Spring Web
- **Clases clave**:
  - `SolicitudController`: Endpoints para consulta de histórico

### Endpoints Principales

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/ctrl/solicitudes` | Obtiene todas las solicitudes almacenadas |
| GET | `/ctrl/solicitudes/{id}` | Obtiene una solicitud específica por ID |
| GET | `/ctrl/actuator/health` | Endpoint de verificación de salud |

## Módulo Común

El módulo común contiene componentes compartidos entre todos los microservicios.

### Propósito Principal

- Definir modelos de datos compartidos
- Proporcionar utilidades comunes
- Evitar duplicación de código
- Asegurar consistencia entre servicios

### Componentes Internos

#### 📋 Modelos de Datos

- **Tecnologías**: Java, Lombok, Jackson
- **Clases clave**:
  - `Solicitud`: Entidad principal que representa una solicitud
  - `SolicitudEstado`: Enumeración de posibles estados

#### 🛠️ Utilidades Comunes

- **Tecnologías**: Java
- **Clases clave**:
  - `DateUtils`: Utilidades para manejo de fechas
  - `ValidationUtils`: Funciones de validación comunes

#### 🔄 Serializadores y Deserializadores

- **Tecnologías**: Jackson
- **Clases clave**:
  - `JacksonConfig`: Configuración para serialización/deserialización
  - `CustomSerializers`: Serializadores personalizados

## Servicios de Infraestructura

Los servicios de infraestructura proporcionan capacidades esenciales para el funcionamiento del sistema.

### Apache Kafka

- **Propósito**: Intermediario de mensajería para comunicación entre servicios
- **Configuración**:
  - 3 topics principales: solicitudes-entrada, solicitudes-procesadas, solicitudes-finalizadas
  - Configurado con retención de mensajes para garantizar entrega
  - Particionamiento para escalabilidad

### MongoDB

- **Propósito**: Almacenamiento persistente de solicitudes
- **Configuración**:
  - Colección principal: `solicitud`
  - Índices en campos frecuentemente consultados (id, estado, fechaCreacion)
  - Configurado para almacenamiento eficiente de documentos JSON

### Docker y Docker Compose

- **Propósito**: Contenerización y orquestación de servicios
- **Configuración**:
  - Contenedores separados para cada servicio
  - Redes definidas para comunicación interna
  - Volúmenes para persistencia de datos

## Interacción entre Componentes

La comunicación entre los componentes del sistema sigue un patrón definido:

1. **Usuario → Frontend Service**:
   - Interacción a través de la interfaz web
   - Creación de solicitudes mediante formularios
   - Visualización de actualizaciones en tiempo real

2. **Frontend Service → Kafka → INGR Service**:
   - El Frontend publica solicitudes nuevas en el topic `solicitudes-entrada`
   - El INGR Service consume estos mensajes para validación

3. **INGR Service → Kafka → CTRL Service**:
   - El INGR Service publica solicitudes validadas en el topic `solicitudes-procesadas`
   - El CTRL Service consume estos mensajes para procesamiento final

4. **CTRL Service → MongoDB**:
   - El CTRL Service almacena las solicitudes procesadas en MongoDB
   - Permite consultas históricas y análisis

5. **CTRL Service → Kafka → Frontend Service**:
   - El CTRL Service publica notificaciones en el topic `solicitudes-finalizadas`
   - El Frontend Service consume estos mensajes para actualizaciones en tiempo real

6. **Frontend Service → Usuario**:
   - Actualizaciones enviadas al cliente mediante WebSockets
   - La interfaz se actualiza sin necesidad de recargar la página

### Diagrama de Secuencia Simplificado

```
┌────────┐     ┌────────┐     ┌────────┐     ┌────────┐     ┌────────┐     ┌────────┐
│Usuario │     │Frontend│     │ Kafka  │     │  INGR  │     │  CTRL  │     │MongoDB │
└───┬────┘     └───┬────┘     └───┬────┘     └───┬────┘     └───┬────┘     └───┬────┘
    │              │              │              │              │              │
    │ Crear        │              │              │              │              │
    │ solicitud    │              │              │              │              │
    │─────────────>│              │              │              │              │
    │              │              │              │              │              │
    │              │ Publicar     │              │              │              │
    │              │ solicitud    │              │              │              │
    │              │─────────────>│              │              │              │
    │              │              │              │              │              │
    │              │              │ Consumir     │              │              │
    │              │              │ solicitud    │              │              │
    │              │              │─────────────>│              │              │
    │              │              │              │              │              │
    │              │              │              │ Validar      │              │
    │              │              │              │ solicitud    │              │
    │              │              │              │──────┐       │              │
    │              │              │              │      │       │              │
    │              │              │              │<─────┘       │              │
    │              │              │              │              │              │
    │              │              │              │ Publicar     │              │
    │              │              │              │ validada     │              │
    │              │              │<─────────────│              │              │
    │              │              │              │              │              │
    │              │              │ Consumir     │              │              │
    │              │              │ validada     │              │              │
    │              │              │─────────────────────────────>│              │
    │              │              │              │              │              │
    │              │              │              │              │ Procesar     │
    │              │              │              │              │ solicitud    │
    │              │              │              │              │──────┐       │
    │              │              │              │              │      │       │
    │              │              │              │              │<─────┘       │
    │              │              │              │              │              │
    │              │              │              │              │ Guardar      │
    │              │              │              │              │─────────────>│
    │              │              │              │              │              │
    │              │              │              │              │ Publicar     │
    │              │              │              │              │ procesada    │
    │              │              │<─────────────────────────────│              │
    │              │              │              │              │              │
    │              │ Consumir     │              │              │              │
    │              │ procesada    │              │              │              │
    │              │<─────────────│              │              │              │
    │              │              │              │              │              │
    │              │ Notificar    │              │              │              │
    │              │ cliente      │              │              │              │
    │              │──────┐       │              │              │              │
    │              │      │       │              │              │              │
    │              │<─────┘       │              │              │              │
    │              │              │              │              │              │
    │ Recibir      │              │              │              │              │
    │ actualización│              │              │              │              │
    │<─────────────│              │              │              │              │
    │              │              │              │              │              │
```

---

*[Volver al índice principal](WIKI.md)* 
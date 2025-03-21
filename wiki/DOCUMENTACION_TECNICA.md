# 📚 Documentación Técnica

Este documento proporciona detalles técnicos sobre el Sistema de Solicitudes Distribuido, incluyendo diagramas, modelos de datos, API y patrones de diseño implementados.

## 📋 Índice

- [Modelo de Datos](#modelo-de-datos)
- [API REST](#api-rest)
- [Diagramas Técnicos](#diagramas-técnicos)
- [Patrones de Diseño](#patrones-de-diseño)
- [WebSockets](#websockets)
- [Configuración de Kafka](#configuración-de-kafka)
- [Configuración de MongoDB](#configuración-de-mongodb)

## Modelo de Datos

### Clase Solicitud

La clase `Solicitud` es la entidad principal del sistema, compartida entre todos los microservicios.

```java
/**
 * Clase modelo que representa una solicitud en el sistema.
 * Esta clase es utilizada por todos los microservicios para transmitir
 * y procesar la información de las solicitudes.
 */
public class Solicitud {
    // Atributos de la clase
    private String id;              // Identificador único de la solicitud
    private String nombreCliente;   // Nombre del cliente o identificador de la solicitud
    private String descripcion;     // Descripción detallada de la solicitud
    private int cantidad;           // Cantidad numérica (usado como ID de Petición)
    private String estado;          // Estado actual de la solicitud (CREADA, VALIDADA, PROCESADA, RECHAZADA)
    private LocalDateTime fechaCreacion;      // Fecha y hora de creación
    private LocalDateTime fechaProcesamiento; // Fecha y hora de procesamiento

    // Constructores, getters y setters
    // ...
}
```

### Estados de la Solicitud

| Estado | Descripción | Asignado por |
|--------|-------------|--------------|
| `CREADA` | Estado inicial cuando se crea la solicitud | Frontend Service |
| `VALIDADA` | La solicitud ha pasado la validación | INGR Service |
| `RECHAZADA` | La solicitud no cumple los criterios de validación | INGR Service |
| `PROCESADA` | La solicitud ha sido procesada completamente | CTRL Service |

### Modelo JSON

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "nombreCliente": "Ejemplo Cliente",
  "descripcion": "Descripción de la solicitud",
  "cantidad": 12345,
  "estado": "PROCESADA",
  "fechaCreacion": "2023-03-20T14:30:45.123",
  "fechaProcesamiento": "2023-03-20T14:31:12.456"
}
```

### Esquema MongoDB

Colección: `solicitud`

```javascript
{
  "_id": "550e8400-e29b-41d4-a716-446655440000",  // Mismo que el campo id
  "nombreCliente": "Ejemplo Cliente",
  "descripcion": "Descripción de la solicitud",
  "cantidad": 12345,
  "estado": "PROCESADA",
  "fechaCreacion": ISODate("2023-03-20T14:30:45.123Z"),
  "fechaProcesamiento": ISODate("2023-03-20T14:31:12.456Z"),
  "_class": "com.solicitudes.model.Solicitud"     // Añadido por Spring Data
}
```

## API REST

### Frontend Service

#### Crear Solicitud

**Endpoint**: `POST /api/solicitudes`

**Request Body**:
```json
{
  "nombreCliente": "Ejemplo Cliente",
  "descripcion": "Pedido de material de oficina",
  "cantidad": 12345
}
```

**Response**: `200 OK`
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "nombreCliente": "Ejemplo Cliente",
  "descripcion": "Pedido de material de oficina",
  "cantidad": 12345,
  "estado": "CREADA",
  "fechaCreacion": "2023-03-20T14:30:45.123",
  "fechaProcesamiento": null
}
```

#### Obtener Histórico

**Endpoint**: `GET /api/solicitudes/historico`

**Response**: `200 OK`
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "nombreCliente": "Ejemplo Cliente",
    "descripcion": "Pedido de material de oficina",
    "cantidad": 12345,
    "estado": "PROCESADA",
    "fechaCreacion": "2023-03-20T14:30:45.123",
    "fechaProcesamiento": "2023-03-20T14:31:12.456"
  },
  {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "nombreCliente": "Otro Cliente",
    "descripcion": "Otra solicitud",
    "cantidad": 67890,
    "estado": "PROCESADA",
    "fechaCreacion": "2023-03-20T15:00:00.000",
    "fechaProcesamiento": "2023-03-20T15:01:30.000"
  }
]
```

### CTRL Service

#### Obtener Todas las Solicitudes

**Endpoint**: `GET /ctrl/solicitudes`

**Response**: `200 OK`
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "nombreCliente": "Ejemplo Cliente",
    "descripcion": "Pedido de material de oficina",
    "cantidad": 12345,
    "estado": "PROCESADA",
    "fechaCreacion": "2023-03-20T14:30:45.123",
    "fechaProcesamiento": "2023-03-20T14:31:12.456"
  },
  // Más solicitudes...
]
```

## Diagramas Técnicos

### Diagrama de Secuencia: Creación de Solicitud

```
┌─────────┐     ┌────────────┐     ┌────────────┐     ┌────────────┐     ┌────────┐      ┌──────────┐
│ Cliente  │     │  Frontend  │     │    Kafka   │     │    INGR    │     │  Kafka  │     │   CTRL   │
│ Browser  │     │  Service   │     │            │     │  Service   │     │         │     │ Service  │
└────┬────┘     └──────┬─────┘     └──────┬─────┘     └──────┬─────┘     └────┬────┘     └────┬─────┘
     │                 │                  │                  │                 │               │
     │ POST /api/solic.│                  │                  │                 │               │
     │────────────────>│                  │                  │                 │               │
     │                 │                 │ Asignar ID,fecha │                  │               │
     │                 │─────────────────>│                  │                 │               │
     │                 │                  │                  │                 │               │
     │                 │    Send to       │                  │                 │               │
     │                 │ solicitudes-entr.│                  │                 │               │
     │                 │────────────────>│                  │                 │               │
     │                 │                  │                  │                 │               │
     │                 │                  │   Consume from   │                 │               │
     │                 │                  │ solicitudes-entr.│                 │               │
     │                 │                  │─────────────────>│                 │               │
     │                 │                  │                  │                 │               │
     │                 │                  │                  │ Validar         │               │
     │                 │                  │                  │────────────────>│               │
     │                 │                  │                  │                 │               │
     │                 │                  │                  │   Send to       │               │
     │                 │                  │                  │solicitudes-proc.│               │
     │                 │                  │                  │────────────────>│               │
     │                 │                  │                  │                 │               │
     │                 │                  │                  │                 │  Consume from │
     │                 │                  │                  │                 │solic.-proc.   │
     │                 │                  │                  │                 │──────────────>│
     │                 │                  │                  │                 │               │
     │                 │                  │                  │                 │               │Procesar
     │                 │                  │                  │                 │               │───────>
     │                 │                  │                  │                 │               │
     │                 │                  │                  │                 │               │Guardar
     │                 │                  │                  │                 │               │MongoDB
     │                 │                  │                  │                 │               │───────>
     │                 │                  │                  │                 │               │
     │                 │                  │                  │                 │   Send to     │
     │                 │                  │                  │                 │solic.-final.  │
     │                 │                  │                  │                 │<──────────────│
     │                 │                  │                  │                 │               │
     │  Consume from   │                  │                  │                 │               │
     │solic.-final. via│                  │                  │                 │               │
     │  WebSocket      │                  │                  │                 │               │
     │<────────────────│                  │                  │                 │               │
     │                 │                  │                  │                 │               │
     │ Actualizar UI   │                  │                  │                 │               │
     │────────────────>│                  │                  │                 │               │
     │                 │                  │                  │                 │               │
```

### Diagrama de Clases

```
┌───────────────┐      ┌───────────────────┐      ┌───────────────────┐
│  <<Entity>>   │      │  <<Controller>>   │      │   <<Service>>     │
│   Solicitud   │◄─────┤SolicitudController│◄─────┤ SolicitudService  │
└───────────────┘      └───────────────────┘      └───────────────────┘
       ▲                                                 │
       │                                                 │
       │                                                 ▼
┌───────────────┐                               ┌───────────────────┐
│ <<Repository>>│                               │<<KafkaProducer>>  │
│SolicitudRepo  │                               │  KafkaTemplate    │
└───────────────┘                               └───────────────────┘
```

## Patrones de Diseño

### Patrón Pub-Sub (Publicación-Suscripción)

El sistema implementa el patrón Pub-Sub mediante Apache Kafka:

1. Los **publicadores** son:
   - Frontend Service: Publica solicitudes creadas
   - INGR Service: Publica solicitudes validadas
   - CTRL Service: Publica confirmaciones de procesamiento

2. Los **suscriptores** son:
   - INGR Service: Suscrito a solicitudes creadas
   - CTRL Service: Suscrito a solicitudes validadas
   - Frontend Service: Suscrito a confirmaciones de procesamiento

```java
// Ejemplo de publicador (Frontend Service)
@Service
public class SolicitudService {
    @Autowired
    private KafkaTemplate<String, Solicitud> kafkaTemplate;
    
    public Solicitud enviarSolicitud(Solicitud solicitud) {
        // Preparar solicitud...
        kafkaTemplate.send("solicitudes-entrada", solicitud);
        return solicitud;
    }
}

// Ejemplo de suscriptor (INGR Service)
@Service
public class SolicitudValidatorService {
    @Autowired
    private KafkaTemplate<String, Solicitud> kafkaTemplate;
    
    @KafkaListener(topics = "solicitudes-entrada", groupId = "ingr-group")
    public void validarSolicitud(Solicitud solicitud) {
        // Validar solicitud...
        kafkaTemplate.send("solicitudes-procesadas", solicitud);
    }
}
```

### Patrón Observer

Implementado mediante WebSockets para notificaciones en tiempo real:

```java
// Frontend Service - Recibir notificaciones y enviar a clientes
@Service
public class NotificacionService {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @KafkaListener(topics = "solicitudes-finalizadas", groupId = "frontend-group")
    public void enviarNotificacion(Solicitud solicitud) {
        // Enviar a todos los clientes suscritos
        messagingTemplate.convertAndSend("/topic/solicitudes", solicitud);
    }
}
```

### Patrón Repository

Implementado para el acceso a datos en MongoDB:

```java
// CTRL Service - Repositorio para acceso a MongoDB
public interface SolicitudRepository extends MongoRepository<Solicitud, String> {
    // Métodos adicionales específicos si son necesarios
}
```

## WebSockets

### Configuración del Servidor

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }
    
    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").withSockJS();
    }
}
```

### Configuración del Cliente

```javascript
// Código JavaScript en app.js
function connectWebSocket() {
    const socket = new SockJS('/ws');
    const stompClient = Stomp.over(socket);
    
    stompClient.connect({}, function() {
        console.log('Conectado al WebSocket');
        
        stompClient.subscribe('/topic/solicitudes', function(message) {
            const solicitud = JSON.parse(message.body);
            updateRequestsList(solicitud);
        });
    });
}
```

## Configuración de Kafka

### Configuración en INGR Service

```java
@Configuration
@EnableKafka
public class KafkaConfig {
    
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    
    @Bean
    public ConsumerFactory<String, Solicitud> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "ingr-group");
        
        JsonDeserializer<Solicitud> deserializer = new JsonDeserializer<>(Solicitud.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }
    
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Solicitud> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Solicitud> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
    
    @Bean
    public ProducerFactory<String, Solicitud> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }
    
    @Bean
    public KafkaTemplate<String, Solicitud> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
```

### Topics de Kafka

| Nombre del Topic | Propósito | Productor | Consumidor |
|------------------|-----------|-----------|------------|
| `solicitudes-entrada` | Solicitudes nuevas creadas | Frontend | INGR |
| `solicitudes-procesadas` | Solicitudes validadas | INGR | CTRL |
| `solicitudes-finalizadas` | Solicitudes procesadas | CTRL | Frontend |

## Configuración de MongoDB

### Configuración en CTRL Service

```properties
# application.properties
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=solicitudes_db
```

### Repositorio MongoDB

```java
// CTRL Service
public interface SolicitudRepository extends MongoRepository<Solicitud, String> {
    // Spring Data genera automáticamente las implementaciones
}
```

### Consultas MongoDB

```java
// Ejemplo en SolicitudController
@GetMapping("/solicitudes")
public ResponseEntity<List<Solicitud>> obtenerTodasLasSolicitudes() {
    // Usar MongoTemplate para flexibilidad en consultas
    List<Solicitud> solicitudes = mongoTemplate.findAll(Solicitud.class, "solicitud");
    
    // O usar el repositorio para operaciones estándar
    // List<Solicitud> solicitudes = solicitudRepository.findAll();
    
    return new ResponseEntity<>(solicitudes, HttpStatus.OK);
}
```

## Serialización/Deserialización

El sistema utiliza Jackson para la serialización/deserialización JSON:

```java
// Configuración para fechas
@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
private LocalDateTime fechaCreacion;

// Deserializador personalizado para LocalDateTime (si es necesario)
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String dateString = p.getText();
        return LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME);
    }
}
```

---

*[Volver al índice principal](WIKI.md)* 
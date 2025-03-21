# 🧩 Componentes del Sistema

Este documento detalla los principales componentes de cada microservicio, sus interacciones y responsabilidades.

## Frontend Service

El microservicio Frontend es el punto de entrada para los usuarios. Proporciona la interfaz de usuario y gestiona la comunicación con los otros servicios.

### Componentes Principales

#### Controllers

| Clase | Responsabilidad |
|-------|----------------|
| **SolicitudController** | Maneja endpoints REST para creación y consulta de solicitudes |

```java
/**
 * Controlador REST del servicio Frontend.
 * Proporciona endpoints para la creación y consulta de solicitudes.
 */
@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {
    
    @Autowired
    private SolicitudService solicitudService;
    
    @PostMapping
    public ResponseEntity<Solicitud> crearSolicitud(@RequestBody Solicitud solicitud) {
        // Crear y enviar solicitud
    }
    
    @GetMapping("/historico")
    public ResponseEntity<List<Solicitud>> obtenerHistorico() {
        // Obtener histórico de solicitudes
    }
}
```

#### Services

| Clase | Responsabilidad |
|-------|----------------|
| **SolicitudService** | Envía solicitudes y consulta el histórico |
| **NotificacionService** | Recibe actualizaciones de Kafka y envía a WebSockets |

```java
/**
 * Servicio principal del frontend para gestionar solicitudes.
 */
@Service
public class SolicitudService {
    
    @Autowired
    private KafkaTemplate<String, Solicitud> kafkaTemplate;
    
    public Solicitud enviarSolicitud(Solicitud solicitud) {
        // Enviar a Kafka
    }
    
    public List<Solicitud> obtenerHistorico() {
        // Consultar histórico al CTRL Service
    }
}
```

#### Configuración

| Clase | Responsabilidad |
|-------|----------------|
| **WebSocketConfig** | Configura conexiones WebSocket |
| **AppConfig** | Configuración general y beans |

### Interfaz Web

| Recurso | Descripción |
|---------|-------------|
| **index.html** | Página principal con formulario y visualización |
| **app.js** | Lógica JavaScript para formulario y WebSockets |
| **styles.css** | Estilos de la interfaz de usuario |

## INGR Service

El servicio INGR (Ingesta) es responsable de recibir y validar las solicitudes antes de enviarlas para su procesamiento.

### Componentes Principales

#### Services

| Clase | Responsabilidad |
|-------|----------------|
| **SolicitudValidatorService** | Valida y enriquece solicitudes |

```java
/**
 * Servicio encargado de validar las solicitudes que llegan desde el frontend.
 */
@Service
public class SolicitudValidatorService {

    @Autowired
    private KafkaTemplate<String, Solicitud> kafkaTemplate;
    
    @KafkaListener(topics = "solicitudes-entrada", groupId = "ingr-group")
    public void validarSolicitud(Solicitud solicitud) {
        // Validar solicitud
        // Establecer estado
        // Enviar a topic de solicitudes procesadas
    }
}
```

#### Configuration

| Clase | Responsabilidad |
|-------|----------------|
| **KafkaConfig** | Configura conexiones a Kafka |

## CTRL Service

El servicio CTRL (Control) procesa las solicitudes validadas y las almacena en MongoDB. También proporciona endpoints para consultar el histórico.

### Componentes Principales

#### Controllers

| Clase | Responsabilidad |
|-------|----------------|
| **SolicitudController** | Proporciona endpoints para consultar solicitudes |

```java
/**
 * Controlador REST del servicio CTRL.
 * Proporciona endpoints para consultar las solicitudes almacenadas en MongoDB.
 */
@RestController
@RequestMapping("/ctrl")
@CrossOrigin(origins = "*")
public class SolicitudController {

    @Autowired
    private MongoTemplate mongoTemplate;
    
    @GetMapping("/solicitudes")
    public ResponseEntity<List<Solicitud>> obtenerTodasLasSolicitudes() {
        // Obtener solicitudes de MongoDB
    }
}
```

#### Services

| Clase | Responsabilidad |
|-------|----------------|
| **SolicitudProcessorService** | Procesa solicitudes validadas |

```java
/**
 * Servicio encargado de procesar las solicitudes recibidas desde Kafka,
 * almacenarlas en MongoDB y publicar confirmaciones.
 */
@Service
public class SolicitudProcessorService {

    @Autowired
    private SolicitudRepository solicitudRepository;
    
    @Autowired
    private KafkaTemplate<String, Solicitud> kafkaTemplate;
    
    @KafkaListener(topics = "solicitudes-procesadas", groupId = "ctrl-group")
    public void procesarSolicitud(Solicitud solicitud) {
        // Actualizar estado y fecha
        // Guardar en MongoDB
        // Publicar confirmación
    }
}
```

#### Repositories

| Clase | Responsabilidad |
|-------|----------------|
| **SolicitudRepository** | Interfaz para acceso a MongoDB |

```java
/**
 * Repositorio para acceder y manipular las solicitudes almacenadas en MongoDB.
 */
public interface SolicitudRepository extends MongoRepository<Solicitud, String> {
    // Métodos heredados de MongoRepository
}
```

## Módulo Common

El módulo common contiene clases compartidas entre todos los microservicios.

### Componentes Principales

#### Model

| Clase | Responsabilidad |
|-------|----------------|
| **Solicitud** | Modelo de datos para solicitudes |

```java
/**
 * Clase modelo que representa una solicitud en el sistema.
 */
public class Solicitud {
    private String id;              // Identificador único
    private String nombreCliente;   // Nombre del cliente
    private String descripcion;     // Descripción
    private int cantidad;           // ID de Petición
    private String estado;          // Estado actual
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaProcesamiento;
    
    // Constructores, getters y setters
}
```

## Interacciones entre Componentes

```
[Frontend - SolicitudController] → [Frontend - SolicitudService] → [Kafka Topic: solicitudes-entrada]
                                                                        ↓
[Kafka Topic: solicitudes-finalizadas] ← [CTRL - SolicitudProcessorService] ← [INGR - SolicitudValidatorService]
        ↓                                            ↓
[Frontend - NotificacionService]      [MongoDB - solicitud Collection]
        ↓                                            ↑
[WebSocket → Cliente]             [Frontend - SolicitudService] → [CTRL - SolicitudController]
```

## Ciclo de Vida de una Solicitud

1. **Creación**: Usuario completa formulario → Frontend SolicitudController
2. **Envío**: SolicitudService publica en "solicitudes-entrada"
3. **Validación**: INGR SolicitudValidatorService valida y publica en "solicitudes-procesadas"
4. **Procesamiento**: CTRL SolicitudProcessorService procesa y guarda en MongoDB
5. **Notificación**: CTRL publica en "solicitudes-finalizadas"
6. **Visualización**: Frontend NotificacionService actualiza UI vía WebSocket
7. **Consulta**: Usuario solicita histórico → Frontend consulta CTRL

---

*[Volver al índice principal](WIKI.md)* 
# 📊 Flujo de Datos

Este documento detalla cómo fluye la información a través del sistema, desde la creación de una solicitud hasta su procesamiento final y visualización.

## Diagrama de Flujo General

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  1. Cliente │────▶│ 2. Frontend │────▶│   3. INGR   │────▶│   4. CTRL   │
│    (Form)   │     │   Service   │     │   Service   │     │   Service   │
└─────────────┘     └─────────────┘     └─────────────┘     └─────────────┘
                           ▲                                       │
                           │                                       ▼
                           │                                ┌─────────────┐
                           │                                │  5. MongoDB │
                           └────────────────────────────────│    (Save)   │
                           6. Respuesta en tiempo real      └─────────────┘
```

## Etapas del Flujo de Datos

### 1. Creación de Solicitud

**Origen**: Interfaz de usuario (Formulario HTML)

**Datos capturados**:
- Nombre Cliente (campo: `nombreCliente`)
- Descripción (campo: `descripcion`) 
- ID Petición (campo: `cantidad`)

**Formato**:
```json
{
  "nombreCliente": "Ejemplo Cliente",
  "descripcion": "Solicitud de prueba",
  "cantidad": 12345
}
```

**Código JavaScript**:
```javascript
function handleFormSubmit(event) {
    event.preventDefault();
    
    const solicitud = {
        nombreCliente: document.getElementById('nombreCliente').value,
        descripcion: document.getElementById('descripcion').value,
        cantidad: parseInt(document.getElementById('cantidad').value)
    };
    
    // Enviar solicitud al servidor
    fetch('/api/solicitudes', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(solicitud)
    })
    .then(response => response.json())
    .then(data => console.log('Solicitud enviada:', data));
}
```

### 2. Envío a Kafka (Frontend → INGR)

**Componente**: `SolicitudService.enviarSolicitud()`

**Topic Kafka**: `solicitudes-entrada`

**Transformación**:
- Se genera ID único (UUID)
- Se establece fecha de creación
- Se establece estado inicial "CREADA"

**Formato**:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "nombreCliente": "Ejemplo Cliente",
  "descripcion": "Solicitud de prueba",
  "cantidad": 12345,
  "estado": "CREADA",
  "fechaCreacion": "2023-03-20T14:30:45.123",
  "fechaProcesamiento": null
}
```

### 3. Validación (INGR Service)

**Componente**: `SolicitudValidatorService.validarSolicitud()`

**Reglas de validación**:
- La cantidad debe ser positiva
- Nombre y descripción no pueden estar vacíos

**Transformación**:
- Se actualiza el estado a "VALIDADA" o "RECHAZADA"

**Destino**: Topic Kafka `solicitudes-procesadas`

### 4. Procesamiento (CTRL Service)

**Componente**: `SolicitudProcessorService.procesarSolicitud()`

**Transformación**:
- Se establece estado "PROCESADA"
- Se registra fecha de procesamiento

**Almacenamiento**:
- MongoDB, colección `solicitud`

**Notificación**:
- Publica en topic Kafka `solicitudes-finalizadas`

### 5. Almacenamiento (MongoDB)

**Colección**: `solicitud`

**Consulta**:
```java
// Guardar solicitud
solicitudRepository.save(solicitud);

// Consultar todas las solicitudes
List<Solicitud> solicitudes = mongoTemplate.findAll(Solicitud.class, "solicitud");
```

### 6. Notificación al Cliente (WebSocket)

**Componente**: `NotificacionService.enviarNotificacion()`

**Canal WebSocket**: `/topic/solicitudes`

**Código JavaScript**:
```javascript
// Configuración del WebSocket
function connectWebSocket() {
    const socket = new SockJS('/ws');
    const stompClient = Stomp.over(socket);
    
    stompClient.connect({}, function() {
        console.log('Conectado al WebSocket');
        
        // Suscribirse al topic de solicitudes
        stompClient.subscribe('/topic/solicitudes', function(message) {
            const solicitud = JSON.parse(message.body);
            console.log('Actualización recibida:', solicitud);
            updateRequestsList(solicitud);
        });
    });
}
```

## Consulta del Histórico

### Flujo de Consulta

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Cliente   │───▶│   Frontend  │───▶│     CTRL    │───▶│   MongoDB   │
│ (Botón Ver) │    │  Controller │    │ Controller  │    │   (Query)   │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
       ▲                                     │
       └─────────────────────────────────────┘
                  Respuesta JSON
```

### Endpoint de Consulta

**URL**: `GET /api/solicitudes/historico`

**Componentes involucrados**:
1. Frontend: `SolicitudController.obtenerHistorico()`
2. Frontend: `SolicitudService.obtenerHistorico()`
3. CTRL: `SolicitudController.obtenerTodasLasSolicitudes()`

**Formato de Respuesta**:
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "nombreCliente": "Ejemplo Cliente",
    "descripcion": "Solicitud de prueba",
    "cantidad": 12345,
    "estado": "PROCESADA",
    "fechaCreacion": "2023-03-20T14:30:45.123",
    "fechaProcesamiento": "2023-03-20T14:31:12.456"
  },
  {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "nombreCliente": "Otro Cliente",
    "descripcion": "Segunda solicitud",
    "cantidad": 67890,
    "estado": "PROCESADA",
    "fechaCreacion": "2023-03-20T15:10:22.789",
    "fechaProcesamiento": "2023-03-20T15:11:05.123"
  }
]
```

## Persistencia de los Datos

### Estructura de la Colección MongoDB

Colección: `solicitud`

Ejemplo de documento:
```json
{
  "_id": "550e8400-e29b-41d4-a716-446655440000",
  "nombreCliente": "Ejemplo Cliente",
  "descripcion": "Solicitud de prueba",
  "cantidad": 12345,
  "estado": "PROCESADA",
  "fechaCreacion": "2023-03-20T14:30:45.123Z",
  "fechaProcesamiento": "2023-03-20T14:31:12.456Z",
  "_class": "com.solicitudes.model.Solicitud"
}
```

### Índices Recomendados

```javascript
// Crear índice por estado para optimizar consultas filtradas
db.solicitud.createIndex({ "estado": 1 });

// Crear índice por fecha de creación para ordenamiento
db.solicitud.createIndex({ "fechaCreacion": -1 });
```

## Transformaciones de Datos

| Etapa | Estado Inicial | Estado Final | Campos Añadidos/Modificados |
|-------|----------------|--------------|------------------------------|
| Frontend | N/A | "CREADA" | id, fechaCreacion, estado |
| INGR | "CREADA" | "VALIDADA" o "RECHAZADA" | estado |
| CTRL | "VALIDADA" | "PROCESADA" | estado, fechaProcesamiento |

## Mapeo de Datos entre Sistemas

| Sistema Origen | Sistema Destino | Formato | Protocolo | Transformación |
|----------------|-----------------|---------|-----------|----------------|
| Frontend UI | Frontend Service | JSON | HTTP | Serialización de form |
| Frontend Service | Kafka | JSON | Kafka | Adición de metadatos |
| Kafka | INGR Service | JSON | Kafka | Deserialización |
| INGR Service | Kafka | JSON | Kafka | Actualización de estado |
| Kafka | CTRL Service | JSON | Kafka | Deserialización |
| CTRL Service | MongoDB | BSON | TCP | Mapeo a documento |
| CTRL Service | Kafka | JSON | Kafka | Adición fecha proceso |
| Kafka | Frontend Service | JSON | Kafka | Deserialización |
| Frontend Service | Cliente | JSON | WebSocket | Serialización |

---

*[Volver al índice principal](WIKI.md)* 
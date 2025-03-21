# 🔄 Flujo de Datos

Este documento detalla el flujo de datos en el Sistema de Solicitudes Distribuido, desde el momento en que un usuario crea una solicitud hasta que ésta es procesada y notificada.

## 📋 Índice

- [Visión General](#visión-general)
- [Ciclo de Vida de una Solicitud](#ciclo-de-vida-de-una-solicitud)
- [Flujo Principal](#flujo-principal)
- [Modelo de Evento](#modelo-de-evento)
- [Topics de Kafka](#topics-de-kafka)
- [Estados de una Solicitud](#estados-de-una-solicitud)
- [Manejo de Errores](#manejo-de-errores)
- [Escenarios Alternativos](#escenarios-alternativos)

## Visión General

El sistema implementa un flujo de procesamiento dirigido por eventos, donde cada solicitud atraviesa diferentes etapas desde su creación hasta su finalización. La comunicación entre servicios se realiza de forma asíncrona mediante Apache Kafka, lo que permite un desacoplamiento efectivo y alta escalabilidad.

```
Usuario → Frontend → Kafka → INGR → Kafka → CTRL → MongoDB
                      ↑                               |
                      └───────────────────────────────┘
```

## Ciclo de Vida de una Solicitud

Una solicitud en el sistema pasa por diferentes estados durante su procesamiento:

1. **CREADA**: La solicitud ha sido creada por el usuario pero aún no ha sido validada.
2. **EN_VALIDACION**: La solicitud está siendo validada por el servicio INGR.
3. **VALIDADA**: La solicitud ha pasado todas las validaciones y está lista para ser procesada.
4. **RECHAZADA**: La solicitud no ha pasado las validaciones y ha sido rechazada.
5. **EN_PROCESO**: La solicitud está siendo procesada por el servicio CTRL.
6. **COMPLETADA**: La solicitud ha sido procesada correctamente y está almacenada en la base de datos.
7. **ERROR**: Ha ocurrido un error durante el procesamiento de la solicitud.

## Flujo Principal

El flujo principal de datos en el sistema sigue estos pasos:

### 1. Creación de la Solicitud (Frontend Service)

```json
// Datos de entrada del formulario
{
  "nombreCliente": "Juan Pérez",
  "descripcion": "Solicitud de servicio técnico",
  "cantidad": 1,
  "tipoPrioridad": "NORMAL",
  "departamento": "SOPORTE"
}
```

- El usuario rellena un formulario en la interfaz web
- El Frontend Service valida los datos básicos del formulario
- Se crea un objeto `Solicitud` con un estado inicial "CREADA"
- Se genera un ID único para la solicitud
- Se añade timestamp de creación

```json
// Objeto Solicitud creado
{
  "id": "SOL-2023-0001",
  "nombreCliente": "Juan Pérez",
  "descripcion": "Solicitud de servicio técnico",
  "cantidad": 1,
  "tipoPrioridad": "NORMAL",
  "departamento": "SOPORTE",
  "estado": "CREADA",
  "fechaCreacion": "2023-06-15T14:30:00Z",
  "ultimaActualizacion": "2023-06-15T14:30:00Z"
}
```

### 2. Publicación en Kafka (Frontend Service)

- El Frontend Service serializa el objeto `Solicitud`
- Publica el mensaje en el topic `solicitudes-entrada`
- La clave del mensaje es el ID de la solicitud para garantizar orden
- Se envía una respuesta al usuario confirmando la recepción

### 3. Validación (INGR Service)

- El INGR Service consume el mensaje del topic `solicitudes-entrada`
- Cambia el estado de la solicitud a "EN_VALIDACION"
- Aplica reglas de validación:
  - Verifica campos obligatorios
  - Valida rangos y formatos
  - Aplica reglas de negocio específicas
- Si la validación es exitosa:
  - Cambia el estado a "VALIDADA"
  - Enriquece la solicitud con datos adicionales
- Si la validación falla:
  - Cambia el estado a "RECHAZADA"
  - Añade información sobre los motivos del rechazo

```json
// Objeto Solicitud después de validación exitosa
{
  "id": "SOL-2023-0001",
  "nombreCliente": "Juan Pérez",
  "descripcion": "Solicitud de servicio técnico",
  "cantidad": 1,
  "tipoPrioridad": "NORMAL",
  "departamento": "SOPORTE",
  "estado": "VALIDADA",
  "fechaCreacion": "2023-06-15T14:30:00Z",
  "ultimaActualizacion": "2023-06-15T14:31:05Z",
  "validadoPor": "ingr-service-1",
  "tiempoEstimado": "24h",
  "codigoProcesamiento": "SP-TECH-001"
}
```

### 4. Publicación del Resultado de Validación (INGR Service)

- El INGR Service serializa el objeto `Solicitud` actualizado
- Publica el mensaje en el topic `solicitudes-procesadas`
- La clave del mensaje sigue siendo el ID de la solicitud

### 5. Procesamiento Final (CTRL Service)

- El CTRL Service consume el mensaje del topic `solicitudes-procesadas`
- Ignora solicitudes con estado "RECHAZADA"
- Para solicitudes "VALIDADA":
  - Cambia el estado a "EN_PROCESO"
  - Aplica lógica de procesamiento específica según el departamento
  - Genera información adicional de procesamiento
  - Cambia el estado a "COMPLETADA"
  - Almacena la solicitud en MongoDB

```json
// Objeto Solicitud después de procesamiento final
{
  "id": "SOL-2023-0001",
  "nombreCliente": "Juan Pérez",
  "descripcion": "Solicitud de servicio técnico",
  "cantidad": 1,
  "tipoPrioridad": "NORMAL",
  "departamento": "SOPORTE",
  "estado": "COMPLETADA",
  "fechaCreacion": "2023-06-15T14:30:00Z",
  "ultimaActualizacion": "2023-06-15T14:32:30Z",
  "validadoPor": "ingr-service-1",
  "tiempoEstimado": "24h",
  "codigoProcesamiento": "SP-TECH-001",
  "procesadoPor": "ctrl-service-1",
  "referenciaProceso": "PROC-2023-0001",
  "observaciones": "Asignado a equipo de soporte técnico nivel 1"
}
```

### 6. Notificación de Finalización (CTRL Service)

- El CTRL Service serializa el objeto `Solicitud` final
- Publica el mensaje en el topic `solicitudes-finalizadas`
- La clave del mensaje es el ID de la solicitud

### 7. Actualización en Tiempo Real (Frontend Service)

- El Frontend Service consume el mensaje del topic `solicitudes-finalizadas`
- Procesa la información final de la solicitud
- Envía una notificación al cliente mediante WebSocket
- La interfaz del usuario se actualiza en tiempo real con el estado final

## Modelo de Evento

El sistema utiliza un modelo de evento consistente para la comunicación entre servicios:

```json
{
  "eventId": "evt-12345",
  "eventType": "SolicitudEvent",
  "source": "frontend-service",
  "timestamp": "2023-06-15T14:30:00Z",
  "payload": {
    // Objeto Solicitud
  },
  "metadata": {
    "version": "1.0",
    "correlationId": "corr-67890",
    "userId": "user-001"
  }
}
```

Este formato garantiza:
- Trazabilidad mediante IDs únicos
- Información de origen del evento
- Timestamps para ordenación temporal
- Metadata para procesamiento adicional

## Topics de Kafka

El sistema utiliza tres topics principales en Kafka:

| Topic | Propósito | Productores | Consumidores |
|-------|-----------|-------------|--------------|
| `solicitudes-entrada` | Recepción de nuevas solicitudes | Frontend Service | INGR Service |
| `solicitudes-procesadas` | Solicitudes validadas | INGR Service | CTRL Service |
| `solicitudes-finalizadas` | Solicitudes completadas | CTRL Service | Frontend Service |

Configuración de topics:

- Retención: 7 días
- Replicación: Factor 3
- Particiones: 3-5 por topic
- Compresión: GZIP

## Estados de una Solicitud

El siguiente diagrama muestra las transiciones de estado posibles:

```
             ┌───────────────┐
             │     CREADA    │
             └───────┬───────┘
                     │
                     ▼
             ┌───────────────┐
             │ EN_VALIDACION │
             └───────┬───────┘
                     │
           ┌─────────┴─────────┐
           │                   │
           ▼                   ▼
  ┌───────────────┐    ┌───────────────┐
  │    VALIDADA   │    │   RECHAZADA   │
  └───────┬───────┘    └───────────────┘
          │
          ▼
  ┌───────────────┐
  │  EN_PROCESO   │
  └───────┬───────┘
          │
          ├─────────────┐
          │             │
          ▼             ▼
  ┌───────────────┐    ┌───────────────┐
  │  COMPLETADA   │    │     ERROR     │
  └───────────────┘    └───────────────┘
```

## Manejo de Errores

El sistema implementa varios mecanismos para manejar errores durante el flujo de datos:

### Retry Automático

- Kafka está configurado con políticas de reintentos
- Intentos máximos: 3
- Backoff exponencial entre reintentos

### Dead Letter Queue

- Topic especial: `solicitudes-error`
- Almacena mensajes que no pudieron ser procesados después de reintentos
- Un servicio de monitoreo analiza estos mensajes

### Circuit Breaker

- Implementado en las comunicaciones entre servicios
- Evita fallos en cascada
- Thresholds configurables para apertura del circuito

### Manejo de Excepciones

Cada servicio implementa manejo específico para diferentes tipos de errores:

- **Errores de validación**: Rechaza la solicitud con información detallada
- **Errores de comunicación**: Reintenta con backoff
- **Errores internos**: Registra en logs y envía a monitoreo

## Escenarios Alternativos

### Solicitud Rechazada

Cuando una solicitud es rechazada durante la validación:

1. INGR Service cambia el estado a "RECHAZADA"
2. Añade motivos de rechazo en el campo `motivosRechazo`
3. Publica en `solicitudes-procesadas` con el estado "RECHAZADA"
4. Frontend Service recibe la notificación y actualiza la interfaz
5. No se almacena en MongoDB (opcionalmente puede guardarse en una colección separada)

### Error Durante Procesamiento

Si ocurre un error durante el procesamiento en CTRL Service:

1. CTRL Service detecta el error
2. Cambia el estado a "ERROR"
3. Documenta el error en el campo `detalleError`
4. Intenta reiniciar el procesamiento (configurable)
5. Si persiste, publica en `solicitudes-finalizadas` con estado "ERROR"
6. Frontend Service notifica al usuario
7. El registro queda en MongoDB para diagnóstico

### Timeout de Procesamiento

Si una solicitud tarda demasiado en procesarse:

1. Un servicio de monitoreo detecta solicitudes estancadas
2. Se puede reiniciar manualmente el procesamiento
3. Los administradores reciben alertas
4. Las solicitudes pueden marcarse como "EN_REVISION"

---

*[Volver al índice principal](WIKI.md)* 
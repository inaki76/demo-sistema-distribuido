# Manual de Monitoreo y Verificación del Sistema

## 1. Verificación de MongoDB

### Conexión a MongoDB
```bash
# Conectar al contenedor de MongoDB
docker exec -it mongodb mongosh

# O conectar desde fuera del contenedor
mongosh --host localhost --port 27017
```

### Consultar solicitudes almacenadas
```bash
# Seleccionar base de datos
use solicitudes-db

# Listar colecciones
show collections

# Consultar todas las solicitudes
db.solicitudes.find()

# Consultar solicitudes procesadas
db.solicitudes.find({estado: "PROCESADA"})

# Consultar solicitudes por ID
db.solicitudes.find({id: "12345"})

# Consultar con formato más legible
db.solicitudes.find().pretty()
```

## 2. Monitoreo de Kafka

### Listar todos los topics
```bash
# Ejecutar desde el contenedor de Kafka
docker exec -it kafka kafka-topics --bootstrap-server localhost:9092 --list
```

### Topics principales del sistema
- `solicitudes-entrada`: Solicitudes iniciales enviadas desde el Frontend
- `solicitudes-procesadas`: Solicitudes validadas por el servicio INGR
- `solicitudes-finalizadas`: Confirmaciones de procesamiento del servicio CTRL

### Consumir mensajes de un topic
```bash
# Ver mensajes desde el inicio
docker exec -it kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic solicitudes-entrada --from-beginning

# Ver nuevos mensajes en tiempo real
docker exec -it kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic solicitudes-finalizadas
```

### Verificar detalles de un topic
```bash
docker exec -it kafka kafka-topics --bootstrap-server localhost:9092 --describe --topic solicitudes-procesadas
```

### Producir mensajes de prueba
```bash
docker exec -it kafka kafka-console-producer --bootstrap-server localhost:9092 --topic solicitudes-entrada
# Escribir mensaje JSON y presionar Enter
{"id":"test123","descripcion":"Solicitud de prueba","fecha":"2024-03-20T14:30:00"}
```

## 3. Verificación del Funcionamiento como Usuario

### Interfaz Web
1. Accede a http://localhost:8080
2. Crea una nueva solicitud con el formulario
3. Observa la actualización en tiempo real mediante WebSocket

### API REST del Frontend Service

#### Crear solicitud
```bash
curl -X POST http://localhost:8080/api/solicitudes -H "Content-Type: application/json" -d '{"descripcion":"Solicitud desde curl"}'
```

#### Obtener todas las solicitudes
```bash
curl http://localhost:8080/api/solicitudes
```

#### Obtener solicitud por ID
```bash
curl http://localhost:8080/api/solicitudes/ID_SOLICITUD
```

## 4. Verificación de Logs de Microservicios

### Inspeccionar logs mientras se procesan solicitudes
```bash
# Frontend Service
type .\frontend-service\logs\frontend.log

# INGR Service
type .\ingr-service\logs\ingr.log

# CTRL Service
type .\ctrl-service\logs\ctrl.log
```

## 5. Diagnóstico del Sistema

### Estado de los contenedores Docker
```bash
docker ps
```

### Verificar conexión a Kafka
```bash
telnet localhost 9092
```

### Verificar conexión a MongoDB
```bash
telnet localhost 27017
```

### Verificar servicios Java activos
```bash
netstat -ano | findstr "8080"
netstat -ano | findstr "8081"
netstat -ano | findstr "8082"
```

## 6. Estructura Interna de los Datos

### Modelo de datos en MongoDB
```json
{
  "_id": "ObjectId()",
  "id": "string",
  "descripcion": "string",
  "estado": "string", // "RECIBIDA", "PROCESADA", "FINALIZADA"
  "fechaCreacion": "ISODate()",
  "fechaProcesamiento": "ISODate()",
  "metadatos": {}
}
```

### Formato de mensajes en Kafka
```json
{
  "id": "string",
  "descripcion": "string",
  "estado": "string",
  "timestamp": "string",
  "metadata": {}
}
```

## 7. Diagrama de Flujo de una Solicitud

1. **Frontend** → Recibe solicitud → Publica en `solicitudes-entrada` → Espera respuesta
2. **INGR** → Consume de `solicitudes-entrada` → Valida → Publica en `solicitudes-procesadas`
3. **CTRL** → Consume de `solicitudes-procesadas` → Guarda en MongoDB → Publica en `solicitudes-finalizadas`
4. **Frontend** → Consume de `solicitudes-finalizadas` → Actualiza interfaz vía WebSocket 
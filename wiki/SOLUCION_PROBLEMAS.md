# 🔧 Solución de Problemas

Esta guía proporciona soluciones a los problemas más comunes que pueden surgir al trabajar con el Sistema de Solicitudes Distribuido.

## 📋 Índice

- [Problemas de Infraestructura](#problemas-de-infraestructura)
- [Problemas de Servicios](#problemas-de-servicios)
- [Problemas de Conectividad](#problemas-de-conectividad)
- [Problemas con Kafka](#problemas-con-kafka)
- [Problemas con MongoDB](#problemas-con-mongodb)
- [Problemas con el Frontend](#problemas-con-el-frontend)
- [Errores Comunes](#errores-comunes)

## Problemas de Infraestructura

### Error: No se pueden iniciar los contenedores Docker

```
Error response from daemon: driver failed programming external connectivity on endpoint...
```

**Solución:**
1. Verifica que no haya otros servicios usando los mismos puertos (9092, 2181, 27017)
2. Detén los contenedores y elimínalos:
   ```bash
   docker-compose down
   ```
3. Reinicia Docker Desktop (o el servicio docker en Linux)
4. Inténtalo de nuevo:
   ```bash
   docker-compose up -d
   ```

### Error: Servicio de Docker no disponible

**Solución:**
1. Verifica que Docker Desktop esté instalado y en ejecución
2. Reinicia el ordenador si es necesario
3. En Windows, asegúrate de que WSL2 esté habilitado y funcionando correctamente

## Problemas de Servicios

### Error: Servicios Java no inician

**Síntomas:** Al ejecutar `start-services.bat` o `start-services.sh`, no aparecen ventanas de consola o aparecen brevemente y se cierran.

**Solución:**
1. Verifica que Java 11+ esté instalado y configurado correctamente:
   ```bash
   java -version
   ```
2. Asegúrate de que Maven está correctamente instalado:
   ```bash
   mvn -version
   ```
3. Verifica que la infraestructura (Docker) esté en funcionamiento
4. Revisa los logs en `frontend-service/logs`, `ingr-service/logs` y `ctrl-service/logs`
5. Intenta iniciar cada servicio manualmente:
   ```bash
   cd frontend-service
   mvn spring-boot:run
   ```

### Error: "Invalid source release: 11" al compilar

**Solución:**
1. Verifica que tienes JDK 11 o superior instalado
2. Configura JAVA_HOME correctamente:
   - Windows: `set JAVA_HOME=C:\Program Files\Java\jdk-11`
   - Linux/Mac: `export JAVA_HOME=/usr/lib/jvm/java-11-openjdk`
3. Intenta compilar nuevamente

## Problemas de Conectividad

### Error: El servicio Frontend no puede conectar con el servicio CTRL

**Síntomas:** La aplicación muestra "0 solicitudes en el histórico" y en los logs aparece:
```
No se pudo obtener el histórico de solicitudes: Connection refused
```

**Solución:**
1. Asegúrate de que el servicio CTRL esté en funcionamiento
2. Verifica la configuración en `frontend-service/src/main/resources/application.properties`:
   ```properties
   ctrl.service.url=http://localhost:8082
   ```
3. Si estás usando Docker para los servicios, cambia la URL a:
   ```properties
   ctrl.service.url=http://ctrl-service:8082
   ```
4. Comprueba que el puerto 8082 no esté bloqueado por el firewall

### Error: CORS no permitido

**Síntomas:** En la consola del navegador aparece:
```
Access to XMLHttpRequest has been blocked by CORS policy
```

**Solución:**
1. Verifica que el controlador en CTRL Service tenga la anotación `@CrossOrigin`:
   ```java
   @RestController
   @CrossOrigin(origins = "*")
   public class SolicitudController {
       // ...
   }
   ```
2. Si usas un proxy o balanceador de carga, configura los encabezados CORS adecuadamente

## Problemas con Kafka

### Error: No se pueden enviar mensajes a Kafka

**Síntomas:** Al crear solicitudes, aparece en los logs:
```
org.apache.kafka.common.errors.TimeoutException: Topic solicitudes-entrada not present
```

**Solución:**
1. Verifica que Kafka esté en funcionamiento:
   ```bash
   docker ps | grep kafka
   ```
2. Conéctate al contenedor de Kafka y crea el topic manualmente:
   ```bash
   docker exec -it kafka bash
   kafka-topics.sh --create --topic solicitudes-entrada --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
   ```
3. Asegúrate de que la configuración de Kafka en cada servicio sea correcta:
   ```properties
   spring.kafka.bootstrap-servers=localhost:9092
   ```

### Error: Deserialización fallida en los mensajes Kafka

**Síntomas:** En los logs aparece:
```
Error deserializing key/value for partition solicitudes-entrada...
```

**Solución:**
1. Verifica que todos los servicios usen la misma versión de la clase `Solicitud`
2. Asegúrate de que la configuración de serialización sea consistente:
   ```java
   @Bean
   public JsonDeserializer<Solicitud> jsonDeserializer() {
       JsonDeserializer<Solicitud> deserializer = new JsonDeserializer<>(Solicitud.class);
       deserializer.addTrustedPackages("*");
       return deserializer;
   }
   ```

## Problemas con MongoDB

### Error: No se puede conectar a MongoDB

**Síntomas:** En los logs del servicio CTRL aparece:
```
com.mongodb.MongoSocketOpenException: Exception opening socket
```

**Solución:**
1. Verifica que MongoDB esté en funcionamiento:
   ```bash
   docker ps | grep mongo
   ```
2. Comprueba la configuración en `ctrl-service/src/main/resources/application.properties`:
   ```properties
   spring.data.mongodb.host=localhost
   spring.data.mongodb.port=27017
   spring.data.mongodb.database=solicitudes_db
   ```
3. Si usas Docker para los servicios, cambia el host:
   ```properties
   spring.data.mongodb.host=mongodb
   ```

### Error: Documentos no se guardan correctamente en MongoDB

**Síntomas:** Las solicitudes no aparecen en la base de datos después de ser procesadas.

**Solución:**
1. Verifica que la clase `Solicitud` tenga las anotaciones adecuadas:
   ```java
   public class Solicitud {
       @Id
       private String id;
       // ...
   }
   ```
2. Comprueba el repositorio y su implementación:
   ```java
   public interface SolicitudRepository extends MongoRepository<Solicitud, String> {
       // ...
   }
   ```
3. Conéctate a MongoDB para verificar manualmente:
   ```bash
   docker exec -it mongodb mongosh
   use solicitudes_db
   db.solicitud.find()
   ```

## Problemas con el Frontend

### Error: No se muestran las solicitudes en tiempo real

**Síntomas:** Al crear solicitudes, no aparecen en la lista de "Solicitudes Recibidas".

**Solución:**
1. Comprueba que WebSocket esté configurado correctamente:
   ```javascript
   function connectWebSocket() {
       const socket = new SockJS('/ws');
       // ...
   }
   ```
2. Verifica en la consola del navegador si hay errores de WebSocket
3. Asegúrate de que el servicio Frontend esté suscrito al topic `solicitudes-finalizadas`
4. Habilita el modo de depuración en el archivo `application.properties`:
   ```properties
   logging.level.org.springframework.web.socket=DEBUG
   ```

### Error: Interfaz de usuario no carga correctamente

**Síntomas:** Elementos de la UI no aparecen o están mal formateados.

**Solución:**
1. Limpia la caché del navegador (Ctrl+F5)
2. Verifica que todos los archivos estáticos se carguen correctamente (en la pestaña Network de las herramientas de desarrollo)
3. Asegúrate de que Bootstrap y los scripts JavaScript estén incluidos:
   ```html
   <link rel="stylesheet" href="/css/bootstrap.min.css" />
   <script src="/js/jquery.min.js"></script>
   <script src="/js/bootstrap.min.js"></script>
   <script src="/js/sockjs.min.js"></script>
   <script src="/js/stomp.min.js"></script>
   ```

## Errores Comunes

### Error: "Unsupported major.minor version"

**Causa:** Versión de Java incorrecta.

**Solución:**
1. Instala Java 11 o superior
2. Configura JAVA_HOME adecuadamente
3. Verifica con `java -version`

### Error: "Could not resolve dependencies for project"

**Causa:** Problemas con la descarga de dependencias Maven.

**Solución:**
1. Verifica la conexión a Internet
2. Limpia el repositorio local de Maven:
   ```bash
   mvn clean
   mvn dependency:purge-local-repository
   ```
3. Intenta compilar de nuevo:
   ```bash
   mvn clean install -DskipTests
   ```

### Error: "Address already in use"

**Causa:** El puerto ya está siendo utilizado por otra aplicación.

**Solución:**
1. Identifica el proceso que está usando el puerto:
   - Windows: `netstat -ano | findstr 8080`
   - Linux/Mac: `lsof -i :8080`
2. Termina el proceso:
   - Windows: `taskkill /F /PID <pid>`
   - Linux/Mac: `kill -9 <pid>`
3. Alternativamente, cambia el puerto en `application.properties`:
   ```properties
   server.port=8081
   ```

### Error: Java deja de responder con alta carga de solicitudes

**Causa:** Configuración de memoria insuficiente.

**Solución:**
1. Aumenta la memoria asignada a la JVM en los scripts de inicio:
   ```bash
   java -Xmx512m -jar ...
   ```
2. Optimiza la configuración de conexiones a Kafka y MongoDB para evitar fugas de recursos

---

Si experimentas un problema que no está listado aquí, por favor reporta el error detallado en el repositorio del proyecto para que podamos actualizar esta guía.

---

*[Volver al índice principal](WIKI.md)* 
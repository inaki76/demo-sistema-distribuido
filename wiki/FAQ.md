# ❓ Preguntas Frecuentes (FAQ)

Este documento responde a las preguntas más comunes sobre el Sistema de Solicitudes Distribuido.

## 📋 Índice

- [Preguntas Generales](#preguntas-generales)
- [Instalación y Configuración](#instalación-y-configuración)
- [Uso del Sistema](#uso-del-sistema)
- [Arquitectura y Diseño](#arquitectura-y-diseño)
- [Problemas Conocidos](#problemas-conocidos)
- [Preguntas de Desarrollo](#preguntas-de-desarrollo)

## Preguntas Generales

### ¿Qué es el Sistema de Solicitudes Distribuido?

El Sistema de Solicitudes Distribuido es una aplicación basada en microservicios que permite crear, procesar y rastrear solicitudes a través de múltiples etapas de procesamiento. Utiliza Apache Kafka para la comunicación entre servicios, MongoDB para el almacenamiento de datos y WebSockets para actualizaciones en tiempo real.

### ¿Para qué casos de uso está diseñado el sistema?

El sistema está diseñado para gestionar flujos de trabajo donde las solicitudes deben pasar por diferentes etapas de procesamiento, como:

- Sistemas de gestión de tickets
- Procesamiento de pedidos
- Flujos de aprobación
- Gestión de solicitudes internas
- Sistemas de seguimiento de estado

### ¿Cuáles son los requisitos mínimos del sistema?

- Java 11 o superior
- Maven 3.6 o superior
- Docker y Docker Compose
- 4GB de RAM mínimo
- 10GB de espacio en disco

### ¿El sistema es escalable?

Sí, la arquitectura basada en microservicios y el uso de Kafka permiten escalar horizontalmente cada componente de forma independiente. Puedes ejecutar múltiples instancias de cada servicio y configurar un balanceador de carga para distribuir el tráfico.

## Instalación y Configuración

### ¿Cómo puedo instalar el sistema en un entorno de producción?

Para un entorno de producción, recomendamos:

1. Utilizar Kubernetes para orquestar los contenedores
2. Configurar réplicas para cada servicio
3. Implementar un cluster de Kafka con múltiples brokers
4. Configurar MongoDB con replicación
5. Utilizar un balanceador de carga externo

Consulta la [Guía de Instalación](GUIA_INSTALACION.md) para más detalles.

### ¿Puedo ejecutar el sistema sin Docker?

Sí, pero tendrás que instalar y configurar manualmente:
- Zookeeper
- Kafka
- MongoDB

Después, puedes ejecutar cada servicio con `mvn spring-boot:run` o compilarlos y ejecutar los JAR resultantes.

### ¿Cómo cambio los puertos predeterminados?

Los puertos se pueden modificar en los archivos `application.properties` de cada servicio:

```properties
# En frontend-service/src/main/resources/application.properties
server.port=8080

# En ingr-service/src/main/resources/application.properties
server.port=8081

# En ctrl-service/src/main/resources/application.properties
server.port=8082
```

### ¿Cómo configuro el sistema para usar una base de datos MongoDB externa?

Edita el archivo `ctrl-service/src/main/resources/application.properties`:

```properties
spring.data.mongodb.host=tu-servidor-mongodb
spring.data.mongodb.port=27017
spring.data.mongodb.database=solicitudes_db
spring.data.mongodb.username=usuario
spring.data.mongodb.password=contraseña
```

## Uso del Sistema

### ¿Cómo puedo ver las solicitudes procesadas?

Las solicitudes procesadas se muestran en la sección "Histórico de Solicitudes" en la interfaz web. También puedes acceder a ellas directamente a través de la API:

```
GET http://localhost:8080/api/solicitudes/historico
```

O consultando directamente la base de datos MongoDB.

### ¿Puedo eliminar solicitudes del sistema?

Actualmente, el sistema no proporciona una interfaz para eliminar solicitudes. Sin embargo, puedes eliminarlas directamente de la base de datos MongoDB:

```js
use solicitudes_db
db.solicitud.deleteOne({id: "id-de-la-solicitud"})
```

### ¿Qué significan los diferentes estados de las solicitudes?

- **CREADA**: La solicitud ha sido creada en el frontend y enviada a Kafka
- **VALIDADA**: La solicitud ha pasado la validación en el servicio INGR
- **PROCESADA**: La solicitud ha sido procesada completamente por el servicio CTRL
- **RECHAZADA**: La solicitud no cumplió con los criterios de validación

### ¿Hay un límite en el número de solicitudes que puedo crear?

No hay un límite explícito en el código, pero debes considerar las limitaciones de recursos:
- Capacidad de procesamiento de Kafka
- Capacidad de almacenamiento de MongoDB
- Recursos del sistema (CPU, memoria)

Para entornos de producción con alta carga, considera ajustar la configuración de cada componente.

## Arquitectura y Diseño

### ¿Por qué se eligió Apache Kafka en lugar de un sistema de colas tradicional?

Kafka fue elegido por:
1. Alta capacidad de procesamiento (throughput)
2. Persistencia de mensajes
3. Capacidad para reproducir mensajes (replay)
4. Escalabilidad horizontal
5. Modelo pub-sub que permite múltiples consumidores

### ¿Cómo se manejan los fallos en los servicios?

El sistema implementa varios mecanismos de resiliencia:
1. Kafka mantiene los mensajes incluso si un servicio falla
2. Los mensajes no procesados pueden ser procesados cuando el servicio se recupera
3. Cada servicio mantiene su propio estado
4. MongoDB proporciona persistencia para los datos procesados

### ¿Puedo añadir nuevos microservicios al sistema?

Sí, el sistema está diseñado para ser extensible. Puedes añadir nuevos servicios que:
1. Consuman mensajes de los topics de Kafka existentes
2. Creen nuevos topics para funcionalidades adicionales
3. Implementen nuevas APIs REST

### ¿Cómo funciona la comunicación en tiempo real?

El sistema utiliza WebSockets (STOMP sobre SockJS) para proporcionar actualizaciones en tiempo real:
1. El frontend se conecta al endpoint WebSocket `/ws`
2. Se suscribe al topic `/topic/solicitudes`
3. El servicio frontend recibe mensajes de Kafka (topic `solicitudes-finalizadas`)
4. Estos mensajes se reenvían a través de WebSocket a todos los clientes conectados

## Problemas Conocidos

### Al enviar solicitudes muy rápidamente, algunas no se procesan

Este problema puede ocurrir debido a la configuración predeterminada de Kafka. Para solucionarlo:
1. Aumenta el número de particiones del topic
2. Ajusta la configuración de consumidores
3. Considera aumentar los recursos asignados a los servicios

### La interfaz web se desconecta después de un tiempo de inactividad

El WebSocket puede desconectarse debido a timeouts. La solución es implementar reconexión automática en el cliente:

```javascript
function connectWebSocket() {
    // ... código existente ...
    
    // Reconexión automática
    stompClient.onclose = function() {
        console.log('Conexión WebSocket cerrada. Reconectando...');
        setTimeout(connectWebSocket, 5000);
    };
}
```

### Algunas solicitudes aparecen duplicadas en el histórico

Esto puede ocurrir debido a la semántica "at-least-once" de Kafka. Para solucionar este problema:
1. Implementa una lógica de idempotencia en el servicio CTRL
2. Verifica la existencia de la solicitud antes de guardarla en MongoDB
3. Utiliza IDs únicos para cada solicitud

## Preguntas de Desarrollo

### ¿Cómo puedo añadir un nuevo campo a las solicitudes?

Para añadir un nuevo campo:
1. Modifica la clase `Solicitud` en el módulo `common`
2. Actualiza la interfaz de usuario en el frontend para incluir el nuevo campo
3. Asegúrate de que todos los servicios sean compatibles con la nueva estructura

### ¿Cómo puedo implementar autenticación en el sistema?

Puedes implementar autenticación utilizando Spring Security:
1. Añade las dependencias de Spring Security a cada servicio
2. Configura un proveedor de autenticación (JDBC, LDAP, OAuth2)
3. Implementa filtros de seguridad para proteger los endpoints REST
4. Asegura la conexión WebSocket

### ¿Cómo puedo realizar pruebas de carga del sistema?

Recomendamos utilizar herramientas como:
- JMeter para probar los endpoints REST
- Un script personalizado que utilice la biblioteca Stompit para probar WebSockets
- Kafka Producer API para enviar mensajes directamente a Kafka

### ¿Es posible usar una base de datos diferente a MongoDB?

Sí, puedes modificar el servicio CTRL para utilizar otra base de datos:
1. Cambia las dependencias en el `pom.xml`
2. Actualiza la configuración en `application.properties`
3. Modifica el repositorio para usar la nueva base de datos
4. Asegúrate de que la nueva base de datos pueda almacenar la estructura de `Solicitud`

### ¿Cómo puedo contribuir al proyecto?

Las contribuciones son bienvenidas:
1. Haz un fork del repositorio
2. Crea una rama para tu funcionalidad o corrección
3. Implementa tus cambios siguiendo las convenciones de código existentes
4. Añade pruebas para las nuevas funcionalidades
5. Envía un pull request con una descripción detallada de los cambios

---

Si tienes más preguntas que no están cubiertas aquí, no dudes en contactar al equipo de desarrollo o abrir un issue en el repositorio del proyecto.

---

*[Volver al índice principal](WIKI.md)* 
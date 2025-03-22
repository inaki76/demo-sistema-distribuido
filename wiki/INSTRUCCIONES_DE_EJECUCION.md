# Instrucciones para Ejecutar el Sistema de Procesamiento de Solicitudes

## Prerrequisitos
Para ejecutar el sistema completo, necesitas:

1. **Java 8 o superior** - Ya tienes Java 20 instalado en tu sistema.
2. **Docker Desktop** - Necesario para ejecutar Kafka, Zookeeper y MongoDB. 
   - Descargar desde: [https://www.docker.com/products/docker-desktop/](https://www.docker.com/products/docker-desktop/)
3. **Maven** - Para compilar el proyecto.

## Opciones de Ejecución

### Opción 1: Ejecución Completa con Docker (Recomendado)

1. Instala Docker Desktop y ábrelo.
2. Abre una terminal en la carpeta raíz del proyecto.
3. Ejecuta `docker compose -f docker-compose-simple.yml up -d` para iniciar Kafka, Zookeeper y MongoDB.
4. Espera unos segundos a que los servicios estén disponibles.
5. Ejecuta el script `.\start-services.bat` para iniciar todos los microservicios Java.

### Opción 2: Ejecución Parcial (Solo Servicios Java)

Si no puedes instalar Docker, puedes ejecutar solo los servicios Java, pero ten en cuenta que:
- No funcionarán correctamente sin Kafka y MongoDB.
- Es posible que veas errores de conexión.

Para ejecutar solo los servicios Java:
1. Ejecuta el script `.\start-services.bat`.
2. Revisa los mensajes de error en las ventanas de cada servicio.

### Opción 3: Ejecución Manual

Para iniciar cada servicio individualmente:

```
# Servicio INGR
java -jar ingr-service/target/ingr-service-1.0-SNAPSHOT.jar

# Servicio CTRL
java -jar ctrl-service/target/ctrl-service-1.0-SNAPSHOT.jar

# Servicio FRONTEND
java -jar frontend-service/target/frontend-service-1.0-SNAPSHOT.jar
```

## Verificación del Sistema

Cuando el sistema esté en funcionamiento:

1. Abre un navegador web y accede a: `http://localhost:8080`
2. Deberías ver la interfaz de usuario del sistema de procesamiento de solicitudes.
3. Puedes crear solicitudes completando el formulario con los siguientes campos:
   - **Solicitud**: Nombre o identificador de la solicitud
   - **Descripción**: Detalles de la solicitud
   - **ID Petición**: Número identificador de la petición
4. Las solicitudes creadas se enviarán a través de Kafka entre los microservicios.
5. Puedes ver el histórico de solicitudes pulsando el botón "Ver histórico de solicitudes".

## Estructura del Frontend

El frontend ha sido organizado en los siguientes archivos:

- **HTML**: `frontend-service/src/main/resources/static/index.html`
- **CSS**: `frontend-service/src/main/resources/static/css/styles.css`
- **JavaScript**: `frontend-service/src/main/resources/static/js/app.js`

Esta organización facilita el mantenimiento y la legibilidad del código.

## Solución de Problemas

Si encuentras errores:

1. **Docker no muestra los contenedores con `docker ps`**:
   - Verifica que Docker Desktop esté en ejecución y completamente iniciado.
   - Intenta reiniciar Docker Desktop.

2. **Errores de conexión a Kafka**:
   - Verifica que los puertos estén abiertos: `netstat -an | findstr "9092"` (para Kafka).
   - Comprueba los logs de los contenedores: `docker logs kafka`.

3. **Errores de conexión a MongoDB**:
   - Verifica que el puerto esté abierto: `netstat -an | findstr "27017"` (para MongoDB).
   - Comprueba los logs de los contenedores: `docker logs mongodb`.

4. **No se muestran las solicitudes históricas**:
   - Verifica en la consola del navegador (F12) si hay errores.
   - Comprueba los logs del servicio CTRL para ver si está devolviendo solicitudes.
   - Verifica que los formatos de fecha JSON sean correctos en application.properties.

5. **Errores en la compilación**:
   - Ejecuta `mvn clean install -DskipTests` para recompilar el proyecto.

6. **Reiniciar la aplicación tras cambios**:
   - Detén todos los servicios Java (cierra las ventanas o usa `taskkill /f /im java.exe` en Windows).
   - Compila el proyecto: `mvn clean install -DskipTests`.
   - Inicia los servicios nuevamente con `.\start-services.bat`.

7. **Puertos ocupados**:
   - Verifica que los puertos 8080, 8081, 8082, 9092 (Kafka), 2181 (Zookeeper) y 27017 (MongoDB) estén disponibles.

## Apagado del Sistema

1. Cierra las ventanas de los microservicios Java.
2. Detén los contenedores de Docker: `docker compose -f docker-compose-simple.yml down`. 
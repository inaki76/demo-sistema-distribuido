# 🚀 Guía de Instalación

Esta guía detalla los pasos necesarios para instalar y configurar el Sistema de Solicitudes Distribuido en diferentes entornos.

## Requisitos Previos

Antes de comenzar, asegúrate de tener instalados los siguientes componentes:

| Requisito | Versión Mínima | Notas |
|-----------|----------------|-------|
| Java JDK | 11 | OpenJDK o Oracle JDK |
| Maven | 3.6.0 | Para compilación y gestión de dependencias |
| Docker | 19.03+ | Para ejecutar servicios de infraestructura |
| Docker Compose | 1.25+ | Para orquestación de contenedores |
| Git | 2.0+ | Para clonar el repositorio |

## Opciones de Instalación

Existen dos formas principales de instalar el sistema:

1. **Instalación Completa** - Incluye todos los servicios (recomendada)
2. **Instalación Parcial** - Solo microservicios Java (requiere infraestructura externa)

## Instalación Completa

### 1. Clonar el Repositorio

```bash
git clone https://github.com/tu-usuario/sistema-solicitudes.git
cd sistema-solicitudes
```

### 2. Iniciar Servicios de Infraestructura con Docker

Este paso iniciará MongoDB, Kafka y Zookeeper en contenedores Docker:

```bash
docker-compose up -d
```

Verifica que los contenedores estén funcionando:

```bash
docker ps
```

Deberías ver tres contenedores ejecutándose:
- kafka
- zookeeper
- mongodb

### 3. Compilar el Proyecto

Compila todos los microservicios utilizando Maven:

```bash
mvn clean install -DskipTests
```

> **Nota**: Utilizamos `-DskipTests` para omitir las pruebas durante la instalación inicial. Para un entorno de producción, se recomienda ejecutar las pruebas.

### 4. Iniciar los Microservicios

En Windows:
```bash
.\start-services.bat
```

En Linux/Mac:
```bash
chmod +x start-services.sh
./start-services.sh
```

## Instalación Parcial (Solo Microservicios)

Si ya tienes Kafka y MongoDB funcionando en tu entorno, puedes optar por ejecutar solo los microservicios Java.

### 1. Configurar las Conexiones

Ajusta los archivos de configuración para apuntar a tus servicios existentes:

**frontend-service/src/main/resources/application.properties**:
```properties
spring.kafka.bootstrap-servers=tu-servidor-kafka:9092
ctrl.service.url=http://localhost:8082
```

**ingr-service/src/main/resources/application.properties**:
```properties
spring.kafka.bootstrap-servers=tu-servidor-kafka:9092
```

**ctrl-service/src/main/resources/application.properties**:
```properties
spring.kafka.bootstrap-servers=tu-servidor-kafka:9092
spring.data.mongodb.host=tu-servidor-mongodb
spring.data.mongodb.port=27017
spring.data.mongodb.database=solicitudes_db
```

### 2. Compilar e Iniciar los Servicios

Compila y ejecuta cada servicio individualmente:

```bash
# Compilar todos los módulos
mvn clean install -DskipTests

# Iniciar cada servicio en una terminal separada
java -jar frontend-service/target/frontend-service-1.0-SNAPSHOT.jar
java -jar ingr-service/target/ingr-service-1.0-SNAPSHOT.jar
java -jar ctrl-service/target/ctrl-service-1.0-SNAPSHOT.jar
```

## Verificación de la Instalación

### 1. Verificar Servicios de Infraestructura

**Kafka**: Debería estar disponible en `localhost:9092`
```bash
docker logs kafka
# Buscas mensajes como "Kafka Server started"
```

**MongoDB**: Debería estar disponible en `localhost:27017`
```bash
docker logs mongodb
# Buscas mensajes como "Waiting for connections"
```

### 2. Verificar Microservicios

**Frontend Service**: http://localhost:8080
- Deberías ver la página principal con el formulario

**INGR Service**: http://localhost:8081/actuator/health
- Deberías ver `{"status":"UP"}`

**CTRL Service**: http://localhost:8082/actuator/health
- Deberías ver `{"status":"UP"}`

## Configuración Avanzada

### Cambio de Puertos

Si necesitas cambiar los puertos predeterminados, modifica:

1. Los archivos `application.properties` de cada servicio
2. El archivo `docker-compose.yml` para los servicios de infraestructura
3. Referencias cruzadas entre servicios

### Configuración de Memoria

Para ajustar la memoria asignada a los servicios Java:

```bash
java -Xms256m -Xmx512m -jar frontend-service/target/frontend-service-1.0-SNAPSHOT.jar
```

### Configuración de Logs

Los logs se almacenan por defecto en:
- Windows: `C:\logs\`
- Linux/Mac: `/var/log/solicitudes/`

Para cambiar la ubicación, modifica `application.properties`:

```properties
logging.file.path=/tu/ruta/personalizada
```

## Problemas Comunes

| Problema | Causa Probable | Solución |
|----------|----------------|----------|
| Error de conexión a Kafka | Kafka no está en ejecución | Verifica `docker ps` y reinicia con `docker-compose up -d kafka` |
| Error de conexión a MongoDB | MongoDB no está en ejecución | Verifica `docker ps` y reinicia con `docker-compose up -d mongodb` |
| Puertos ocupados | Otro servicio usa el mismo puerto | Cambia los puertos en `application.properties` |
| Error de Java | Versión incorrecta de JDK | Verifica `java -version` y asegúrate de usar Java 11+ |

## Instalación en Entorno de Producción

Para entornos de producción, se recomienda:

1. **Configurar réplicas** de Kafka y MongoDB para alta disponibilidad
2. **Implementar balanceo de carga** para los microservicios
3. **Configurar HTTPS** con certificados válidos
4. **Implementar autenticación** para los endpoints REST
5. **Configurar monitoreo** con Prometheus y Grafana

---

*[Volver al índice principal](WIKI.md)* 
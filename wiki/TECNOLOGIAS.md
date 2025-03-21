# 💻 Tecnologías Utilizadas

Este documento detalla las tecnologías clave utilizadas en el Sistema de Solicitudes Distribuido, explicando sus roles y beneficios para el proyecto.

## 📋 Índice

- [Lenguajes y Frameworks](#lenguajes-y-frameworks)
- [Mensajería y Comunicación](#mensajería-y-comunicación)
- [Bases de Datos](#bases-de-datos)
- [Frontend y UI](#frontend-y-ui)
- [Herramientas de Desarrollo](#herramientas-de-desarrollo)
- [Infraestructura y Contenedores](#infraestructura-y-contenedores)

## Lenguajes y Frameworks

### Java 11

![Java](https://img.shields.io/badge/Java-11-orange)

Java es el lenguaje principal para todos los servicios backend del sistema.

**Características clave para el proyecto:**
- Rendimiento optimizado con JIT (Just-In-Time) compiler
- Robustez y estabilidad probada en entornos empresariales
- Amplio ecosistema de bibliotecas y herramientas
- Tipado estático para detección temprana de errores
- Portabilidad entre diferentes sistemas operativos

### Spring Boot 2.7.x

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7-green)

Framework que simplifica la creación de aplicaciones Spring autoconfigurables.

**Características clave para el proyecto:**
- Configuración automática que reduce el código boilerplate
- Servidor embebido para facilitar el despliegue
- Gestión simplificada de dependencias
- Facilidad para implementar servicios REST
- Integración nativa con Kafka y WebSocket

### Spring WebFlux

Framework reactivo para aplicaciones web no-bloqueantes.

**Características clave para el proyecto:**
- Programación reactiva para mejorar el rendimiento
- APIs asíncronas que optimizan los recursos del sistema
- Mejor manejo de concurrencia
- Capacidad para manejar más conexiones simultáneas

## Mensajería y Comunicación

### Apache Kafka

![Kafka](https://img.shields.io/badge/Apache%20Kafka-3.3-blue)

Plataforma distribuida de streaming para la comunicación entre microservicios.

**Características clave para el proyecto:**
- Alta capacidad de procesamiento para mensajes
- Persistencia de mensajes que asegura la entrega
- Capacidad de escalado horizontal
- Garantía de orden en la entrega dentro de particiones
- Capacidad para reproducir (replay) eventos históricos

### WebSocket

Protocolo que proporciona comunicación bidireccional entre cliente y servidor.

**Características clave para el proyecto:**
- Actualizaciones en tiempo real a la interfaz de usuario
- Conexión persistente que reduce sobrecarga de establecimiento
- Comunicación bidireccional para servicios interactivos
- Bajo consumo de recursos en comparación con polling

### Spring STOMP

Implementación del protocolo STOMP sobre WebSocket.

**Características clave para el proyecto:**
- Abstracción de nivel superior sobre WebSockets
- Mecanismo de publicación/suscripción para distribución de mensajes
- Facilita la comunicación cliente-servidor
- Integración nativa con Spring

## Bases de Datos

### MongoDB

![MongoDB](https://img.shields.io/badge/MongoDB-4.4-green)

Base de datos NoSQL orientada a documentos.

**Características clave para el proyecto:**
- Esquema flexible para adaptarse a cambios en el modelo de datos
- Alto rendimiento para operaciones de lectura y escritura
- Capacidad de escalado horizontal mediante sharding
- Almacenamiento en formato BSON (similar a JSON)
- Indexación eficiente para consultas rápidas

### Spring Data MongoDB

Biblioteca que facilita la integración de MongoDB con Spring.

**Características clave para el proyecto:**
- Abstracción de operaciones básicas de CRUD
- Consultas derivadas de nombres de métodos
- Soporte para anotaciones de mapeo
- Gestión transparente de conexiones
- Integración con el sistema de transacciones de Spring

## Frontend y UI

### HTML5, CSS3 y JavaScript

![HTML5](https://img.shields.io/badge/HTML5-CSS3-blue) ![JavaScript](https://img.shields.io/badge/JavaScript-ES6-yellow)

Tecnologías fundamentales para la interfaz de usuario.

**Características clave para el proyecto:**
- Interfaces responsive adaptadas a diferentes dispositivos
- Manipulación dinámica del DOM
- Estilos modernos y consistentes
- Interactividad del lado del cliente

### Bootstrap

![Bootstrap](https://img.shields.io/badge/Bootstrap-5.0-purple)

Framework de CSS para desarrollo de interfaces modernas y responsivas.

**Características clave para el proyecto:**
- Diseño responsive integrado
- Componentes predefinidos que aceleran el desarrollo
- Consistencia visual en diferentes navegadores
- Accesibilidad incorporada

### SockJS y STOMP.js

Bibliotecas para la implementación de WebSockets en el cliente.

**Características clave para el proyecto:**
- Fallback automático a otros protocolos si WebSocket no está disponible
- Cliente compatible con el servidor STOMP de Spring
- API simple para suscripción a eventos
- Manejo automatizado de reconexión

## Herramientas de Desarrollo

### Maven

![Maven](https://img.shields.io/badge/Maven-3.6-red)

Herramienta de gestión y construcción de proyectos.

**Características clave para el proyecto:**
- Gestión declarativa de dependencias
- Estructura de proyecto estandarizada
- Ciclo de vida de construcción predefinido
- Integración con CI/CD

### JUnit y Spring Test

Frameworks para pruebas unitarias e integración.

**Características clave para el proyecto:**
- Pruebas automatizadas para verificar funcionalidad
- Mocks y stubs para aislar componentes
- Integración con Spring para probar componentes en su contexto
- Cobertura de código para asegurar calidad

### Lombok

Biblioteca que reduce el código boilerplate en Java.

**Características clave para el proyecto:**
- Generación automática de getters, setters y constructores
- Reducción significativa de código repetitivo
- Implementaciones automáticas de toString, equals y hashCode
- Código más limpio y mantenible

## Infraestructura y Contenedores

### Docker

![Docker](https://img.shields.io/badge/Docker-20.10-blue)

Plataforma para desarrollar, enviar y ejecutar aplicaciones en contenedores.

**Características clave para el proyecto:**
- Entorno consistente entre desarrollo y producción
- Empaquetado de aplicaciones con sus dependencias
- Aislamiento entre servicios
- Facilidad para actualizar y escalar componentes

### Docker Compose

Herramienta para definir y ejecutar aplicaciones Docker multi-contenedor.

**Características clave para el proyecto:**
- Definición declarativa de la infraestructura como código
- Orquestación simplificada de múltiples servicios
- Gestión automatizada de redes entre contenedores
- Entorno de desarrollo local consistente

## Matriz de Tecnologías por Servicio

| Tecnología          | Frontend Service | INGR Service | CTRL Service | Común      |
|---------------------|------------------|--------------|--------------|------------|
| Java 11             | ✓                | ✓            | ✓            | ✓          |
| Spring Boot         | ✓                | ✓            | ✓            | -          |
| Apache Kafka        | ✓                | ✓            | ✓            | -          |
| WebSocket           | ✓                | -            | -            | -          |
| MongoDB             | -                | -            | ✓            | -          |
| HTML/CSS/JavaScript | ✓                | -            | -            | -          |
| Bootstrap           | ✓                | -            | -            | -          |
| Docker              | ✓                | ✓            | ✓            | -          |
| Maven               | ✓                | ✓            | ✓            | ✓          |
| JUnit               | ✓                | ✓            | ✓            | ✓          |
| Lombok              | ✓                | ✓            | ✓            | ✓          |

## Versiones Específicas

| Tecnología           | Versión  | Notas                                      |
|----------------------|----------|-------------------------------------------|
| Java                 | 11.0.15+ | OpenJDK o Oracle JDK                      |
| Spring Boot          | 2.7.9    | Última versión estable de la rama 2.7     |
| Spring Kafka         | 2.8.11   | Compatible con Kafka 3.3                   |
| MongoDB              | 4.4.18   | Versión LTS                               |
| Spring Data MongoDB  | 3.4.5    | Integrada con Spring Boot                 |
| Apache Kafka         | 3.3.2    | Alta estabilidad y rendimiento            |
| Bootstrap            | 5.2.3    | Soporte para los navegadores modernos     |
| SockJS               | 1.6.1    | Compatible con la mayoría de navegadores  |
| Docker               | 20.10+   | Necesario para entornos de contenedores   |
| Maven                | 3.8.6    | Recomendado para gestión de dependencias  |

---

*[Volver al índice principal](WIKI.md)* 
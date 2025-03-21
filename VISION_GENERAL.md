# 🔍 Visión General

El Sistema de Solicitudes Distribuido es una aplicación basada en microservicios que permite la creación, validación, procesamiento y consulta de solicitudes. Este sistema utiliza tecnologías modernas como Apache Kafka, MongoDB y WebSockets para proporcionar una experiencia en tiempo real, escalable y resiliente.

## Propósito

El sistema está diseñado para gestionar solicitudes de manera asíncrona y distribuida, proporcionando:

- **Procesamiento eficiente**: Las solicitudes se procesan de manera independiente sin bloquear al usuario
- **Alta disponibilidad**: Los componentes pueden escalar horizontalmente según la demanda
- **Experiencia en tiempo real**: Los usuarios reciben actualizaciones instantáneas sobre el estado de sus solicitudes
- **Persistencia confiable**: Todas las solicitudes quedan registradas en una base de datos NoSQL
- **Interfaz intuitiva**: Una interfaz web simple que facilita la creación y seguimiento de solicitudes

## Características principales

- **Procesamiento asíncrono** mediante colas de mensajes (Apache Kafka)
- **Persistencia de datos** en base de datos NoSQL (MongoDB)
- **Actualización en tiempo real** de la interfaz de usuario mediante WebSockets
- **Arquitectura distribuida** y tolerante a fallos
- **Interfaz de usuario** sencilla e intuitiva
- **Validación** de solicitudes según reglas de negocio
- **Histórico** de todas las solicitudes procesadas

## Beneficios

- **Para usuarios**: Experiencia fluida y respuestas en tiempo real
- **Para desarrolladores**: Arquitectura modular fácil de mantener y extender
- **Para operaciones**: Despliegue y escalado independiente de componentes
- **Para el negocio**: Sistema robusto con capacidad para crecer según las necesidades

## ¿Por qué microservicios?

La arquitectura de microservicios fue elegida por:

1. **Modularidad**: Cada servicio tiene una responsabilidad única y bien definida
2. **Escalabilidad**: Cada componente puede escalar de manera independiente
3. **Tecnología poliglota**: Permite usar diferentes tecnologías para cada componente
4. **Resiliencia**: El fallo en un servicio no afecta al sistema completo
5. **Desarrollo paralelo**: Equipos diferentes pueden trabajar en servicios distintos

---

*[Volver al índice principal](WIKI.md)* 
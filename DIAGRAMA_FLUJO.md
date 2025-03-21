# Diagrama de Flujo de Ejecución

Este diagrama muestra el flujo de ejecución del sistema de procesamiento de solicitudes.

## Diagrama de Componentes y Comunicación

```mermaid
graph TD
    Usuario[Usuario] --> Frontend[Frontend Service <br> Puerto 8080]
    
    subgraph "Microservicios"
        Frontend -.-> INGR[INGR Service <br> Puerto 8081]
        INGR -.-> CTRL[CTRL Service <br> Puerto 8082]
        CTRL -.-> Frontend
    end
    
    subgraph "Infraestructura Docker"
        Kafka[Kafka <br> Puerto 9092]
        Zookeeper[Zookeeper <br> Puerto 2181]
        MongoDB[MongoDB <br> Puerto 27017]
        
        Zookeeper --> Kafka
    end
    
    Frontend -- Publicar: solicitudes-entrada --> Kafka
    Kafka -- Consumir: solicitudes-entrada --> INGR
    INGR -- Publicar: solicitudes-procesadas --> Kafka
    Kafka -- Consumir: solicitudes-procesadas --> CTRL
    CTRL -- Almacenar datos --> MongoDB
    CTRL -- Publicar: solicitudes-finalizadas --> Kafka
    Kafka -- Consumir: solicitudes-finalizadas --> Frontend
    Frontend -- HTTP GET /api/solicitudes/historico --> Frontend
    Frontend -- HTTP GET /ctrl/solicitudes --> CTRL
    CTRL -- Consultar solicitudes --> MongoDB

    style Usuario fill:#f9f,stroke:#333,stroke-width:2px
    style Frontend fill:#bbf,stroke:#333,stroke-width:2px
    style INGR fill:#bbf,stroke:#333,stroke-width:2px
    style CTRL fill:#bbf,stroke:#333,stroke-width:2px
    style Kafka fill:#dfd,stroke:#333,stroke-width:2px
    style Zookeeper fill:#dfd,stroke:#333,stroke-width:2px
    style MongoDB fill:#dfd,stroke:#333,stroke-width:2px
```

## Diagrama de Secuencia

```mermaid
sequenceDiagram
    actor Usuario
    participant Frontend
    participant Kafka
    participant INGR
    participant CTRL
    participant MongoDB
    
    Usuario->>Frontend: Envía solicitud
    Frontend->>Kafka: Publica en solicitudes-entrada
    Kafka->>INGR: Consume mensaje
    INGR->>INGR: Valida y transforma
    INGR->>Kafka: Publica en solicitudes-procesadas
    Kafka->>CTRL: Consume mensaje
    CTRL->>MongoDB: Almacena solicitud
    CTRL->>Kafka: Publica en solicitudes-finalizadas
    Kafka->>Frontend: Consume mensaje
    Frontend->>Usuario: Actualiza interfaz (WebSocket)
    
    Note over Usuario,Frontend: Flujo de consulta de histórico
    Usuario->>Frontend: Solicita histórico
    Frontend->>CTRL: GET /ctrl/solicitudes
    CTRL->>MongoDB: Consulta colección "solicitud"
    MongoDB->>CTRL: Retorna solicitudes
    CTRL->>Frontend: Responde con JSON
    Frontend->>Usuario: Muestra solicitudes históricas
```

## Flujo de Inicio del Sistema

```mermaid
flowchart TB
    Start[Inicio] --> Docker[Iniciar Docker Desktop]
    Docker --> ComposeUp[docker compose -f docker-compose-simple.yml up -d]
    ComposeUp --> WaitServices[Esperar inicialización de servicios]
    WaitServices --> StartJava[Ejecutar start-services.bat]
    StartJava --> StartINGR[Iniciar INGR Service]
    StartINGR --> StartCTRL[Iniciar CTRL Service]
    StartCTRL --> StartFrontend[Iniciar Frontend Service]
    StartFrontend --> Access[Acceder a http://localhost:8080]
    
    style Start fill:#f96,stroke:#333,stroke-width:2px
    style Docker fill:#9cf,stroke:#333,stroke-width:2px
    style ComposeUp fill:#9cf,stroke:#333,stroke-width:2px
    style StartJava fill:#9cf,stroke:#333,stroke-width:2px
    style Access fill:#6f9,stroke:#333,stroke-width:2px
```

## Estructura del Frontend

```mermaid
graph TD
    subgraph "Frontend Service"
        HTML[index.html <br> Estructura básica]
        CSS[styles.css <br> Estilos visuales]
        JS[app.js <br> Lógica del cliente]
        
        HTML --> CSS
        HTML --> JS
        
        subgraph "JavaScript Modules"
            Init[Inicialización]
            WebSocket[Conexión WebSocket]
            FormHandler[Manejo del formulario]
            HistoricoHandler[Consulta de histórico]
            UIUpdater[Actualización de UI]
            
            Init --> WebSocket
            Init --> FormHandler
            Init --> HistoricoHandler
            FormHandler --> UIUpdater
            WebSocket --> UIUpdater
            HistoricoHandler --> UIUpdater
        end
    end
    
    style HTML fill:#ff9,stroke:#333,stroke-width:2px
    style CSS fill:#f9f,stroke:#333,stroke-width:2px
    style JS fill:#9cf,stroke:#333,stroke-width:2px
    style Init fill:#9cf,stroke:#333,stroke-width:1px
    style WebSocket fill:#9cf,stroke:#333,stroke-width:1px
    style FormHandler fill:#9cf,stroke:#333,stroke-width:1px
    style HistoricoHandler fill:#9cf,stroke:#333,stroke-width:1px
    style UIUpdater fill:#9cf,stroke:#333,stroke-width:1px
``` 
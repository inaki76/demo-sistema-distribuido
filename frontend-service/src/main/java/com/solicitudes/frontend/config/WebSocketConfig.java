package com.solicitudes.frontend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.lang.NonNull;

/**
 * Configuración de WebSockets para el servicio Frontend.
 * Permite la comunicación en tiempo real entre el servidor y los clientes.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configura el broker de mensajes para WebSockets.
     * Define los prefijos para los destinos de los mensajes.
     * 
     * @param registry El registro de configuración del broker de mensajes
     */
    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry registry) {
        // Prefijo para destinos a los que los clientes pueden suscribirse
        registry.enableSimpleBroker("/topic");
        // Prefijo para mensajes enviados desde clientes
        registry.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registra los endpoints de Stomp sobre WebSocket.
     * Define la ruta donde los clientes se conectarán.
     * 
     * @param registry El registro de endpoints de Stomp
     */
    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        // Endpoint para conectarse con SockJS (compatibilidad con navegadores antiguos)
        registry.addEndpoint("/ws").withSockJS();
    }
} 
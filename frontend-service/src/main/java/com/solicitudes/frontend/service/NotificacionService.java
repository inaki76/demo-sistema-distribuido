package com.solicitudes.frontend.service;

import com.solicitudes.model.Solicitud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de recibir notificaciones desde Kafka
 * y enviarlas al cliente mediante WebSockets.
 */
@Service
public class NotificacionService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Recibe las solicitudes finalizadas desde Kafka y las envía al frontend
     * mediante WebSocket para actualizar la interfaz en tiempo real.
     * 
     * @param solicitud La solicitud procesada recibida de Kafka
     */
    @KafkaListener(topics = "solicitudes-finalizadas", groupId = "frontend-group")
    public void enviarNotificacion(Solicitud solicitud) {
        System.out.println("Recibida solicitud finalizada: " + solicitud.getId());
        // Enviar a todos los clientes conectados por WebSocket
        messagingTemplate.convertAndSend("/topic/solicitudes", solicitud);
    }
} 
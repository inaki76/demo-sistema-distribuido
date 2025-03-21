package com.solicitudes.ingr.service;

import com.solicitudes.model.Solicitud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de validar las solicitudes que llegan desde el frontend.
 * Procesa los mensajes recibidos en Kafka, realiza validaciones y publica 
 * las solicitudes validadas en otro topic.
 */
@Service
public class SolicitudValidatorService {

    @Autowired
    private KafkaTemplate<String, Solicitud> kafkaTemplate;

    /**
     * Valida las solicitudes recibidas desde el frontend.
     * 1. Aplica reglas de negocio (validación simple en este caso)
     * 2. Establece el estado como "VALIDADA"
     * 3. Publica la solicitud validada en el topic "solicitudes-procesadas"
     * 
     * @param solicitud La solicitud a validar recibida del frontend
     */
    @KafkaListener(topics = "solicitudes-entrada", groupId = "ingr-group")
    public void validarSolicitud(Solicitud solicitud) {
        System.out.println("Validando solicitud: " + solicitud.getId());
        
        // Simular validación de negocio - Por ejemplo, validar que la cantidad sea positiva
        if (solicitud.getCantidad() <= 0) {
            solicitud.setEstado("RECHAZADA");
            System.out.println("Solicitud rechazada: cantidad debe ser positiva");
        } else {
            solicitud.setEstado("VALIDADA");
            System.out.println("Solicitud validada correctamente");
        }
        
        // Publicar resultado de la validación para el servicio CTRL
        kafkaTemplate.send("solicitudes-procesadas", solicitud);
        System.out.println("Solicitud enviada al procesador: " + solicitud.getId());
    }
} 
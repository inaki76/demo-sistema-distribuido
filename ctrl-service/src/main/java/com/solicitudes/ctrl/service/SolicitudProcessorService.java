package com.solicitudes.ctrl.service;

import com.solicitudes.model.Solicitud;
import com.solicitudes.ctrl.repository.SolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Servicio encargado de procesar las solicitudes recibidas desde Kafka,
 * almacenarlas en MongoDB y publicar confirmaciones.
 */
@Service
public class SolicitudProcessorService {

    @Autowired
    private SolicitudRepository solicitudRepository;
    
    @Autowired
    private KafkaTemplate<String, Solicitud> kafkaTemplate;

    /**
     * Procesa las solicitudes validadas que llegan desde el servicio INGR.
     * 1. Actualiza el estado a PROCESADA
     * 2. Registra la fecha de procesamiento
     * 3. Almacena en MongoDB
     * 4. Publica confirmación en Kafka
     * 
     * @param solicitud La solicitud validada recibida del topic Kafka
     */
    @KafkaListener(topics = "solicitudes-procesadas", groupId = "ctrl-group")
    public void procesarSolicitud(Solicitud solicitud) {
        System.out.println("Solicitud recibida para procesar: " + solicitud.getId());
        
        // Actualizar estado y fecha de procesamiento
        solicitud.setEstado("PROCESADA");
        solicitud.setFechaProcesamiento(LocalDateTime.now());
        
        // Guardar en MongoDB
        solicitudRepository.save(solicitud);
        System.out.println("Solicitud guardada en base de datos: " + solicitud.getId());
        
        // Publicar confirmación para el frontend
        kafkaTemplate.send("solicitudes-finalizadas", solicitud);
        System.out.println("Notificación enviada al frontend: " + solicitud.getId());
    }
} 
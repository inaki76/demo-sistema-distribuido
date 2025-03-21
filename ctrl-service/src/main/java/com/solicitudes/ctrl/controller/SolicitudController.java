package com.solicitudes.ctrl.controller;

import com.solicitudes.model.Solicitud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST del servicio CTRL.
 * Proporciona endpoints para consultar las solicitudes almacenadas en MongoDB.
 */
@RestController
@RequestMapping("/ctrl")
@CrossOrigin(origins = "*")
public class SolicitudController {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Obtiene todas las solicitudes almacenadas en la colección "solicitud" de MongoDB.
     * Este endpoint es utilizado por el frontend para mostrar el histórico de solicitudes.
     * 
     * @return Lista de todas las solicitudes con sus detalles
     */
    @GetMapping("/solicitudes")
    public ResponseEntity<List<Solicitud>> obtenerTodasLasSolicitudes() {
        // Usar MongoTemplate para obtener solicitudes de la colección correcta
        List<Solicitud> solicitudes = mongoTemplate.findAll(Solicitud.class, "solicitud");
        System.out.println("Solicitudes encontradas en colección 'solicitud': " + solicitudes.size());
        
        // Imprimir detalles de las solicitudes para depuración
        for (Solicitud s : solicitudes) {
            System.out.println("ID: " + s.getId() + ", Cliente: " + s.getNombreCliente() + 
                               ", Estado: " + s.getEstado() + ", Fecha: " + s.getFechaCreacion());
        }
        
        // Configurar cabeceras para la respuesta
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        return new ResponseEntity<>(solicitudes, headers, HttpStatus.OK);
    }
} 
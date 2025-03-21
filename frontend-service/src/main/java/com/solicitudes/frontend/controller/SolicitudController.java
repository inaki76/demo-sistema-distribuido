package com.solicitudes.frontend.controller;

import com.solicitudes.model.Solicitud;
import com.solicitudes.frontend.service.SolicitudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST del servicio Frontend.
 * Proporciona endpoints para la creación y consulta de solicitudes.
 */
@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    @Autowired
    private SolicitudService solicitudService;

    /**
     * Endpoint para crear una nueva solicitud.
     * Recibe los datos del formulario y los envía a Kafka.
     * 
     * @param solicitud La solicitud creada por el usuario
     * @return La solicitud creada con su ID asignado
     */
    @PostMapping
    public ResponseEntity<Solicitud> crearSolicitud(@RequestBody Solicitud solicitud) {
        System.out.println("Recibida petición POST /api/solicitudes");
        Solicitud nuevaSolicitud = solicitudService.enviarSolicitud(solicitud);
        return ResponseEntity.ok(nuevaSolicitud);
    }
    
    /**
     * Endpoint para obtener el histórico de solicitudes.
     * Consulta el servicio CTRL para obtener todas las solicitudes procesadas.
     * 
     * @return Lista de todas las solicitudes procesadas
     */
    @GetMapping("/historico")
    public ResponseEntity<List<Solicitud>> obtenerHistorico() {
        System.out.println("Recibida petición GET /api/solicitudes/historico");
        List<Solicitud> solicitudes = solicitudService.obtenerHistorico();
        System.out.println("Retornando " + solicitudes.size() + " solicitudes en el histórico");
        return ResponseEntity.ok(solicitudes);
    }
} 
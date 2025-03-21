package com.solicitudes.frontend.service;

import com.solicitudes.model.Solicitud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Servicio principal del frontend para gestionar solicitudes.
 * Se encarga de enviar solicitudes nuevas a Kafka y consultar 
 * el histórico de solicitudes procesadas.
 */
@Service
public class SolicitudService {

    @Autowired
    private KafkaTemplate<String, Solicitud> kafkaTemplate;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${ctrl.service.url:http://localhost:8082}")
    private String ctrlServiceUrl;

    /**
     * Envía una nueva solicitud al sistema.
     * La publica en el topic de Kafka para que sea procesada por el servicio INGR.
     * 
     * @param solicitud La solicitud creada por el usuario
     * @return La misma solicitud con su ID generado
     */
    public Solicitud enviarSolicitud(Solicitud solicitud) {
        // Enviar a Kafka
        kafkaTemplate.send("solicitudes-entrada", solicitud);
        System.out.println("Solicitud enviada a Kafka: " + solicitud.getId());
        return solicitud;
    }
    
    /**
     * Obtiene el histórico de solicitudes procesadas.
     * Consulta al servicio CTRL para obtener todas las solicitudes almacenadas en MongoDB.
     * Implementa dos métodos alternativos para obtener las solicitudes.
     * 
     * @return Lista de solicitudes procesadas
     */
    public List<Solicitud> obtenerHistorico() {
        try {
            // Consultar al servicio CTRL para obtener las solicitudes almacenadas
            String url = ctrlServiceUrl + "/ctrl/solicitudes";
            System.out.println("Consultando histórico en: " + url);
            
            // Configurar cabeceras para aceptar JSON
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // Primer intento: usando exchange con ParameterizedTypeReference
            try {
                ResponseEntity<List<Solicitud>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<Solicitud>>() {}
                );
                
                List<Solicitud> solicitudes = response.getBody();
                
                if (solicitudes != null && !solicitudes.isEmpty()) {
                    System.out.println("Método exchange: Solicitudes obtenidas: " + solicitudes.size());
                    return solicitudes;
                } else {
                    System.out.println("Método exchange: No se obtuvieron solicitudes o lista vacía");
                }
            } catch (Exception e) {
                System.err.println("Error en método exchange: " + e.getMessage());
            }
            
            // Segundo intento: usando getForObject con array
            try {
                System.out.println("Intentando método getForObject con array");
                Solicitud[] solicitudesArray = restTemplate.getForObject(url, Solicitud[].class);
                
                if (solicitudesArray != null && solicitudesArray.length > 0) {
                    List<Solicitud> solicitudes = Arrays.asList(solicitudesArray);
                    System.out.println("Método getForObject: Solicitudes obtenidas: " + solicitudes.size());
                    return solicitudes;
                } else {
                    System.out.println("Método getForObject: No se obtuvieron solicitudes o array vacío");
                }
            } catch (Exception e) {
                System.err.println("Error en método getForObject: " + e.getMessage());
            }
            
            // Si llegamos aquí, ambos métodos fallaron
            System.out.println("No se pudieron obtener solicitudes con ningún método");
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Error general al obtener histórico de solicitudes: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
} 
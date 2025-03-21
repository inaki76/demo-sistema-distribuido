package com.solicitudes.frontend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuración general del servicio Frontend.
 * Define los beans necesarios para el funcionamiento de la aplicación.
 */
@Configuration
public class AppConfig {
    /**
     * Crea un bean RestTemplate para realizar peticiones HTTP.
     * Se utiliza para comunicarse con el servicio CTRL.
     * 
     * @return Instancia de RestTemplate configurada
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
} 
package com.solicitudes.frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del servicio Frontend.
 * Este servicio maneja la interfaz de usuario y la comunicación con los demás microservicios.
 */
@SpringBootApplication
public class FrontendServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FrontendServiceApplication.class, args);
    }
} 
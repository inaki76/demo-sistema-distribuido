package com.solicitudes.ingr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del servicio INGR.
 * Este servicio se encarga de validar las solicitudes recibidas desde el frontend
 * y enviarlas al servicio CTRL para su procesamiento.
 */
@SpringBootApplication
public class IngrServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(IngrServiceApplication.class, args);
    }
} 
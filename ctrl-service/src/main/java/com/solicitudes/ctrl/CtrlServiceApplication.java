package com.solicitudes.ctrl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del servicio CTRL.
 * Este servicio se encarga de almacenar las solicitudes en MongoDB
 * y publicar confirmaciones al frontend.
 */
@SpringBootApplication
public class CtrlServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CtrlServiceApplication.class, args);
    }
} 
package com.solicitudes.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Clase modelo que representa una solicitud en el sistema.
 * Esta clase es utilizada por todos los microservicios para transmitir
 * y procesar la información de las solicitudes.
 */
public class Solicitud {
    // Atributos de la clase
    private String id;              // Identificador único de la solicitud
    private String nombreCliente;   // Nombre del cliente o identificador de la solicitud
    private String descripcion;     // Descripción detallada de la solicitud
    private int cantidad;           // Cantidad numérica (usado como ID de Petición)
    private String estado;          // Estado actual de la solicitud (CREADA, VALIDADA, PROCESADA, RECHAZADA)
    private LocalDateTime fechaCreacion;      // Fecha y hora de creación
    private LocalDateTime fechaProcesamiento; // Fecha y hora de procesamiento

    /**
     * Constructor por defecto para serialización.
     * Inicializa la solicitud con un ID aleatorio, fecha actual y estado CREADA.
     */
    public Solicitud() {
        this.id = UUID.randomUUID().toString();
        this.fechaCreacion = LocalDateTime.now();
        this.estado = "CREADA";
    }

    /**
     * Constructor con los campos principales de la solicitud.
     * @param nombreCliente Nombre del cliente o identificador de la solicitud
     * @param descripcion Descripción detallada de la solicitud
     * @param cantidad Cantidad numérica (usado como ID de Petición)
     */
    public Solicitud(String nombreCliente, String descripcion, int cantidad) {
        this();
        this.nombreCliente = nombreCliente;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
    }

    // Métodos getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaProcesamiento() {
        return fechaProcesamiento;
    }

    public void setFechaProcesamiento(LocalDateTime fechaProcesamiento) {
        this.fechaProcesamiento = fechaProcesamiento;
    }
} 
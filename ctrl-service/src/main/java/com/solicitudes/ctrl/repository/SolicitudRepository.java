package com.solicitudes.ctrl.repository;

import com.solicitudes.model.Solicitud;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repositorio para acceder y manipular las solicitudes almacenadas en MongoDB.
 * Extiende MongoRepository para heredar operaciones CRUD básicas.
 */
public interface SolicitudRepository extends MongoRepository<Solicitud, String> {
    // No es necesario definir métodos adicionales, ya que MongoRepository
    // proporciona las operaciones básicas como save(), findAll(), findById(), etc.
} 
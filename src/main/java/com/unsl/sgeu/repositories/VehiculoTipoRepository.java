package com.unsl.sgeu.repositories;

import com.unsl.sgeu.models.VehiculoTipo;

import java.util.List;
import java.util.Optional;

public interface VehiculoTipoRepository {

    // -------------------- CRUD Básico --------------------
    VehiculoTipo save(VehiculoTipo vehiculoTipo);
    void update(VehiculoTipo vehiculoTipo);
    void deleteById(Short id);
    Optional<VehiculoTipo> findById(Short id);
    List<VehiculoTipo> findAll();

    // -------------------- Búsquedas específicas --------------------
    Optional<VehiculoTipo> findByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCase(String nombre);
}

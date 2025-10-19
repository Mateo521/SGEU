package com.unsl.sgeu.repositories;

import com.unsl.sgeu.models.Estacionamiento;
import java.util.List;
import java.util.Optional;

public interface EstacionamientoRepository {

    List<Estacionamiento> findAll();

    List<Estacionamiento> findByEstadoTrue();

    List<Estacionamiento> findByEstadoFalse();

    boolean existsByNombreIgnoreCase(String nombre);

    Optional<Estacionamiento> findById(Long id);

    void save(Estacionamiento estacionamiento);

    void deleteById(Long id);
}

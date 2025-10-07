package com.unsl.sgeu.repositories;

import com.unsl.sgeu.models.Estacionamiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstacionamientoRepository extends JpaRepository<Estacionamiento, Long> {
    List<Estacionamiento> findByEstadoTrue();   // activos
    List<Estacionamiento> findByEstadoFalse();  // desactivados
    boolean existsByNombreIgnoreCase(String nombre);
}

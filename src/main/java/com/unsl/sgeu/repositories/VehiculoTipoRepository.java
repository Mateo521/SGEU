package com.unsl.sgeu.repositories;

import com.unsl.sgeu.models.VehiculoTipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehiculoTipoRepository extends JpaRepository<VehiculoTipo, Short> {
    Optional<VehiculoTipo> findByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCase(String nombre);
}

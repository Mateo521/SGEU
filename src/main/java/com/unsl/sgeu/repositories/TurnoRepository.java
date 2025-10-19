package com.unsl.sgeu.repositories;

import com.unsl.sgeu.models.Estacionamiento;
import com.unsl.sgeu.models.Turno;

import java.util.List;
import java.util.Optional;

public interface TurnoRepository {

    Optional<Turno> findById(Long id);

    List<Turno> findByEmpleadoId(Long empleadoId);

    Optional<Turno> findByEmpleadoIdAndFechaFinIsNull(Long empleadoId);

    boolean existsByEmpleadoAndEstacionamiento(Long empleadoId, Long estacionamientoId);
    
    Estacionamiento findEstacionamientoActivoByEmpleadoUsuario(String usuario);

    Turno save(Turno turno);

    void update(Turno turno);

    void delete(Long id);

    boolean existsById(Long id);
}

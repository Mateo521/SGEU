// src/main/java/com/unsl/sgeu/repositories/TurnoRepository.java
package com.unsl.sgeu.repositories;

import com.unsl.sgeu.models.Estacionamiento;
import com.unsl.sgeu.models.Turno;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface TurnoRepository extends JpaRepository<Turno, Long> {

    @Query("""
        SELECT t FROM Turno t
        WHERE (:empleadoId IS NULL OR t.empleado.id = :empleadoId)
          AND (:estId IS NULL OR t.estacionamiento.id = :estId)
          AND (:fecha IS NULL OR t.fechaInicio = :fecha)
    """)
    Page<Turno> search(
            @Param("empleadoId") Long empleadoId,
            @Param("estId") Long estId,
            @Param("fecha") LocalDate fecha,
            Pageable pageable
    );

    @Query("""
        SELECT t FROM Turno t
        WHERE (:empleadoId IS NULL OR t.empleado.id = :empleadoId)
          AND (:estId IS NULL OR t.estacionamiento.id = :estId)
          AND ( (:desde IS NULL OR t.fechaInicio >= :desde)
            AND  (:hasta IS NULL OR t.fechaInicio <= :hasta) )
    """)
    Page<Turno> searchRange(
            @Param("empleadoId") Long empleadoId,
            @Param("estId") Long estId,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta,
            Pageable pageable
    );

     @Query("""
        SELECT t.estacionamiento 
        FROM Turno t 
        WHERE t.empleado.nombreUsuario = :usuario 
          AND t.fechaInicio = CURRENT_DATE
    """)
    Estacionamiento findEstacionamientoActivoByEmpleadoUsuario(@Param("usuario") String usuario);
}

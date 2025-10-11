// src/main/java/com/unsl/sgeu/repositories/TurnoRepository.java
package com.unsl.sgeu.repositories;

import com.unsl.sgeu.models.Estacionamiento;
import com.unsl.sgeu.models.Turno;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
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














        @Query("SELECT DISTINCT t.estacionamiento.id FROM Turno t WHERE t.empleado.id = :empleadoId")
    List<Long> findEstacionamientoIdsByEmpleadoId(@Param("empleadoId") Long empleadoId);

    /**
     * Obtiene los estacionamientos donde trabaja un empleado
     */
    @Query("SELECT DISTINCT t.estacionamiento FROM Turno t WHERE t.empleado.id = :empleadoId")
    List<Estacionamiento> findEstacionamientosByEmpleadoId(@Param("empleadoId") Long empleadoId);

    /**
     * Verifica si un empleado trabaja en un estacionamiento específico
     */
    @Query("SELECT COUNT(t) > 0 FROM Turno t WHERE t.empleado.id = :empleadoId AND t.estacionamiento.id = :estacionamientoId")
    boolean existsByEmpleadoAndEstacionamiento(@Param("empleadoId") Long empleadoId, @Param("estacionamientoId") Long estacionamientoId);

    /**
     * Obtiene turnos por empleado
     */
    @Query("SELECT t FROM Turno t WHERE t.empleado.id = :empleadoId")
    List<Turno> findByEmpleadoId(@Param("empleadoId") Long empleadoId);




}

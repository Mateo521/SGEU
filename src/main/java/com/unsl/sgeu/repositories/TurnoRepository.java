// src/main/java/com/unsl/sgeu/repositories/TurnoRepository.java
package com.unsl.sgeu.repositories;

import com.unsl.sgeu.models.Estacionamiento;
import com.unsl.sgeu.models.Turno;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional; // <--- IMPORT NUEVO


@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {

    //Busca turnos filtrando opcionalmente por empleadoId, estacionamientoId o una fecha específica (fechaInicio).
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
      Pageable pageable);

    //Similar a search, pero permite filtrar por un rango de fechas (desde y hasta).
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
      Pageable pageable);

      //Devuelve el estacionamiento actual donde trabaja un empleado, identificado por su nombre de usuario

  @Query("""
          SELECT DISTINCT t.estacionamiento
          FROM Turno t
          WHERE t.empleado.nombreUsuario = :usuario
            AND t.fechaFin IS NULL
      """)
  Estacionamiento findEstacionamientoActivoByEmpleadoUsuario(@Param("usuario") String usuario);

  //Devuelve una lista con los IDs de los estacionamientos donde un empleado tiene un turno activo.
  @Query("SELECT DISTINCT t.estacionamiento.id FROM Turno t " +
      "WHERE t.empleado.id = :empleadoId AND t.fechaFin IS NULL")
  List<Long> findEstacionamientoIdsByEmpleadoId(@Param("empleadoId") Long empleadoId);

    //Similar al anterior, pero devuelve objetos Estacionamiento completos.
  @Query("SELECT DISTINCT t.estacionamiento FROM Turno t WHERE t.empleado.id = :empleadoId AND t.fechaFin IS NULL")
  List<Estacionamiento> findEstacionamientosByEmpleadoId(@Param("empleadoId") Long empleadoId);

  //Verifica si ya existe un turno entre un empleado y un estacionamiento específicos.
  @Query("SELECT COUNT(t) > 0 FROM Turno t WHERE t.empleado.id = :empleadoId AND t.estacionamiento.id = :estacionamientoId")
  boolean existsByEmpleadoAndEstacionamiento(@Param("empleadoId") Long empleadoId,
      @Param("estacionamientoId") Long estacionamientoId);


      //Obtiene todos los turnos (activos e inactivos) de un empleado específico.
  @Query("SELECT t FROM Turno t WHERE t.empleado.id = :empleadoId")
  List<Turno> findByEmpleadoId(@Param("empleadoId") Long empleadoId);

  // Consulta generada automáticamente por Spring Data. Busca el turno activo (sin fecha de fin) de un empleado.
  
Optional<Turno> findByEmpleadoIdAndFechaFinIsNull(Long empleadoId);
    // Spring genera la consulta automáticamente:
    // SELECT * FROM turno 
    // WHERE empleado_id = :empleadoId AND fecha_fin IS NULL
    // Devuelve un Optional<Turno> porque puede que no exista ningún turno activo.

}

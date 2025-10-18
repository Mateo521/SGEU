package com.unsl.sgeu.repositories;

import com.unsl.sgeu.models.RegistroEstacionamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

// Repository que maneja la entidad RegistroEstacionamiento
@Repository
public interface RegistroEstacionamientoRepository extends JpaRepository<RegistroEstacionamiento, Long> {  
    // Al extender JpaRepository, Spring genera automáticamente toda la lógica CRUD
    // (findAll, findById, save, deleteById, etc.) para la entidad RegistroEstacionamiento.
    // Internamente usa EntityManager de JPA, por lo que no hace falta escribir SQL ni conexión manual.

    List<RegistroEstacionamiento> findByPatenteOrderByFechaHoraDesc(String patente);
    // Genera la consulta:
    // SELECT * FROM registro_estacionamiento WHERE patente = :patente ORDER BY fecha_hora DESC
    // Devuelve todos los registros de la patente indicada, ordenados del más reciente al más antiguo.

    boolean existsByPatente(String patente);
    // Verifica si existe algún registro con la patente dada.
    // Internamente ejecuta:
    // SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END FROM registro_estacionamiento r WHERE patente = :patente

    long countByPatente(String patente);
    // Cuenta todos los registros de la patente indicada.
    // Genera la consulta:
    // SELECT COUNT(*) FROM registro_estacionamiento WHERE patente = :patente

    long countByPatenteAndIdEstacionamiento(String patente, Long idEstacionamiento);
    // Cuenta todos los registros de la patente en un estacionamiento específico.
    // Genera la consulta:
    // SELECT COUNT(*) FROM registro_estacionamiento WHERE patente = :patente AND id_estacionamiento = :idEstacionamiento


    @Modifying
    @Query("DELETE FROM RegistroEstacionamiento r WHERE r.patente = :patente")
    int deleteByPatente(@Param("patente") String patente);

    @Query(value = "SELECT r.patente " +
            "FROM registro_estacionamiento r " +
            "WHERE r.id_est = :idEst " +
            "GROUP BY r.patente " +
            "HAVING COUNT(*) % 2 = 1 " +
            "AND TIMESTAMPDIFF(HOUR, MAX(r.fecha_hora), NOW()) >= 4", nativeQuery = true)
    List<String> findPatentesAdentroMasDeCuatroHoras(@Param("idEst") Long idEst);

    List<RegistroEstacionamiento> findByIdEstacionamientoAndTipoAndFechaHoraBetweenOrderByFechaHoraDesc(
            Long idEstacionamiento, String tipo, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // Buscar por est y rango de fechas
    List<RegistroEstacionamiento> findByIdEstacionamientoAndFechaHoraBetweenOrderByFechaHoraDesc(
            Long idEstacionamiento, LocalDateTime fechaInicio, LocalDateTime fechaFin);



            @Query(value = """
  SELECT re.*
  FROM registro_estacionamiento re
  WHERE re.id_est = :idEst
    AND re.patente IN (
      SELECT re2.patente
      FROM registro_estacionamiento re2
      WHERE re2.id_est = :idEst
      GROUP BY re2.patente
      HAVING COUNT(*) % 2 = 1
    )
  ORDER BY re.patente, re.fecha_hora
""", nativeQuery = true)
List<RegistroEstacionamiento> findRegistrosDePatentesImpares(@Param("idEst") Long idEst);

@Query(value = """
  SELECT re.*
  FROM registro_estacionamiento re
  WHERE re.id_est = :idEst
    AND re.tipo_movimiento = "SALIDA"
  ORDER BY  re.fecha_hora DESC LIMIT  5 ;
""", nativeQuery = true)
List<RegistroEstacionamiento> findRegistrosDePatentePares(@Param("idEst") Long idEst);

}

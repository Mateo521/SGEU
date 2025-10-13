package com.unsl.sgeu.repositories;

import com.unsl.sgeu.models.Estacionamiento;
import com.unsl.sgeu.models.RegistroEstacionamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RegistroEstacionamientoRepository extends JpaRepository<RegistroEstacionamiento, Long> {

    List<RegistroEstacionamiento> findByPatenteOrderByFechaHoraDesc(String patente);

    boolean existsByPatente(String patente);

    long countByPatente(String patente);



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
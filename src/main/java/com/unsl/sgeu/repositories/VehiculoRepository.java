package com.unsl.sgeu.repositories;

import com.unsl.sgeu.models.Vehiculo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public interface VehiculoRepository extends JpaRepository<Vehiculo, String> {

    Vehiculo findByPatente(String patente);

    Vehiculo findByCodigoQr(String codigoQr);

    @Query("""
            SELECT DISTINCT v FROM Vehiculo v
            JOIN RegistroEstacionamiento r ON v.patente = r.patente
            WHERE r.idEstacionamiento = :idEstacionamiento
            ORDER BY v.patente
            """)
    List<Vehiculo> findVehiculosByEstacionamiento(@Param("idEstacionamiento") Long idEstacionamiento);

    @Query("""
            SELECT DISTINCT v FROM Vehiculo v
            JOIN RegistroEstacionamiento r ON v.patente = r.patente
            WHERE r.idEstacionamiento IN :idsEstacionamientos
            ORDER BY v.patente
            """)
    List<Vehiculo> findVehiculosByEstacionamientos(@Param("idsEstacionamientos") List<Long> idsEstacionamientos);

    @Query("""
            SELECT DISTINCT v FROM Vehiculo v
            JOIN RegistroEstacionamiento r ON v.patente = r.patente
            WHERE r.idEstacionamiento = :idEstacionamiento
            AND UPPER(v.patente) LIKE UPPER(CONCAT('%', :patente, '%'))
            ORDER BY v.patente
            """)
    List<Vehiculo> findVehiculosByEstacionamientoAndPatente(
            @Param("idEstacionamiento") Long idEstacionamiento,
            @Param("patente") String patente);

    @Query("""
            SELECT DISTINCT v FROM Vehiculo v
            JOIN RegistroEstacionamiento r ON v.patente = r.patente
            WHERE r.idEstacionamiento IN :idsEstacionamientos
            AND UPPER(v.patente) LIKE UPPER(CONCAT('%', :patente, '%'))
            ORDER BY v.patente
            """)
    List<Vehiculo> findVehiculosByEstacionamientosAndPatente(
            @Param("idsEstacionamientos") List<Long> idsEstacionamientos,
            @Param("patente") String patente);
/* 
    @Query("""
            SELECT v FROM Vehiculo v
            WHERE UPPER(v.patente) LIKE UPPER(CONCAT('%', :patente, '%'))
            ORDER BY v.patente
            """)
    List<Vehiculo> findByPatenteContainingIgnoreCase(@Param("patente") String patente);
*/
    @Query("SELECT v FROM Vehiculo v WHERE v.patente = :patente")
    Vehiculo findVehiculoByPatente(@Param("patente") String patente);

    @Query("""
            SELECT DISTINCT v FROM Vehiculo v
            WHERE v.patente IN (
                SELECT DISTINCT re.patente FROM RegistroEstacionamiento re
                WHERE re.idEstacionamiento IN :idsEstacionamientos
            )
            OR v.patente IN (
                SELECT v2.patente FROM Vehiculo v2
                WHERE v2.patente NOT IN (
                    SELECT DISTINCT re2.patente FROM RegistroEstacionamiento re2
                )
            )
            ORDER BY v.patente
            """)
    List<Vehiculo> findAllVehiculosByGuardiaEstacionamientos(
            @Param("idsEstacionamientos") List<Long> idsEstacionamientos);

    @Query("SELECT e.nombre FROM RegistroEstacionamiento re " +
            "JOIN Estacionamiento e ON re.idEstacionamiento = e.idEst " +
            "WHERE re.patente = :patente AND re.idEstacionamiento IN :idsEstacionamientos " +
            "ORDER BY re.fechaHora ASC LIMIT 1")
    String findEstacionamientoOrigenByPatente(@Param("patente") String patente,
            @Param("idsEstacionamientos") List<Long> idsEstacionamientos);

     
    Page<Vehiculo> findByPatenteContainingIgnoreCase(String patente, Pageable pageable);

    
    @Query("SELECT v FROM Vehiculo v WHERE UPPER(v.patente) LIKE UPPER(CONCAT('%', :patente, '%')) ORDER BY v.patente")
    List<Vehiculo> findByPatenteContainingIgnoreCase(@Param("patente") String patente);
}

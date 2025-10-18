package com.unsl.sgeu.repositories;

import com.unsl.sgeu.models.Vehiculo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// Repository que maneja la entidad Vehiculo
@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, String> {  
    // Al extender JpaRepository, Spring genera automáticamente toda la lógica CRUD
    // (findAll, findById, save, deleteById, etc.) para la entidad Vehiculo.
    // Internamente usa EntityManager de JPA, por lo que no hace falta escribir SQL ni conexión manual.

    Vehiculo findByPatente(String patente);
    // Genera la consulta:
    // SELECT * FROM vehiculo WHERE patente = :patente
    // Devuelve el vehículo con la patente exacta, o null si no existe.

    Vehiculo findByCodigoQr(String codigoQr);
    // Genera la consulta:
    // SELECT * FROM vehiculo WHERE codigo_qr = :codigoQr
    // Devuelve el vehículo con el código QR especificado, o null si no existe.

    Page<Vehiculo> findByPatenteContainingIgnoreCase(String patente, Pageable pageable);
    // Genera la consulta:
    // SELECT * FROM vehiculo WHERE LOWER(patente) LIKE LOWER(:patente)
    // Devuelve una página de resultados (Page<Vehiculo>) que contienen la cadena dada,
    // útil para búsquedas parciales con paginación.

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

     

    
    @Query("SELECT v FROM Vehiculo v WHERE UPPER(v.patente) LIKE UPPER(CONCAT('%', :patente, '%')) ORDER BY v.patente")
    List<Vehiculo> findByPatenteContainingIgnoreCase(@Param("patente") String patente);
}

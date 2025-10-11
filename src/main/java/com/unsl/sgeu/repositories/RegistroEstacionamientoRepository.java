package com.unsl.sgeu.repositories;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.unsl.sgeu.models.RegistroEstacionamiento;

public interface RegistroEstacionamientoRepository extends JpaRepository<RegistroEstacionamiento, Long> {

    long countByPatente(String patente);

    @Query (value = "SELECT r.patente " +
               "FROM registro_estacionamiento r " +
               "GROUP BY r.patente " +
               "HAVING COUNT(*) % 2 = 1 " +
               "AND TIMESTAMPDIFF(HOUR, MAX(r.fecha_hora), NOW()) >= 4",
       nativeQuery = true)
    List<String> findPatentesAdentroMasDeCuatroHoras();


}
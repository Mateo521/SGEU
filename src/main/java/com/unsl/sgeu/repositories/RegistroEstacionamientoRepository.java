package com.unsl.sgeu.repositories;

import com.unsl.sgeu.models.RegistroEstacionamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistroEstacionamientoRepository extends JpaRepository<RegistroEstacionamiento, Long> {
    
    List<RegistroEstacionamiento> findByPatenteOrderByFechaHoraDesc(String patente);
    
    boolean existsByPatente(String patente);
    
    long countByPatente(String patente);
    
     
    @Modifying
    @Query("DELETE FROM RegistroEstacionamiento r WHERE r.patente = :patente")
    int deleteByPatente(@Param("patente") String patente);
}

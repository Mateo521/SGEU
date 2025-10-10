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
    
    // ✅ Verificar si existe algún registro para una patente
    boolean existsByPatente(String patente);
    
    // ✅ Obtener registros por patente ordenados por fecha (más reciente primero)
    List<RegistroEstacionamiento> findByPatenteOrderByFechaHoraDesc(String patente);
    
    // ✅ Contar registros por patente
    long countByPatente(String patente);
    
    // ✅ Eliminar registros por patente
    @Modifying
    @Query("DELETE FROM RegistroEstacionamiento r WHERE r.patente = :patente")
    void deleteByPatente(@Param("patente") String patente);
    
    // ✅ Obtener todos los registros ordenados por fecha
    List<RegistroEstacionamiento> findAllByOrderByFechaHoraDesc();
}

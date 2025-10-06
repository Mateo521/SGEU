package com.unsl.sgeu.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import com.unsl.sgeu.models.RegistroEstacionamiento;

public interface RegistroEstacionamientoRepository extends JpaRepository<RegistroEstacionamiento, String> {

    RegistroEstacionamiento findByPatente(String patente);

    
}
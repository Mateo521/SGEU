package com.unsl.sgeu.repositories;

import com.unsl.sgeu.models.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonaRepository extends JpaRepository<Persona, Long> {

    Optional<Persona> findByEmailIgnoreCase(String email);

    Optional<Persona> findByTelefono(String telefono);

    // Si querés mantenerlo por legibilidad, aunque ya existe existsById:
    boolean existsByDni(Long dni);  // opcional
}

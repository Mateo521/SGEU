
package com.unsl.sgeu.repositories;

import com.unsl.sgeu.models.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonaRepository extends JpaRepository<Persona, Long> {
    // Buscar persona por email
    Persona findByEmail(String email);

    // Buscar persona por teléfono
    Persona findByTelefono(String telefono);

    boolean existsByDni(Long dni);
}

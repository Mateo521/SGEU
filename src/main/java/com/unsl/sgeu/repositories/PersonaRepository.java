package com.unsl.sgeu.repositories;

import com.unsl.sgeu.models.Persona;

import java.util.List;
import java.util.Optional;

public interface PersonaRepository {

    List<Persona> findAll();

    Optional<Persona> findById(Long id);

    Optional<Persona> findByEmailIgnoreCase(String email);

    Optional<Persona> findByTelefono(String telefono);

    boolean existsByDni(Long dni);

    Persona save(Persona persona);

    void update(Persona persona);

    void deleteById(Long id);
}

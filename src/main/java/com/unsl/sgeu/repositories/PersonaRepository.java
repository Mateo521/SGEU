package com.unsl.sgeu.repositories;

import com.unsl.sgeu.models.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import java.util.Collection;
import java.util.List;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {  
    // Al extender JpaRepository, Spring genera automáticamente toda la lógica CRUD
    // (findAll, findById, save, deleteById, etc.) para la entidad Persona.
    // Internamente usa EntityManager de JPA, por lo que no hace falta escribir SQL ni conexión manual.

    Optional<Persona> findByEmailIgnoreCase(String email);
    // automáticamente la consulta:
    // SELECT * FROM persona WHERE LOWER(email) = LOWER(:email)

    Optional<Persona> findByTelefono(String telefono);
    // La consulta generada internamente sería:
    // SELECT * FROM persona WHERE telefono = :telefono

    boolean existsByDni(Long dni);
    // El prefijo "existsBy" le indica a Spring que debe comprobar la existencia del registro.
    // Internamente ejecuta una consulta tipo:
    // SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END FROM persona p WHERE p.dni = :dni

    List<Persona> findByDniIn(Collection<Long> dnis);

}

package com.unsl.sgeu.repositories;

import com.unsl.sgeu.models.Estacionamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repository que maneja la entidad Estacionamiento
@Repository
public interface EstacionamientoRepository extends JpaRepository<Estacionamiento, Long> {  
    // Al extender JpaRepository, Spring genera automáticamente toda la lógica CRUD
    // (findAll, findById, save, deleteById, etc.) para la entidad Estacionamiento.
    // Internamente usa EntityManager de JPA, por lo que no hace falta escribir SQL ni conexión manual.

    List<Estacionamiento> findAll();
    // Genera la consulta:
    // SELECT * FROM estacionamiento
    // Devuelve todos los registros de la tabla estacionamiento.

    List<Estacionamiento> findByEstadoTrue();   // activos
    // Genera la consulta:
    // SELECT * FROM estacionamiento WHERE estado = TRUE
    // Devuelve todos los estacionamientos que estén activos.

    List<Estacionamiento> findByEstadoFalse();  // desactivados
    // Genera la consulta:
    // SELECT * FROM estacionamiento WHERE estado = FALSE
    // Devuelve todos los estacionamientos que estén desactivados.

    boolean existsByNombreIgnoreCase(String nombre);
    // Verifica si existe algún estacionamiento con el nombre dado (case-insensitive).
    // Internamente ejecuta:
    // SELECT CASE WHEN COUNT(e) > 0 THEN TRUE ELSE FALSE END FROM estacionamiento e WHERE LOWER(e.nombre) = LOWER(:nombre)
}


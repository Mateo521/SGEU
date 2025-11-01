package com.unsl.sgeu.repositories;

import com.unsl.sgeu.models.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repository que maneja la entidad Categoria
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Short> {  
    // Al extender JpaRepository, Spring genera automáticamente toda la lógica CRUD
    // (findAll, findById, save, deleteById, etc.) para la entidad Categoria.
    // Internamente usa EntityManager de JPA, por lo que no hace falta escribir SQL ni conexión manual.

    Optional<Categoria> findByNombreIgnoreCase(String nombre);
    // Spring genera automáticamente la consulta:
    // SELECT * FROM categoria WHERE LOWER(nombre) = LOWER(:nombre)

    boolean existsByNombreIgnoreCase(String nombre);
    // El prefijo "existsBy" indica que verifica existencia.
    // Internamente ejecuta:
    // SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END FROM categoria c WHERE LOWER(c.nombre) = LOWER(:nombre)
}


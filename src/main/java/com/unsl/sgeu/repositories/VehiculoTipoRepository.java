package com.unsl.sgeu.repositories;

import com.unsl.sgeu.models.VehiculoTipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repository que maneja la entidad VehiculoTipo
@Repository
public interface VehiculoTipoRepository extends JpaRepository<VehiculoTipo, Short> {  
    // Al extender JpaRepository, Spring genera automáticamente toda la lógica CRUD
    // (findAll, findById, save, deleteById, etc.) para la entidad VehiculoTipo.
    // Internamente usa EntityManager de JPA, por lo que no hace falta escribir SQL ni conexión manual.

    Optional<VehiculoTipo> findByNombreIgnoreCase(String nombre);
    // Genera automáticamente la consulta:
    // SELECT * FROM vehiculo_tipo WHERE LOWER(nombre) = LOWER(:nombre)
    // Devuelve Optional<VehiculoTipo> por si no existe ningún registro.

    boolean existsByNombreIgnoreCase(String nombre);
    // Verifica si existe algún VehiculoTipo con el nombre dado (case-insensitive).
    // Internamente ejecuta:
    // SELECT CASE WHEN COUNT(vt) > 0 THEN TRUE ELSE FALSE END FROM vehiculo_tipo vt WHERE LOWER(vt.nombre) = LOWER(:nombre)
}


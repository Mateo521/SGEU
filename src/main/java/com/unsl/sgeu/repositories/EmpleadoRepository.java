package com.unsl.sgeu.repositories;

import com.unsl.sgeu.models.Empleado;
import com.unsl.sgeu.models.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Repository que maneja la entidad Empleado
@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {  
    // Al extender JpaRepository, Spring genera automáticamente toda la lógica CRUD
    // (findAll, findById, save, deleteById, etc.) para la entidad Empleado.
    // Internamente usa EntityManager de JPA, por lo que no hace falta escribir SQL ni conexión manual.

    // Buscar por nombre de usuario (exacto)
    Empleado findByNombreUsuario(String nombreUsuario);
    // Genera automáticamente la consulta:
    // SELECT * FROM empleado WHERE nombre_usuario = :nombreUsuario
    // Devuelve directamente el Empleado o null si no existe.

    // Variante segura (case-insensitive) y opcional
    Optional<Empleado> findByNombreUsuarioIgnoreCase(String nombreUsuario);
    // Genera la consulta internamente:
    // SELECT * FROM empleado WHERE LOWER(nombre_usuario) = LOWER(:nombreUsuario)
    // Devuelve Optional<Empleado> por si no existe registro.

    // Login
    Optional<Empleado> findByNombreUsuarioAndContrasenia(String nombreUsuario, String contrasenia);
    // Genera la consulta:
    // SELECT * FROM empleado WHERE nombre_usuario = :nombreUsuario AND contrasenia = :contrasenia
    // Devuelve Optional<Empleado> por si no existe coincidencia.

    // Filtrar por rol (admin/guardia)
    List<Empleado> findByRol(Rol rol);
    // Genera la consulta:
    // SELECT * FROM empleado WHERE rol = :rol
    // Devuelve una lista de empleados con el rol especificado.

    // Utilidades
    boolean existsByNombreUsuario(String nombreUsuario);
    // El prefijo "existsBy" indica que verifica existencia.
    // Internamente ejecuta:
    // SELECT CASE WHEN COUNT(e) > 0 THEN TRUE ELSE FALSE END FROM empleado e WHERE nombre_usuario = :nombreUsuario

    boolean existsByCorreo(String correo);
    // Consulta sobre existencia por correo:
    // SELECT CASE WHEN COUNT(e) > 0 THEN TRUE ELSE FALSE END FROM empleado e WHERE correo = :correo

    Optional<Empleado> findByCorreoIgnoreCase(String correo);
    // Consulta case-insensitive sobre el correo:
    // SELECT * FROM empleado WHERE LOWER(correo) = LOWER(:correo)
    // Devuelve Optional<Empleado> por si no existe registro.
}


package com.unsl.sgeu.repositories;

import com.unsl.sgeu.models.Empleado;
import com.unsl.sgeu.models.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    // Buscar por nombre de usuario (exacto)
    Empleado findByNombreUsuario(String nombreUsuario);

    // Variante segura (case-insensitive) y opcional
    Optional<Empleado> findByNombreUsuarioIgnoreCase(String nombreUsuario);

    // Login (ojo: en producción NO guardes contraseñas en texto plano)
    Optional<Empleado> findByNombreUsuarioAndContrasenia(String nombreUsuario, String contrasenia);

    // Filtrar por rol (admin/guardia)
    List<Empleado> findByRol(Rol rol);

    // Utilidades
    boolean existsByNombreUsuario(String nombreUsuario);
    Optional<Empleado> findByCorreoIgnoreCase(String correo);

    
}

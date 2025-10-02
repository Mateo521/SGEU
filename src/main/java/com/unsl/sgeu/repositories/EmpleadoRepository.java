package com.unsl.sgeu.repositories;


import com.unsl.sgeu.models.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    // Buscar por nombre de usuario
    Empleado findByNombreUsuario(String nombreUsuario);

    // Buscar por nombre de usuario y contraseña
    Empleado findByNombreUsuarioAndContrasenia(String nombreUsuario, String contrasenia);

    // Buscar empleados por cargo (ejemplo: "Administrador" o "Guardia")
    java.util.List<Empleado> findByCargo(String cargo);
}

package com.unsl.sgeu.repositories;

import com.unsl.sgeu.models.Empleado;
import com.unsl.sgeu.models.Rol;
import java.util.List;
import java.util.Optional;

public interface EmpleadoRepository {

    List<Empleado> findAll();

    Optional<Empleado> findById(Long id);

    Empleado save(Empleado empleado);

    void deleteById(Long id);

    Empleado findByNombreUsuario(String nombreUsuario);

    Optional<Empleado> findByNombreUsuarioIgnoreCase(String nombreUsuario);

    Optional<Empleado> findByNombreUsuarioAndContrasenia(String nombreUsuario, String contrasenia);

    List<Empleado> findByRol(Rol rol);

    boolean existsByNombreUsuario(String nombreUsuario);

    Optional<Empleado> findByCorreoIgnoreCase(String correo);
}

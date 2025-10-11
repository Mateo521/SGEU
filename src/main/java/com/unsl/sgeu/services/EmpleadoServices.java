package com.unsl.sgeu.services;

import com.unsl.sgeu.models.Empleado;
import com.unsl.sgeu.models.Estacionamiento;
import com.unsl.sgeu.models.Rol;
import com.unsl.sgeu.repositories.EmpleadoRepository;
import com.unsl.sgeu.repositories.TurnoRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class EmpleadoServices {

    private final EmpleadoRepository empleadoRepository;
    private final PasswordEncoder passwordEncoder;
    private final TurnoRepository turnoRepository;

    public EmpleadoServices(EmpleadoRepository empleadoRepository,
            PasswordEncoder passwordEncoder,
            TurnoRepository turnoRepository) {
        this.empleadoRepository = empleadoRepository;
        this.passwordEncoder = passwordEncoder;
        this.turnoRepository = turnoRepository;
    }

    /* ===================== AUTH ===================== */

    public boolean login(String nombreUsuario, String contrasenia) {
        Optional<Empleado> opt = empleadoRepository.findByNombreUsuarioIgnoreCase(nombreUsuario);
        if (opt.isEmpty())
            return false;
        Empleado empleado = opt.get();
        return passwordEncoder.matches(contrasenia, empleado.getContrasenia());
    }

    /** Registra con rol explícito (admin/guardia). */
    public boolean register(String nombre,
            String apellido,
            String nombreUsuario,
            String contrasenia,
            String correo,
            Rol rol) {
        if (empleadoRepository.existsByNombreUsuario(nombreUsuario)) {
            return false; // usuario ya existe
        }
        Empleado nuevo = new Empleado();
        nuevo.setNombre(nombre);
        nuevo.setApellido(apellido);
        nuevo.setNombreUsuario(nombreUsuario);
        nuevo.setCorreo(correo);
        nuevo.setContrasenia(passwordEncoder.encode(contrasenia));
        nuevo.setRol(rol != null ? rol : Rol.Guardia);

        empleadoRepository.save(nuevo);
        return true;
    }

    /** Overload: por compatibilidad si aún te llega "cargo" como String. */
    public boolean register(String nombre,
            String apellido,
            String nombreUsuario,
            String contrasenia,
            String correo,
            String cargoStr) {
        Rol rol = ("Administrador".equalsIgnoreCase(cargoStr)) ? Rol.Administrador : Rol.Guardia;
        return register(nombre, apellido, nombreUsuario, contrasenia, correo, rol);
    }

    /* ===================== QUERIES ===================== */

    public String obtenerNombreEmpleado(Long id) {
        return empleadoRepository.findById(id)
                .map(e -> e.getNombre() + " " + e.getApellido())
                .orElse("Nombre no encontrado");
    }

    /** Devuelve el rol como String ("admin"/"guardia") */
    public String obtenerRolEmpleado(String nombreUsuario) {
        return empleadoRepository.findByNombreUsuarioIgnoreCase(nombreUsuario)
                .map(e -> e.getRol().name())
                .orElse("Rol no encontrado");
    }

    public Iterable<Empleado> listarEmpleados() {
        return empleadoRepository.findAll();
    }

    public String obtenerNombrePorUsuario(String nombreUsuario) {
        return empleadoRepository.findByNombreUsuarioIgnoreCase(nombreUsuario)
                .map(e -> e.getNombre() + " " + e.getApellido())
                .orElse("Nombre no encontrado");
    }

    public Estacionamiento obtenerEstacionamientoActivo(String usuario) {
        return turnoRepository.findEstacionamientoActivoByEmpleadoUsuario(usuario);
    }

    public Long obtenerIdPorUsuario(String nombreUsuario) {
        try {
            Empleado empleado = empleadoRepository.findByNombreUsuario(nombreUsuario);
            return empleado != null ? empleado.getId() : null;
        } catch (Exception e) {
            System.err.println("Error al obtener ID por usuario: " + e.getMessage());
            return null;
        }
    }

}

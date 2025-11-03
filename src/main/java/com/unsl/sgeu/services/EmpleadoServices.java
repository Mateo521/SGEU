package com.unsl.sgeu.services;

import com.unsl.sgeu.dto.EmpleadoDTO;
import com.unsl.sgeu.dto.SessionDTO;
import com.unsl.sgeu.mappers.EmpleadoMapper;
import com.unsl.sgeu.models.Empleado;
import com.unsl.sgeu.models.Estacionamiento;
import com.unsl.sgeu.models.Rol;
import com.unsl.sgeu.repositories.EmpleadoRepository;
import com.unsl.sgeu.repositories.TurnoRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmpleadoServices {

    private final EmpleadoRepository empleadoRepository;
    private final PasswordEncoder passwordEncoder;
    private final TurnoRepository turnoRepository;

    public EmpleadoServices(
            EmpleadoRepository empleadoRepository,
            PasswordEncoder passwordEncoder,
            TurnoRepository turnoRepository) {
        this.empleadoRepository = empleadoRepository;
        this.passwordEncoder = passwordEncoder;
        this.turnoRepository = turnoRepository;
    }

    /* ===================== AUTH ===================== */

    public SessionDTO autenticarYObtenerDatosSesion(String nombreUsuario, String contrasenia) {
        // 1. Verificar credenciales
        Optional<Empleado> opt = empleadoRepository.findByNombreUsuarioIgnoreCase(nombreUsuario);
        if (opt.isEmpty() || !passwordEncoder.matches(contrasenia, opt.get().getContrasenia())) {
            return SessionDTO.loginFallido();
        }

        // 2. Obtener datos del empleado
        Empleado empleado = opt.get();

        // 3. Si es guardia, validar turno activo y horario
        if (empleado.getRol() == Rol.Guardia) {
            // Buscar turno activo (sin fecha de fin)
            var turnoOpt = turnoRepository.findByEmpleadoIdAndFechaFinIsNull(empleado.getId());
            if (turnoOpt.isEmpty()) {
                // No tiene turno activo
                return SessionDTO.loginFallido("fuera_turno");
            }
            var turno = turnoOpt.get();
            java.time.LocalTime ahora = java.time.LocalTime.now();
            if (ahora.isBefore(turno.getHoraIngreso()) || ahora.isAfter(turno.getHoraSalida())) {
                // No está en horario de turno
                return SessionDTO.loginFallido("fuera_turno");
            }
        }

        // 4. Obtener estacionamiento activo
        Estacionamiento estacionamiento = turnoRepository.findEstacionamientoActivoByEmpleadoUsuario(nombreUsuario);

        // 5. Construir DTO de sesión
        return SessionDTO.loginExitoso(
            empleado.getId(),
            empleado.getNombreUsuario(),
            empleado.getRol().name(),
            empleado.getNombre() + " " + empleado.getApellido(),
            estacionamiento != null ? estacionamiento.getIdEst() : null,
            estacionamiento != null ? estacionamiento.getNombre() : null
        );
    }

    // Registra con rol explícito (admin/guardia)
    public boolean register(String nombre,
            String apellido,
            String nombreUsuario,
            String contrasenia,
            String correo,
            Rol rol) {
        if (empleadoRepository.existsByNombreUsuario(nombreUsuario)) {
            return false; // usuario q ya existe
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

    //Aca comparamos que el string del select sea igual a administrador o no
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

    public Iterable<Empleado> listarEmpleados() {
        return empleadoRepository.findAll();
    }

    /** Devuelve solo guardias en formato DTO */
    public List<EmpleadoDTO> listarGuardias() {
        return empleadoRepository.findByRol(Rol.Guardia)
                .stream()
                .map(EmpleadoMapper::toDTO)
                .toList();
    }

    /* ===================== UPDATE ===================== */

    public EmpleadoDTO actualizarEmpleado(Long id, EmpleadoDTO dto) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + id));

        // Aplicar los cambios desde el DTO usando el mapper
        EmpleadoMapper.updateEntityFromDTO(empleado, dto);

        // Guardar los cambios en la BD
        Empleado actualizado = empleadoRepository.save(empleado);

        // Retornar el DTO actualizado para respuesta al frontend
        return EmpleadoMapper.toDTO(actualizado);
    }
}

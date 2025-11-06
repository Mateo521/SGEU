package com.unsl.sgeu.servicesimpl;

import com.unsl.sgeu.dto.EmpleadoDTO;
import com.unsl.sgeu.dto.SessionDTO;
import com.unsl.sgeu.mappers.EmpleadoMapper;
import com.unsl.sgeu.models.Empleado;
import com.unsl.sgeu.models.Estacionamiento;
import com.unsl.sgeu.models.Rol;
import com.unsl.sgeu.repositories.EmpleadoRepository;
import com.unsl.sgeu.repositories.TurnoRepository;
import com.unsl.sgeu.services.EmpleadoServices;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class EmpleadoServiceImpl implements EmpleadoServices {

    private final EmpleadoRepository empleadoRepository;
    private final PasswordEncoder passwordEncoder;
    private final TurnoRepository turnoRepository;

    public EmpleadoServiceImpl(EmpleadoRepository empleadoRepository,
                               PasswordEncoder passwordEncoder,
                               TurnoRepository turnoRepository) {
        this.empleadoRepository = empleadoRepository;
        this.passwordEncoder = passwordEncoder;
        this.turnoRepository = turnoRepository;
    }

    /* ===================== AUTH ===================== */

    @Override
    @Transactional
    public SessionDTO autenticarYObtenerDatosSesion(String nombreUsuario, String contrasenia) {
        Optional<Empleado> opt = empleadoRepository.findByNombreUsuarioIgnoreCase(nombreUsuario);
        if (opt.isEmpty() || !passwordEncoder.matches(contrasenia, opt.get().getContrasenia())) {
            return SessionDTO.loginFallido();
        }

        Empleado empleado = opt.get();

        if (empleado.getRol() == Rol.Guardia) {
            var turnoOpt = turnoRepository.findByEmpleadoIdAndFechaFinIsNull(empleado.getId());
            if (turnoOpt.isEmpty()) {
                return SessionDTO.loginFallido("fuera_turno");
            }
            var turno = turnoOpt.get();
            LocalTime ahora = LocalTime.now();
            if (ahora.isBefore(turno.getHoraIngreso()) || ahora.isAfter(turno.getHoraSalida())) {
                return SessionDTO.loginFallido("fuera_turno");
            }
        }

        Estacionamiento estacionamiento = turnoRepository.findEstacionamientoActivoByEmpleadoUsuario(nombreUsuario);

        return SessionDTO.loginExitoso(
                empleado.getId(),
                empleado.getNombreUsuario(),
                empleado.getRol().name(),
                empleado.getNombre() + " " + empleado.getApellido(),
                (estacionamiento != null ? estacionamiento.getIdEst() : null),
                (estacionamiento != null ? estacionamiento.getNombre() : null)
        );
    }

    /* ===================== REGISTER ===================== */

    @Override
    @Transactional
    public boolean register(String nombre,
                            String apellido,
                            String nombreUsuario,
                            String contrasenia,
                            String correo,
                            Rol rol) {
        if (empleadoRepository.existsByNombreUsuario(nombreUsuario) || empleadoRepository.existsByCorreo(correo)) {
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

    @Override
    @Transactional
    public boolean register(String nombre,
                            String apellido,
                            String nombreUsuario,
                            String contrasenia,
                            String correo,
                            String cargoStr) {
        if (nombre == null || nombre.isBlank() ||
            apellido == null || apellido.isBlank() ||
            nombreUsuario == null || nombreUsuario.isBlank() ||
            contrasenia == null || contrasenia.isBlank() ||
            correo == null || correo.isBlank()) {
            return false;
        }

        if (!correo.matches("^[^@]+@[^@]+\\.[a-zA-Z]{2,}$")) {
            return false;
        }

        if (contrasenia.length() < 8) {
            return false;
        }
        Rol rol = ("Administrador".equalsIgnoreCase(cargoStr)) ? Rol.Administrador : Rol.Guardia;
        return register(nombre, apellido, nombreUsuario, contrasenia, correo, rol);
    }

    /* ===================== QUERIES ===================== */

    @Override
    @Transactional(readOnly = true)
    public String obtenerNombreEmpleado(Long id) {
        return empleadoRepository.findById(id)
                .map(e -> e.getNombre() + " " + e.getApellido())
                .orElse("Nombre no encontrado");
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<Empleado> listarEmpleados() {
        return empleadoRepository.findAll();
    }

    /** Devuelve solo guardias en formato DTO */
    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoDTO> listarGuardias() {
        return empleadoRepository.findByRol(Rol.Guardia)
                .stream()
                .map(EmpleadoMapper::toDTO)
                .toList();
    }

    /* ===================== UPDATE ===================== */

    @Override
    @Transactional
    public EmpleadoDTO actualizarEmpleado(Long id, EmpleadoDTO dto) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + id));

        // Aplicar cambios desde el DTO usando el mapper
        EmpleadoMapper.updateEntityFromDTO(empleado, dto);

        // Guardar y devolver DTO actualizado
        Empleado actualizado = empleadoRepository.save(empleado);
        return EmpleadoMapper.toDTO(actualizado);
    }
}

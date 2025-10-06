package com.unsl.sgeu.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.unsl.sgeu.models.Empleado;
import com.unsl.sgeu.repositories.EmpleadoRepository;

@Service
public class EmpleadoServices {

    @Autowired
    private EmpleadoRepository empleadoRepository;
    private final PasswordEncoder passwordEncoder;

    public EmpleadoServices(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public boolean login(String nombreUsuario, String contrasenia) {
        Empleado empleado = empleadoRepository.findByNombreUsuario(nombreUsuario);
        if (empleado == null) {
            return false; 
        }
        return passwordEncoder.matches(contrasenia, empleado.getContrasenia());
    }

    public boolean register(String nombre, String apellido, String nombreUsuario, String contrasenia, String correo, String cargo) {
        Empleado empleadoExistente = empleadoRepository.findByNombreUsuario(nombreUsuario);
        if (empleadoExistente != null) {
            return false; 
        }
        Empleado nuevoEmpleado = new Empleado();
        nuevoEmpleado.setNombre(nombre);
        nuevoEmpleado.setApellido(apellido);
        nuevoEmpleado.setNombreUsuario(nombreUsuario);
        String passwordEncriptada = passwordEncoder.encode(contrasenia);
        nuevoEmpleado.setContrasenia(passwordEncriptada);
        nuevoEmpleado.setCargo(cargo);
        nuevoEmpleado.setCorreo(correo);
        empleadoRepository.save(nuevoEmpleado);
        return true;
    }

    public String obtenerNombreEmpleado(Long id) {
        Optional<Empleado> empleado = empleadoRepository.findById(id);
        if (empleado.isPresent()) {
            return empleado.get().getNombre() + " " + empleado.get().getApellido();
        }
        return "Nombre no encontrado";
    }

    public String obtenerCargoEmpleado(String nombreUsuario) {
        Empleado empleado = empleadoRepository.findByNombreUsuario(nombreUsuario);
        if (empleado != null) {
            return empleado.getCargo();
        }
        return "Cargo no encontrado";
    }

    public Iterable<Empleado> listarEmpleados() {
        return empleadoRepository.findAll();
    }

    
}

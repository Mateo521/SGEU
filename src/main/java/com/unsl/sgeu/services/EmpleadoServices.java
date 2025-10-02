package com.unsl.sgeu.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unsl.sgeu.models.Empleado;
import com.unsl.sgeu.repositories.EmpleadoRepository;

@Service
public class EmpleadoServices {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    public boolean login(String nombreUsuario, String contrasenia) {
        Empleado empleado = empleadoRepository.findByNombreUsuarioAndContrasenia(nombreUsuario, contrasenia);
        if (empleado == null) {
            return false; 
        }
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


    
}

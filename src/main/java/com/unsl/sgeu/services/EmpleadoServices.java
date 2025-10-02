package com.unsl.sgeu.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unsl.sgeu.models.Empleado;
import com.unsl.sgeu.repositories.EmpleadoRepository;

@Service
public class EmpleadoServices {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    public boolean login(String nombreUsuario, String contrasenia) {
        // Lógica de autenticación
        Empleado empleado = empleadoRepository.findByNombreUsuarioAndContrasenia(nombreUsuario, contrasenia);
        if (empleado == null) {
            return false; // Usuario o contraseña incorrectos
        }
        return true; // Reemplazar con la lógica real
    }

    public String obtenerNombreEmpleado(Long id) {
        // Lógica para obtener el nombre del empleado por su ID
        return "Nombre del Empleado"; // Reemplazar con la lógica real
    }

    // Otros métodos relacionados con empleados
    public String obtenerCargoEmpleado(Long id) {
        // Lógica para obtener el cargo del empleado por su ID
        return "Cargo del Empleado"; // Reemplazar con la lógica real
    }


    
}

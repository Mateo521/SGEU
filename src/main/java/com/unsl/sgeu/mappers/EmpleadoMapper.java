// src/main/java/com/unsl/sgeu/mappers/EmpleadoMapper.java
package com.unsl.sgeu.mappers;

import com.unsl.sgeu.dto.EmpleadoDTO;
import com.unsl.sgeu.models.Empleado;

public class EmpleadoMapper {

    private static String nombreCompleto(Empleado e) {
        String nombre = (e.getNombre() != null) ? e.getNombre().trim() : "";
        String apellido = (e.getApellido() != null) ? e.getApellido().trim() : "";
        if (nombre.isEmpty()) return apellido;
        if (apellido.isEmpty()) return nombre;
        return nombre + " " + apellido;
    }

    public static EmpleadoDTO toDTO(Empleado e) {
        if (e == null) return null;
        return new EmpleadoDTO(
                e.getId(),
                nombreCompleto(e),
                e.getNombreUsuario(),
                e.getCorreo()
        );
    }
}

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

    /** Entidad → DTO */
    public static EmpleadoDTO toDTO(Empleado e) {
        if (e == null) return null;
        return new EmpleadoDTO(
                e.getId(),
                nombreCompleto(e),
                e.getNombreUsuario(),
                e.getCorreo()
        );
    }

    /** DTO → Entidad */
    public static void updateEntityFromDTO(Empleado e, EmpleadoDTO dto) {
        if (dto == null || e == null) return;

        // Nombre completo: separar si viene junto
        if (dto.getNombreCompleto() != null && !dto.getNombreCompleto().isBlank()) {
            String[] partes = dto.getNombreCompleto().trim().split(" ", 2);
            e.setNombre(partes[0]);
            if (partes.length > 1) e.setApellido(partes[1]);
        }

        if (dto.getNombreUsuario() != null && !dto.getNombreUsuario().isBlank())
            e.setNombreUsuario(dto.getNombreUsuario().trim());

        if (dto.getCorreo() != null && !dto.getCorreo().isBlank())
            e.setCorreo(dto.getCorreo().trim());
    }
}

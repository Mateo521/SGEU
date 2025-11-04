package com.unsl.sgeu.services;

import com.unsl.sgeu.dto.EmpleadoDTO;
import com.unsl.sgeu.dto.SessionDTO;
import com.unsl.sgeu.models.Empleado;
import com.unsl.sgeu.models.Rol;

import java.util.List;

public interface EmpleadoServices {

    /* ===================== AUTH ===================== */
    SessionDTO autenticarYObtenerDatosSesion(String nombreUsuario, String contrasenia);

    /* ===================== REGISTER ===================== */
    boolean register(String nombre,
                     String apellido,
                     String nombreUsuario,
                     String contrasenia,
                     String correo,
                     Rol rol);

    boolean register(String nombre,
                     String apellido,
                     String nombreUsuario,
                     String contrasenia,
                     String correo,
                     String cargoStr);

    /* ===================== QUERIES ===================== */
    String obtenerNombreEmpleado(Long id);

    Iterable<Empleado> listarEmpleados();

    /** Devuelve solo guardias en formato DTO */
    List<EmpleadoDTO> listarGuardias();

    /* ===================== UPDATE ===================== */
    EmpleadoDTO actualizarEmpleado(Long id, EmpleadoDTO dto);
}

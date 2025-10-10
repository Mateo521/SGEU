// src/main/java/com/unsl/sgeu/mappers/TurnoMapper.java
package com.unsl.sgeu.mappers;

import com.unsl.sgeu.dto.TurnoCreateDTO;
import com.unsl.sgeu.dto.TurnoDTO;
import com.unsl.sgeu.models.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class TurnoMapper {

    public static Turno fromCreateDTO(TurnoCreateDTO dto, Empleado empleado, Estacionamiento est) {
        Turno t = new Turno();
        t.setEmpleado(empleado);
        t.setEstacionamiento(est);
        t.setFechaInicio(LocalDate.parse(dto.getFechaInicio()));
        t.setFechaFin(dto.getFechaFin() != null && !dto.getFechaFin().isBlank()
                ? LocalDate.parse(dto.getFechaFin()) : null);
        t.setHoraIngreso(LocalTime.parse(dto.getHoraIngreso()));
        t.setHoraSalida(LocalTime.parse(dto.getHoraSalida()));
        return t;
    }

    public static void copyFromCreateDTO(Turno t, TurnoCreateDTO dto, Empleado empleado, Estacionamiento est) {
        t.setEmpleado(empleado);
        t.setEstacionamiento(est);
        t.setFechaInicio(LocalDate.parse(dto.getFechaInicio()));
        t.setFechaFin(dto.getFechaFin() != null && !dto.getFechaFin().isBlank()
                ? LocalDate.parse(dto.getFechaFin()) : null);
        t.setHoraIngreso(LocalTime.parse(dto.getHoraIngreso()));
        t.setHoraSalida(LocalTime.parse(dto.getHoraSalida()));
    }

    public static TurnoDTO toDTO(Turno t) {
        TurnoDTO d = new TurnoDTO();
        d.setId(t.getId());
        d.setEmpleadoId(t.getEmpleado().getId()); // ajustá si tu PK es DNI u otro campo
        d.setEmpleadoNombre(t.getEmpleado().getNombre());
        d.setEstacionamientoId(t.getEstacionamiento().getIdEst());
        d.setEstacionamientoNombre(t.getEstacionamiento().getNombre());
        d.setFechaInicio(t.getFechaInicio().toString());
        d.setFechaFin(t.getFechaFin() != null ? t.getFechaFin().toString() : null);
        d.setHoraIngreso(t.getHoraIngreso().toString());
        d.setHoraSalida(t.getHoraSalida().toString());
        return d;
    }
}

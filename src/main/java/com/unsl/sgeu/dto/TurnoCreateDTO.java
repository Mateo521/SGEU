// src/main/java/com/unsl/sgeu/dto/TurnoCreateDTO.java
package com.unsl.sgeu.dto;

import jakarta.validation.constraints.*;
public class TurnoCreateDTO {
    @NotNull private Long empleadoId;      // id_empleado
    @NotNull private Long estacionamientoId; // id_est
    @NotBlank private String fechaInicio;  // "2025-10-09"
    private String fechaFin;               // opcional
    @NotBlank private String horaIngreso;  // "08:00"
    @NotBlank private String horaSalida;   // "14:00"

    // getters/setters
    public Long getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(Long empleadoId) { this.empleadoId = empleadoId; }
    public Long getEstacionamientoId() { return estacionamientoId; }
    public void setEstacionamientoId(Long estacionamientoId) { this.estacionamientoId = estacionamientoId; }
    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }
    public String getFechaFin() { return fechaFin; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }
    public String getHoraIngreso() { return horaIngreso; }
    public void setHoraIngreso(String horaIngreso) { this.horaIngreso = horaIngreso; }
    public String getHoraSalida() { return horaSalida; }
    public void setHoraSalida(String horaSalida) { this.horaSalida = horaSalida; }
}

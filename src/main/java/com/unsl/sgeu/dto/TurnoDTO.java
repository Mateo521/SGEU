// src/main/java/com/unsl/sgeu/dto/TurnoDTO.java
package com.unsl.sgeu.dto;

public class TurnoDTO {
    private Long id;
    private Long empleadoId;
    private String empleadoNombre;
    private Long estacionamientoId;
    private String estacionamientoNombre;
    private String fechaInicio; // ISO "2025-10-09"
    private String fechaFin;    // puede null
    private String horaIngreso; // "08:00"
    private String horaSalida;  // "14:00"

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(Long empleadoId) { this.empleadoId = empleadoId; }
    public String getEmpleadoNombre() { return empleadoNombre; }
    public void setEmpleadoNombre(String empleadoNombre) { this.empleadoNombre = empleadoNombre; }
    public Long getEstacionamientoId() { return estacionamientoId; }
    public void setEstacionamientoId(Long estacionamientoId) { this.estacionamientoId = estacionamientoId; }
    public String getEstacionamientoNombre() { return estacionamientoNombre; }
    public void setEstacionamientoNombre(String estacionamientoNombre) { this.estacionamientoNombre = estacionamientoNombre; }
    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }
    public String getFechaFin() { return fechaFin; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }
    public String getHoraIngreso() { return horaIngreso; }
    public void setHoraIngreso(String horaIngreso) { this.horaIngreso = horaIngreso; }
    public String getHoraSalida() { return horaSalida; }
    public void setHoraSalida(String horaSalida) { this.horaSalida = horaSalida; }
}

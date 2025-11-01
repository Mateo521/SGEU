package com.unsl.sgeu.dto;

public class EstacionamientoStatsDTO {
    //unifico los datos que necesito para el reporte en un solo DTO
    private Long id;
    private String nombre;
    private Integer capacidad;
    private Long ingresos;
    private Double porcentaje;

    public EstacionamientoStatsDTO() {}

    public EstacionamientoStatsDTO(Long id, String nombre, Integer capacidad, Long ingresos, Double porcentaje) {
        this.id = id; this.nombre = nombre; this.capacidad = capacidad; this.ingresos = ingresos; this.porcentaje = porcentaje;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }
    public Long getIngresos() { return ingresos; }
    public void setIngresos(Long ingresos) { this.ingresos = ingresos; }
    public Double getPorcentaje() { return porcentaje; }
    public void setPorcentaje(Double porcentaje) { this.porcentaje = porcentaje; }
}

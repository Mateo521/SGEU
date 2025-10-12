package com.unsl.sgeu.dto;

public class ModoConteoDTO {
    private String modo;
    private Long cantidad;
    private Double porcentaje;

    public ModoConteoDTO() {}
    public ModoConteoDTO(String modo, Long cantidad, Double porcentaje){ this.modo = modo; this.cantidad = cantidad; this.porcentaje = porcentaje; }
    public String getModo(){ return modo; }
    public void setModo(String modo){ this.modo = modo; }
    public Long getCantidad(){ return cantidad; }
    public void setCantidad(Long cantidad){ this.cantidad = cantidad; }
    public Double getPorcentaje(){ return porcentaje; }
    public void setPorcentaje(Double porcentaje){ this.porcentaje = porcentaje; }
}

package com.unsl.sgeu.dto;

public class EvolucionDTO {
    private String dia;
    private Long cantidad;

    public EvolucionDTO() {}
    public EvolucionDTO(String dia, Long cantidad){ this.dia = dia; this.cantidad = cantidad; }
    public String getDia(){ return dia; }
    public void setDia(String dia){ this.dia = dia; }
    public Long getCantidad(){ return cantidad; }
    public void setCantidad(Long cantidad){ this.cantidad = cantidad; }
}

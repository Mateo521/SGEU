package com.unsl.sgeu.dto;

public class TipoVehiculoDTO {
    private String tipo;
    private Long cantidad;
    private Double porcentaje;

    public TipoVehiculoDTO() {}
    public TipoVehiculoDTO(String tipo, Long cantidad, Double porcentaje){
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.porcentaje = porcentaje;
    }

    public String getTipo(){ return tipo; }
    public void setTipo(String tipo){ this.tipo = tipo; }
    public Long getCantidad(){ return cantidad; }
    public void setCantidad(Long cantidad){ this.cantidad = cantidad; }
    public Double getPorcentaje(){ return porcentaje; }
    public void setPorcentaje(Double porcentaje){ this.porcentaje = porcentaje; }
}
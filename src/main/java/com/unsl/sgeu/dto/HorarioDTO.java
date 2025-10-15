package com.unsl.sgeu.dto;

public class HorarioDTO {
    private Integer hora;
    private Long cantidad;
    public HorarioDTO(){}
    public HorarioDTO(Integer hora, Long cantidad){ this.hora = hora; this.cantidad = cantidad; }
    public Integer getHora(){ return hora; }
    public void setHora(Integer hora){ this.hora = hora; }
    public Long getCantidad(){ return cantidad; }
    public void setCantidad(Long cantidad){ this.cantidad = cantidad; }
}

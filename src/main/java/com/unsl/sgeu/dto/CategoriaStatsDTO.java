package com.unsl.sgeu.dto;

public class CategoriaStatsDTO {
    private String categoria;
    private long cantidad;
    private double porcentaje;

    public CategoriaStatsDTO() {}

    public CategoriaStatsDTO(String categoria, long cantidad, double porcentaje) {
        this.categoria = categoria;
        this.cantidad = cantidad;
        this.porcentaje = porcentaje;
    }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public long getCantidad() { return cantidad; }
    public void setCantidad(long cantidad) { this.cantidad = cantidad; }
    public double getPorcentaje() { return porcentaje; }
    public void setPorcentaje(double porcentaje) { this.porcentaje = porcentaje; }
}

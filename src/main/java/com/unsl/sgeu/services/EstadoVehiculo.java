package com.unsl.sgeu.services;

import com.unsl.sgeu.models.RegistroEstacionamiento;

public class EstadoVehiculo {
    private String patente;
    private boolean tieneRegistros;
    private boolean estaEstacionado;
    private int cantidadRegistros;
    private RegistroEstacionamiento ultimoRegistro;
    private String fechaUltimoRegistro;
    
    public EstadoVehiculo() {}
    
    public EstadoVehiculo(String patente) {
        this.patente = patente;
        this.tieneRegistros = false;
        this.estaEstacionado = false;
        this.cantidadRegistros = 0;
    }
    
    // Getters y Setters
    public String getPatente() { return patente; }
    public void setPatente(String patente) { this.patente = patente; }
    
    public boolean isTieneRegistros() { return tieneRegistros; }
    public void setTieneRegistros(boolean tieneRegistros) { this.tieneRegistros = tieneRegistros; }
    
    public boolean isEstaEstacionado() { return estaEstacionado; }
    public void setEstaEstacionado(boolean estaEstacionado) { this.estaEstacionado = estaEstacionado; }
    
    public int getCantidadRegistros() { return cantidadRegistros; }
    public void setCantidadRegistros(int cantidadRegistros) { this.cantidadRegistros = cantidadRegistros; }
    
    public RegistroEstacionamiento getUltimoRegistro() { return ultimoRegistro; }
    public void setUltimoRegistro(RegistroEstacionamiento ultimoRegistro) { this.ultimoRegistro = ultimoRegistro; }
    
    public String getFechaUltimoRegistro() { return fechaUltimoRegistro; }
    public void setFechaUltimoRegistro(String fechaUltimoRegistro) { this.fechaUltimoRegistro = fechaUltimoRegistro; }
}

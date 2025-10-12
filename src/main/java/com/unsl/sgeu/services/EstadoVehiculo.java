package com.unsl.sgeu.services;

import com.unsl.sgeu.models.RegistroEstacionamiento;

public class EstadoVehiculo {
    private String patente;
    private boolean tieneRegistros;
    private boolean estaEstacionado;  
    private int cantidadRegistros;
    private RegistroEstacionamiento ultimoRegistro;
    private String fechaUltimoRegistro;
    
    
    private Long idEstacionamiento;
    private String nombreEstacionamiento;
    
    public EstadoVehiculo() {}
    
    public EstadoVehiculo(String patente) {
        this.patente = patente;
        this.tieneRegistros = false;
        this.estaEstacionado = false;
        this.cantidadRegistros = 0;
    }
    
   
    public EstadoVehiculo(boolean estaAdentro, Long idEstacionamiento, String nombreEstacionamiento) {
        this.estaEstacionado = estaAdentro;
        this.idEstacionamiento = idEstacionamiento;
        this.nombreEstacionamiento = nombreEstacionamiento;
        this.tieneRegistros = (idEstacionamiento != null);
    }
    
    // Getters y Setters existentes
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
    
     
    public Long getIdEstacionamiento() { return idEstacionamiento; }
    public void setIdEstacionamiento(Long idEstacionamiento) { this.idEstacionamiento = idEstacionamiento; }
    
    public String getNombreEstacionamiento() { return nombreEstacionamiento; }
    public void setNombreEstacionamiento(String nombreEstacionamiento) { this.nombreEstacionamiento = nombreEstacionamiento; }
    
 
    public boolean isEstaAdentro() { 
        return this.estaEstacionado; 
    }
    
    public void setEstaAdentro(boolean estaAdentro) { 
        this.estaEstacionado = estaAdentro; 
    }
}

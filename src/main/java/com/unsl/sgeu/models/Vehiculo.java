package com.unsl.sgeu.models;

import jakarta.persistence.*;

@Entity
@Table(name = "vehiculo")
public class Vehiculo {
    
    @Id
    private String patente;
    
    @Column(name = "codigo_qr", unique = true)
    private String codigoQr;
    
    @Column(nullable = false)
    private String color;
    
    @Column(nullable = false)
    private String modelo;
    
    @Column(nullable = false)
    private String tipo;
    
    @Column(name = "dni_duenio", nullable = false)
    private Long dniDuenio;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dni_duenio", insertable = false, updatable = false)
    private Persona duenio;
    
    // Constructores
    public Vehiculo() {}
    
    public Vehiculo(String patente, String codigoQr, String color, String modelo, String tipo, Long dniDuenio) {
        this.patente = patente;
        this.codigoQr = codigoQr;
        this.color = color;
        this.modelo = modelo;
        this.tipo = tipo;
        this.dniDuenio = dniDuenio;
    }
    
    // Getters y Setters
    public String getPatente() { return patente; }
    public void setPatente(String patente) { this.patente = patente; }
    
    public String getCodigoQr() { return codigoQr; }
    public void setCodigoQr(String codigoQr) { this.codigoQr = codigoQr; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public Long getDniDuenio() { return dniDuenio; }
    public void setDniDuenio(Long dniDuenio) { this.dniDuenio = dniDuenio; }
    
    public Persona getDuenio() { return duenio; }
    public void setDuenio(Persona duenio) { this.duenio = duenio; }
}

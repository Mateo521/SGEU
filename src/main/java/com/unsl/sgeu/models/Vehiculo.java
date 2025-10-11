package com.unsl.sgeu.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

@Entity
@Table(name = "vehiculo")
public class Vehiculo {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dni_duenio", referencedColumnName = "dni", insertable = false, updatable = false)
    private Persona propietario;

    // ✅ AGREGAR GETTER:
    public Persona getPropietario() {
        return propietario;
    }

    public void setPropietario(Persona propietario) {
        this.propietario = propietario;
    }

    @Id
    private String patente;

    @Column(name = "codigo_qr")
    private String codigoQr;

    @Column(nullable = false)
    private String modelo;

    @Column(nullable = false)
    private String color;

    @Column(name = "id_vehiculo_tipo")
    private Integer idVehiculoTipo;

    @Column(name = "dni_duenio")
    @JsonProperty("dniDuenio")
    private Long dniDuenio;

    @Column(name = "tipo")
    private String tipo;

    // Constructores
    public Vehiculo() {
    }

    // Getters y Setters
    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }

    public String getCodigoQr() {
        return codigoQr;
    }

    public void setCodigoQr(String codigoQr) {
        this.codigoQr = codigoQr;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    // ✅ DEBE ACEPTAR Integer
    public Integer getIdVehiculoTipo() {
        return idVehiculoTipo;
    }

    public void setIdVehiculoTipo(Integer idVehiculoTipo) {
        this.idVehiculoTipo = idVehiculoTipo;
    }

    public Long getDniDuenio() {
        return dniDuenio;
    }

    public void setDniDuenio(Long dniDuenio) {
        this.dniDuenio = dniDuenio;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}

package com.unsl.sgeu.models;

import jakarta.persistence.*;

@Entity
@Table(name = "vehiculo")
public class Vehiculo {

    @Id
    @Column(name = "patente", length = 15)
    private String patente;

    @Column(name = "codigo_qr", nullable = false, unique = true, length = 128)
    private String codigoQr;

    @Column(name = "modelo", length = 255)
    private String modelo;

    @Column(name = "color", length = 40)
    private String color;

    // FK -> vehiculo_tipo.id_vehiculo_tipo
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_vehiculo_tipo", nullable = false)
    private VehiculoTipo vehiculoTipo;

    // ---------- Constructores ----------
    public Vehiculo() {}

    public Vehiculo(String patente, String codigoQr, String modelo, String color, VehiculoTipo vehiculoTipo) {
        this.patente = patente;
        this.codigoQr = codigoQr;
        this.modelo = modelo;
        this.color = color;
        this.vehiculoTipo = vehiculoTipo;
    }

    // ---------- Getters / Setters ----------
    public String getPatente() { return patente; }
    public void setPatente(String patente) { this.patente = patente; }

    public String getCodigoQr() { return codigoQr; }
    public void setCodigoQr(String codigoQr) { this.codigoQr = codigoQr; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public VehiculoTipo getVehiculoTipo() { return vehiculoTipo; }
    public void setVehiculoTipo(VehiculoTipo vehiculoTipo) { this.vehiculoTipo = vehiculoTipo; }

    // ---------- Helpers para el controller ----------
    /** Devuelve el id de tipo de vehículo sin exponer la entidad. */
    @Transient
    public Short getIdVehiculoTipo() {
        return (vehiculoTipo != null) ? vehiculoTipo.getId() : null;
    }

    /** Setea el tipo por id creando un proxy ligero (evita fetch previo). */
    public void setIdVehiculoTipo(Short idVehiculoTipo) {
        if (idVehiculoTipo == null) {
            this.vehiculoTipo = null;
        } else {
            VehiculoTipo vt = new VehiculoTipo();
            vt.setId(idVehiculoTipo);
            this.vehiculoTipo = vt;
        }
    }
}

package com.unsl.sgeu.models;

import jakarta.persistence.*;

@Entity
@Table(name = "estacionamiento")
public class Estacionamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_est")  
    private Long idEst;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private Integer capacidad;

    @Column(nullable = false)
    private Boolean estado = true;

    // Getters y Setters
    public Long getIdEst() { return idEst; }
    public void setIdEst(Long idEst) { this.idEst = idEst; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }
}


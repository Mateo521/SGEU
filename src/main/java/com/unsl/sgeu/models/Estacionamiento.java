package com.unsl.sgeu.models;

import jakarta.persistence.*;

@Entity
@Table(name = "`Estacionamiento`")
public class Estacionamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)  private String nombre;
    @Column(nullable = false)  private String direccion;
    @Column(nullable = false)  private Integer capacidad;

    @Column(nullable = false)  private Boolean estado = true; // true=activo, false=desactivado

    public Estacionamiento() {}

    public Estacionamiento(String nombre, String direccion, Integer capacidad) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.capacidad = capacidad;
        this.estado = true;
    }

    // Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }
}

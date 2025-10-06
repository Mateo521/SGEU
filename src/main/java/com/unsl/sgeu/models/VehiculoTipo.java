package com.unsl.sgeu.models;

import jakarta.persistence.*;

@Entity
@Table(name = "vehiculo_tipo")
public class VehiculoTipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehiculo_tipo")
    private Short id;

    @Column(name = "nombre", nullable = false, unique = true, length = 20)
    private String nombre;

    public VehiculoTipo() {}
    public VehiculoTipo(String nombre) { this.nombre = nombre; }

    public Short getId() { return id; }
    public void setId(Short id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}

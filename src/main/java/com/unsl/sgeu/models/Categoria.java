package com.unsl.sgeu.models;

import jakarta.persistence.*;

@Entity
@Table(name = "categoria")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Short id;

    @Column(name = "nombre", nullable = false, unique = true, length = 30)
    private String nombre;

    // ---------- Constructores ----------
    public Categoria() {
    }

    public Categoria(String nombre) {
        this.nombre = nombre;
    }

    public Categoria(Short id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // ---------- Getters y Setters ----------
    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // ---------- toString ----------
    @Override
    public String toString() {
        return nombre;
    }
}

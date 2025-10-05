package com.unsl.sgeu.models;

import jakarta.persistence.*;

@Entity
@Table(name = "persona")
public class Persona {
    
    @Id
    private Long dni;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String categoria;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String telefono;
    
    // Constructores
    public Persona() {}
    
    public Persona(Long dni, String nombre, String categoria, String email, String telefono) {
        this.dni = dni;
        this.nombre = nombre;
        this.categoria = categoria;
        this.email = email;
        this.telefono = telefono;
    }
    
    // Getters y Setters
    public Long getDni() { return dni; }
    public void setDni(Long dni) { this.dni = dni; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}


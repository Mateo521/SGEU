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
    private String telefono;
    
    @Column(nullable = false)
    private String email;
    
    @Column(name = "id_categoria")
    private Integer idCategoria;  
    
    @Column(name = "categoria")
    private String categoria;
    
    // Constructores
    public Persona() {}
    
    // Getters y Setters
    public Long getDni() { return dni; }
    public void setDni(Long dni) { this.dni = dni; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    // ✅ DEBE ACEPTAR Integer
    public Integer getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Integer idCategoria) { this.idCategoria = idCategoria; }
    
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
}

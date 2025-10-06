package com.unsl.sgeu.models;

import jakarta.persistence.*;

@Entity
@Table(name = "persona")
public class Persona {

    @Id
    @Column(name = "dni")
    private Long dni;

    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    // En SQL podía ser NULL; si querés que sea obligatorio, dejá nullable=false.
    @Column(name = "telefono", length = 30)
    private String telefono;

    // En SQL es UNIQUE (puede ser NULL). Si querés exigirlo, poné nullable=false.
    @Column(name = "email", length = 255, unique = true)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    // ---------- Constructores ----------
    public Persona() {}

    public Persona(Long dni, String nombre, Categoria categoria, String email, String telefono) {
        this.dni = dni;
        this.nombre = nombre;
        this.categoria = categoria;
        this.email = email;
        this.telefono = telefono;
    }

    // ---------- Getters / Setters ----------
    public Long getDni() { return dni; }
    public void setDni(Long dni) { this.dni = dni; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    // ---------- Helpers para el controller ----------
    /** Devuelve el id de categoría sin exponer la entidad */
    @Transient
    public Short getIdCategoria() {
        return (categoria != null) ? categoria.getId() : null;
    }

    /** Setea la categoría por id (crea un proxy ligero para evitar un fetch previo) */
    public void setIdCategoria(Short idCategoria) {
        if (idCategoria == null) {
            this.categoria = null;
        } else {
            Categoria c = new Categoria();
            c.setId(idCategoria);
            this.categoria = c;
        }
    }

    @Override
    public String toString() {
        return "Persona{dni=" + dni +
               ", nombre='" + nombre + '\'' +
               ", telefono='" + (telefono != null ? telefono : "—") + '\'' +
               ", email='" + (email != null ? email : "—") + '\'' +
               ", idCategoria=" + getIdCategoria() +
               '}';
    }
}

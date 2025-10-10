package com.unsl.sgeu.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registro_estacionamiento")
public class RegistroEstacionamiento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "patente", nullable = false)
    private String patente;
    
    @Column(name = "tipo", nullable = false) // "ingreso" o "egreso"
    private String tipo;
    
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;
    
    @Column(name = "id_estacionamiento")
    private Long idEstacionamiento;
    
    // Constructores
    public RegistroEstacionamiento() {}
    
    public RegistroEstacionamiento(String patente, String tipo, LocalDateTime fechaHora) {
        this.patente = patente;
        this.tipo = tipo;
        this.fechaHora = fechaHora;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getPatente() { return patente; }
    public void setPatente(String patente) { this.patente = patente; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    
    public Long getIdEstacionamiento() { return idEstacionamiento; }
    public void setIdEstacionamiento(Long idEstacionamiento) { this.idEstacionamiento = idEstacionamiento; }
    
    @Override
    public String toString() {
        return "RegistroEstacionamiento{" +
                "id=" + id +
                ", patente='" + patente + '\'' +
                ", tipo='" + tipo + '\'' +
                ", fechaHora=" + fechaHora +
                ", idEstacionamiento=" + idEstacionamiento +
                '}';
    }
}

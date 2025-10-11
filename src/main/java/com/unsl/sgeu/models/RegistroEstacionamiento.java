package com.unsl.sgeu.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "registro_estacionamiento")
public class RegistroEstacionamiento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registro")   
    private Long id;
    
    @Column(name = "patente", nullable = false)
    private String patente;
    
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;
    
    @Column(name = "tipo_movimiento", nullable = false)   
    private String tipo;   
    
    @Column(name = "modo")
    private String modo;
    
    @Column(name = "id_est")
    private Long idEstacionamiento;
    
 
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getPatente() { return patente; }
    public void setPatente(String patente) { this.patente = patente; }
    
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public String getModo() { return modo; }
    public void setModo(String modo) { this.modo = modo; }
    
    public Long getIdEstacionamiento() { return idEstacionamiento; }
    public void setIdEstacionamiento(Long idEstacionamiento) { this.idEstacionamiento = idEstacionamiento; }
}

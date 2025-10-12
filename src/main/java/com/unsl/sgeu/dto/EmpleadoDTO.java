// src/main/java/com/unsl/sgeu/dto/EmpleadoDTO.java
package com.unsl.sgeu.dto;

public class EmpleadoDTO {
    private Long id;
    private String nombreCompleto;
    private String nombreUsuario;
    private String correo;

    public EmpleadoDTO() {}

    public EmpleadoDTO(Long id, String nombreCompleto, String nombreUsuario, String correo) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
}

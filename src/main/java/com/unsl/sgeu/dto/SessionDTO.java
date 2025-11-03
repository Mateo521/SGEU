package com.unsl.sgeu.dto;

/**
 * DTO para encapsular los datos de sesión del usuario
 */
public class SessionDTO {
    private Long usuarioId;
    private String nombreUsuario;
    private String rol;
    private String nombreCompleto;
    private Long estacionamientoId;
    private String estacionamientoNombre;
    private Boolean loginExitoso;
    private String motivoFallo;
    
    public SessionDTO() {
        this.loginExitoso = false;
        this.motivoFallo = null;
    }
    
    // Constructor para login fallido
    public static SessionDTO loginFallido() {
        return new SessionDTO();
    }
    public static SessionDTO loginFallido(String motivo) {
        SessionDTO dto = new SessionDTO();
        dto.motivoFallo = motivo;
        return dto;
    }
    public String getMotivoFallo() { return motivoFallo; }
    public void setMotivoFallo(String motivoFallo) { this.motivoFallo = motivoFallo; }
    
    // Constructor para login exitoso
    public static SessionDTO loginExitoso(Long usuarioId, String nombreUsuario, String rol, 
                                        String nombreCompleto, Long estacionamientoId, 
                                        String estacionamientoNombre) {
        SessionDTO dto = new SessionDTO();
        dto.usuarioId = usuarioId;
        dto.nombreUsuario = nombreUsuario;
        dto.rol = rol;
        dto.nombreCompleto = nombreCompleto;
        dto.estacionamientoId = estacionamientoId;
        dto.estacionamientoNombre = estacionamientoNombre;
        dto.loginExitoso = true;
        return dto;
    }
    
    // Getters
    public Long getUsuarioId() { return usuarioId; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getRol() { return rol; }
    public String getNombreCompleto() { return nombreCompleto; }
    public Long getEstacionamientoId() { return estacionamientoId; }
    public String getEstacionamientoNombre() { return estacionamientoNombre; }
    public Boolean getLoginExitoso() { return loginExitoso; }
    
    @Override
    public String toString() {
        if (!loginExitoso) return "Login fallido" + (motivoFallo != null ? ": " + motivoFallo : "");
        return String.format("SessionDTO[usuarioId=%d, nombreUsuario='%s', rol='%s', " +
                           "nombreCompleto='%s', estacionamientoId=%d, estacionamientoNombre='%s']",
                           usuarioId, nombreUsuario, rol, nombreCompleto, 
                           estacionamientoId, estacionamientoNombre);
    }
}
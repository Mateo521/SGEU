package com.unsl.sgeu.dto;
import com.unsl.sgeu.models.Persona;
import com.unsl.sgeu.models.Vehiculo;

public class VehiculoListadoDTO {
    private String patente;
    private String modelo;
    private String color;
    private String tipo;
    private String codigoQr;
    

    private Long dniDuenio;
    private String nombreDuenio;
    private String telefonoDuenio;
    private String emailDuenio;
    private String categoriaDuenio;


    public VehiculoListadoDTO() {}


    public VehiculoListadoDTO(Vehiculo vehiculo, Persona persona) {
        this.patente = vehiculo.getPatente();
        this.modelo = vehiculo.getModelo();
        this.color = vehiculo.getColor();
        this.tipo = vehiculo.getTipo();
        this.codigoQr = vehiculo.getCodigoQr();
        
        if (persona != null) {
            this.dniDuenio = persona.getDni();
            this.nombreDuenio = persona.getNombre();
            this.telefonoDuenio = persona.getTelefono();
            this.emailDuenio = persona.getEmail();
            this.categoriaDuenio = persona.getCategoria();
        }
    }


    public String getPatente() { return patente; }
    public void setPatente(String patente) { this.patente = patente; }
    
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public String getCodigoQr() { return codigoQr; }
    public void setCodigoQr(String codigoQr) { this.codigoQr = codigoQr; }
    
    public Long getDniDuenio() { return dniDuenio; }
    public void setDniDuenio(Long dniDuenio) { this.dniDuenio = dniDuenio; }
    
    public String getNombreDuenio() { return nombreDuenio; }
    public void setNombreDuenio(String nombreDuenio) { this.nombreDuenio = nombreDuenio; }
    
    public String getTelefonoDuenio() { return telefonoDuenio; }
    public void setTelefonoDuenio(String telefonoDuenio) { this.telefonoDuenio = telefonoDuenio; }
    
    public String getEmailDuenio() { return emailDuenio; }
    public void setEmailDuenio(String emailDuenio) { this.emailDuenio = emailDuenio; }
    
    public String getCategoriaDuenio() { return categoriaDuenio; }
    public void setCategoriaDuenio(String categoriaDuenio) { this.categoriaDuenio = categoriaDuenio; }
}

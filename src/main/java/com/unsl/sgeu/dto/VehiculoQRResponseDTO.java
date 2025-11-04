package com.unsl.sgeu.dto;

import jakarta.validation.constraints.*;

public class VehiculoQRResponseDTO {
    @NotBlank(message = "La patente es obligatoria")
    private String patente;
    private String modelo;
    private String color;
    private String tipo;
    private Long dniDuenio;
    private String nombreDuenio;
    private String categoriaDuenio;
    private String telefonoDuenio;
    private String emailDuenio;
    private boolean estaAdentro;
    private String accionDisponible;
    private String mensajeAccion;

    // Constructor
    public VehiculoQRResponseDTO() {
    }

    public VehiculoQRResponseDTO(String patente, String modelo, String color, String tipo, Long dniDuenio,
            String nombreDuenio, String categoriaDuenio, String telefonoDuenio,
            String emailDuenio, boolean estaAdentro, String accionDisponible, String mensajeAccion) {
        this.patente = patente;
        this.modelo = modelo;
        this.color = color;
        this.tipo = tipo;
        this.dniDuenio = dniDuenio;
        this.nombreDuenio = nombreDuenio;
        this.categoriaDuenio = categoriaDuenio;
        this.telefonoDuenio = telefonoDuenio;
        this.emailDuenio = emailDuenio;
        this.estaAdentro = estaAdentro;
        this.accionDisponible = accionDisponible;
        this.mensajeAccion = mensajeAccion;
    }

    // Getters y Setters
    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Long getDniDuenio() {
        return dniDuenio;
    }

    public void setDniDuenio(Long dniDuenio) {
        this.dniDuenio = dniDuenio;
    }

    public String getNombreDuenio() {
        return nombreDuenio;
    }

    public void setNombreDuenio(String nombreDuenio) {
        this.nombreDuenio = nombreDuenio;
    }

    public String getCategoriaDuenio() {
        return categoriaDuenio;
    }

    public void setCategoriaDuenio(String categoriaDuenio) {
        this.categoriaDuenio = categoriaDuenio;
    }

    public String getTelefonoDuenio() {
        return telefonoDuenio;
    }

    public void setTelefonoDuenio(String telefonoDuenio) {
        this.telefonoDuenio = telefonoDuenio;
    }

    public String getEmailDuenio() {
        return emailDuenio;
    }

    public void setEmailDuenio(String emailDuenio) {
        this.emailDuenio = emailDuenio;
    }

    public boolean isEstaAdentro() {
        return estaAdentro;
    }

    public void setEstaAdentro(boolean estaAdentro) {
        this.estaAdentro = estaAdentro;
    }

    public String getAccionDisponible() {
        return accionDisponible;
    }

    public void setAccionDisponible(String accionDisponible) {
        this.accionDisponible = accionDisponible;
    }

    public String getMensajeAccion() {
        return mensajeAccion;
    }

    public void setMensajeAccion(String mensajeAccion) {
        this.mensajeAccion = mensajeAccion;
    }
}

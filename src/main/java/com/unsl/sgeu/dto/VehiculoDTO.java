package com.unsl.sgeu.dto;

import jakarta.validation.constraints.*;

public class VehiculoDTO {

    @NotBlank(message = "La patente es obligatoria")
    @Pattern(regexp = "^[A-Z]{2,3}[0-9]{3}[A-Z]{0,2}$", 
             message = "Formato de patente inválido. Ejemplos válidos: ABC123, AB123CD")
    @Size(min = 6, max = 7, message = "La patente debe tener 6 o 7 caracteres")
    private String patente;

    @NotBlank(message = "El modelo es obligatorio")
    @Size(min = 2, max = 50, message = "El modelo debe tener entre 2 y 50 caracteres")
    private String modelo;

    @NotBlank(message = "El color es obligatorio")
    @Size(min = 3, max = 30, message = "El color debe tener entre 3 y 30 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El color solo puede contener letras y espacios")
    private String color;

    @NotBlank(message = "El tipo de vehículo es obligatorio")
    @Pattern(regexp = "^(auto|moto)$", 
             message = "El tipo debe ser: auto o moto")
    private String tipoNombre;

    private String codigoQr;

    // Getters y Setters
    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente != null ? patente.trim().toUpperCase() : null;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo != null ? modelo.trim() : null;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color != null ? color.trim() : null;   
    }

    public String getTipoNombre() {
        return tipoNombre;
    }

    public void setTipoNombre(String tipoNombre) {
        this.tipoNombre = tipoNombre;
    }

    public String getCodigoQr() {
        return codigoQr;
    }

    public void setCodigoQr(String codigoQr) {
        this.codigoQr = codigoQr;
    }
}

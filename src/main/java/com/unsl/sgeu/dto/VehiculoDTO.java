package com.unsl.sgeu.dto;

import jakarta.validation.constraints.*;

public class VehiculoDTO {

    @NotBlank(message = "La patente es obligatoria")
    @Pattern(regexp = "^[A-Z]{2,3}[0-9]{3}[A-Z]{0,2}$|^[A-Z]{3}[0-9]{3}$",
             message = "Formato de patente inválido. Ejemplo: ABC123 o AB123CD")
    private String patente;

    @NotBlank(message = "El modelo es obligatorio")
    @Size(min = 2, max = 100)
    private String modelo;

    @NotBlank(message = "El color es obligatorio")
    @Size(min = 2, max = 50)
    private String color;

    @NotBlank(message = "El tipo de vehículo es obligatorio")
    @Pattern(regexp = "^(auto|moto)$", message = "Tipo inválido (auto o moto)")
    private String tipoNombre;

    private Short vehiculoTipoId;

    // Getters & Setters
    public String getPatente() { return patente; }
    public void setPatente(String patente) { this.patente = patente.trim().toUpperCase(); }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo.trim(); }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color.trim(); }

    public String getTipoNombre() { return tipoNombre; }
    public void setTipoNombre(String tipoNombre) { this.tipoNombre = tipoNombre.trim().toLowerCase(); }

    public Short getVehiculoTipoId() { return vehiculoTipoId; }
    public void setVehiculoTipoId(Short vehiculoTipoId) { this.vehiculoTipoId = vehiculoTipoId; }
}

// com.unsl.sgeu.dto.RegistroVehiculoFormDTO
package com.unsl.sgeu.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class RegistroVehiculoFormDTO {

    @Valid
    @NotNull(message = "Los datos del vehículo son obligatorios")
    private VehiculoDTO vehiculo;

    @Valid
    @NotNull(message = "Los datos de la persona son obligatorios")
    private PersonaDTO persona;

    // Getters y Setters
    public VehiculoDTO getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(VehiculoDTO vehiculo) {
        this.vehiculo = vehiculo;
    }

    public PersonaDTO getPersona() {
        return persona;
    }

    public void setPersona(PersonaDTO persona) {
        this.persona = persona;
    }
}

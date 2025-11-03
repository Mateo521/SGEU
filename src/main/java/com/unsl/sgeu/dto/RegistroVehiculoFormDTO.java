// com.unsl.sgeu.dto.RegistroVehiculoFormDTO
package com.unsl.sgeu.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class RegistroVehiculoFormDTO {

    @NotNull(message = "Los datos de la persona son obligatorios")
    @Valid
    private PersonaDTO persona;

    @NotNull(message = "Los datos del vehículo son obligatorios")
    @Valid
    private VehiculoDTO vehiculo;

    public PersonaDTO getPersona() { return persona; }
    public void setPersona(PersonaDTO persona) { this.persona = persona; }

    public VehiculoDTO getVehiculo() { return vehiculo; }
    public void setVehiculo(VehiculoDTO vehiculo) { this.vehiculo = vehiculo; }
}

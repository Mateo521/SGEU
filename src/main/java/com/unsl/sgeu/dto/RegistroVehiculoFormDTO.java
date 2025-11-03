package com.unsl.sgeu.dto;

import jakarta.validation.Valid;

public class RegistroVehiculoFormDTO {

    @Valid
    private PersonaDTO persona;

    @Valid
    private VehiculoDTO vehiculo;

    public PersonaDTO getPersona() { return persona; }
    public void setPersona(PersonaDTO persona) { this.persona = persona; }

    public VehiculoDTO getVehiculo() { return vehiculo; }
    public void setVehiculo(VehiculoDTO vehiculo) { this.vehiculo = vehiculo; }
}

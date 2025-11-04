package com.unsl.sgeu.dto;

import jakarta.validation.constraints.*;

public class PersonaDTO {

    @NotNull(message = "El DNI es obligatorio")
    @Min(value = 1000000, message = "El DNI debe tener al menos 7 dígitos")
    @Max(value = 99999999, message = "El DNI no puede tener más de 8 dígitos")
    private Long dni;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El nombre solo puede contener letras y espacios")
    private String nombre;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9\\-\\s()]+$", message = "El teléfono solo puede contener números, guiones, espacios y paréntesis")
    @Size(min = 7, max = 20, message = "El teléfono debe tener entre 7 y 20 caracteres")
    private String telefono;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    @Size(max = 100, message = "El email no puede superar los 100 caracteres")
    private String email;

    @NotBlank(message = "La categoría es obligatoria")
    @Pattern(regexp = "^(docente|no_docente|estudiante|visitante)$", 
             message = "La categoría debe ser: docente, no_docente, estudiante o visitante")
    private String categoriaNombre;

    // Getters y Setters
    public Long getDni() {
        return dni;
    }

    public void setDni(Long dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    public void setCategoriaNombre(String categoriaNombre) {
        this.categoriaNombre = categoriaNombre;
    }
}

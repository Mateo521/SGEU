package com.unsl.sgeu.dto;

import jakarta.validation.constraints.*;

public class PersonaDTO {

    @NotNull(message = "El DNI es obligatorio")
    @Min(value = 1000000, message = "El DNI debe tener al menos 7 dígitos")
    @Max(value = 99999999, message = "El DNI no puede tener más de 8 dígitos")
    private Long dni;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(min = 7, max = 20, message = "El teléfono debe tener entre 7 y 20 caracteres")
    private String telefono;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de correo inválido")
    private String email;

    @NotBlank(message = "La categoría es obligatoria")
    @Pattern(regexp = "^(docente|no_docente|estudiante|visitante)$",
             message = "Categoría inválida. Debe ser: docente, no_docente, estudiante o visitante")
    private String categoriaNombre;

    private Short idCategoria;

    // Getters & Setters
    public Long getDni() { return dni; }
    public void setDni(Long dni) { this.dni = dni; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre.trim(); }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono.trim(); }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email.trim().toLowerCase(); }

    public String getCategoriaNombre() { return categoriaNombre; }
    public void setCategoriaNombre(String categoriaNombre) {
        this.categoriaNombre = categoriaNombre.trim().toLowerCase();
    }

    public Short getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Short idCategoria) { this.idCategoria = idCategoria; }
}

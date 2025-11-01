package com.unsl.sgeu.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public class VehiculoFormDTO {
    
   
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", 
             message = "El nombre solo puede contener letras y espacios")
    private String nombre;
    
    @NotBlank(message = "La categoría es obligatoria")
    @Pattern(regexp = "^(docente|no_docente|estudiante|visitante)$", 
             message = "Categoría inválida. Debe ser: docente, no_docente, estudiante o visitante")
    private String categoriaNombre;
    
    private Short idCategoria;
    
    @NotNull(message = "El DNI es obligatorio")
    @Min(value = 1000000, message = "El DNI debe tener al menos 7 dígitos")
    @Max(value = 99999999, message = "El DNI no puede tener más de 8 dígitos")
    @Positive(message = "El DNI debe ser un número positivo")
    private Long dni;
    
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9\\-\\s()]+$", 
             message = "El teléfono solo puede contener números, guiones, espacios y paréntesis")
    @Size(min = 7, max = 20, message = "El teléfono debe tener entre 7 y 20 caracteres")
    private String telefono;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido (ejemplo@dominio.com)")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email;
    
    private List<Long> dnisAdicionales;
    
  
    
    @NotBlank(message = "La patente es obligatoria")
    @Pattern(regexp = "^[A-Z]{2,3}[0-9]{3}[A-Z]{0,2}$|^[A-Z]{3}[0-9]{3}$", 
             message = "Formato de patente inválido. Ejemplos válidos: ABC123, AB123CD")
    @Size(min = 6, max = 7, message = "La patente debe tener entre 6 y 7 caracteres")
    private String patente;
    
    @NotBlank(message = "El modelo es obligatorio")
    @Size(min = 2, max = 100, message = "El modelo debe tener entre 2 y 100 caracteres")
    private String modelo;
    
    @NotBlank(message = "El color es obligatorio")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", 
             message = "El color solo puede contener letras y espacios")
    @Size(min = 2, max = 50, message = "El color debe tener entre 2 y 50 caracteres")
    private String color;
    
    @NotBlank(message = "El tipo de vehículo es obligatorio")
    @Pattern(regexp = "^(auto|moto)$", 
             message = "Tipo de vehículo inválido. Debe ser: auto o moto")
    private String tipoNombre;
    
    private Short vehiculoTipoId;
    
    
    
    public VehiculoFormDTO() {}
    
    public VehiculoFormDTO(String nombre, String categoriaNombre, Long dni, String telefono, String email,
                          String patente, String modelo, String color, String tipoNombre) {
        this.nombre = nombre;
        this.categoriaNombre = categoriaNombre;
        this.dni = dni;
        this.telefono = telefono;
        this.email = email;
        this.patente = patente;
        this.modelo = modelo;
        this.color = color;
        this.tipoNombre = tipoNombre;
        
        // Mapear nombres a IDs
        this.idCategoria = mapearCategoriaAId(categoriaNombre);
        this.vehiculoTipoId = mapearTipoAId(tipoNombre);
    }
    
 
    
    private Short mapearCategoriaAId(String categoria) {
        if (categoria == null) return null;
        switch (categoria.toLowerCase()) {
            case "docente": return 1;
            case "no_docente": return 2;
            case "estudiante": return 3;
            case "visitante": return 4;
            default: return null;
        }
    }
    
    private Short mapearTipoAId(String tipo) {
        if (tipo == null) return null;
        switch (tipo.toLowerCase()) {
            case "auto": return 1;
            case "moto": return 2;
            default: return null;
        }
    }
    
 
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { 
        this.nombre = nombre != null ? nombre.trim() : null; 
    }
    
    public String getCategoriaNombre() { return categoriaNombre; }
    public void setCategoriaNombre(String categoriaNombre) { 
        this.categoriaNombre = categoriaNombre != null ? categoriaNombre.toLowerCase().trim() : null;
        this.idCategoria = mapearCategoriaAId(this.categoriaNombre);
    }
    
    public Short getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Short idCategoria) { this.idCategoria = idCategoria; }
    
    public Long getDni() { return dni; }
    public void setDni(Long dni) { this.dni = dni; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { 
        this.telefono = telefono != null ? telefono.trim() : null; 
    }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { 
        this.email = email != null ? email.trim().toLowerCase() : null; 
    }
    
    public List<Long> getDnisAdicionales() { return dnisAdicionales; }
    public void setDnisAdicionales(List<Long> dnisAdicionales) { 
        this.dnisAdicionales = dnisAdicionales; 
    }
    
    public String getPatente() { return patente; }
    public void setPatente(String patente) { 
        this.patente = patente != null ? patente.toUpperCase().trim() : null; 
    }
    
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { 
        this.modelo = modelo != null ? modelo.trim() : null; 
    }
    
    public String getColor() { return color; }
    public void setColor(String color) { 
        this.color = color != null ? color.trim() : null; 
    }
    
    public String getTipoNombre() { return tipoNombre; }
    public void setTipoNombre(String tipoNombre) { 
        this.tipoNombre = tipoNombre != null ? tipoNombre.toLowerCase().trim() : null;
        this.vehiculoTipoId = mapearTipoAId(this.tipoNombre);
    }
    
    public Short getVehiculoTipoId() { return vehiculoTipoId; }
    public void setVehiculoTipoId(Short vehiculoTipoId) { 
        this.vehiculoTipoId = vehiculoTipoId; 
    }
    
    @Override
    public String toString() {
        return "VehiculoFormDTO{" +
                "nombre='" + nombre + '\'' +
                ", categoriaNombre='" + categoriaNombre + '\'' +
                ", dni=" + dni +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                ", patente='" + patente + '\'' +
                ", modelo='" + modelo + '\'' +
                ", color='" + color + '\'' +
                ", tipoNombre='" + tipoNombre + '\'' +
                '}';
    }
}

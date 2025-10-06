package com.unsl.sgeu.dto;

import java.util.List;

public class VehiculoFormDTO {
    // Datos de la persona
    private String nombre;
    private String categoriaNombre;  // Para el select del HTML
    private Short idCategoria;       // Para el controlador
    private Long dni;
    private String telefono;
    private String email;
    private List<Long> dnisAdicionales; // Para múltiples propietarios
    
    // Datos del vehículo
    private String patente;
    private String modelo;
    private String color;
    private String tipoNombre;       // Para el select del HTML
    private Short vehiculoTipoId;    // Para el controlador
    
    // Constructores
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
    
    // Métodos de mapeo
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
    
    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getCategoriaNombre() { return categoriaNombre; }
    public void setCategoriaNombre(String categoriaNombre) { 
        this.categoriaNombre = categoriaNombre;
        this.idCategoria = mapearCategoriaAId(categoriaNombre);
    }
    
    public Short getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Short idCategoria) { this.idCategoria = idCategoria; }
    
    public Long getDni() { return dni; }
    public void setDni(Long dni) { this.dni = dni; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public List<Long> getDnisAdicionales() { return dnisAdicionales; }
    public void setDnisAdicionales(List<Long> dnisAdicionales) { this.dnisAdicionales = dnisAdicionales; }
    
    public String getPatente() { return patente; }
    public void setPatente(String patente) { this.patente = patente; }
    
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public String getTipoNombre() { return tipoNombre; }
    public void setTipoNombre(String tipoNombre) { 
        this.tipoNombre = tipoNombre;
        this.vehiculoTipoId = mapearTipoAId(tipoNombre);
    }
    
    public Short getVehiculoTipoId() { return vehiculoTipoId; }
    public void setVehiculoTipoId(Short vehiculoTipoId) { this.vehiculoTipoId = vehiculoTipoId; }
}

package com.unsl.sgeu.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO para alta/edición de Vehículo y propietario principal,
 * alineado al esquema normalizado:
 *  - persona(dni, nombre, telefono, email, id_categoria)
 *  - vehiculo(patente, codigo_qr[lo genera el service], modelo, color, id_vehiculo_tipo)
 *  - persona_vehiculo(dni, patente) para N:M
 *
 * NOTA: se envían IDs (idCategoria, vehiculoTipoId), no nombres texto.
 */
public class VehiculoFormDTO {

    // -------- Persona (propietario principal) --------
    private Long dni;
    private String nombre;
    private String telefono;
    private String email;

    /** FK a categoria.id_categoria */
    private Short idCategoria;

    /** Dueños adicionales para relación N:M (opcional) */
    private List<Long> dnisAdicionales = new ArrayList<>();

    // -------- Vehículo --------
    private String patente;
    private String modelo;
    private String color;

    /** FK a vehiculo_tipo.id_vehiculo_tipo */
    private Short vehiculoTipoId;

    // -------- Constructores --------
    public VehiculoFormDTO() {}

    // -------- Getters/Setters --------
    public Long getDni() { return dni; }
    public void setDni(Long dni) { this.dni = dni; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Short getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Short idCategoria) { this.idCategoria = idCategoria; }

    public List<Long> getDnisAdicionales() { return dnisAdicionales; }
    public void setDnisAdicionales(List<Long> dnisAdicionales) { this.dnisAdicionales = dnisAdicionales; }

    public String getPatente() { return patente; }
    public void setPatente(String patente) { this.patente = patente; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Short getVehiculoTipoId() { return vehiculoTipoId; }
    public void setVehiculoTipoId(Short vehiculoTipoId) { this.vehiculoTipoId = vehiculoTipoId; }
}

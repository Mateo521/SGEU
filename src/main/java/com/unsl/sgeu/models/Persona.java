package com.unsl.sgeu.models;



import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Persona {

  @Id
  private Long dni;

  private String nombre;
  private String telefono;
  private String email;

  // docente, no_docente, alumno, visitante (por ahora como String simple)
  private String categoria;

  @OneToMany(mappedBy = "duenio", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Vehiculo> vehiculos = new ArrayList<>();

  // getters/setters
  public Long getDni() { return dni; }
  public void setDni(Long dni) { this.dni = dni; }

  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }

  public String getTelefono() { return telefono; }
  public void setTelefono(String telefono) { this.telefono = telefono; }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }

  public String getCategoria() { return categoria; }
  public void setCategoria(String categoria) { this.categoria = categoria; }

  public List<Vehiculo> getVehiculos() { return vehiculos; }
  public void setVehiculos(List<Vehiculo> vehiculos) { this.vehiculos = vehiculos; }
}


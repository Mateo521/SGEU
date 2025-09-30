package com.unsl.sgeu.models;

import jakarta.persistence.*;

@Entity
public class Empleado {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nombre;
  private String apellido;
  private String correo;

  @Column(name = "nombre_usuario", unique = true)
  private String nombreUsuario;

  private String contrasenia;

  private String cargo; // puede ser "administrador" o "guardia"

  // Getters y setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }

  public String getApellido() { return apellido; }
  public void setApellido(String apellido) { this.apellido = apellido; }

  public String getCorreo() { return correo; }
  public void setCorreo(String correo) { this.correo = correo; }

  public String getNombreUsuario() { return nombreUsuario; }
  public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

  public String getContrasenia() { return contrasenia; }
  public void setContrasenia(String contrasenia) { this.contrasenia = contrasenia; }

  public String getCargo() { return cargo; }
  public void setCargo(String cargo) { this.cargo = cargo; }
}

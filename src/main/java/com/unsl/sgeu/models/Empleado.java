package com.unsl.sgeu.models;

import jakarta.persistence.*;

@Entity
@Table(name = "empleado", uniqueConstraints = {
    @UniqueConstraint(name = "uq_empleado_usuario", columnNames = "nombre_usuario"),
    @UniqueConstraint(name = "uq_empleado_correo", columnNames = "correo")
})
public class Empleado {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 255)
  private String nombre;

  @Column(nullable = false, length = 255)
  private String apellido;

  @Column(length = 255)
  private String correo;

  @Column(name = "nombre_usuario", nullable = false, length = 255)
  private String nombreUsuario;

  @Column(nullable = false, length = 255)
  private String contrasenia;

  @Enumerated(EnumType.STRING)
  @Column(name = "rol", nullable = false, length = 20) // valores en enum: Guardia | Administrador
  private Rol rol = Rol.Guardia;

  // Getters / Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getApellido() {
    return apellido;
  }

  public void setApellido(String apellido) {
    this.apellido = apellido;
  }

  public String getCorreo() {
    return correo;
  }

  public void setCorreo(String correo) {
    this.correo = correo;
  }

  public String getNombreUsuario() {
    return nombreUsuario;
  }

  public void setNombreUsuario(String nombreUsuario) {
    this.nombreUsuario = nombreUsuario;
  }

  public String getContrasenia() {
    return contrasenia;
  }

  public void setContrasenia(String contrasenia) {
    this.contrasenia = contrasenia;
  }

  public Rol getRol() {
    return rol;
  }

  public void setRol(Rol rol) {
    this.rol = rol;
  }
}

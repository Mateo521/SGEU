package com.unsl.sgeu.models;

import jakarta.persistence.*;

@Entity
public class Vehiculo {

  @Id
  private String patente; // PK es la patente

  @Column(name = "codigo_qr")
  private String codigoQr;

  private String modelo;
  private String color;
  private String tipo; // auto, moto, etc. (por ahora String)

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "dni_duenio")
  private Persona duenio;

  // getters/setters
  public String getPatente() { return patente; }
  public void setPatente(String patente) { this.patente = patente; }

  public String getCodigoQr() { return codigoQr; }
  public void setCodigoQr(String codigoQr) { this.codigoQr = codigoQr; }

  public String getModelo() { return modelo; }
  public void setModelo(String modelo) { this.modelo = modelo; }

  public String getColor() { return color; }
  public void setColor(String color) { this.color = color; }

  public String getTipo() { return tipo; }
  public void setTipo(String tipo) { this.tipo = tipo; }

  public Persona getDuenio() { return duenio; }
  public void setDuenio(Persona duenio) { this.duenio = duenio; }
}

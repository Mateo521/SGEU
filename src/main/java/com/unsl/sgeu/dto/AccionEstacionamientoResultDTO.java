package com.unsl.sgeu.dto;

public class AccionEstacionamientoResultDTO {
    private boolean resultado;
    private String mensaje;
    private String patente;
    private String accion;

    // Constructor
    public AccionEstacionamientoResultDTO(boolean resultado, String mensaje, String patente, String accion) {
        this.resultado = resultado;
        this.mensaje = mensaje;
        this.patente = patente;
        this.accion = accion;
    }

    // Getters y Setters
    public boolean isResultado() {
        return resultado;
    }

    public void setResultado(boolean resultado) {
        this.resultado = resultado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }
}

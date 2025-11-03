package com.unsl.sgeu.dto;

public class VehiculoRegistroResultadoDTO {
    private String patente;
    private String codigoQr;
    private String rutaImagenQR;
    private String vehiculoInfo;

    public VehiculoRegistroResultadoDTO() {}

    public VehiculoRegistroResultadoDTO(String patente, String codigoQr, String rutaImagenQR, String vehiculoInfo) {
        this.patente = patente;
        this.codigoQr = codigoQr;
        this.rutaImagenQR = rutaImagenQR;
        this.vehiculoInfo = vehiculoInfo;
    }

    public String getPatente() { return patente; }
    public void setPatente(String patente) { this.patente = patente; }

    public String getCodigoQr() { return codigoQr; }
    public void setCodigoQr(String codigoQr) { this.codigoQr = codigoQr; }

    public String getRutaImagenQR() { return rutaImagenQR; }
    public void setRutaImagenQR(String rutaImagenQR) { this.rutaImagenQR = rutaImagenQR; }

    public String getVehiculoInfo() { return vehiculoInfo; }
    public void setVehiculoInfo(String vehiculoInfo) { this.vehiculoInfo = vehiculoInfo; }
}

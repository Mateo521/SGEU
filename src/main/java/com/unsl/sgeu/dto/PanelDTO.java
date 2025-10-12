package com.unsl.sgeu.dto;



import com.unsl.sgeu.models.RegistroEstacionamiento;
import java.util.List;

public class PanelDTO {
    private Long idEstacionamiento;
    private String nombreEstacionamiento;
    private List<RegistroEstacionamiento> vehiculosActualmente;
    private List<RegistroEstacionamiento> ingresosDelDia;
    private List<RegistroEstacionamiento> egresosDelDia;
    private int totalVehiculosAdentro;
    private int totalIngresosHoy;
    private int totalEgresosHoy;
    
    // Constructor
    public PanelDTO() {}
    
    public PanelDTO(Long idEstacionamiento, String nombreEstacionamiento) {
        this.idEstacionamiento = idEstacionamiento;
        this.nombreEstacionamiento = nombreEstacionamiento;
    }
    
    // Getters ysetters
    public Long getIdEstacionamiento() { return idEstacionamiento; }
    public void setIdEstacionamiento(Long idEstacionamiento) { this.idEstacionamiento = idEstacionamiento; }
    
    public String getNombreEstacionamiento() { return nombreEstacionamiento; }
    public void setNombreEstacionamiento(String nombreEstacionamiento) { this.nombreEstacionamiento = nombreEstacionamiento; }
    
    public List<RegistroEstacionamiento> getVehiculosActualmente() { return vehiculosActualmente; }
    public void setVehiculosActualmente(List<RegistroEstacionamiento> vehiculosActualmente) { 
        this.vehiculosActualmente = vehiculosActualmente;
        this.totalVehiculosAdentro = vehiculosActualmente != null ? vehiculosActualmente.size() : 0;
    }
    
    public List<RegistroEstacionamiento> getIngresosDelDia() { return ingresosDelDia; }
    public void setIngresosDelDia(List<RegistroEstacionamiento> ingresosDelDia) { 
        this.ingresosDelDia = ingresosDelDia;
        this.totalIngresosHoy = ingresosDelDia != null ? ingresosDelDia.size() : 0;
    }
    
    public List<RegistroEstacionamiento> getEgresosDelDia() { return egresosDelDia; }
    public void setEgresosDelDia(List<RegistroEstacionamiento> egresosDelDia) { 
        this.egresosDelDia = egresosDelDia;
        this.totalEgresosHoy = egresosDelDia != null ? egresosDelDia.size() : 0;
    }
    
    public int getTotalVehiculosAdentro() { return totalVehiculosAdentro; }
    public int getTotalIngresosHoy() { return totalIngresosHoy; }
    public int getTotalEgresosHoy() { return totalEgresosHoy; }
}

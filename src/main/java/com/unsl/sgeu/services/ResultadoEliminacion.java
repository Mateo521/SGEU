package com.unsl.sgeu.services;


public class ResultadoEliminacion {
    private boolean exitoso;
    private String mensaje;
    
    public ResultadoEliminacion(boolean exitoso, String mensaje) {
        this.exitoso = exitoso;
        this.mensaje = mensaje;
    }
    
    // Getters y setters
    public boolean isExitoso() { 
        return exitoso; 
    }
    
    public void setExitoso(boolean exitoso) { 
        this.exitoso = exitoso; 
    }
    
    public String getMensaje() { 
        return mensaje; 
    }
    
    public void setMensaje(String mensaje) { 
        this.mensaje = mensaje; 
    }
}


package com.unsl.sgeu.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.unsl.sgeu.services.*;
import com.unsl.sgeu.models.*;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.PostConstruct;

public class VehiculoController {

    private final VehiculoService vehiculoService;

    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    public void crearVehiculo(String patente, Persona persona) {
       
        Vehiculo vehiculo = vehiculoService.crearVehiculoConCodigoQr(patente, persona);
        
     
        System.out.println("Vehículo creado con código QR: " + vehiculo.getCodigoQr());
    }
}

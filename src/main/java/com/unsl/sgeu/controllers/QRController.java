package com.unsl.sgeu.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.unsl.sgeu.services.VehiculoService;
import com.unsl.sgeu.services.PersonaService;
import com.unsl.sgeu.models.Vehiculo;
import com.unsl.sgeu.models.Persona;

import java.util.HashMap;
import java.util.Map;

@RestController
@Controller
@RequestMapping("/qr")
public class QRController {

    @Autowired
    private VehiculoService vehiculoService;
    
    @Autowired
    private PersonaService personaService;

    @GetMapping("/leer")  
    public String mostrarLectorQR() {
        return "leerqr";
    }

    @PostMapping("/procesar")
    @ResponseBody
    public ResponseEntity<?> leerCodigoQR(@RequestBody Map<String, String> request) {
        try {
            String codigoQR = request.get("codigo");
            
            // ✅ AGREGAR DEBUG
            System.out.println("=== DEBUG LEER QR ===");
            System.out.println("Código QR recibido: " + codigoQR);
            
            if (codigoQR == null || codigoQR.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "Código QR vacío"));
            }

            // Buscar vehículo por código QR
            Vehiculo vehiculo = vehiculoService.buscarPorQr(codigoQR);
            
            if (vehiculo == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "Vehículo no encontrado"));
            }

          
       
            Map<String, Object> response = new HashMap<>();
            response.put("patente", vehiculo.getPatente());
            response.put("modelo", vehiculo.getModelo() != null ? vehiculo.getModelo() : "Sin modelo");
            response.put("color", vehiculo.getColor() != null ? vehiculo.getColor() : "Sin color");
            response.put("tipo", vehiculo.getTipo() != null ? vehiculo.getTipo() : "Sin tipo");
            response.put("dniDuenio", vehiculo.getDniDuenio());
           
            if (vehiculo.getDniDuenio() != null) {
                try {
                    Persona persona = personaService.buscarPorDni(vehiculo.getDniDuenio());
                    if (persona != null) {
                        response.put("nombreDuenio", persona.getNombre());
                        response.put("categoriaDuenio", persona.getCategoria());
                        response.put("telefonoDuenio", persona.getTelefono());
                        response.put("emailDuenio", persona.getEmail());
                        
                        System.out.println("Información del dueño:");
                        System.out.println("- Nombre: " + persona.getNombre());
                        System.out.println("- Categoría: " + persona.getCategoria());
                    }
                } catch (Exception e) {
                    System.err.println("Error al buscar persona: " + e.getMessage());
                }
            }
            
       
            System.out.println("Respuesta enviada: " + response);
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error en leerCodigoQR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "Error interno del servidor: " + e.getMessage()));
        }
    }
}

package com.unsl.sgeu.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.unsl.sgeu.services.EstacionamientoService;
import com.unsl.sgeu.services.RegistroEstacionamientoService;
import com.unsl.sgeu.services.VehiculoOperacionException;
import com.unsl.sgeu.services.VehiculoService;
import com.unsl.sgeu.dto.AccionEstacionamientoResultDTO;
import com.unsl.sgeu.dto.EstacionamientoDTO;
import com.unsl.sgeu.dto.VehiculoQRResponseDTO;

import jakarta.servlet.http.HttpSession;

import java.util.Map;

@RestController
@RequestMapping("/qr")
public class QRController {

    @Autowired
    private VehiculoService vehiculoService;   
    @Autowired
    private EstacionamientoService estacionamientoService;

    @Autowired
    private RegistroEstacionamientoService registroEstacionamientoService;

    @GetMapping("/leer")
    public String mostrarLectorQR() {
        return "leerqr";
    }

    @PostMapping("/procesar")
    @ResponseBody
    public ResponseEntity<?> leerCodigoQR(@RequestBody Map<String, String> request, HttpSession session) {
        try {
            String codigoQR = request.get("codigo");
            
            if (codigoQR == null || codigoQR.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("mensaje", "Código QR vacío"));
            }

            Long estacionamientoId = (Long) session.getAttribute("estacionamientoId");
            EstacionamientoDTO estacionamiento = estacionamientoService.obtener(estacionamientoId);
            
            if (estacionamiento == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("mensaje", "No hay estacionamiento seleccionado"));
            }

            VehiculoQRResponseDTO response = vehiculoService.obtenerDatosQR(codigoQR, estacionamiento);
            return ResponseEntity.ok(response);

        } catch (VehiculoOperacionException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", e.getMessage()));
                    
        } catch (Exception e) {
            System.err.println("Error en leerCodigoQR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "Error interno del servidor: " + e.getMessage()));
        }
    }

    @PostMapping("/procesar-accion")
    @ResponseBody
    public ResponseEntity<?> procesarAccion(@RequestBody Map<String, String> request, HttpSession session) {
        try {
            String patente = request.get("patente");
            String accion = request.get("accion");

            if (patente == null || accion == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("mensaje", "Datos incompletos"));
            }

            Long estacionamientoId = (Long) session.getAttribute("estacionamientoId");
            EstacionamientoDTO estacionamiento = estacionamientoService.obtener(estacionamientoId);
            
            if (estacionamiento == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("mensaje", "No hay estacionamiento seleccionado"));
            }

            AccionEstacionamientoResultDTO resultado = 
                registroEstacionamientoService.procesarAccion(patente, accion, estacionamiento);
            
            return ResponseEntity.ok(resultado);

        } catch (VehiculoOperacionException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("mensaje", e.getMessage()));
                    
        } catch (Exception e) {
            System.err.println("Error en procesarAccion: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "Error interno del servidor"));
        }
    }
}

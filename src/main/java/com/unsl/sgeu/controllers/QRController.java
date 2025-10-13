package com.unsl.sgeu.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.unsl.sgeu.services.VehiculoService;

import jakarta.servlet.http.HttpSession;

import com.unsl.sgeu.services.PersonaService;
import com.unsl.sgeu.services.RegistroEstacionamientoService;
import com.unsl.sgeu.models.Vehiculo;
import com.unsl.sgeu.models.Estacionamiento;
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

    @Autowired
    private RegistroEstacionamientoService registroestacionamientoService;

    @GetMapping("/leer")
    public String mostrarLectorQR() {
        return "leerqr";
    }

    @PostMapping("/procesar")
    @ResponseBody
    public ResponseEntity<?> leerCodigoQR(@RequestBody Map<String, String> request, HttpSession session) {
        try {
            String codigoQR = request.get("codigo");

            System.out.println("Código QR recibido: " + codigoQR);

            if (codigoQR == null || codigoQR.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("mensaje", "Código QR vacío"));
            }

            Vehiculo vehiculo = vehiculoService.buscarPorCodigoQr(codigoQR);

            if (vehiculo == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("mensaje", "Vehículo no encontrado"));
            }

        
            Estacionamiento estacionamiento = (Estacionamiento) session.getAttribute("estacionamiento");
            if (estacionamiento == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("mensaje", "No hay estacionamiento seleccionado"));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("patente", vehiculo.getPatente());
            response.put("modelo", vehiculo.getModelo() != null ? vehiculo.getModelo() : "Sin modelo");
            response.put("color", vehiculo.getColor() != null ? vehiculo.getColor() : "Sin color");
            response.put("tipo", vehiculo.getTipo() != null ? vehiculo.getTipo() : "Sin tipo");
            response.put("dniDuenio", vehiculo.getDniDuenio());

          
            boolean estaAdentro = !registroestacionamientoService.esPar(vehiculo.getPatente());
            response.put("estaAdentro", estaAdentro);

           
            if (estaAdentro) {
                response.put("accionDisponible", "Salida");
                response.put("mensajeAccion", "El vehículo está dentro del estacionamiento");
            } else {
                response.put("accionDisponible", "Entrada");
                response.put("mensajeAccion", "El vehículo está fuera del estacionamiento");
            }

            //  info del dueño
            if (vehiculo.getDniDuenio() != null) {
                try {
                    Persona persona = personaService.buscarPorDni(vehiculo.getDniDuenio());
                    if (persona != null) {
                        response.put("nombreDuenio", persona.getNombre());
                        response.put("categoriaDuenio", persona.getCategoria());
                        response.put("telefonoDuenio", persona.getTelefono());
                        response.put("emailDuenio", persona.getEmail());
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

 
    @PostMapping("/procesar-accion")
    @ResponseBody
    public ResponseEntity<?> procesarAccion(@RequestBody Map<String, String> request, HttpSession session) {
        try {
            String patente = request.get("patente");
            String accion = request.get("accion"); // entrada o salida

            if (patente == null || accion == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("mensaje", "Datos incompletos"));
            }

            Estacionamiento estacionamiento = (Estacionamiento) session.getAttribute("estacionamiento");
            if (estacionamiento == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("mensaje", "No hay estacionamiento seleccionado"));
            }

        
            boolean resultado = false;
            String mensaje = "";

            if ("Entrada".equals(accion) && vehiculoService.existePatente(patente)
                    && registroestacionamientoService.esPar(patente)) {

                registroestacionamientoService.registrarEntrada(patente, estacionamiento);
                resultado = true;
                mensaje = "Entrada registrada correctamente";

            } else if ("Salida".equals(accion) && !registroestacionamientoService.esPar(patente)) {

                registroestacionamientoService.registrarSalida(patente, estacionamiento, 1);
                resultado = true;
                mensaje = "Salida registrada correctamente";

            } else {
                resultado = false;
                if ("Entrada".equals(accion)) {
                    mensaje = "El vehículo ya está dentro del estacionamiento";
                } else {
                    mensaje = "El vehículo no está dentro del estacionamiento";
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("resultado", resultado);
            response.put("mensaje", mensaje);
            response.put("patente", patente);
            response.put("accion", accion);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error en procesarAccion: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "Error interno del servidor"));
        }
    }
}

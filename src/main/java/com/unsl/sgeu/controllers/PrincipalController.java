package com.unsl.sgeu.controllers;

import com.unsl.sgeu.models.DetallesInfo;
import com.unsl.sgeu.models.LeerQR;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PrincipalController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/leerqr")
    public String leerqr() {
        return "leerqr";
    }

    @GetMapping("/registrar-vehiculo")
    public String registrarVehiculo() {
        return "registrarvehiculo";  
    }

    /*
     * @PostMapping("/leerqr")
     * public ResponseEntity<String> recibirQR(@RequestBody LeerQR qr) {
     * System.out.println("Código leído: " + qr.getCodigo());
     * return ResponseEntity.ok("QR recibido correctamente: " + qr.getCodigo());
     * }
     */
    @PostMapping("/detalles_info")
    public String recibirFormulario(@ModelAttribute DetallesInfo detallesInfo) {

        System.out.println("Nombre: " + detallesInfo.getNombre());
        System.out.println("Correo: " + detallesInfo.getCorreo());

        return "detalles_info";
    }



    

@GetMapping("/ieManual")
    public String showManual() {
        return "ieManual"; }
        
}
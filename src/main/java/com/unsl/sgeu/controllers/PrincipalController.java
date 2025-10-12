package com.unsl.sgeu.controllers;

import com.unsl.sgeu.models.DetallesInfo;
import com.unsl.sgeu.services.RegistroEstacionamientoService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class PrincipalController {

 

    @ModelAttribute("patentesVencidas")
    public List<String> cargarPatentesVencidas() {
        return registroestacionamientoService.obtenerPatentesAdentroMasDeCuatroHoras();
    }

    // Aca luego separo los 2 diferentes logins, redirigiendo a diferentes paginas
    @GetMapping("/")
    public String index(HttpSession session, Model model) {

        if (session.getAttribute("user") == null) {
            System.out.println("Acceso no autorizado. Redirigiendo a login.");
            return "redirect:/login";
        }
        return "index";

    }
   

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/usuarios")
    public String usuarios() {
        return "table-user";
    }

    @GetMapping("/ajustes")
    public String ajustes() {
        return "ajustes";
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



    

@GetMapping("/ieManual")
    public String showManual() {
        return "ieManual"; }
        
}
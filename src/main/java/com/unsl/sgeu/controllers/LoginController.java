package com.unsl.sgeu.controllers;

import com.unsl.sgeu.dto.SessionDTO;
import com.unsl.sgeu.services.EmpleadoServices;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final EmpleadoServices empleadoServices;

    public LoginController(EmpleadoServices empleadoServices) {
        this.empleadoServices = empleadoServices;
    }

    @PostMapping("/login")
    public String login(@RequestParam String nombreUsuario,
            @RequestParam String contrasenia,
            HttpSession session) {

        SessionDTO sesion = empleadoServices.autenticarYObtenerDatosSesion(nombreUsuario, contrasenia);
        if (!sesion.getLoginExitoso()) {
            return "redirect:/login?error=true";
        }

        // Almacenamos los datos del DTO en la sesión
        session.setAttribute("usuarioId", sesion.getUsuarioId());
        session.setAttribute("user", sesion.getNombreUsuario());
        session.setAttribute("rol", sesion.getRol());
        session.setAttribute("nombreCompleto", sesion.getNombreCompleto());
        session.setAttribute("estacionamientoId", sesion.getEstacionamientoId());
        session.setAttribute("estacionamientoNombre", sesion.getEstacionamientoNombre());

        System.out.println("=== VERIFICACIÓN SESIÓN ===");
        System.out.println(sesion);
        System.out.println("========================");

        return "redirect:/?success=true";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        System.out.println("Usuario " + session.getAttribute("user") + " cerró sesión.");
        session.invalidate();
        return "redirect:/login";
    }
}

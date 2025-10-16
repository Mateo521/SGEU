package com.unsl.sgeu.controllers;

import com.unsl.sgeu.models.Estacionamiento;
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

        boolean ok = empleadoServices.login(nombreUsuario, contrasenia);
        if (!ok) {
            return "redirect:/login?error=true";
        }

        // Nuevo: obtenemos rol y nombre completo por nombre de usuario
        String rol = empleadoServices.obtenerRolEmpleado(nombreUsuario);
        String nombreCompleto = empleadoServices.obtenerNombrePorUsuario(nombreUsuario);
        Long usuarioId = empleadoServices.obtenerIdPorUsuario(nombreUsuario);
        Estacionamiento estacionamiento = empleadoServices.obtenerEstacionamientoActivo(nombreUsuario);


         System.out.println("estacionamiento antes::" + estacionamiento);
        // Guardamos en sesión lo necesario
        session.setAttribute("usuarioId", usuarioId);
        session.setAttribute("user", nombreUsuario);
        session.setAttribute("rol", rol);
        session.setAttribute("nombreCompleto", nombreCompleto);
        session.setAttribute("estacionamiento", estacionamiento);

        System.out.println("=== VERIFICACIÓN SESIÓN ===");
        System.out.println("Usuario: " + session.getAttribute("user"));
        System.out.println("ID: " + session.getAttribute("usuarioId"));
        System.out.println("Rol: " + session.getAttribute("rol"));
        System.out.println("Nombre: " + session.getAttribute("nombreCompleto"));
        System.out.println("Estacionamiento activo: " + session.getAttribute("estacionamiento"));
        System.out.println("========================");

        System.out.println("Usuario " + nombreUsuario + " inició sesión. Rol: " + rol);


        return "redirect:/?success=true";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        System.out.println("Usuario " + session.getAttribute("user") + " cerró sesión.");
        session.invalidate();
        return "redirect:/login";
    }
}

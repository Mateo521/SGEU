package com.unsl.sgeu.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.unsl.sgeu.services.EmpleadoServices;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private EmpleadoServices empleadoServices;
    
    
    @PostMapping("/login")
    public String login(@RequestParam String nombreUsuario,
                        @RequestParam String contrasenia,
                        HttpSession session) {
        if (empleadoServices.login(nombreUsuario, contrasenia)) {
            String cargo = empleadoServices.obtenerCargoEmpleado(nombreUsuario);
            String empleadoID = empleadoServices.obtenerNombreEmpleado((long) 1);
            System.out.println("Usuario " + nombreUsuario + " ha iniciado sesión. Cargo: " + cargo);
            System.out.println("Empleado ID: " + empleadoID);
            session.setAttribute("user", nombreUsuario);
            //return "redirect:/"; // te manda a index.html
            return "redirect:/?success=true";
        } else {
            return "redirect:/login?error=true";
        }
    }



    @GetMapping("/logout")
    public String logout(HttpSession session) {
        System.out.println("Usuario ha cerrado sesión.");
        session.invalidate();
        return "redirect:/login";
    }
}

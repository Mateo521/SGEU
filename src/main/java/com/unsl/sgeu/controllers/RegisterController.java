package com.unsl.sgeu.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import com.unsl.sgeu.services.EmpleadoServices;

@Controller
public class RegisterController {
    @Autowired
    private EmpleadoServices empleadoServices;

    
    @PostMapping("/register")
    public String register(@RequestParam String nombre,
                        @RequestParam String apellido,
                        @RequestParam String nombreUsuario,
                        @RequestParam String contrasenia,
                        @RequestParam String correo,
                        @RequestParam String cargo,
                        Model model) {

        if (empleadoServices.register(nombre, apellido, nombreUsuario, contrasenia, correo, cargo)) {
            model.addAttribute("registerSuccess", true);
        } else {
            model.addAttribute("registerError", true);
        }
        return "ajustes";
    }
}

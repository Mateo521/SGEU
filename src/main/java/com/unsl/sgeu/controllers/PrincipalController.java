package com.unsl.sgeu.controllers;
import com.unsl.sgeu.models.DetallesInfo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class PrincipalController {

    @GetMapping("/detalles_info")
    public String info(Model model) {
        model.addAttribute("detallesInfo", new DetallesInfo());
        return "detalles_info";  
    }


    @GetMapping("/")
    public String index() {
        return "index";
    }
    

      @GetMapping("/leerqr")
    public String leerqr() {
        return "leerqr";
    }

    
    @PostMapping("/detalles_info")
    public String recibirFormulario(@ModelAttribute DetallesInfo detallesInfo) {

        System.out.println("Nombre: " + detallesInfo.getNombre());
        System.out.println("Correo: " + detallesInfo.getCorreo());

        return "detalles_info";  
    }
}
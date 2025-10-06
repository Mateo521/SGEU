package com.unsl.sgeu.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class vehiculoController {

  @GetMapping("/search")
    public String buscar(@RequestParam(value="q", required=false) String query,
                         @RequestParam(value="category", required=false) String category,
                         Model model) {

        if ("Entrada".equals(category)) {
        //buscar behiculo en la tabla
        
        
        } else{



        }

        model.addAttribute("category", category);
        return "ieManual"; // tu vista (templates/ieManual.html)
    }
}
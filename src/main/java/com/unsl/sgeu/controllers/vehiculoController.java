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

        if (query != null) {
            // Si q = "1" → éxito, cualquier otro valor → error
            boolean resultado = "1".equals(query) && "Entrada".equals(category);
            model.addAttribute("resultado", resultado);
            model.addAttribute("patente", query);
        }

        model.addAttribute("category", category);
        return "ieManual"; // tu vista (templates/ieManual.html)
    }
}
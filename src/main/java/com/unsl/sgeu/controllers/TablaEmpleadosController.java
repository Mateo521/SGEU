package com.unsl.sgeu.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.unsl.sgeu.services.EmpleadoServices;

@Controller
public class TablaEmpleadosController {
    private final EmpleadoServices empleadoServices;

    public TablaEmpleadosController(EmpleadoServices empleadoServices) {
        this.empleadoServices = empleadoServices;
    }

    @GetMapping("/table_user")
    public String tableUser(Model model) {
        model.addAttribute("empleados", empleadoServices.listarEmpleados());
        System.out.println("Accediendo a la tabla de empleados");
        for (var emp : empleadoServices.listarEmpleados()) {
            System.out.println(emp);
        }
        return "table_user";
    } 
    
}

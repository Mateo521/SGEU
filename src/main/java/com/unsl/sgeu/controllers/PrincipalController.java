package com.unsl.sgeu.controllers;

import com.unsl.sgeu.models.DetallesInfo;
import com.unsl.sgeu.models.Vehiculo;
import com.unsl.sgeu.services.RegistroEstacionamientoService;
import com.unsl.sgeu.services.VehiculoService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class PrincipalController {

    @Autowired
       private RegistroEstacionamientoService registroestacionamientoService;
    @Autowired
    private VehiculoService vehiculoService;

    @ModelAttribute("patentesVencidas")
    public List<String> cargarPatentesVencidas() {
        return registroestacionamientoService.obtenerPatentesAdentroMasDeCuatroHoras();
    }

    // Aca luego separo los 2 diferentes logins, redirigiendo a diferentes paginas
    @GetMapping("/")
    public String index(HttpSession session, Model model,  @RequestParam(value = "buscar", required = false) String buscar,
            @RequestParam(value = "estacionamientoFiltro", required = false) Long estacionamientoFiltro, HttpServletRequest request) {

        if (session.getAttribute("user") == null) {
            System.out.println("Acceso no autorizado. Redirigiendo a login.");
            return "redirect:/login";
        }

        System.out.println(" DEBUG COMPLETO");
        System.out.println("URL completa: " + request.getRequestURL());
        System.out.println("URI: " + request.getRequestURI());
        System.out.println("Método HTTP: " + request.getMethod());
        System.out.println("Query String: " + request.getQueryString());
        System.out.println("Context Path: " + request.getContextPath());

        String rol = (String) session.getAttribute("rol");
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        String nombreCompleto = (String) session.getAttribute("nombreCompleto");

        System.out.println("Usuario: " + nombreCompleto + " | Rol: " + rol + " | ID: " + usuarioId);

        boolean esAdministrador = "ADMINISTRADOR".equals(rol) || "Administrador".equals(rol);
        boolean esGuardia = "GUARDIA".equals(rol) || "Guardia".equals(rol);

        System.out.println("Es Administrador: " + esAdministrador + " | Es Guardia: " + esGuardia);

        if (!esAdministrador && !esGuardia) {
            System.out.println(" Usuario sin permisos");
            model.addAttribute("error", "No tiene permisos para acceder a esta sección");
            return "error";
        }

        List<Vehiculo> lista;

        try {
            if (esAdministrador) {
                System.out.println(" Procesando como admin");
                if (buscar != null && !buscar.trim().isEmpty()) {
                    System.out.println("Admin buscando: " + buscar);
                    lista = vehiculoService.buscarVehiculosPorPatente(buscar.trim());
                } else {
                    System.out.println("Admin obteniendo todos los vehics.");
                    lista = vehiculoService.obtenerTodos();
                }
            } else {
                System.out.println(" Procesando como GUARDIA");
                if (buscar != null && !buscar.trim().isEmpty()) {
                    System.out.println("Guardia buscando: " + buscar);
                    lista = vehiculoService.buscarPorPatenteYGuardia(buscar.trim(), usuarioId);
                } else {
                    System.out.println("Guardia obteniendo vehículos asignados");
                    lista = vehiculoService.obtenerPorGuardia(usuarioId);
                }
            }
        } catch (Exception e) {
            System.err.println(" Error al obtener vehículos: " + e.getMessage());
            e.printStackTrace();
            lista = List.of();
            model.addAttribute("error", " Error al cargar vehículos: " + e.getMessage());
        }

        System.out.println(" Total vehiculos encontrados: " + lista.size());

        model.addAttribute("vehiculos", lista);
        model.addAttribute("buscar", buscar);
        model.addAttribute("rol", rol);
        model.addAttribute("esAdministrador", esAdministrador);
        model.addAttribute("esGuardia", esGuardia);
        model.addAttribute("nombreCompleto", nombreCompleto);

        System.out.println(" Retornando vista 'vehiculos'");
        return "vehiculos";

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
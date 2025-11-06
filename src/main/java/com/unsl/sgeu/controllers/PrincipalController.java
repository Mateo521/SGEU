package com.unsl.sgeu.controllers;

import com.unsl.sgeu.dto.EstacionamientoDTO;
import com.unsl.sgeu.models.Estacionamiento;
import com.unsl.sgeu.models.Vehiculo;
import com.unsl.sgeu.services.EstacionamientoService;
import com.unsl.sgeu.services.RegistroEstacionamientoService;
import com.unsl.sgeu.services.VehiculoService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Map;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.unsl.sgeu.dto.PanelDTO;
import com.unsl.sgeu.dto.VehiculoListadoDTO;
import com.unsl.sgeu.dto.PaginaDTO;

import org.springframework.data.domain.Page;

import org.springframework.data.domain.Sort;

@Controller
@SessionAttributes
public class PrincipalController {

    @Autowired
    private RegistroEstacionamientoService registroEstacionamientoService;
    @Autowired
    private VehiculoService vehiculoService;
    // Aca luego separo los 2 diferentes logins, redirigiendo a diferentes paginas

    @Autowired
    private EstacionamientoService estacionamientoService;

    @GetMapping("/")
    public String index(HttpSession session, Model model,
            @RequestParam(value = "buscar", required = false) String buscar,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        String rol = (String) session.getAttribute("rol");
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        String nombreCompleto = (String) session.getAttribute("nombreCompleto");

        boolean esAdministrador = "ADMINISTRADOR".equalsIgnoreCase(rol) || "Administrador".equalsIgnoreCase(rol);
        boolean esGuardia = "GUARDIA".equalsIgnoreCase(rol) || "Guardia".equalsIgnoreCase(rol);

        if (!esAdministrador && !esGuardia) {
            model.addAttribute("error", "No tiene permisos para acceder a esta sección");
            return "error";
        }

        // paneles (solo guardia) 
        List<PanelDTO> paneles = new ArrayList<>();
        if (esGuardia) {
            try {
                paneles = registroEstacionamientoService.construirPanelesParaGuardia(usuarioId);
            } catch (Exception e) {
                System.err.println("Error generando panel: " + e.getMessage());
                e.printStackTrace();
            }
        }

        try {
            //  paginacion
            PaginaDTO<VehiculoListadoDTO> paginaVehiculos = vehiculoService.listarVehiculosConDuenioPaginado(
                    (buscar != null ? buscar.trim() : null), page, size);

           
            Map<String, String> estacionamientosOrigen = new HashMap<>();
            if (esGuardia && !paginaVehiculos.getContent().isEmpty()) {
                List<String> patentes = paginaVehiculos.getContent().stream()
                        .map(VehiculoListadoDTO::getPatente)
                        .toList();
                estacionamientosOrigen = vehiculoService.obtenerEstacionamientosOrigenBatch(patentes, usuarioId);
            }

            model.addAttribute("vehiculos", paginaVehiculos.getContent());
            model.addAttribute("paginaVehiculos", paginaVehiculos);
            model.addAttribute("estacionamientosOrigen", estacionamientosOrigen);

        } catch (Exception e) {
            System.err.println("Error al obtener vehículos: " + e.getMessage());
            e.printStackTrace();
            PaginaDTO<VehiculoListadoDTO> paginaVacia = new PaginaDTO<>(new ArrayList<>(), page, size, 0);
            model.addAttribute("vehiculos", new ArrayList<>());
            model.addAttribute("paginaVehiculos", paginaVacia);
            model.addAttribute("estacionamientosOrigen", new HashMap<>());
            model.addAttribute("error", "Error al cargar vehículos: " + e.getMessage());
        }

        model.addAttribute("paneles", paneles);
        model.addAttribute("buscar", buscar);
        model.addAttribute("rol", rol);
        model.addAttribute("esAdministrador", esAdministrador);
        model.addAttribute("esGuardia", esGuardia);
        model.addAttribute("nombreCompleto", nombreCompleto);

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

    @GetMapping("/ieManual")
    public String showManual() {
        return "ieManual";
    }

}
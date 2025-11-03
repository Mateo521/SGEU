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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
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
            @RequestParam(value = "size", defaultValue = "10") int size,
            HttpServletRequest request) {

        if (session.getAttribute("user") == null) {
             return "redirect:/login";
        }

         

        String rol = (String) session.getAttribute("rol");
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        String nombreCompleto = (String) session.getAttribute("nombreCompleto");

 
        boolean esAdministrador = "ADMINISTRADOR".equals(rol) || "Administrador".equals(rol);
        boolean esGuardia = "GUARDIA".equals(rol) || "Guardia".equals(rol);

 
        if (!esAdministrador && !esGuardia) {
             model.addAttribute("error", "No tiene permisos para acceder a esta sección");
            return "error";
        }

        List<PanelDTO> paneles = new ArrayList<>();

        if (esGuardia) {
            try {
              

                List<EstacionamientoDTO> estacionamientosGuardia = estacionamientoService.obtenerPorEmpleado(usuarioId);

                if (estacionamientosGuardia.isEmpty()) {

                    
                } else {

                    for (EstacionamientoDTO est : estacionamientosGuardia) {
                        PanelDTO panel = new PanelDTO(est.getId(), est.getNombre());

                        panel.setVehiculosActualmente(
                                registroEstacionamientoService
                                        .obtenerVehiculosActualmenteEnEstacionamiento(est.getId()));
                        panel.setIngresosDelDia(
                                registroEstacionamientoService.obtenerIngresosDelDia(est.getId()));
                        panel.setEgresosDelDia(
                                registroEstacionamientoService.obtenerEgresosDelDia(est.getId()));

                        paneles.add(panel);

                   
                    }
                }

            } catch (Exception e) {
                System.err.println("Error generando panel: " + e.getMessage());
                e.printStackTrace();
            }
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("patente").ascending());
        Page<Vehiculo> paginaVehiculos;
        Map<String, String> estacionamientosOrigen = new HashMap<>();

        try {
            if (esAdministrador) {
                 if (buscar != null && !buscar.trim().isEmpty()) {
                     paginaVehiculos = vehiculoService.buscarVehiculosPorPatentePaginado(buscar.trim(), pageable);
                } else {
                     paginaVehiculos = vehiculoService.obtenerTodosPaginado(pageable);
                }
            } else {
                 if (buscar != null && !buscar.trim().isEmpty()) {
                     paginaVehiculos = vehiculoService.buscarPorPatenteYGuardiaPaginado(buscar.trim(), usuarioId,
                            pageable);
                } else {
                     paginaVehiculos = vehiculoService.obtenerTodosPaginado(pageable);
                }

                for (Vehiculo vehiculo : paginaVehiculos.getContent()) {
                    try {
                        String estacionamientoOrigen = vehiculoService.obtenerEstacionamientoOrigenVehiculo(
                                vehiculo.getPatente(), usuarioId);
                        estacionamientosOrigen.put(vehiculo.getPatente(), estacionamientoOrigen);
                    } catch (Exception e) {
                        System.err.println("Error obteniendo estacionamiento origen para " + vehiculo.getPatente()
                                + ": " + e.getMessage());
                        estacionamientosOrigen.put(vehiculo.getPatente(), "Desconocido");
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error al obtener vehículos paginados: " + e.getMessage());
            e.printStackTrace();
            paginaVehiculos = Page.empty(pageable);
            model.addAttribute("error", "Error al cargar vehículos: " + e.getMessage());
        }

        

         

        model.addAttribute("paneles", paneles);
        model.addAttribute("vehiculos", paginaVehiculos.getContent());
        model.addAttribute("paginaVehiculos", paginaVehiculos);
        model.addAttribute("buscar", buscar);
        model.addAttribute("rol", rol);
        model.addAttribute("esAdministrador", esAdministrador);
        model.addAttribute("esGuardia", esGuardia);
        model.addAttribute("nombreCompleto", nombreCompleto);
        model.addAttribute("estacionamientosOrigen", estacionamientosOrigen);

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
package com.unsl.sgeu.controllers;

import com.unsl.sgeu.dto.EstacionamientoDTO;
import com.unsl.sgeu.models.DetallesInfo;
import com.unsl.sgeu.models.Estacionamiento;
import com.unsl.sgeu.models.Vehiculo;
import com.unsl.sgeu.services.EstacionamientoService;
import com.unsl.sgeu.services.RegistroEstacionamientoService;
import com.unsl.sgeu.services.VehiculoService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Map;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
            System.out.println("Acceso no autorizado. Redirigiendo a login.");
            return "redirect:/login";
        }
  
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
            System.out.println("Usuario sin permisos");
            model.addAttribute("error", "No tiene permisos para acceder a esta sección");
            return "error";
        }

        List<PanelDTO> paneles = new ArrayList<>();

        if (esGuardia) {
            try {
                System.out.println("Generando panel para guardia ID: " + usuarioId);

                
                List<EstacionamientoDTO> estacionamientosGuardia = estacionamientoService.obtenerPorEmpleado(usuarioId);

                for (EstacionamientoDTO est : estacionamientosGuardia) {
                    PanelDTO panel = new PanelDTO(est.getId(), est.getNombre());

                    panel.setVehiculosActualmente(
                            registroEstacionamientoService.obtenerVehiculosActualmenteEnEstacionamiento(est.getId()));
                    panel.setIngresosDelDia(
                            registroEstacionamientoService.obtenerIngresosDelDia(est.getId()));
                    panel.setEgresosDelDia(
                            registroEstacionamientoService.obtenerEgresosDelDia(est.getId()));

                    paneles.add(panel);

                    System.out.println("Panel creado - Vehículos adentro: " + panel.getTotalVehiculosAdentro() +
                            ", Ingresos: " + panel.getTotalIngresosHoy() +
                            ", Egresos: " + panel.getTotalEgresosHoy());
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
                System.out.println("Procesando como ADMINISTRADOR - Página: " + page + ", Tamaño: " + size);
                if (buscar != null && !buscar.trim().isEmpty()) {
                    System.out.println("Admin buscando paginado: " + buscar);
                    paginaVehiculos = vehiculoService.buscarVehiculosPorPatentePaginado(buscar.trim(), pageable);
                } else {
                    System.out.println("Admin obteniendo todos paginado");
                    paginaVehiculos = vehiculoService.obtenerTodosPaginado(pageable);
                }
            } else {
                System.out.println("Procesando como GUARDIA - Página: " + page + ", Tamaño: " + size);
                if (buscar != null && !buscar.trim().isEmpty()) {
                    System.out.println("Guardia buscando paginado: " + buscar);
                    paginaVehiculos = vehiculoService.buscarPorPatenteYGuardiaPaginado(buscar.trim(), usuarioId,
                            pageable);
                } else {
                    System.out.println("Guardia obteniendo todos paginado");
                    paginaVehiculos = vehiculoService.obtenerTodosPorGuardiaPaginado(usuarioId, pageable);
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

       
      
        System.out.println("Página actual: " + paginaVehiculos.getNumber());
        System.out.println("Elementos en esta página: " + paginaVehiculos.getNumberOfElements());
        System.out.println("Total elementos: " + paginaVehiculos.getTotalElements());
        System.out.println("Total páginas: " + paginaVehiculos.getTotalPages());
        System.out.println("Es primera página: " + paginaVehiculos.isFirst());
        System.out.println("Es última página: " + paginaVehiculos.isLast());

        if (esGuardia) {
            System.out.println("Estacionamientos origen mapeados: " + estacionamientosOrigen.size());
            System.out.println("Paneles creados: " + paneles.size());
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

    /*
     * @PostMapping("/leerqr")
     * public ResponseEntity<String> recibirQR(@RequestBody LeerQR qr) {
     * System.out.println("Código leído: " + qr.getCodigo());
     * return ResponseEntity.ok("QR recibido correctamente: " + qr.getCodigo());
     * }
     */

    @GetMapping("/ieManual")
    public String showManual() {
        return "ieManual";
    }

    @ModelAttribute("patentesVencidas")
    public List<String> cargarPatentesVencidas(HttpSession session) {
        Estacionamiento est = (Estacionamiento) session.getAttribute("estacionamiento");
        if (est == null) {
            // No hay sesión activa o no hay estacionamiento seleccionado
            System.out.println("ESTACIONAMIENTO NULL");
            return Collections.emptyList(); // Devuelve lista vacía en ese caso
        }
        System.out.println("ESTACIONAMIENOT: " + est.getIdEst());
        return registroEstacionamientoService.obtenerPatentesAdentroMasDeCuatroHoras(est);
    }
}
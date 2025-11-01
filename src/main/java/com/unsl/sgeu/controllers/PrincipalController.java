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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Map;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import com.unsl.sgeu.dto.PanelDTO;
import com.unsl.sgeu.util.Pagina;

@Controller
@SessionAttributes({ "user", "rol", "nombreCompleto", "usuarioId" })
public class PrincipalController {

    private final RegistroEstacionamientoService registroEstacionamientoService;
    private final VehiculoService vehiculoService;
    private final EstacionamientoService estacionamientoService;

    @Autowired
    public PrincipalController(
            RegistroEstacionamientoService registroEstacionamientoService,
            VehiculoService vehiculoService,
            EstacionamientoService estacionamientoService) {
        this.registroEstacionamientoService = registroEstacionamientoService;
        this.vehiculoService = vehiculoService;
        this.estacionamientoService = estacionamientoService;
    }



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
              

                List<EstacionamientoDTO> estacionamientosGuardia = estacionamientoService.obtenerPorEmpleado(usuarioId);

                if (estacionamientosGuardia.isEmpty()) {
                    System.out.println("No se encontraron turnos para el empleado: " + usuarioId);

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

                        System.out.println("Panel creado - Estacionamiento: " + est.getNombre() +
                                " | Vehículos adentro: " + panel.getTotalVehiculosAdentro() +
                                ", Ingresos: " + panel.getTotalIngresosHoy() +
                                ", Egresos: " + panel.getTotalEgresosHoy());
                    }
                }

            } catch (Exception e) {
                System.err.println("Error generando panel: " + e.getMessage());
                e.printStackTrace();
            }
        }

        Map<String, String> estacionamientosOrigen = new HashMap<>();
        Pagina<Vehiculo> paginaVehiculos;

        try {
            if (esAdministrador) {
                System.out.println("Procesando como ADMINISTRADOR - Página: " + page + ", Tamaño: " + size);
                if (buscar != null && !buscar.trim().isEmpty()) {
                    System.out.println("Admin buscando paginado: " + buscar);
                    List<Vehiculo> vehiculos = vehiculoService.buscarPorPatente(buscar.trim(), page, size);
                    long total = vehiculoService.contarPorPatente(buscar.trim());
                    paginaVehiculos = new Pagina<>(vehiculos, page, size, total);
                } else {
                    System.out.println("Admin obteniendo todos paginado");
                    List<Vehiculo> vehiculos = vehiculoService.obtenerTodos(page, size);
                    long total = vehiculoService.contarTotal();
                    paginaVehiculos = new Pagina<>(vehiculos, page, size, total);
                }
            } else {
                System.out.println("Procesando como GUARDIA - Página: " + page + ", Tamaño: " + size);
                if (buscar != null && !buscar.trim().isEmpty()) {
                    System.out.println("Guardia buscando paginado: " + buscar);
                    List<Vehiculo> vehiculos = vehiculoService.buscarPorPatenteYGuardia(buscar.trim(), usuarioId, page, size);
                    long total = vehiculoService.contarPorPatenteYGuardia(buscar.trim(), usuarioId);
                    paginaVehiculos = new Pagina<>(vehiculos, page, size, total);
                } else {
                    System.out.println("Guardia obteniendo todos paginado");
                    List<Vehiculo> vehiculos = vehiculoService.obtenerPorGuardia(usuarioId, page, size);
                    long total = vehiculoService.contarPorGuardia(usuarioId);
                    paginaVehiculos = new Pagina<>(vehiculos, page, size, total);
                }

                for (Vehiculo vehiculo : paginaVehiculos.getContenido()) {
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
            paginaVehiculos = new Pagina<>(new ArrayList<>(), page, size, 0);
            model.addAttribute("error", "Error al cargar vehículos: " + e.getMessage());
        }

        System.out.println("=== INFORMACIÓN DE PAGINACIÓN ===");
        System.out.println("Página actual: " + paginaVehiculos.getNumeroPagina());
        System.out.println("Elementos en esta página: " + paginaVehiculos.getNumeroElementos());
        System.out.println("Total elementos: " + paginaVehiculos.getTotalElementos());
        System.out.println("Total páginas: " + paginaVehiculos.getTotalPaginas());
        System.out.println("Es primera página: " + paginaVehiculos.esPrimera());
        System.out.println("Es última página: " + paginaVehiculos.esUltima());

        if (esGuardia) {
            System.out.println("=== INFORMACIÓN DE GUARDIA ===");
            System.out.println("Estacionamientos origen mapeados: " + estacionamientosOrigen.size());
            System.out.println("Paneles creados: " + paneles.size());
        }

        model.addAttribute("paneles", paneles);
        model.addAttribute("vehiculos", paginaVehiculos.getContenido());
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

    /*
     * @ModelAttribute("patentesVencidas")
     * public List<String> cargarPatentesVencidas(HttpSession session) {
     * Estacionamiento est = (Estacionamiento)
     * session.getAttribute("estacionamiento");
     * if (est == null) {
     * // No hay sesión activa o no hay estacionamiento seleccionado
     * System.out.println("ESTACIONAMIENTO NULL");
     * return Collections.emptyList(); // Devuelve lista vacía en ese caso
     * }
     * System.out.println("ESTACIONAMIENOT: " + est.getIdEst());
     * return
     * registroEstacionamientoService.obtenerPatentesAdentroMasDeCuatroHoras(est);
     * }
     */
}
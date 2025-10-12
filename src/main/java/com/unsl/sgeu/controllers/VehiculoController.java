package com.unsl.sgeu.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;

import com.unsl.sgeu.dto.VehiculoFormDTO;
import com.unsl.sgeu.models.Estacionamiento;
import com.unsl.sgeu.models.Persona;
import com.unsl.sgeu.models.Vehiculo;
import com.unsl.sgeu.services.*;
import java.util.List;

@SessionAttributes
@Controller
//@RequestMapping("/vehiculos")
public class VehiculoController {
    @Autowired
    private RegistroEstacionamientoService registroestacionamientoService;

    @Autowired
    private VehiculoService vehiculoService;

    @Autowired
    private PersonaService personaService;

    @Autowired
    private QRCodeService qrCodeService;

    /*@GetMapping
    public String listarVehiculos(
            @RequestParam(value = "buscar", required = false) String buscar,
            @RequestParam(value = "estacionamientoFiltro", required = false) Long estacionamientoFiltro,
            Model model,
            HttpSession session,
            HttpServletRequest request) {

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
    }*/

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String testMethod(Model model) {
        System.out.println(" METODO TEST FUNCIONANDO");
        model.addAttribute("vehiculos", List.of());
        model.addAttribute("mensaje", "Método de prueba funcionando correctamente");
        return "vehiculos";
    }

    @GetMapping("/eliminar/{patente}")
    public String eliminarVehiculo(@PathVariable String patente,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        try {
        
            String rol = (String) session.getAttribute("rol");
            boolean esAdministrador = "ADMINISTRADOR".equals(rol) || "Administrador".equals(rol);
            boolean esGuardia = "GUARDIA".equals(rol) || "Guardia".equals(rol);

            if (!esAdministrador && !esGuardia) {
                redirectAttributes.addFlashAttribute("error", "No tiene permisos para eliminar vehículos");
                return "redirect:/vehiculos";
            }

            System.out.println("NLIMINANDO VEHÍCULO (NORMAL)");
            System.out.println("Patente: " + patente + " | Usuario: " + rol);

            ResultadoEliminacion resultado = vehiculoService.eliminarVehiculo(patente);

            if (resultado.isExitoso()) {
                redirectAttributes.addFlashAttribute("success", resultado.getMensaje());
            } else {
                redirectAttributes.addFlashAttribute("error", resultado.getMensaje());
            }

        } catch (Exception e) {
            System.err.println("Error al eliminar vehículo: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error",
                    "Error inesperado al eliminar el vehículo: " + e.getMessage());
        }

        return "redirect:/vehiculos";
    }

    
    @PostMapping("/eliminar-con-historial/{patente}")
    public String eliminarVehiculoConHistorial(@PathVariable String patente,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        try {
          
            String rol = (String) session.getAttribute("rol");
            boolean esAdministrador = "ADMINISTRADOR".equals(rol) || "Administrador".equals(rol);
/*
probando control
            if (!esAdministrador) {
                redirectAttributes.addFlashAttribute("error",
                        "Solo los administradores pueden eliminar vehículos con historial");
                return "redirect:/vehiculos";
            }
 */
            System.out.println("ELIMINANDO VEHICULO CON HISTORIAL");
            System.out.println("Patente: " + patente + " | Admin: " + session.getAttribute("nombreCompleto"));

            ResultadoEliminacion resultado = vehiculoService.eliminarVehiculoConHistorial(patente);

            if (resultado.isExitoso()) {
                redirectAttributes.addFlashAttribute("success", resultado.getMensaje());
            } else {
                redirectAttributes.addFlashAttribute("error", resultado.getMensaje());
            }

        } catch (Exception e) {
            System.err.println("Error al eliminar vehículo con historial: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error",
                    "Error crítico al eliminar: " + e.getMessage());
        }

        return "redirect:/vehiculos";
    }

    @GetMapping("/agregar")
    public String mostrarFormulario(Model model) {
        model.addAttribute("vehiculoForm", new VehiculoFormDTO());
        return "registrarvehiculo";
    }

    @PostMapping("/vehiculos/agregar")
    public String agregarVehiculo(@ModelAttribute VehiculoFormDTO form,
            RedirectAttributes redirectAttributes) {
        try {
            // Validaciones básicas
            if (vehiculoService.existePatente(form.getPatente())) {
                redirectAttributes.addFlashAttribute("error", "Ya existe un vehículo con esa patente");
                return "redirect:/vehiculos/agregar";
            }

            if (form.getDni() == null || form.getPatente() == null ||
                    form.getCategoriaNombre() == null || form.getTipoNombre() == null) {
                redirectAttributes.addFlashAttribute("error", "Todos los campos son obligatorios");
                return "redirect:/vehiculos/agregar";
            }

            Integer categoriaId = mapearCategoriaAId(form.getCategoriaNombre());
            if (categoriaId == null) {
                redirectAttributes.addFlashAttribute("error", "Categoría inválida");
                return "redirect:/vehiculos/agregar";
            }

            Integer vehiculoTipoId = mapearTipoAId(form.getTipoNombre());
            if (vehiculoTipoId == null) {
                redirectAttributes.addFlashAttribute("error", "Tipo de vehículo inválido");
                return "redirect:/vehiculos/agregar";
            }

            Persona persona = personaService.existePersona(form.getDni())
                    ? personaService.buscarPorDni(form.getDni())
                    : new Persona();

            persona.setDni(form.getDni());
            persona.setNombre(form.getNombre());
            persona.setTelefono(form.getTelefono());
            persona.setEmail(form.getEmail());
            persona.setIdCategoria(categoriaId);
            persona.setCategoria(form.getCategoriaNombre());

            personaService.guardarPersona(persona);

            String codigoQr = vehiculoService.generarCodigoQR(form.getPatente());

            Vehiculo vehiculo = new Vehiculo();
            vehiculo.setPatente(form.getPatente());
            vehiculo.setCodigoQr(codigoQr);
            vehiculo.setModelo(form.getModelo());
            vehiculo.setColor(form.getColor());
            vehiculo.setIdVehiculoTipo(vehiculoTipoId);
            vehiculo.setDniDuenio(form.getDni());
            vehiculo.setTipo(form.getTipoNombre());

            Vehiculo vehiculoGuardado = vehiculoService.guardarVehiculo(vehiculo);

            try {
                String rutaArchivoQR = qrCodeService.generarImagenQR(
                        vehiculoGuardado.getCodigoQr(),
                        vehiculoGuardado.getPatente());
                System.out.println("Archivo QR guardado: " + rutaArchivoQR);
            } catch (Exception e) {
                System.err.println("Error guardando archivo QR: " + e.getMessage());

            }

            String rutaImagenQR = "/qr-image/" + vehiculoGuardado.getCodigoQr();

            redirectAttributes.addFlashAttribute("success", "Vehículo agregado exitosamente");
            redirectAttributes.addFlashAttribute("codigoQR", vehiculoGuardado.getCodigoQr());
            redirectAttributes.addFlashAttribute("imagenQR", rutaImagenQR);
            redirectAttributes.addFlashAttribute("patente", form.getPatente());
            redirectAttributes.addFlashAttribute("vehiculoInfo", crearInfoVehiculo(form, persona));

            return "redirect:/vehiculos/agregar";

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al agregar el vehículo: " + e.getMessage());
            return "redirect:/vehiculos/agregar";
        }
    }

    private Integer mapearCategoriaAId(String categoria) {
        if (categoria == null)
            return null;
        switch (categoria.toLowerCase()) {
            case "docente":
                return 1;
            case "no_docente":
                return 2;
            case "estudiante":
                return 3;
            case "visitante":
                return 4;
            default:
                return null;
        }
    }

    private Integer mapearTipoAId(String tipo) {
        if (tipo == null)
            return null;
        switch (tipo.toLowerCase()) {
            case "auto":
                return 1;
            case "moto":
                return 2;
            default:
                return null;
        }
    }

    private String mapearIdACategoria(Integer id) {
        if (id == null)
            return "Sin categoría";
        switch (id) {
            case 1:
                return "Docente";
            case 2:
                return "No Docente";
            case 3:
                return "Estudiante";
            case 4:
                return "Visitante";
            default:
                return "Sin categoría";
        }
    }

    private String crearInfoVehiculo(VehiculoFormDTO form, Persona persona) {
        // Usar los datos del formulario directamente para evitar problemas
        String nombrePropietario = form.getNombre() != null ? form.getNombre() : "Sin nombre";
        String categoriaNombre = form.getCategoriaNombre() != null
                ? mapearCategoriaNombreATexto(form.getCategoriaNombre())
                : "Sin categoría";

        return String.format(
                "Propietario: %s (DNI: %s) | Patente: %s | Modelo: %s %s | Categoría: %s",
                nombrePropietario,
                form.getDni(),
                form.getPatente(),
                nullToDash(form.getModelo()),
                nullToDash(form.getColor()),
                categoriaNombre);
    }

    private String mapearCategoriaNombreATexto(String categoria) {
        if (categoria == null)
            return "Sin categoría";
        switch (categoria.toLowerCase()) {
            case "docente":
                return "Docente";
            case "no_docente":
                return "No Docente";
            case "estudiante":
                return "Estudiante";
            case "visitante":
                return "Visitante";
            default:
                return categoria;
        }
    }

    private String nullToDash(String s) {
        return (s == null || s.isBlank()) ? "—" : s;
    }


    @GetMapping("/search")
    public String buscar(@RequestParam(value="q", required=false) String patente,
                         @RequestParam(value="category", required=false) String category,
                         Model model,
                         HttpSession session) {

        boolean resultado=true;
          Estacionamiento est1 = (Estacionamiento) session.getAttribute("estacionamiento");
        System.out.println(est1.getIdEst());
System.out.println(""+"Entrada".equals(category)+" "+vehiculoService.existePatente(patente)+" "+registroestacionamientoService.esPar(patente));
        if ("Entrada".equals(category) && vehiculoService.existePatente(patente) && registroestacionamientoService.esPar(patente)) {
        //buscar vehiculo en la tabla
        System.out.println("entro al entrada");
            registroestacionamientoService.registrarEntrada(patente, session);
            model.addAttribute("category", category);
            model.addAttribute("resultado", resultado);
            model.addAttribute("patente", patente);
            resultado = true;
        return "ieManual";
       } else if ("Salida".equals(category) && !registroestacionamientoService.esPar(patente)){
         System.out.println("entro al salida");
            registroestacionamientoService.registrarSalida(patente, session);
            model.addAttribute("category", category);
            model.addAttribute("resultado", resultado);
            model.addAttribute("patente", patente);
            resultado = true;
            return "ieManual";
        }  
         System.out.println("no entro a ninguno");
            resultado = false;
    model.addAttribute("category", category);
    model.addAttribute("resultado", resultado);
    model.addAttribute("patente", patente);
        return "ieManual";   // tu vista (templates/ieManual.html)
    }

}


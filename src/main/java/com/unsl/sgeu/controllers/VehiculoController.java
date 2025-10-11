package com.unsl.sgeu.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.unsl.sgeu.dto.VehiculoFormDTO;
import com.unsl.sgeu.models.Persona;
import com.unsl.sgeu.models.Vehiculo;
import com.unsl.sgeu.services.*;
import java.util.List;

@Controller
@RequestMapping("/vehiculos")
public class VehiculoController {

    @Autowired
    private VehiculoService vehiculoService;

    @Autowired
    private PersonaService personaService;

    @GetMapping
    public String listarVehiculos(Model model) {
        System.out.println("=== DEBUG LISTAR VEHÍCULOS ===");

        List<Vehiculo> lista = vehiculoService.obtenerTodos();
        System.out.println("Total vehículos: " + lista.size());

        model.addAttribute("vehiculos", lista);
        return "vehiculos";
    }

    @GetMapping("/eliminar/{patente}")
    public String eliminarVehiculo(@PathVariable String patente,
            RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== ELIMINANDO VEHÍCULO ===");
            System.out.println("Patente a eliminar: " + patente);

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

    @GetMapping("/agregar")
    public String mostrarFormulario(Model model) {
        model.addAttribute("vehiculoForm", new VehiculoFormDTO());
        return "registrarvehiculo";
    }

    @GetMapping("/eliminar-con-historial/{patente}")
    public String eliminarVehiculoConHistorial(@PathVariable String patente,
            RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== ELIMINANDO VEHÍCULO CON HISTORIAL ===");
            System.out.println("Patente a eliminar: " + patente);

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
                    "❌ <strong>Error inesperado</strong><br>" +
                            "🛠️ <strong>Detalle:</strong> " + e.getMessage());
        }

        return "redirect:/vehiculos";
    }

    @PostMapping("/agregar")
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

            // Respuesta exitosa
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

}

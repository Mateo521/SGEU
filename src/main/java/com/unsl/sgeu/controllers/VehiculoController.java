
package com.unsl.sgeu.controllers;

import com.unsl.sgeu.dto.VehiculoFormDTO;
import com.unsl.sgeu.models.Persona;
import com.unsl.sgeu.models.Vehiculo;
import com.unsl.sgeu.services.PersonaService;
import com.unsl.sgeu.services.VehiculoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;
    private final PersonaService personaService;

    public VehiculoController(VehiculoService vehiculoService, PersonaService personaService) {
        this.vehiculoService = vehiculoService;
        this.personaService = personaService;
    }

    @GetMapping("/agregar")
    public String mostrarFormulario(Model model) {
        model.addAttribute("vehiculoForm", new VehiculoFormDTO());
        return "registrarvehiculo";
    }

    @PostMapping("/agregar")
    public String agregarVehiculo(@ModelAttribute VehiculoFormDTO form,
            RedirectAttributes redirectAttributes) {
        try {
            // Verificar si la patente ya existe
            if (vehiculoService.existePatente(form.getPatente())) {
                redirectAttributes.addFlashAttribute("error", "Ya existe un vehículo con esa patente");
                return "redirect:/vehiculos/agregar";
            }

          
            Persona persona;
            if (personaService.existePersona(form.getDni())) {
                persona = personaService.buscarPorDni(form.getDni());
                // Actualizar datos de la persona
                persona.setNombre(form.getNombre());
                persona.setCategoria(form.getCategoria());
                persona.setEmail(form.getEmail());
                persona.setTelefono(form.getTelefono());
            } else {
                persona = new Persona(form.getDni(), form.getNombre(),
                        form.getCategoria(), form.getEmail(), form.getTelefono());
            }

            personaService.guardarPersona(persona);

         
            Vehiculo vehiculo = new Vehiculo(form.getPatente(), null, form.getColor(),
                    form.getModelo(), form.getTipo(), form.getDni());

            Vehiculo vehiculoGuardado = vehiculoService.guardarVehiculo(vehiculo);

        
            String rutaImagenQR = "/qr-codes/qr_" + form.getPatente().replace(" ", "_") + ".png";
            String rutaImagenQRAlVuelo = "/qr-image/" + vehiculoGuardado.getCodigoQr();

            // Agregar atributos para mostrar en la vista
            redirectAttributes.addFlashAttribute("success",
                    "Vehículo agregado exitosamente. Código QR: " + vehiculoGuardado.getCodigoQr());
            redirectAttributes.addFlashAttribute("codigoQR", vehiculoGuardado.getCodigoQr());
            redirectAttributes.addFlashAttribute("imagenQR", rutaImagenQRAlVuelo); // Usar esta
            redirectAttributes.addFlashAttribute("patente", form.getPatente());
            redirectAttributes.addFlashAttribute("vehiculoInfo", crearInfoVehiculo(form, persona));

            return "redirect:/vehiculos/agregar";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al agregar el vehículo: " + e.getMessage());
            return "redirect:/vehiculos/agregar";
        }
    }

    // Método auxiliar para crear información completa del vehículo
    private String crearInfoVehiculo(VehiculoFormDTO form, Persona persona) {
        return String.format("Propietario: %s | Patente: %s | Modelo: %s %s | Categoría: %s",
                persona.getNombre(),
                form.getPatente(),
                form.getModelo(),
                form.getColor(),
                persona.getCategoria());
    }

  @GetMapping("/search")
    public String buscar(@RequestParam(value="q", required=false) String query,
                         @RequestParam(value="category", required=false) String category,
                         @ModelAttribute VehiculoFormDTO form,
                         Model model) {
        String patente = query;
        boolean resultado;
        if ("Entrada".equals(category)) {
        //buscar vehiculo en la tabla
        resultado = "1".equals(query);
        } else{
            resultado = "2".equals(query);
        }
        
    model.addAttribute("category", category);
    model.addAttribute("resultado", resultado);
    model.addAttribute("patente", patente);
        return "ieManual"; // tu vista (templates/ieManual.html)
    }
}

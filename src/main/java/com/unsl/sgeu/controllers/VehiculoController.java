
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
            
            // Crear o actualizar persona
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
            
            // Crear vehículo
            Vehiculo vehiculo = new Vehiculo(form.getPatente(), null, form.getColor(), 
                                           form.getModelo(), form.getTipo(), form.getDni());
            
            Vehiculo vehiculoGuardado = vehiculoService.guardarVehiculo(vehiculo);
            
            redirectAttributes.addFlashAttribute("success", 
                "Vehículo agregado exitosamente. Código QR: " + vehiculoGuardado.getCodigoQr());
            redirectAttributes.addFlashAttribute("codigoQR", vehiculoGuardado.getCodigoQr());
            
            return "redirect:/vehiculos/agregar";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al agregar el vehículo: " + e.getMessage());
            return "redirect:/vehiculos/agregar";
        }
    }
}

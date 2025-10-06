package com.unsl.sgeu.controllers;

import com.unsl.sgeu.dto.VehiculoFormDTO;
import com.unsl.sgeu.models.Persona;
import com.unsl.sgeu.models.Vehiculo;
import com.unsl.sgeu.models.Categoria;
import com.unsl.sgeu.models.VehiculoTipo;
import com.unsl.sgeu.services.PersonaService;
import com.unsl.sgeu.services.VehiculoService;
import com.unsl.sgeu.services.CategoriaService;
import com.unsl.sgeu.services.VehiculoTipoService;
import com.unsl.sgeu.services.PersonaVehiculoService;
import com.unsl.sgeu.services.RegistroEstacionamientoService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Controller
//@RequestMapping("/vehiculos")
public class VehiculoController {
    

    private final VehiculoService vehiculoService;
    private final PersonaService personaService;
    private final CategoriaService categoriaService;
    private final VehiculoTipoService vehiculoTipoService;
    private final PersonaVehiculoService personaVehiculoService;
    private final RegistroEstacionamientoService registroestacionamientoService;

    public VehiculoController(
            VehiculoService vehiculoService,
            PersonaService personaService,
            CategoriaService categoriaService,
            VehiculoTipoService vehiculoTipoService,
            PersonaVehiculoService personaVehiculoService,
            RegistroEstacionamientoService registroestacionamientoService) {
        this.vehiculoService = vehiculoService;
        this.personaService = personaService;
        this.categoriaService = categoriaService;
        this.vehiculoTipoService = vehiculoTipoService;
        this.personaVehiculoService = personaVehiculoService;
        this.registroestacionamientoService = registroestacionamientoService;
    }
    @GetMapping("/vehiculos/agregar")
    public String mostrarFormulario(Model model) {
        model.addAttribute("vehiculoForm", new VehiculoFormDTO());
        // Si querés, podés cargar listas para selects:
        // model.addAttribute("categorias", categoriaService.listarTodas());
        // model.addAttribute("tiposVehiculo", vehiculoTipoService.listarTodos());
        return "registrarvehiculo";
    }

    @PostMapping("/vehiculos/agregar")
    public String agregarVehiculo(@ModelAttribute VehiculoFormDTO form,
                                  RedirectAttributes redirectAttributes) {
        try {
            // 1) Validación rápida
            if (vehiculoService.existePatente(form.getPatente())) {
                redirectAttributes.addFlashAttribute("error", "Ya existe un vehículo con esa patente");
                return "redirect:/vehiculos/agregar";
            }
            if (form.getDni() == null || form.getPatente() == null) {
                redirectAttributes.addFlashAttribute("error", "DNI y Patente son obligatorios");
                return "redirect:/vehiculos/agregar";
            }

            // 2) Resolver categoría (id) desde id o nombre
            Short categoriaId = form.getIdCategoria();
            if (categoriaId == null && form.getNombre() != null) {
                Categoria cat = categoriaService.getOrCreateByNombre(form.getNombre().trim());
                categoriaId = cat.getId();
            }
            if (categoriaId == null) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar una categoría válida");
                return "redirect:/vehiculos/agregar";
            }

            // 3) Upsert de Persona
            Persona persona = personaService.existePersona(form.getDni())
                    ? personaService.buscarPorDni(form.getDni())
                    : new Persona();
            persona.setDni(form.getDni());
            persona.setNombre(form.getNombre());
            persona.setTelefono(form.getTelefono());
            persona.setEmail(form.getEmail());
            persona.setIdCategoria(categoriaId);
            personaService.guardarPersona(persona);

            // 4) Resolver tipo de vehículo (id) desde id o nombre
            Short vehiculoTipoId = form.getVehiculoTipoId();
            if (vehiculoTipoId == null && form.getVehiculoTipoId() != null) {
                VehiculoTipo tv = vehiculoTipoService.getByNombre(form.getVehiculoTipoId().toString());
                if (tv == null) {
                    redirectAttributes.addFlashAttribute("error", "Tipo de vehículo inválido");
                    return "redirect:/vehiculos/agregar";
                }
                vehiculoTipoId = tv.getId();
            }
            if (vehiculoTipoId == null) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar el tipo de vehículo");
                return "redirect:/vehiculos/agregar";
            }

            // 5) Generar código QR (único) en backend
            String codigoQr = vehiculoService.generarCodigoQR(form.getPatente());

            // 6) Crear y guardar Vehículo (sin dueño directo)
            Vehiculo vehiculo = new Vehiculo();
            vehiculo.setPatente(form.getPatente());
            vehiculo.setCodigoQr(codigoQr);
            vehiculo.setModelo(form.getModelo());
            vehiculo.setColor(form.getColor());
            vehiculo.setIdVehiculoTipo(vehiculoTipoId);

            Vehiculo vehiculoGuardado = vehiculoService.guardarVehiculo(vehiculo);

            // 7) Vincular relación N:M (persona_vehiculo)
            personaVehiculoService.vincular(form.getDni(), form.getPatente());
            if (form.getDnisAdicionales() != null) {
                for (Long dniAdic : form.getDnisAdicionales()) {
                    if (dniAdic != null && !Objects.equals(dniAdic, form.getDni())) {
                        // opcional: validar que exista persona; si no, podrías crearla básica o rechazar
                        if (personaService.existePersona(dniAdic)) {
                            personaVehiculoService.vincular(dniAdic, form.getPatente());
                        }
                    }
                }
            }

            // 8) Rutas de imagen QR (si tenés endpoint que la sirve por codigoQr)
            String rutaImagenQRAlVuelo = "/qr-image/" + vehiculoGuardado.getCodigoQr();

            // 9) Mensajes a la vista
            redirectAttributes.addFlashAttribute("success",
                    "Vehículo agregado exitosamente. Código QR: " + vehiculoGuardado.getCodigoQr());
            redirectAttributes.addFlashAttribute("codigoQR", vehiculoGuardado.getCodigoQr());
            redirectAttributes.addFlashAttribute("imagenQR", rutaImagenQRAlVuelo);
            redirectAttributes.addFlashAttribute("patente", form.getPatente());
            redirectAttributes.addFlashAttribute("vehiculoInfo", crearInfoVehiculo(form, persona, categoriaService));

            return "redirect:/vehiculos/agregar";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al agregar el vehículo: " + e.getMessage());
            return "redirect:/vehiculos/agregar";
        }
    }

    // Info compacta para la vista
    private String crearInfoVehiculo(VehiculoFormDTO form, Persona persona, CategoriaService categoriaService) {
        Short categoriaNombre = form.getIdCategoria();
        if (categoriaNombre == null && persona.getIdCategoria() != null) {
            Categoria cat = categoriaService.getById(persona.getIdCategoria());
            
        }
        return String.format(
                "Propietario: %s | Patente: %s | Modelo: %s %s | Categoría: %s",
                persona.getNombre(),
                form.getPatente(),
                nullToDash(form.getModelo()),
                nullToDash(form.getColor()),
                nullToDash(categoriaNombre.toString())
        );
    }

    private String nullToDash(String s) {
        return (s == null || s.isBlank()) ? "—" : s;
    }

  @GetMapping("/search")
    public String buscar(@RequestParam(value="q", required=false) String patente,
                         @RequestParam(value="category", required=false) String category,
                         Model model) {

        boolean resultado;

        if ("Entrada".equals(category) && vehiculoService.existePatente(patente)) {
        //buscar vehiculo en la tabla
            registroestacionamientoService.registrarEntrada(patente);
            resultado = true;
       } else{
            resultado = false;
        }
        
    model.addAttribute("category", category);
    model.addAttribute("resultado", resultado);
    model.addAttribute("patente", patente);
        return "ieManual";   // tu vista (templates/ieManual.html)
    }
}

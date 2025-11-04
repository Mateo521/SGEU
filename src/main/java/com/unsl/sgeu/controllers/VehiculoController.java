package com.unsl.sgeu.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import com.unsl.sgeu.dto.AccionEstacionamientoResultDTO;
import com.unsl.sgeu.dto.EstacionamientoDTO;
import com.unsl.sgeu.dto.RegistroVehiculoFormDTO;
import com.unsl.sgeu.dto.VehiculoDTO;
import com.unsl.sgeu.dto.PersonaDTO;

import com.unsl.sgeu.services.ResultadoEliminacion;
import com.unsl.sgeu.services.VehiculoOperacionException;

import com.unsl.sgeu.models.Persona;
import com.unsl.sgeu.models.Vehiculo;
import com.unsl.sgeu.services.*;
import java.util.List;

@SessionAttributes
@Controller
public class VehiculoController {
    @Autowired
    private RegistroEstacionamientoService registroestacionamientoService;

    @Autowired
    private EstacionamientoService estacionamientoService;

    @Autowired
    private VehiculoService vehiculoService;

    @Autowired
    private PersonaService personaService;

    @GetMapping("vehiculos/agregar")
    public String mostrarFormularioAgregar(Model model, HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        String rol = (String) session.getAttribute("rol");
        boolean esAdministrador = "ADMINISTRADOR".equals(rol) || "Administrador".equals(rol);
        boolean esGuardia = "GUARDIA".equals(rol) || "Guardia".equals(rol);

        if (!esAdministrador && !esGuardia) {
            redirectAttributes.addFlashAttribute("error", "No tiene permisos para agregar vehículos");
            return "redirect:/";
        }

        model.addAttribute("vehiculoForm", new RegistroVehiculoFormDTO());
        model.addAttribute("esEdicion", false);

        return "registrarvehiculo";
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String testMethod(Model model) {
        model.addAttribute("vehiculos", List.of());
        model.addAttribute("mensaje", "Método de prueba funcionando correctamente");
        return "vehiculos";
    }

    @GetMapping("vehiculos/eliminar/{patente}")
    public String eliminarVehiculo(@PathVariable String patente,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        try {

            String rol = (String) session.getAttribute("rol");
            boolean esAdministrador = "ADMINISTRADOR".equals(rol) || "Administrador".equals(rol);
            boolean esGuardia = "GUARDIA".equals(rol) || "Guardia".equals(rol);

            if (!esAdministrador && !esGuardia) {
                redirectAttributes.addFlashAttribute("error", "No tiene permisos para eliminar vehículos");
                return "redirect:/";
            }

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

        return "redirect:/";
    }

    @PostMapping("vehiculos/eliminar-con-historial/{patente}")
    public String eliminarVehiculoConHistorial(@PathVariable String patente,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        try {

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

        return "redirect:/";
    }

    @GetMapping("/agregar")
    public String mostrarFormulario(Model model) {
        model.addAttribute("vehiculoForm", new RegistroVehiculoFormDTO());
        return "registrarvehiculo";
    }

    @PostMapping("/vehiculos/agregar")
    public String agregarVehiculo(
            @Valid @ModelAttribute("vehiculoForm") RegistroVehiculoFormDTO form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        String rol = (String) session.getAttribute("rol");
        boolean esAdministrador = "ADMINISTRADOR".equalsIgnoreCase(rol);
        boolean esGuardia = "GUARDIA".equalsIgnoreCase(rol);

        if (!esAdministrador && !esGuardia) {
            redirectAttributes.addFlashAttribute("error", "No tiene permisos para agregar vehículos");
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            StringBuilder erroresMsg = new StringBuilder("Errores de validación:\n");
            bindingResult.getAllErrors().forEach(error -> {
                String mensaje = error.getDefaultMessage();
                erroresMsg.append("• ").append(mensaje).append("\n");
            });

            redirectAttributes.addFlashAttribute("error", erroresMsg.toString());
            redirectAttributes.addFlashAttribute("vehiculoForm", form);
            return "redirect:/vehiculos/agregar";
        }

        try {
            var resultado = vehiculoService.registrarNuevoVehiculo(form);

            redirectAttributes.addFlashAttribute("success", "Vehículo agregado exitosamente");
            redirectAttributes.addFlashAttribute("codigoQR", resultado.getCodigoQr());
            redirectAttributes.addFlashAttribute("imagenQR", resultado.getRutaImagenQR());
            redirectAttributes.addFlashAttribute("patente", resultado.getPatente());
            redirectAttributes.addFlashAttribute("vehiculoInfo", resultado.getVehiculoInfo());

            redirectAttributes.addFlashAttribute("modelo", form.getVehiculo().getModelo());
            redirectAttributes.addFlashAttribute("color", form.getVehiculo().getColor());
            redirectAttributes.addFlashAttribute("tipo", form.getVehiculo().getTipoNombre());
            redirectAttributes.addFlashAttribute("dni", form.getPersona().getDni());
            redirectAttributes.addFlashAttribute("nombre", form.getPersona().getNombre());

            return "redirect:/vehiculos/agregar";

        } catch (VehiculoOperacionException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("vehiculoForm", form);
            return "redirect:/vehiculos/agregar";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error",
                    "Error al agregar el vehículo: " + e.getMessage());
            redirectAttributes.addFlashAttribute("vehiculoForm", form);
            return "redirect:/vehiculos/agregar";
        }
    }

    @GetMapping("/vehiculos/editar/{patente}")
    public String mostrarFormularioEdicion(
            @PathVariable String patente,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            if (session.getAttribute("user") == null) {
                return "redirect:/login";
            }

            String rol = (String) session.getAttribute("rol");
            boolean esAdministrador = "ADMINISTRADOR".equalsIgnoreCase(rol);
            boolean esGuardia = "GUARDIA".equalsIgnoreCase(rol);

            if (!esAdministrador && !esGuardia) {
                redirectAttributes.addFlashAttribute("error", "No tiene permisos para editar vehículos");
                return "redirect:/";
            }

            Vehiculo vehiculo = vehiculoService.buscarPorPatente(patente);
            if (vehiculo == null) {
                redirectAttributes.addFlashAttribute("error", "Vehículo no encontrado");
                return "redirect:/";
            }

            Persona persona = personaService.buscarPorDni(vehiculo.getDniDuenio());
            if (persona == null) {
                redirectAttributes.addFlashAttribute("error", "Propietario no encontrado");
                return "redirect:/";
            }

            VehiculoDTO vehiculoDTO = new VehiculoDTO();
            vehiculoDTO.setPatente(vehiculo.getPatente());
            vehiculoDTO.setModelo(vehiculo.getModelo());
            vehiculoDTO.setColor(vehiculo.getColor());
            vehiculoDTO.setTipoNombre(vehiculo.getTipo());

            PersonaDTO personaDTO = new PersonaDTO();
            personaDTO.setDni(persona.getDni());
            personaDTO.setNombre(persona.getNombre());
            personaDTO.setTelefono(persona.getTelefono());
            personaDTO.setEmail(persona.getEmail());
            personaDTO.setCategoriaNombre(persona.getCategoria());

            RegistroVehiculoFormDTO form = new RegistroVehiculoFormDTO();
            form.setVehiculo(vehiculoDTO);
            form.setPersona(personaDTO);

            if (!model.containsAttribute("vehiculoForm")) {
                model.addAttribute("vehiculoForm", form);
            }

            model.addAttribute("esEdicion", true);
            model.addAttribute("patenteOriginal", patente);
            model.addAttribute("esAdministrador", esAdministrador);

            return "editarvehiculo";

        } catch (Exception e) {
            System.err.println("Error al cargar formulario de edición: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al cargar el vehículo");
            return "redirect:/";
        }
    }

    @PostMapping("/vehiculos/editar/{patenteOriginal}")
    public String editarVehiculo(
            @PathVariable String patenteOriginal,
            @Valid @ModelAttribute("vehiculoForm") RegistroVehiculoFormDTO form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model,
            HttpSession session) {

        try {
            if (session.getAttribute("user") == null) {
                return "redirect:/login";
            }

            String rol = (String) session.getAttribute("rol");
            boolean esAdministrador = "ADMINISTRADOR".equalsIgnoreCase(rol);
            boolean esGuardia = "GUARDIA".equalsIgnoreCase(rol);

            if (!esAdministrador && !esGuardia) {
                redirectAttributes.addFlashAttribute("error", "No tiene permisos para editar vehículos");
                return "redirect:/";
            }

            if (!patenteOriginal.equalsIgnoreCase(form.getVehiculo().getPatente())) {
                model.addAttribute("error", "No se puede modificar la patente del vehículo");
                model.addAttribute("vehiculoForm", form);
                model.addAttribute("esEdicion", true);
                model.addAttribute("patenteOriginal", patenteOriginal);
                model.addAttribute("esAdministrador", esAdministrador);
                return "editarvehiculo";
            }

            if (bindingResult.hasErrors()) {
                bindingResult.getAllErrors().forEach(error -> System.out.println(" - " + error.getDefaultMessage()));
                model.addAttribute("vehiculoForm", form);
                model.addAttribute("esEdicion", true);
                model.addAttribute("patenteOriginal", patenteOriginal);
                model.addAttribute("esAdministrador", esAdministrador);
                return "editarvehiculo";
            }

            vehiculoService.actualizarVehiculo(patenteOriginal, form);

            redirectAttributes.addFlashAttribute("success",
                    "Vehículo " + form.getVehiculo().getPatente() + " actualizado exitosamente");
            return "redirect:/";

        } catch (VehiculoOperacionException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("vehiculoForm", form);
            model.addAttribute("esEdicion", true);
            model.addAttribute("patenteOriginal", patenteOriginal);
            model.addAttribute("esAdministrador",
                    "ADMINISTRADOR".equalsIgnoreCase((String) session.getAttribute("rol")));
            return "editarvehiculo";
        } catch (Exception e) {
            System.err.println("Error al editar vehículo: " + e.getMessage());
            e.printStackTrace();

            model.addAttribute("error", "Error inesperado al actualizar: " + e.getMessage());
            model.addAttribute("vehiculoForm", form);
            model.addAttribute("esEdicion", true);
            model.addAttribute("patenteOriginal", patenteOriginal);
            return "editarvehiculo";
        }
    }

    @PostMapping("/registrar-movimiento")
    public String registrarMovimiento(@RequestParam String patente1,
            @RequestParam String accion,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        String mensaje = "";
        boolean exito = false;

        if (session.getAttribute("estacionamientoId") == null) {
            mensaje = "Guardia sin estacionamiento asignado";
            redirectAttributes.addFlashAttribute("resultado", exito ? "exito" : "error");
            redirectAttributes.addFlashAttribute("mensaje", mensaje);
            return "redirect:/ieManual";
        }
        String patente = patente1.replaceAll("[^a-zA-Z0-9]", "");
        patente = patente.toUpperCase();
        EstacionamientoDTO est1 = estacionamientoService.obtener((Long) session.getAttribute("estacionamientoId"));
        AccionEstacionamientoResultDTO accionResult = registroestacionamientoService.procesarAccion(patente, accion, est1, 0);
        System.out.println(""+accionResult.isResultado());
        redirectAttributes.addFlashAttribute("resultado", accionResult.isResultado());
        System.out.println(""+accionResult.getMensaje());
        redirectAttributes.addFlashAttribute("mensaje", accionResult.getMensaje());

        return "redirect:/ieManual";
    }

}

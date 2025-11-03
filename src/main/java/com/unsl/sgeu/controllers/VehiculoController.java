package com.unsl.sgeu.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import com.unsl.sgeu.dto.EstacionamientoDTO;
import com.unsl.sgeu.dto.RegistroVehiculoFormDTO;
import com.unsl.sgeu.dto.VehiculoDTO;
import com.unsl.sgeu.dto.PersonaDTO;
import com.unsl.sgeu.mappers.PersonaMapper;
import com.unsl.sgeu.mappers.VehiculoMapper;
import com.unsl.sgeu.models.Estacionamiento;
import com.unsl.sgeu.models.Persona;
import com.unsl.sgeu.models.Vehiculo;
import com.unsl.sgeu.services.*;
import java.util.List;

@SessionAttributes
@Controller
// @RequestMapping("/vehiculos")
public class VehiculoController {
    @Autowired
    private RegistroEstacionamientoService registroestacionamientoService;

    @Autowired
    private EstacionamientoService estacionamientoService;

    @Autowired
    private VehiculoService vehiculoService;

    @Autowired
    private PersonaService personaService;

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private PersonaMapper personaMapper;
    @Autowired
    private VehiculoMapper vehiculoMapper;

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

            // String rol = (String) session.getAttribute("rol");
            // boolean esAdministrador = "ADMINISTRADOR".equals(rol) ||
            // "Administrador".equals(rol);
            /*
             * probando control
             * if (!esAdministrador) {
             * redirectAttributes.addFlashAttribute("error",
             * "Solo los administradores pueden eliminar vehículos con historial");
             * return "redirect:/";
             * }
             */
         

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
            // Accesos ahora via form.getVehiculo() y form.getPersona()
            String patente = form.getVehiculo().getPatente();
            if (vehiculoService.existePatente(patente)) {
                redirectAttributes.addFlashAttribute("error",
                        "Ya existe un vehículo con la patente " + patente);
                redirectAttributes.addFlashAttribute("vehiculoForm", form);
                return "redirect:/vehiculos/agregar";
            }

            Integer categoriaId = mapearCategoriaAId(form.getPersona().getCategoriaNombre());
            if (categoriaId == null) {
                redirectAttributes.addFlashAttribute("error", "Categoría inválida");
                return "redirect:/vehiculos/agregar";
            }

            Integer vehiculoTipoId = mapearTipoAId(form.getVehiculo().getTipoNombre());
            if (vehiculoTipoId == null) {
                redirectAttributes.addFlashAttribute("error", "Tipo de vehículo inválido");
                return "redirect:/vehiculos/agregar";
            }

            Long dni = form.getPersona().getDni();
            Persona persona = personaService.existePersona(dni)
                    ? personaService.buscarPorDni(dni)
                    : new Persona();

            persona.setDni(dni);
            persona.setNombre(form.getPersona().getNombre());
            persona.setTelefono(form.getPersona().getTelefono());
            persona.setEmail(form.getPersona().getEmail());
            persona.setIdCategoria(categoriaId);
            persona.setCategoria(form.getPersona().getCategoriaNombre());

            personaService.guardarPersona(persona);

            // Vehiculo
            String codigoQr = vehiculoService.generarCodigoQR(patente);

            Vehiculo vehiculo = new Vehiculo();
            vehiculo.setPatente(patente);
            vehiculo.setCodigoQr(codigoQr);
            vehiculo.setModelo(form.getVehiculo().getModelo());
            vehiculo.setColor(form.getVehiculo().getColor());
            vehiculo.setIdVehiculoTipo(vehiculoTipoId);
            vehiculo.setDniDuenio(dni);
            vehiculo.setTipo(form.getVehiculo().getTipoNombre());

            Vehiculo vehiculoGuardado = vehiculoService.guardarVehiculo(vehiculo);

            try {
                String rutaArchivoQR = qrCodeService.generarImagenQR(
                        vehiculoGuardado.getCodigoQr(),
                        vehiculoGuardado.getPatente());

                    } catch (Exception e) {
                System.err.println("Error guardando archivo QR (no crítico): " + e.getMessage());
            }

            String rutaImagenQR = "/qr-codes/qr_" + vehiculoGuardado.getPatente() + ".png";

            redirectAttributes.addFlashAttribute("success", "Vehículo agregado exitosamente");
            redirectAttributes.addFlashAttribute("codigoQR", vehiculoGuardado.getCodigoQr());
            redirectAttributes.addFlashAttribute("imagenQR", rutaImagenQR);
            redirectAttributes.addFlashAttribute("patente", patente);
            redirectAttributes.addFlashAttribute("vehiculoInfo", crearInfoVehiculo(form, persona));

            // impresión de QR (atributos para la vista)
            redirectAttributes.addFlashAttribute("modelo", form.getVehiculo().getModelo());
            redirectAttributes.addFlashAttribute("color", form.getVehiculo().getColor());
            redirectAttributes.addFlashAttribute("tipo", form.getVehiculo().getTipoNombre());
            redirectAttributes.addFlashAttribute("dni", dni);
            redirectAttributes.addFlashAttribute("nombre", form.getPersona().getNombre());

            return "redirect:/vehiculos/agregar";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error",
                    "Error al agregar el vehículo: " + e.getMessage());
            redirectAttributes.addFlashAttribute("vehiculoForm", form);
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

    private String crearInfoVehiculo(RegistroVehiculoFormDTO form, Persona persona) {
        // Extraer sub-objetos
        var personaForm = form.getPersona();
        var vehiculoForm = form.getVehiculo();

        String nombrePropietario = personaForm.getNombre() != null ? personaForm.getNombre() : "Sin nombre";
        String categoriaNombre = personaForm.getCategoriaNombre() != null
                ? mapearCategoriaNombreATexto(personaForm.getCategoriaNombre())
                : "Sin categoría";

        return String.format(
                "Propietario: %s (DNI: %s) | Patente: %s | Modelo: %s %s | Categoría: %s",
                nombrePropietario,
                personaForm.getDni(),
                vehiculoForm.getPatente(),
                nullToDash(vehiculoForm.getModelo()),
                nullToDash(vehiculoForm.getColor()),
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

            // Componer el DTO principal
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

            if (bindingResult.hasErrors()) {
                bindingResult.getAllErrors().forEach(error -> System.out.println(" - " + error.getDefaultMessage()));

                model.addAttribute("vehiculoForm", form);
                model.addAttribute("esEdicion", true);
                model.addAttribute("patenteOriginal", patenteOriginal);
                model.addAttribute("esAdministrador", esAdministrador);
                return "editarvehiculo";
            }

            var personaForm = form.getPersona();
            var vehiculoForm = form.getVehiculo();

            Vehiculo vehiculoExistente = vehiculoService.buscarPorPatente(patenteOriginal);
            if (vehiculoExistente == null) {
                redirectAttributes.addFlashAttribute("error", "El vehículo original no existe");
                return "redirect:/";
            }

            // Si se intenta cambiar la patente, validar duplicado
            if (!patenteOriginal.equalsIgnoreCase(vehiculoForm.getPatente())) {
                if (vehiculoService.existePatente(vehiculoForm.getPatente())) {
                    model.addAttribute("error", "Ya existe un vehículo con la patente " + vehiculoForm.getPatente());
                    model.addAttribute("vehiculoForm", form);
                    model.addAttribute("esEdicion", true);
                    model.addAttribute("patenteOriginal", patenteOriginal);
                    model.addAttribute("esAdministrador", esAdministrador);
                    return "editarvehiculo";
                }
            }

            Persona personaExistente = personaService.buscarPorDni(personaForm.getDni());
            if (personaExistente == null) {
                model.addAttribute("error", "No existe una persona con el DNI " + personaForm.getDni());
                model.addAttribute("vehiculoForm", form);
                model.addAttribute("esEdicion", true);
                model.addAttribute("patenteOriginal", patenteOriginal);
                model.addAttribute("esAdministrador", esAdministrador);
                return "editarvehiculo";
            }

            Integer categoriaId = mapearCategoriaAId(personaForm.getCategoriaNombre());
            Integer tipoId = mapearTipoAId(vehiculoForm.getTipoNombre());

            if (categoriaId == null) {
                model.addAttribute("error", "Categoría inválida: " + personaForm.getCategoriaNombre());
                model.addAttribute("vehiculoForm", form);
                model.addAttribute("esEdicion", true);
                model.addAttribute("patenteOriginal", patenteOriginal);
                model.addAttribute("esAdministrador", esAdministrador);
                return "editarvehiculo";
            }

            if (tipoId == null) {
                model.addAttribute("error", "Tipo de vehículo inválido: " + vehiculoForm.getTipoNombre());
                model.addAttribute("vehiculoForm", form);
                model.addAttribute("esEdicion", true);
                model.addAttribute("patenteOriginal", patenteOriginal);
                model.addAttribute("esAdministrador", esAdministrador);
                return "editarvehiculo";
            }

            boolean actualizado = vehiculoService.actualizarVehiculo(
                    patenteOriginal,
                    form,
                    categoriaId,
                    tipoId);

            if (actualizado) {
                redirectAttributes.addFlashAttribute("success",
                        "Vehículo " + vehiculoForm.getPatente() + " actualizado exitosamente");
                return "redirect:/";
            } else {
                model.addAttribute("error", "Error al actualizar el vehículo en la base de datos");
                model.addAttribute("vehiculoForm", form);
                model.addAttribute("esEdicion", true);
                model.addAttribute("patenteOriginal", patenteOriginal);
                model.addAttribute("esAdministrador", esAdministrador);
                return "editarvehiculo";
            }

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

        if ("ingreso".equalsIgnoreCase(accion)) {
            if (registroestacionamientoService.estacionamientoIsFull(est1)) {
                mensaje = "Estacionamiento lleno";
                redirectAttributes.addFlashAttribute("resultado", exito ? "exito" : "error");
                redirectAttributes.addFlashAttribute("mensaje", mensaje);
                return "redirect:/ieManual";
            }

            else if (!vehiculoService.existePatente(patente)) {
                mensaje = "La patente '" + patente + "' no está registrada en el sistema.";
            } else if (!registroestacionamientoService.esPar(patente, est1)) {
                mensaje = "El vehículo con patente '" + patente + "' ya se encuentra dentro del estacionamiento.";
            } else {

                registroestacionamientoService.registrarEntrada(patente, est1, 0);
                mensaje = "Ingreso de '" + patente + "' registrado con éxito.";
                exito = true;
            }
        } else if ("egreso".equalsIgnoreCase(accion)) {

            if (registroestacionamientoService.esPar(patente, est1)) {
                mensaje = "El vehículo con patente '" + patente + "' no registra una entrada previa.";
            } else {

                registroestacionamientoService.registrarSalida(patente, est1, 0);
                mensaje = "Salida de '" + patente + "' registrada con éxito.";
                exito = true;
            }
        } else {
            mensaje = "Acción no válida.";
        }

        redirectAttributes.addFlashAttribute("resultado", exito ? "exito" : "error");
        redirectAttributes.addFlashAttribute("mensaje", mensaje);

        return "redirect:/ieManual";
    }

}

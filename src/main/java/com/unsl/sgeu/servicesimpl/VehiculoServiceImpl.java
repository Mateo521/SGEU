package com.unsl.sgeu.servicesimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.unsl.sgeu.dto.EstacionamientoDTO;
import com.unsl.sgeu.dto.RegistroVehiculoFormDTO;
import com.unsl.sgeu.dto.VehiculoRegistroResultadoDTO;
import com.unsl.sgeu.models.Persona;
import com.unsl.sgeu.models.Vehiculo;
import com.unsl.sgeu.repositories.VehiculoRepository;
import com.unsl.sgeu.services.EstacionamientoService;
import com.unsl.sgeu.services.EstadoVehiculo;
import com.unsl.sgeu.services.PersonaService;
import com.unsl.sgeu.services.QRCodeService;
import com.unsl.sgeu.services.RegistroEstacionamientoService;
import com.unsl.sgeu.services.ResultadoEliminacion;
import com.unsl.sgeu.services.VehiculoOperacionException;
import com.unsl.sgeu.services.VehiculoService;

import java.util.UUID;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import com.unsl.sgeu.dto.VehiculoQRResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Service
public class VehiculoServiceImpl implements VehiculoService {

    @Autowired
    private VehiculoRepository vehiculoRepo;

    @Autowired
    private EstacionamientoService estacionamientoService;

     @Autowired
     @Lazy   
    private RegistroEstacionamientoService registroEstacionamientoService;

    @Autowired
    private PersonaService personaService;

    @Autowired
    private QRCodeService qrCodeService;

@Transactional
     public VehiculoRegistroResultadoDTO registrarNuevoVehiculo(RegistroVehiculoFormDTO form) {
        try {
            var personaForm = form.getPersona();
            var vehiculoForm = form.getVehiculo();

            String patente = vehiculoForm.getPatente();
            if (existePatente(patente)) {
                throw new VehiculoOperacionException(
                    "Ya existe un vehículo con la patente " + patente
                );
            }

            Integer categoriaId = mapearCategoriaAId(personaForm.getCategoriaNombre());
            if (categoriaId == null) {
                throw new VehiculoOperacionException("Categoría inválida");
            }

            Integer tipoId = mapearTipoAId(vehiculoForm.getTipoNombre());
            if (tipoId == null) {
                throw new VehiculoOperacionException("Tipo de vehículo inválido");
            }

            Long dni = personaForm.getDni();
            Persona persona = personaService.existePersona(dni)
                    ? personaService.buscarPorDni(dni)
                    : new Persona();

            persona.setDni(dni);
            persona.setNombre(personaForm.getNombre());
            persona.setTelefono(personaForm.getTelefono());
            persona.setEmail(personaForm.getEmail());
            persona.setIdCategoria(categoriaId);
            persona.setCategoria(personaForm.getCategoriaNombre());
            personaService.guardarPersona(persona);

            String codigoQr = generarCodigoQR(patente);

            Vehiculo vehiculo = new Vehiculo();
            vehiculo.setPatente(patente);
            vehiculo.setCodigoQr(codigoQr);
            vehiculo.setModelo(vehiculoForm.getModelo());
            vehiculo.setColor(vehiculoForm.getColor());
            vehiculo.setIdVehiculoTipo(tipoId);
            vehiculo.setDniDuenio(dni);
            vehiculo.setTipo(vehiculoForm.getTipoNombre());

            Vehiculo vehiculoGuardado = guardarVehiculo(vehiculo);

            String rutaArchivoQR = null;
            try {
                rutaArchivoQR = qrCodeService.generarImagenQR(
                        vehiculoGuardado.getCodigoQr(),
                        vehiculoGuardado.getPatente());
            } catch (Exception e) {
                System.err.println("Error guardando archivo QR (no crítico): " + e.getMessage());
            }

           
            String rutaImagenQR = (rutaArchivoQR != null)
                    ? rutaArchivoQR
                    : "/sgeu/qr-codes/qr_" + vehiculoGuardado.getPatente() + ".png";

            String vehiculoInfo = crearInfoVehiculo(form, persona);

            return new VehiculoRegistroResultadoDTO(
                    vehiculoGuardado.getPatente(),
                    vehiculoGuardado.getCodigoQr(),
                    rutaImagenQR,
                    vehiculoInfo
            );

        } catch (VehiculoOperacionException ex) {
            throw ex;  
        } catch (Exception e) {
            e.printStackTrace();
            throw new VehiculoOperacionException("Error al registrar el vehículo: " + e.getMessage(), e);
        }
    }


 public VehiculoQRResponseDTO obtenerDatosQR(String codigoQR, EstacionamientoDTO estacionamiento) {
        Vehiculo vehiculo = buscarPorCodigoQr(codigoQR);
        
        if (vehiculo == null) {
            throw new VehiculoOperacionException("Vehículo con código QR '" + codigoQR + "' no encontrado");
        }

        VehiculoQRResponseDTO response = new VehiculoQRResponseDTO();
        
        // Datos básicos del vehículo
        response.setPatente(vehiculo.getPatente());
        response.setModelo(vehiculo.getModelo() != null ? vehiculo.getModelo() : "Sin modelo");
        response.setColor(vehiculo.getColor() != null ? vehiculo.getColor() : "Sin color");
        response.setTipo(vehiculo.getTipo() != null ? vehiculo.getTipo() : "Sin tipo");
        response.setDniDuenio(vehiculo.getDniDuenio());

        // Estado en estacionamiento
        boolean estaAdentro = !registroEstacionamientoService.esPar(vehiculo.getPatente(), estacionamiento);
        response.setEstaAdentro(estaAdentro);

        // Determinar acción disponible
        if (!estaAdentro) {
            if (registroEstacionamientoService.estacionamientoIsFull(estacionamiento)) {
                response.setAccionDisponible("estacionamiento lleno");
                response.setMensajeAccion("El vehículo no puede ingresar");
            } else {
                response.setAccionDisponible("Entrada");
                response.setMensajeAccion("El vehículo está fuera del estacionamiento");
            }
        } else {
            response.setAccionDisponible("Salida");
            response.setMensajeAccion("El vehículo está dentro del estacionamiento");
        }

       
        if (vehiculo.getDniDuenio() != null) {
            Persona persona = personaService.buscarPorDni(vehiculo.getDniDuenio());
            if (persona != null) {
                response.setNombreDuenio(persona.getNombre());
                response.setCategoriaDuenio(persona.getCategoria());
                response.setTelefonoDuenio(persona.getTelefono());
                response.setEmailDuenio(persona.getEmail());
            }
        }

        return response;
    }


@Transactional
     public void actualizarVehiculo(String patenteOriginal, RegistroVehiculoFormDTO form) {
        var personaForm = form.getPersona();
        var vehiculoForm = form.getVehiculo();

        Integer categoriaId = mapearCategoriaAId(personaForm.getCategoriaNombre());
        if (categoriaId == null) {
            throw new VehiculoOperacionException(
                "Categoría inválida: " + personaForm.getCategoriaNombre()
            );
        }

        Integer tipoId = mapearTipoAId(vehiculoForm.getTipoNombre());
        if (tipoId == null) {
            throw new VehiculoOperacionException(
                "Tipo de vehículo inválido: " + vehiculoForm.getTipoNombre()
            );
        }

         if (!patenteOriginal.equalsIgnoreCase(vehiculoForm.getPatente())) {
            if (existePatente(vehiculoForm.getPatente())) {
                throw new VehiculoOperacionException(
                    "Ya existe un vehículo con la patente " + vehiculoForm.getPatente()
                );
            }
         }

         boolean ok = actualizarVehiculo(patenteOriginal, form, categoriaId, tipoId);
        if (!ok) {
            throw new VehiculoOperacionException("Error al actualizar el vehículo en la base de datos");
        }
       // return true;
    }

 
    private Integer mapearCategoriaAId(String categoria) {
        if (categoria == null) return null;
        switch (categoria.toLowerCase()) {
            case "docente":     return 1;
            case "no_docente":  return 2;
            case "estudiante":  return 3;
            case "visitante":   return 4;
            default:            return null;
        }
    }

    private Integer mapearTipoAId(String tipo) {
        if (tipo == null) return null;
        switch (tipo.toLowerCase()) {
            case "auto": return 1;
            case "moto": return 2;
            default:     return null;
        }
    }

    private String mapearCategoriaNombreATexto(String categoria) {
        if (categoria == null) return "Sin categoría";
        switch (categoria.toLowerCase()) {
            case "docente":     return "Docente";
            case "no_docente":  return "No Docente";
            case "estudiante":  return "Estudiante";
            case "visitante":   return "Visitante";
            default:            return categoria;
        }
    }

    private String nullToDash(String s) {
        return (s == null || s.isBlank()) ? "—" : s;
    }

    private String crearInfoVehiculo(RegistroVehiculoFormDTO form, Persona persona) {
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


    public List<Vehiculo> obtenerTodos() {
        return vehiculoRepo.findAll();
    }

    public boolean existePatente(String patente) {
        return vehiculoRepo.findByPatente(patente) != null;
    }

    public Vehiculo guardarVehiculo(Vehiculo vehiculo) {
        return vehiculoRepo.save(vehiculo);
    }
    

    public Vehiculo buscarPorPatente(String patente) {
        return vehiculoRepo.findByPatente(patente);
    }

    public boolean actualizarVehiculo(String patenteOriginal, RegistroVehiculoFormDTO form,
        Integer categoriaId, Integer tipoId) {
    try {
        Vehiculo vehiculo = vehiculoRepo.findByPatente(patenteOriginal);
        if (vehiculo == null) {
        
            return false;
        }

   
        Persona persona = personaService.buscarPorDni(vehiculo.getDniDuenio());
        if (persona != null) {
            persona.setNombre(form.getPersona().getNombre());
            persona.setTelefono(form.getPersona().getTelefono());
            persona.setEmail(form.getPersona().getEmail());
            persona.setIdCategoria(categoriaId);
            persona.setCategoria(form.getPersona().getCategoriaNombre());
            personaService.guardarPersona(persona);
        }

     
        vehiculo.setModelo(form.getVehiculo().getModelo());
        vehiculo.setColor(form.getVehiculo().getColor());
        vehiculo.setIdVehiculoTipo(tipoId);
        vehiculo.setTipo(form.getVehiculo().getTipoNombre());

        vehiculoRepo.save(vehiculo);
        return true;

    } catch (Exception e) {
        System.err.println("Error en actualizarVehiculo: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}


    public void probarEstadoVehiculo(String patente) {
        EstadoVehiculo estado = registroEstacionamientoService.obtenerEstadoActualVehiculo(patente);

    

        
    }

    public List<Vehiculo> buscarVehiculosPorPatente(String patente) {
        if (patente == null || patente.trim().isEmpty()) {
            return obtenerTodos();
        }
        return vehiculoRepo.findByPatenteContainingIgnoreCase(patente.trim());
    }

    public List<Vehiculo> obtenerPorEstacionamiento(Long idEstacionamiento) {
        return vehiculoRepo.findVehiculosByEstacionamiento(idEstacionamiento);
    }

    public List<Vehiculo> buscarEnEstacionamiento(Long idEstacionamiento, String patente) {
        return vehiculoRepo.findVehiculosByEstacionamientoAndPatente(idEstacionamiento, patente);
    }

    public List<Vehiculo> obtenerPorEstacionamientos(List<Long> idsEstacionamientos) {
        if (idsEstacionamientos == null || idsEstacionamientos.isEmpty()) {
            return List.of();
        }
        return vehiculoRepo.findVehiculosByEstacionamientos(idsEstacionamientos);
    }

    public List<Vehiculo> buscarEnEstacionamientos(List<Long> idsEstacionamientos, String patente) {
        if (idsEstacionamientos == null || idsEstacionamientos.isEmpty()) {
            return List.of();
        }
        return vehiculoRepo.findVehiculosByEstacionamientosAndPatente(idsEstacionamientos, patente);
    }

    public List<Vehiculo> obtenerPorGuardia(Long guardiaId) {
        try {
           

            List<Long> idsEstacionamientos = estacionamientoService.obtenerIdsPorEmpleado(guardiaId);

 
            if (idsEstacionamientos.isEmpty()) {
                 return List.of();
            }

 
            List<Vehiculo> vehiculos = obtenerPorEstacionamientos(idsEstacionamientos);

             return vehiculos;

        } catch (Exception e) {
            System.err.println(" Error al obtener vehículos por guardia: " + e.getMessage());
            e.printStackTrace();
             return obtenerTodos();
        }
    }

    public List<Vehiculo> buscarPorPatenteYGuardia(String patente, Long guardiaId) {
        try {
 
            List<Long> idsEstacionamientos = estacionamientoService.obtenerIdsPorEmpleado(guardiaId);

            if (idsEstacionamientos.isEmpty()) {
                 return List.of();
            }

 
            List<Vehiculo> vehiculos = buscarEnEstacionamientos(idsEstacionamientos, patente);

             return vehiculos;

        } catch (Exception e) {
            System.err.println(" Error al buscar por patente y guardia: " + e.getMessage());
            e.printStackTrace();
             return buscarVehiculosPorPatente(patente);
        }
    }
@Transactional
      public ResultadoEliminacion eliminarVehiculo(String patente) {
     
    try {
        
        probarEstadoVehiculo(patente);
        
       
        Vehiculo vehiculo = vehiculoRepo.findByPatente(patente);
        if (vehiculo == null) {
            String mensaje = " El vehículo con patente " + patente + " no existe";
             return new ResultadoEliminacion(false, mensaje);
        }

        EstadoVehiculo estado = registroEstacionamientoService.obtenerEstadoActualVehiculo(patente);
        
     
        
     
        if (estado.isEstaEstacionado()) {
            String mensaje = String.format(
                "No se puede eliminar el vehículo %s porque se encuentra actualmente en el estacionamiento: %s. " +
                "Debe registrar la salida primero.", 
                patente, estado.getNombreEstacionamiento()
            );
          
            return new ResultadoEliminacion(false, mensaje);
        }

     
        
      
        vehiculoRepo.deleteById(patente);
        
        String mensajeExito = "Vehículo " + patente + " eliminado exitosamente";
      
        return new ResultadoEliminacion(true, mensajeExito);

    } catch (org.springframework.dao.DataIntegrityViolationException e) {
      
        
        System.err.println(" Error de integridad al eliminar vehículo " + patente + ": " + e.getMessage());
        
        String mensaje = String.format(
            " No se puede eliminar el vehículo %s porque tiene registros de estacionamiento asociados. " +
            "Para eliminarlo completamente, use la opción 'Eliminar con historial'.", 
            patente
        );
        
        return new ResultadoEliminacion(false, mensaje);
        
    } catch (Exception e) {
      
        
        System.err.println(" Error inesperado al eliminar vehículo " + patente + ": " + e.getMessage());
        e.printStackTrace();
        
        String mensaje = " Error interno al eliminar el vehículo. Intente nuevamente.";
        return new ResultadoEliminacion(false, mensaje);
    }
}

@Transactional
    public ResultadoEliminacion eliminarVehiculoConHistorial(String patente) {
        try {
            Vehiculo vehiculo = vehiculoRepo.findByPatente(patente);
            if (vehiculo == null) {
                return new ResultadoEliminacion(false, "El vehículo con patente " + patente + " no existe");
            }


            registroEstacionamientoService.eliminarRegistrosPorPatente(patente);
            vehiculoRepo.deleteById(patente);

            return new ResultadoEliminacion(true,
                    "Vehículo " + patente + " y todo su historial eliminados completamente");

        } catch (Exception e) {
            System.err.println("Error al eliminar vehículo con historial: " + e.getMessage());
            e.printStackTrace();
            return new ResultadoEliminacion(false,
                    " Error crítico al eliminar con historial: " + e.getMessage());
        }
    }

    public String generarCodigoQR(String patente) {
        return "qr-" + generarCodigoQrUnico(patente);
    }

    public List<Vehiculo> obtenerTodosVehiculosPorGuardia(Long guardiaId) {
         return obtenerTodos(); 
    }

    public String obtenerEstacionamientoOrigenVehiculo(String patente, Long guardiaId) {
        try {
            List<Long> idsEstacionamientos = estacionamientoService.obtenerIdsPorEmpleado(guardiaId);
            return vehiculoRepo.findEstacionamientoOrigenByPatente(patente, idsEstacionamientos);
        } catch (Exception e) {
            System.err.println("Error al obtener estacionamiento origen: " + e.getMessage());
            return "Desconocido";
        }
    }

    public String generarCodigoQrUnico(String patente) {
        UUID uuid = UUID.nameUUIDFromBytes(patente.getBytes());
        String codigoQr = uuid.toString();
        codigoQr = hashSha256(codigoQr);
        return codigoQr;
    }

    private String hashSha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error en el algoritmo de hash", e);
        }
    }

    public Vehiculo buscarPorCodigoQr(String codigoQr) {
        return vehiculoRepo.findByCodigoQr(codigoQr);
    }

    public long contarVehiculosEnEstacionamiento(Long idEstacionamiento) {
        return obtenerPorEstacionamiento(idEstacionamiento).size();
    }

    public long contarVehiculosEnEstacionamientos(List<Long> idsEstacionamientos) {
        return obtenerPorEstacionamientos(idsEstacionamientos).size();
    }









 
public Page<Vehiculo> obtenerTodosPaginado(Pageable pageable) {

    return vehiculoRepo.findAll(pageable);
}

public Page<Vehiculo> buscarVehiculosPorPatentePaginado(String patente, Pageable pageable) {
    return vehiculoRepo.findByPatenteContainingIgnoreCase(patente, pageable);
}

public Page<Vehiculo> buscarPorPatenteYGuardiaPaginado(String patente, Long guardiaId, Pageable pageable) {
   
    return vehiculoRepo.findByPatenteContainingIgnoreCase(patente, pageable);
}

}

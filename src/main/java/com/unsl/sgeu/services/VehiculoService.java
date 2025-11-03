package com.unsl.sgeu.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unsl.sgeu.dto.RegistroVehiculoFormDTO;
import com.unsl.sgeu.models.Persona;
import com.unsl.sgeu.models.Vehiculo;
import com.unsl.sgeu.repositories.VehiculoRepository;
import java.util.UUID;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Service
public class VehiculoService {

    @Autowired
    private VehiculoRepository vehiculoRepo;

    @Autowired
    private EstacionamientoService estacionamientoService;

    @Autowired
    private RegistroEstacionamientoService registroEstacionamientoService;

    @Autowired
    private PersonaService personaService;

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

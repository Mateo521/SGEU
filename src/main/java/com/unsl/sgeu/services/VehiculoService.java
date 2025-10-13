package com.unsl.sgeu.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unsl.sgeu.dto.VehiculoFormDTO;
import com.unsl.sgeu.models.Persona;
import com.unsl.sgeu.models.Vehiculo;
import com.unsl.sgeu.repositories.VehiculoRepository;
import java.util.UUID;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

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

    public boolean actualizarVehiculo(String patenteOriginal, VehiculoFormDTO form,
            Integer categoriaId, Integer tipoId) {
        try {
            // Buscar vehículo actual
            Vehiculo vehiculo = vehiculoRepo.findByPatente(patenteOriginal);
            if (vehiculo == null)
                return false;

            // Actualizar persona
            Persona persona = personaService.buscarPorDni(vehiculo.getDniDuenio());
            if (persona != null) {
                persona.setNombre(form.getNombre());
                persona.setTelefono(form.getTelefono());
                persona.setEmail(form.getEmail());
                persona.setIdCategoria(categoriaId);
                persona.setCategoria(form.getCategoriaNombre());
                personaService.guardarPersona(persona);
            }

            // Actualizar vehículo
            vehiculo.setPatente(form.getPatente());
            vehiculo.setModelo(form.getModelo());
            vehiculo.setColor(form.getColor());
            vehiculo.setIdVehiculoTipo(tipoId);
            vehiculo.setTipo(form.getTipoNombre());

            vehiculoRepo.save(vehiculo);
            return true;

        } catch (Exception e) {
            System.err.println("Error en actualizarVehiculo: " + e.getMessage());
            return false;
        }
    }

    public void probarEstadoVehiculo(String patente) {
        EstadoVehiculo estado = registroEstacionamientoService.obtenerEstadoActualVehiculo(patente);

     
        System.out.println("   - Patente: " + estado.getPatente());
        System.out.println("   - Tiene registros: " + estado.isTieneRegistros());
        System.out.println("   - Está estacionado: " + estado.isEstaEstacionado());
        System.out.println("   - ID Estacionamiento: " + estado.getIdEstacionamiento());
        System.out.println("   - Nombre estacionamiento: " + estado.getNombreEstacionamiento());
        System.out.println("   - Cantidad registros: " + estado.getCantidadRegistros());

        if (estado.getUltimoRegistro() != null) {
            System.out.println("   - Último tipo: " + estado.getUltimoRegistro().getTipo());
            System.out.println("   - Última fecha: " + estado.getUltimoRegistro().getFechaHora());
        }
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
            System.out.println(" Obteniendo vehículos para guardia ID: " + guardiaId);
            System.out.println(
                    " Tipo de guardiaId: " + (guardiaId != null ? guardiaId.getClass().getSimpleName() : "null"));
            System.out.println(" Valor de guardiaId: " + guardiaId);
            System.out.println(" Llamando a estacionamientoService.obtenerIdsPorEmpleado(" + guardiaId + ")");

            List<Long> idsEstacionamientos = estacionamientoService.obtenerIdsPorEmpleado(guardiaId);

            System.out.println(" IDs devueltos por EstacionamientoService: " + idsEstacionamientos);

            if (idsEstacionamientos.isEmpty()) {
                System.out.println(" Guardia sin estacionamientos asignados - mostrando lista vacía");
                return List.of();
            }

            System.out.println(" Estacionamientos del guardia: " + idsEstacionamientos);

            List<Vehiculo> vehiculos = obtenerPorEstacionamientos(idsEstacionamientos);

            System.out.println(" Vehículos encontrados: " + vehiculos.size());
            return vehiculos;

        } catch (Exception e) {
            System.err.println(" Error al obtener vehículos por guardia: " + e.getMessage());
            e.printStackTrace();
            System.out.println(" FALLBACK: Mostrando todos los vehículos por error");
            return obtenerTodos();
        }
    }

    public List<Vehiculo> buscarPorPatenteYGuardia(String patente, Long guardiaId) {
        try {
            System.out.println(" Buscando patente '" + patente + "' para guardia ID: " + guardiaId);

            List<Long> idsEstacionamientos = estacionamientoService.obtenerIdsPorEmpleado(guardiaId);

            if (idsEstacionamientos.isEmpty()) {
                System.out.println(" Guardia sin estacionamientos asignados - búsqueda vacía");
                return List.of();
            }

            System.out.println(" Buscando en estacionamientos: " + idsEstacionamientos);

            List<Vehiculo> vehiculos = buscarEnEstacionamientos(idsEstacionamientos, patente);

            System.out.println(" Vehículos encontrados: " + vehiculos.size());
            return vehiculos;

        } catch (Exception e) {
            System.err.println(" Error al buscar por patente y guardia: " + e.getMessage());
            e.printStackTrace();
            System.out.println(" FALLBACK: Búsqueda sin filtro por error");
            return buscarVehiculosPorPatente(patente);
        }
    }

      public ResultadoEliminacion eliminarVehiculo(String patente) {
    System.out.println(" Iniciando eliminación del vehículo: " + patente);
    
    try {
        
        probarEstadoVehiculo(patente);
        
       
        Vehiculo vehiculo = vehiculoRepo.findByPatente(patente);
        if (vehiculo == null) {
            String mensaje = " El vehículo con patente " + patente + " no existe";
            System.out.println(mensaje);
            return new ResultadoEliminacion(false, mensaje);
        }

        EstadoVehiculo estado = registroEstacionamientoService.obtenerEstadoActualVehiculo(patente);
        
        System.out.println(" Estado obtenido para eliminación:");
        System.out.println("   - Tiene registros: " + estado.isTieneRegistros());
        System.out.println("   - Está estacionado: " + estado.isEstaEstacionado());
        System.out.println("   - Nombre estacionamiento: " + estado.getNombreEstacionamiento());
        
     
        if (estado.isEstaEstacionado()) {
            String mensaje = String.format(
                "No se puede eliminar el vehículo %s porque se encuentra actualmente en el estacionamiento: %s. " +
                "Debe registrar la salida primero.", 
                patente, estado.getNombreEstacionamiento()
            );
          
            return new ResultadoEliminacion(false, mensaje);
        }

    
        System.out.println(" Vehículo " + patente + " no está en ningún estacionamiento.");
        System.out.println("Procediendo a eliminar...");
        
      
        vehiculoRepo.deleteById(patente);
        
        String mensajeExito = "Vehículo " + patente + " eliminado exitosamente";
        System.out.println(mensajeExito);
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

            System.out.println(" Eliminando registros de estacionamiento para patente: " + patente);

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
}

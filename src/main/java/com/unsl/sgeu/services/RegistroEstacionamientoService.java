package com.unsl.sgeu.services;

import com.unsl.sgeu.repositories.RegistroEstacionamientoRepository;
import com.unsl.sgeu.repositories.VehiculoRepository;
import com.unsl.sgeu.services.VehiculoService;
import com.unsl.sgeu.dto.EstacionamientoDTO;
import com.unsl.sgeu.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.transaction.annotation.Propagation;
import com.unsl.sgeu.dto.AccionEstacionamientoResultDTO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SessionAttributes
@Service
public class RegistroEstacionamientoService {

    @Autowired
    private RegistroEstacionamientoRepository registroRepo;
    @Autowired
    private VehiculoRepository vehiculoRepo;

    @Autowired
    private VehiculoService vehiculoService;

    @Autowired
    private EstacionamientoService estacionamientoService;

    public RegistroEstacionamiento registrarEntrada(String patente, EstacionamientoDTO est, int modo) {
        Vehiculo vehiculo = vehiculoRepo.findByPatente(patente);

        RegistroEstacionamiento registro = new RegistroEstacionamiento();
        registro.setPatente(vehiculo.getPatente());
        registro.setFechaHora(LocalDateTime.now());
         registro.setTipo("ENTRADA");
         registro.setIdEstacionamiento(est.getId());
        if (modo == 0) {
            registro.setModo("MANUAL");
        } else {
            registro.setModo("QR");
        }
        return registroRepo.save(registro);
    }

    public RegistroEstacionamiento registrarSalida(String patente, EstacionamientoDTO est, int modo) {
        Vehiculo vehiculo = vehiculoRepo.findByPatente(patente);
        RegistroEstacionamiento registro = new RegistroEstacionamiento();
        registro.setPatente(vehiculo.getPatente());
        registro.setFechaHora(LocalDateTime.now());
         registro.setTipo("SALIDA");
        registro.setIdEstacionamiento(est.getId());
        if (modo == 0) {
            registro.setModo("MANUAL");
        } else {
            registro.setModo("QR");
        }

        return registroRepo.save(registro);
    }


    public boolean esPar(String patente, EstacionamientoDTO est) {
        
        long cantidad = registroRepo.countByPatenteAndIdEstacionamiento(patente, est.getId());

        return cantidad % 2 == 0;
    }

    public List<String> obtenerPatentesAdentroMasDeCuatroHoras(EstacionamientoDTO est) {
         return registroRepo.findPatentesAdentroMasDeCuatroHoras(est.getId());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void eliminarRegistrosPorPatente(String patente) {
        try {
     

            long cantidadAntes = registroRepo.countByPatente(patente);
 
            if (cantidadAntes > 0) {
                int eliminados = registroRepo.deleteByPatente(patente);
 
                // Verificar que se eliminaronn
                long cantidadDespues = registroRepo.countByPatente(patente);
 
                if (cantidadDespues > 0) {
                    throw new RuntimeException(
                            "No se pudieron eliminar todos los registros. Restantes: " + cantidadDespues);
                }
            }

 
        } catch (Exception e) {
            System.err.println("Error al eliminar registros: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar registros de estacionamiento: " + e.getMessage(), e);
        }
    }

      public AccionEstacionamientoResultDTO procesarAccion(String patente, String accion, EstacionamientoDTO estacionamiento) {
        
        boolean resultado = false;
        String mensaje = "";

        if ("Entrada".equals(accion) && vehiculoService.existePatente(patente) && esPar(patente, estacionamiento)) {
            registrarEntrada(patente, estacionamiento, 1);
            resultado = true;
            mensaje = "Entrada registrada correctamente";

        } else if ("Salida".equals(accion) && !esPar(patente, estacionamiento)) {
            registrarSalida(patente, estacionamiento, 1);
            resultado = true;
            mensaje = "Salida registrada correctamente";

        } else {
            if ("Entrada".equals(accion)) {
                mensaje = "El vehículo ya está dentro del estacionamiento";
            } else {
                mensaje = "El vehículo no está dentro del estacionamiento";
            }
        }

        return new AccionEstacionamientoResultDTO(resultado, mensaje, patente, accion);
    }


    public boolean vehiculoTieneRegistros(String patente) {
        try {
            return registroRepo.existsByPatente(patente);
        } catch (Exception e) {
            System.err.println("Error al verificar registros: " + e.getMessage());
            return false;
        }
    }

    public boolean vehiculoEstaEstacionado(String patente) {
        try {
            List<RegistroEstacionamiento> registros = registroRepo.findByPatenteOrderByFechaHoraDesc(patente);

            if (registros.isEmpty()) {
                return false;
            }

            RegistroEstacionamiento ultimoRegistro = registros.get(0);

            return "ENTRADA".equals(ultimoRegistro.getTipo());

        } catch (Exception e) {
            System.err.println("Error al verificar si está estacionado: " + e.getMessage());
            return false;
        }
    }

    public long contarRegistrosPorPatente(String patente) {
        try {
            return registroRepo.countByPatente(patente);
        } catch (Exception e) {
            System.err.println("Error al contar registros: " + e.getMessage());
            return 0;
        }
    }

    public EstadoVehiculo obtenerEstadoActualVehiculo(String patente) {
        try {
 
            EstadoVehiculo estado = new EstadoVehiculo(patente);

            List<RegistroEstacionamiento> registros = registroRepo.findByPatenteOrderByFechaHoraDesc(patente);

            if (registros.isEmpty()) {
                 estado.setTieneRegistros(false);
                estado.setEstaEstacionado(false);
                estado.setNombreEstacionamiento("Sin registros");
                return estado;
            }

            RegistroEstacionamiento ultimoRegistro = registros.get(0);

            estado.setTieneRegistros(true);
            estado.setUltimoRegistro(ultimoRegistro);
            estado.setIdEstacionamiento(ultimoRegistro.getIdEstacionamiento());
            estado.setFechaUltimoRegistro(ultimoRegistro.getFechaHora().toString());

            boolean estaAdentro = "ENTRADA".equals(ultimoRegistro.getTipo());
            estado.setEstaEstacionado(estaAdentro);

            String nombreEstacionamiento = "Desconocido";
            try {
                EstacionamientoDTO estDto = estacionamientoService.obtener(ultimoRegistro.getIdEstacionamiento());
                if (estDto != null) {
                    nombreEstacionamiento = estDto.getNombre();
                }
            } catch (Exception e) {
                System.err.println("Error obteniendo nombre del estacionamiento: " + e.getMessage());
                nombreEstacionamiento = "Estacionamiento ID: " + ultimoRegistro.getIdEstacionamiento();
            }

            estado.setNombreEstacionamiento(nombreEstacionamiento);

            long cantidadLong = registroRepo.countByPatente(patente);
            estado.setCantidadRegistros((int) cantidadLong);

       

            return estado;

        } catch (Exception e) {
            System.err.println(" Error al obtener estado del vehículo " + patente + ": " + e.getMessage());
            e.printStackTrace();

            EstadoVehiculo estadoError = new EstadoVehiculo(patente);
            estadoError.setTieneRegistros(false);
            estadoError.setEstaEstacionado(false);
            estadoError.setNombreEstacionamiento("Error");
            return estadoError;
        }
    }

    public List<RegistroEstacionamiento> obtenerVehiculosActualmenteEnEstacionamiento(Long idEstacionamiento) {
        try {
 
            List<RegistroEstacionamiento> vehiculosAdentro = new ArrayList<>();

  
             

            List<RegistroEstacionamiento> registrosHoy = registroRepo.findRegistrosDePatentesImpares(idEstacionamiento);

            Map<String, RegistroEstacionamiento> ultimosPorPatente = new HashMap<>();

            for (RegistroEstacionamiento registro : registrosHoy) {
                String patente = registro.getPatente();
                if (!ultimosPorPatente.containsKey(patente)) {
                    ultimosPorPatente.put(patente, registro);
                }
            }

            for (RegistroEstacionamiento registro : ultimosPorPatente.values()) {
                if ("ENTRADA".equals(registro.getTipo())) {
                    vehiculosAdentro.add(registro);
                }
            }

            vehiculosAdentro.sort((a, b) -> b.getFechaHora().compareTo(a.getFechaHora()));

 
            return vehiculosAdentro;

        } catch (Exception e) {
            System.err.println(" Error obteniendo vehículos en estacionamiento: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

     public List<RegistroEstacionamiento> obtenerEgresosDelDia(Long idEstacionamiento) {
        try {

           
            List<RegistroEstacionamiento> egresos = registroRepo
                    .findRegistrosDePatentePares(
                            idEstacionamiento);

            return egresos;

        } catch (Exception e) {
            System.err.println(" Error obteniendo egresos del día: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<RegistroEstacionamiento> obtenerIngresosDelDia(Long idEstacionamiento) {
        try {
 
            LocalDateTime inicioDelDia = LocalDate.now().atStartOfDay();
            LocalDateTime finDelDia = LocalDate.now().atTime(23, 59, 59);

            List<RegistroEstacionamiento> ingresos = registroRepo
                    .findByIdEstacionamientoAndTipoAndFechaHoraBetweenOrderByFechaHoraDesc(
                            idEstacionamiento, "ENTRADA", inicioDelDia, finDelDia);

             return ingresos;

        } catch (Exception e) {
            System.err.println(" Error obteniendo ingresos del día: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public EstadoVehiculo obtenerEstadoDetallado(String patente) {
        try {
 
            List<RegistroEstacionamiento> registros = registroRepo.findByPatenteOrderByFechaHoraDesc(patente);

            EstadoVehiculo estado = new EstadoVehiculo();
            estado.setPatente(patente);
            estado.setTieneRegistros(!registros.isEmpty());
            estado.setCantidadRegistros(registros.size());

            if (!registros.isEmpty()) {
                RegistroEstacionamiento ultimoRegistro = registros.get(0);
                estado.setUltimoRegistro(ultimoRegistro);
                estado.setEstaEstacionado("ingreso".equals(ultimoRegistro.getTipo()));

                // Formatear fecha .
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                estado.setFechaUltimoRegistro(ultimoRegistro.getFechaHora().format(formatter));
            }

            return estado;

        } catch (Exception e) {
            System.err.println("Error al obtener estado detallado: " + e.getMessage());
            return new EstadoVehiculo(patente);
        }
    }

    public String generarMensajeError(String patente) {
        try {
            EstadoVehiculo estado = obtenerEstadoDetallado(patente);

            if (!estado.isTieneRegistros()) {
                return "El vehículo no tiene registros de estacionamiento.";
            }

            if (estado.isEstaEstacionado()) {
                return String.format(
                        "No se puede eliminar el vehículo con patente <strong>%s</strong><br>" +
                                " <strong>Motivo:</strong> El vehículo está actualmente estacionado<br>" +
                                " <strong>Ingreso:</strong> %s<br>" +
                                " <strong>Solución:</strong> Registre primero el egreso del vehículo",
                        patente,
                        estado.getFechaUltimoRegistro());
            } else {
                return String.format(
                        " No se puede eliminar el vehículo con patente <strong>%s</strong><br>" +
                                " <strong>Motivo:</strong> Tiene %d registro(s) de estacionamiento en el historial<br>"
                                +
                                " <strong>Último movimiento:</strong> %s el %s<br>" +
                                " <strong>Opciones:</strong><br>" +
                                "   • Use 'Eliminar con historial' para borrar todo<br>" +
                                "   • O contacte al administrador para limpiar el historial",
                        patente,
                        estado.getCantidadRegistros(),
                        estado.getUltimoRegistro().getTipo().toUpperCase(),
                        estado.getFechaUltimoRegistro());
            }

        } catch (Exception e) {
            return "Error al verificar el estado del vehículo: " + e.getMessage();
        }
    }

   public boolean estacionamientoIsFull (EstacionamientoDTO est){

        List<RegistroEstacionamiento> Re = registroRepo.findRegistrosDePatentesImpares(est.getId());
         return Re.size() == est.getCapacidad();

    }

}

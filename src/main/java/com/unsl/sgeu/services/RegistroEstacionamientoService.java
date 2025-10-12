package com.unsl.sgeu.services;

import com.unsl.sgeu.repositories.RegistroEstacionamientoRepository;
import com.unsl.sgeu.repositories.VehiculoRepository;
import com.unsl.sgeu.config.SessionInterceptor;
import com.unsl.sgeu.controllers.LoginController;
import com.unsl.sgeu.dto.EstacionamientoDTO;
import com.unsl.sgeu.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.transaction.annotation.Propagation;
import jakarta.servlet.http.HttpSession;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import com.unsl.sgeu.services.EstadoVehiculo;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SessionAttributes
@Service
public class RegistroEstacionamientoService {

    @Autowired
    private RegistroEstacionamientoRepository registroRepo;
    @Autowired
    private VehiculoRepository vehiculoRepo;

    @Autowired
    private EstacionamientoService estacionamientoService;

    public RegistroEstacionamiento registrarEntrada(String patente, Estacionamiento est) {
        Vehiculo vehiculo = vehiculoRepo.findByPatente(patente);

        RegistroEstacionamiento registro = new RegistroEstacionamiento();
        registro.setPatente(vehiculo.getPatente());
        registro.setFechaHora(LocalDateTime.now());
        registro.setTipo("ENTRADA");
        System.out.println(est.getIdEst());
        registro.setIdEstacionamiento(est.getIdEst());
        registro.setModo("MANUAL");
        return registroRepo.save(registro);
    }

    public RegistroEstacionamiento registrarSalida(String patente, Estacionamiento est) {
        Vehiculo vehiculo = vehiculoRepo.findByPatente(patente);
        RegistroEstacionamiento registro = new RegistroEstacionamiento();
        registro.setPatente(vehiculo.getPatente());
        registro.setFechaHora(LocalDateTime.now());
        registro.setTipo("SALIDA");
        registro.setIdEstacionamiento(est.getIdEst());
        registro.setModo("MANUAL");
        return registroRepo.save(registro);
    }

    public boolean esPar(String patente) {

        long cantidad = registroRepo.countByPatente(patente);
        return cantidad % 2 == 0;
    }

    public List<String> obtenerPatentesAdentroMasDeCuatroHoras(Estacionamiento est) {
        return registroRepo.findPatentesAdentroMasDeCuatroHoras(est.getIdEst());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void eliminarRegistrosPorPatente(String patente) {
        try {
            System.out.println("=== ELIMINANDO REGISTROSS");
            System.out.println("Patente: " + patente);

            long cantidadAntes = registroRepo.countByPatente(patente);
            System.out.println("Registros encontrados: " + cantidadAntes);

            if (cantidadAntes > 0) {
                int eliminados = registroRepo.deleteByPatente(patente);
                System.out.println("Registros eliminados: " + eliminados);

                // Verificar que se eliminaronn
                long cantidadDespues = registroRepo.countByPatente(patente);
                System.out.println("Registros restantes: " + cantidadDespues);

                if (cantidadDespues > 0) {
                    throw new RuntimeException(
                            "No se pudieron eliminar todos los registros. Restantes: " + cantidadDespues);
                }
            }

            System.out.println("Eliminación de registros completada exitosamente");

        } catch (Exception e) {
            System.err.println("Error al eliminar registros: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar registros de estacionamiento: " + e.getMessage(), e);
        }
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
            System.out.println("Verificando estado actual del vehículo: " + patente);

            EstadoVehiculo estado = new EstadoVehiculo(patente);

            List<RegistroEstacionamiento> registros = registroRepo.findByPatenteOrderByFechaHoraDesc(patente);

            if (registros.isEmpty()) {
                System.out.println("No hay registros para la patente: " + patente);
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

            System.out.println("Estado del vehículo " + patente + ":");
            System.out.println("   - Último movimiento: " + ultimoRegistro.getTipo());
            System.out.println("   - Está adentro: " + estaAdentro);
            System.out.println("   - Estacionamiento: " + nombreEstacionamiento);
            System.out.println("   - Total registros: " + estado.getCantidadRegistros());

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

    public EstadoVehiculo obtenerEstadoDetallado(String patente) {
        try {
            System.out.println("Obteniendo estado detallado para: " + patente);

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

}

package com.unsl.sgeu.services;

import com.unsl.sgeu.models.RegistroEstacionamiento;
import com.unsl.sgeu.repositories.RegistroEstacionamientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class RegistroEstacionamientoService {

    @Autowired
    private RegistroEstacionamientoRepository registroRepo;

     public RegistroEstacionamiento registrarEntrada(String patente) {
        Vehiculo vehiculo = vehiculoRepo.findByPatente(patente);

        RegistroEstacionamiento registro = new RegistroEstacionamiento();
        registro.setPatente(vehiculo.getPatente());
        registro.setFechaHora(LocalDateTime.now());
        registro.setTipoMovimiento("ENTRADA");
        registro.setIdEst(1);
        registro.setModo("MANUAL");
        return registroestacionamientoRepo.save(registro);
    }

     public RegistroEstacionamiento registrarSalida(String patente) {
        Vehiculo vehiculo = vehiculoRepo.findByPatente(patente);
        RegistroEstacionamiento registro = new RegistroEstacionamiento();
        registro.setPatente(vehiculo.getPatente());
        registro.setFechaHora(LocalDateTime.now());
        registro.setTipoMovimiento("SALIDA");
        registro.setIdEst(1);
        registro.setModo("MANUAL");
        return registroestacionamientoRepo.save(registro);
     }

    public boolean esPar(String patente){

       long cantidad = registroestacionamientoRepo.countByPatente(patente);
        return cantidad % 2 == 0;
    }

    
     public List<String> obtenerPatentesAdentroMasDeCuatroHoras() {
        return registroestacionamientoRepo.findPatentesAdentroMasDeCuatroHoras();
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

                // Formatear fecha  .
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

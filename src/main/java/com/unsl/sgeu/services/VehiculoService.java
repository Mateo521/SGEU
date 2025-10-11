package com.unsl.sgeu.services;

import com.unsl.sgeu.models.Vehiculo;
import com.unsl.sgeu.repositories.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.Optional;
import java.util.List;

@Service
public class VehiculoService {

    @Autowired
    private VehiculoRepository vehiculoRepo;

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private RegistroEstacionamientoService registroService; 

   
    // public VehiculoService(VehiculoRepository vehiculoRepo, QRCodeService
    // qrCodeService) {
    // this.vehiculoRepo = vehiculoRepo;
    // this.qrCodeService = qrCodeService;
    // }

    public Vehiculo buscarPorQr(String codigoQr) {
        return vehiculoRepo.findByCodigoQr(codigoQr);
    }

    public Vehiculo buscarPorPatente(String patente) {
        try {
            Optional<Vehiculo> vehiculo = vehiculoRepo.findById(patente);
            return vehiculo.orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    
     
    public ResultadoEliminacion eliminarVehiculo(String patente) {
        try {
            System.out.println("=== VALIDANDO ELIMINACIÓN DE VEHÍCULO ===");
            System.out.println("Patente: " + patente);
            
            if (!vehiculoRepo.existsById(patente)) {
                return new ResultadoEliminacion(false, 
                    " El vehículo con patente <strong>" + patente + "</strong> no existe en el sistema.");
            }
            
            // Verificar registros SIN transacción activa
            boolean tieneRegistros = registroService.vehiculoTieneRegistros(patente);
            System.out.println("Tiene registros: " + tieneRegistros);
            
            if (tieneRegistros) {
                String mensajeDetallado = registroService.generarMensajeError(patente);
                return new ResultadoEliminacion(false, mensajeDetallado);
            }
            
            // Intentar eliminar directamente
            vehiculoRepo.deleteById(patente);
            
            boolean eliminado = !vehiculoRepo.existsById(patente);
            System.out.println("Vehículo eliminado: " + eliminado);
            
            if (eliminado) {
                return new ResultadoEliminacion(true, 
                    "✅ Vehículo con patente <strong>" + patente + "</strong> eliminado exitosamente.");
            } else {
                return new ResultadoEliminacion(false, 
                    " Error inesperado al eliminar el vehículo.");
            }
            
        } catch (Exception e) {
            System.err.println("Error al eliminar vehículo: " + e.getMessage());
            e.printStackTrace();
            
            String mensajeError = " <strong>Error al eliminar el vehículo</strong><br>";
            
            if (e.getMessage().contains("foreign key constraint") || 
                e.getMessage().contains("constraint fails")) {
                mensajeError += "🔗 <strong>Motivo:</strong> El vehículo tiene registros asociados<br>";
                mensajeError += "💡 <strong>Solución:</strong> Use 'Eliminar con historial' o registre el egreso primero";
            } else {
                mensajeError += "🛠️ <strong>Detalle técnico:</strong> " + e.getMessage();
            }
            
            return new ResultadoEliminacion(false, mensajeError);
        }
    }

    
    public ResultadoEliminacion eliminarVehiculoConHistorial(String patente) {
        try {
            System.out.println("=== ELIMINANDO VEHÍCULO CON HISTORIAL ==");
            System.out.println("Patente: " + patente);
            
            if (!vehiculoRepo.existsById(patente)) {
                return new ResultadoEliminacion(false, 
                    "El vehículo con patente <strong>" + patente + "</strong> no existe en el sistema.");
            }
            
            boolean estaEstacionado = registroService.vehiculoEstaEstacionado(patente);
            System.out.println("Está estacionado: " + estaEstacionado);
            
            if (estaEstacionado) {
                EstadoVehiculo estado = registroService.obtenerEstadoDetallado(patente);
                return new ResultadoEliminacion(false, 
                    String.format(
                        "<strong>No se puede eliminar el vehículo</strong><br>" +
                        "<strong>Motivo:</strong> El vehículo está actualmente estacionado<br>" +
                        "<strong>Ingreso:</strong> %s<br>" +
                        "<strong>Solución:</strong> Registre primero el egreso del vehículo",
                        estado.getFechaUltimoRegistro()
                    ));
            }
            
            long cantidadRegistros = registroService.contarRegistrosPorPatente(patente);
            System.out.println("Cantidad de registros a eliminar: " + cantidadRegistros);
            
            
            if (cantidadRegistros > 0) {
                System.out.println("Eliminando " + cantidadRegistros + " registros de estacionamiento");
                
                try {
                    registroService.eliminarRegistrosPorPatente(patente);
                    System.out.println("Registros eliminados exitosamente");
                    
                    // Verificar que se eliminaronn
                    long registrosRestantes = registroService.contarRegistrosPorPatente(patente);
                    if (registrosRestantes > 0) {
                        return new ResultadoEliminacion(false, 
                            "Error: No se pudieron eliminar todos los registros. Quedan " + registrosRestantes + " registros.");
                    }
                    
                } catch (Exception e) {
                    System.err.println("Error eliminando registros: " + e.getMessage());
                    return new ResultadoEliminacion(false, 
                        "Error al eliminar registros de estacionamiento: " + e.getMessage());
                }
            }
           
            System.out.println("Eliminando vehículo...");
            try {
                vehiculoRepo.deleteById(patente);
                
                boolean eliminado = !vehiculoRepo.existsById(patente);
                System.out.println("Vehículo eliminado: " + eliminado);
                
                if (eliminado) {
                    String mensaje = cantidadRegistros > 0 ? 
                        String.format(
                            "<strong>Eliminación completa exitosa</strong><br>" +
                            "<strong>Vehículo:</strong> %s eliminado<br>" +
                            "<strong>Historial:</strong> %d registro(s) eliminado(s)<br>" +
                            " <strong>Estado:</strong> Limpieza completa realizada",
                            patente, cantidadRegistros
                        ) :
                        String.format(
                            "<strong>Vehículo eliminado exitosamente</strong><br>" +
                            "<strong>Patente:</strong> %s<br>" +
                            "<strong>Historial:</strong> Sin registros previos",
                            patente
                        );
                        
                    return new ResultadoEliminacion(true, mensaje);
                } else {
                    return new ResultadoEliminacion(false, 
                        " Error inesperado: El vehículo no se pudo eliminar después del proceso.");
                }
                
            } catch (Exception e) {
                System.err.println("Error eliminando vehículo: " + e.getMessage());
                return new ResultadoEliminacion(false, 
                    " Error al eliminar el vehículo: " + e.getMessage());
            }
                
        } catch (Exception e) {
            System.err.println("Error al eliminar vehículo con historial: " + e.getMessage());
            e.printStackTrace();
            
            String mensajeError = String.format(
                " <strong>Error durante la eliminación</strong><br>" +
                " <strong>Vehículo:</strong> %s<br>" +
                " <strong>Error técnico:</strong> %s<br>" +
                " <strong>Sugerencia:</strong> Contacte al administrador del sistema",
                patente, e.getMessage()
            );
            
            return new ResultadoEliminacion(false, mensajeError);
        }
    }









    public boolean existeVehiculo(String patente) {
        return vehiculoRepo.existsById(patente);
    }

    public Vehiculo guardarVehiculo(Vehiculo vehiculo) {
       
        if (vehiculo.getCodigoQr() == null || vehiculo.getCodigoQr().isEmpty()) {
            vehiculo.setCodigoQr(generarCodigoQR(vehiculo.getPatente()));
        }

      
        Vehiculo vehiculoGuardado = vehiculoRepo.save(vehiculo);

   
        try {
            String rutaImagen = qrCodeService.generarImagenQR(
                    vehiculoGuardado.getCodigoQr(),
                    "qr_" + vehiculoGuardado.getPatente().replace(" ", "_"));
 
        } catch (Exception e) {
            System.err.println("Error generando imagen QR: " + e.getMessage());
        }

        return vehiculoGuardado;
    }

    public boolean existePatente(String patente) {
        return vehiculoRepo.existsById(patente);
    }

    public String generarCodigoQR(String patente) {
        return "qr-" + generarCodigoQrUnico(patente);
    }

    public String generarCodigoQrUnico(String patente) {
        UUID uuid = UUID.nameUUIDFromBytes(patente.getBytes());
        String codigoQr = uuid.toString();
        codigoQr = hashSha256(codigoQr);
        return codigoQr;
    }

    public List<Vehiculo> obtenerTodos() {
        return vehiculoRepo.findAll();
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
}

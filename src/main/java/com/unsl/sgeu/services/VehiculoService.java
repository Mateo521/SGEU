package com.unsl.sgeu.services;

import com.unsl.sgeu.models.Vehiculo;
import com.unsl.sgeu.repositories.VehiculoRepository;
import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
public class VehiculoService {

    private final VehiculoRepository vehiculoRepo;
    private final QRCodeService qrCodeService;

    public VehiculoService(VehiculoRepository vehiculoRepo, QRCodeService qrCodeService) {
        this.vehiculoRepo = vehiculoRepo;
        this.qrCodeService = qrCodeService;
    }




    public Vehiculo buscarPorQr(String codigoQr) {
        return vehiculoRepo.findByCodigoQr(codigoQr);
    }
    
    public Vehiculo guardarVehiculo(Vehiculo vehiculo) {
        // Generar código QR único si no existe
        if (vehiculo.getCodigoQr() == null || vehiculo.getCodigoQr().isEmpty()) {
            vehiculo.setCodigoQr(generarCodigoQR(vehiculo.getPatente()));
        }
        
        // Guardar vehículo primero
        Vehiculo vehiculoGuardado = vehiculoRepo.save(vehiculo);
        
        // Generar imagen QR
        try {
            String rutaImagen = qrCodeService.generarImagenQR(
                vehiculoGuardado.getCodigoQr(), 
                "qr_" + vehiculoGuardado.getPatente().replace(" ", "_")
            );
            // Podrías guardar la ruta de la imagen en la base de datos si quieres
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

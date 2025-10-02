package com.unsl.sgeu.services;

import java.util.UUID;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.unsl.sgeu.models.Persona;
import com.unsl.sgeu.models.Vehiculo;
import com.unsl.sgeu.repositories.VehiculoRepository;
import org.springframework.stereotype.Service;

@Service
public class VehiculoService {

    private final VehiculoRepository vehiculoRepo;

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

    public VehiculoService(VehiculoRepository vehiculoRepo) {
        this.vehiculoRepo = vehiculoRepo;
    }

    public Vehiculo buscarPorQr(String codigoQr) {
        return vehiculoRepo.findByCodigoQr(codigoQr);
    }

    public Vehiculo crearVehiculoConCodigoQr(String patente, Persona persona) {
        return vehiculoRepo.findById(patente).orElseGet(() -> {
            Vehiculo v = new Vehiculo();
            v.setPatente(patente);
            v.setCodigoQr(generarCodigoQrUnico(patente));  
            v.setModelo("Etios");
            v.setColor("Rojo");
            v.setTipo("Auto");
            v.setDuenio(persona);  
            return vehiculoRepo.save(v);
        });
    }
}

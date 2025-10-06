package com.unsl.sgeu.services;

import com.unsl.sgeu.models.RegistroEstacionamiento;
import com.unsl.sgeu.models.Vehiculo;
import com.unsl.sgeu.repositories.RegistroEstacionamientoRepository;
import com.unsl.sgeu.repositories.VehiculoRepository;

import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.UUID;


@Service

public class RegistroEstacionamientoService {
    
     private final RegistroEstacionamientoRepository registroestacionamientoRepo;
     private final VehiculoRepository vehiculoRepo;

    public RegistroEstacionamientoService(RegistroEstacionamientoRepository registroestacionamientoRepo, VehiculoRepository vehiculoRepo) {
        this.registroestacionamientoRepo = registroestacionamientoRepo;
        this.vehiculoRepo = vehiculoRepo;
    }



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
}

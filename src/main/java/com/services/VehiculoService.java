package com.services;



import com.unsl.sgeu.models.Vehiculo;
import com.unsl.sgeu.repositories.VehiculoRepository;
import org.springframework.stereotype.Service;

@Service
public class VehiculoService {

    private final VehiculoRepository vehiculoRepo;

    public VehiculoService(VehiculoRepository vehiculoRepo) {
        this.vehiculoRepo = vehiculoRepo;
    }

    public Vehiculo buscarPorQr(String codigoQr) {
        return vehiculoRepo.findByCodigoQr(codigoQr);
    }
}

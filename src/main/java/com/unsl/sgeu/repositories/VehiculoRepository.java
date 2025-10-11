package com.unsl.sgeu.repositories;

import com.unsl.sgeu.models.Vehiculo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VehiculoRepository extends JpaRepository<Vehiculo, String> {
    // Buscar por patente
    Vehiculo findByPatente(String patente);


    // Buscar por código QR
    Vehiculo findByCodigoQr(String codigoQr);
}

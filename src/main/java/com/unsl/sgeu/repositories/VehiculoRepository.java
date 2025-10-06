package com.unsl.sgeu.repositories;

import com.unsl.sgeu.models.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehiculoRepository extends JpaRepository<Vehiculo, String> {
    // Buscar por patente
    Vehiculo findByPatente(String patente);



    // Buscar por código QR
    Vehiculo findByCodigoQr(String codigoQr);
}

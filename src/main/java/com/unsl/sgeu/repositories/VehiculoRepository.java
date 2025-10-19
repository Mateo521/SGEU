package com.unsl.sgeu.repositories;

import com.unsl.sgeu.models.Vehiculo;

import java.util.List;

public interface VehiculoRepository {

    // -------------------- CRUD Básico --------------------
    void save(Vehiculo vehiculo);
    void update(Vehiculo vehiculo);
    void deleteByPatente(String patente);
    Vehiculo findByPatente(String patente);
    List<Vehiculo> findAll();

    // -------------------- Búsquedas simples --------------------
    Vehiculo findByCodigoQr(String codigoQr);
    List<Vehiculo> findByPatenteContaining(String patente);

    // -------------------- Consultas con RegistroEstacionamiento --------------------
    List<Vehiculo> findVehiculosByEstacionamiento(Long idEstacionamiento);
    List<Vehiculo> findVehiculosByEstacionamientos(List<Long> idsEstacionamientos);
    List<Vehiculo> findVehiculosByEstacionamientoAndPatente(Long idEstacionamiento, String patente);
    List<Vehiculo> findVehiculosByEstacionamientosAndPatente(List<Long> idsEstacionamientos, String patente);
    List<Vehiculo> findAllVehiculosByGuardiaEstacionamientos(List<Long> idsEstacionamientos);

    // -------------------- Métodos adicionales útiles --------------------
    String findEstacionamientoOrigenByPatente(String patente, List<Long> idsEstacionamientos);
}

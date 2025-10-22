package com.unsl.sgeu.repositories;

import com.unsl.sgeu.models.Vehiculo;

import java.util.List;

public interface VehiculoRepository {

    // -------------------- CRUD Básico --------------------
    Vehiculo save(Vehiculo vehiculo);
    void update(Vehiculo vehiculo);
    void delete(String patente);
    Vehiculo findByPatente(String patente);
    List<Vehiculo> findAll();

    // -------------------- Búsquedas simples --------------------
    Vehiculo findByCodigoQr(String codigoQr);
    List<Vehiculo> findByPatenteContainingIgnoreCase(String patente);

    // -------------------- Paginación --------------------
    List<Vehiculo> findAllPaginado(int offset, int limit, String ordenPor);
    List<Vehiculo> findByPatenteContainingPaginado(String patente, int offset, int limit);
    List<Vehiculo> findByGuardiaPaginado(Long idGuardia, int offset, int limit);
    List<Vehiculo> findByPatenteAndGuardiaPaginado(String patente, Long idGuardia, int offset, int limit);
    
    // -------------------- Conteos para paginación --------------------
    long count();
    long countByPatenteContaining(String patente);
    long countByGuardia(Long idGuardia);
    long countByPatenteAndGuardia(String patente, Long idGuardia);

    // -------------------- Consultas con RegistroEstacionamiento --------------------
    List<Vehiculo> findVehiculosByEstacionamiento(Long idEstacionamiento);
    List<Vehiculo> findVehiculosByEstacionamientos(List<Long> idsEstacionamientos);
    List<Vehiculo> findVehiculosByEstacionamientoAndPatente(Long idEstacionamiento, String patente);
    List<Vehiculo> findVehiculosByEstacionamientosAndPatente(List<Long> idsEstacionamientos, String patente);
    List<Vehiculo> findAllVehiculosByGuardiaEstacionamientos(List<Long> idsEstacionamientos);

    // -------------------- Métodos adicionales útiles --------------------
    String findEstacionamientoOrigenByPatente(String patente, List<Long> idsEstacionamientos);
}

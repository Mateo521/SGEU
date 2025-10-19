package com.unsl.sgeu.repositories;

import com.unsl.sgeu.models.RegistroEstacionamiento;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RegistroEstacionamientoRepository {

    List<RegistroEstacionamiento> findAll();

    Optional<RegistroEstacionamiento> findById(Long id);

    List<RegistroEstacionamiento> findByPatenteOrderByFechaHoraDesc(String patente);

    boolean existsByPatente(String patente);

    long countByPatente(String patente);

    long countByPatenteAndIdEstacionamiento(String patente, Long idEstacionamiento);

    int deleteByPatente(String patente);

    List<String> findPatentesAdentroMasDeCuatroHoras(Long idEst);

    List<RegistroEstacionamiento> findByIdEstacionamientoAndTipoAndFechaHoraBetweenOrderByFechaHoraDesc(
            Long idEstacionamiento, String tipo, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    List<RegistroEstacionamiento> findByIdEstacionamientoAndFechaHoraBetweenOrderByFechaHoraDesc(
            Long idEstacionamiento, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    List<RegistroEstacionamiento> findRegistrosDePatentesImpares(Long idEst);

    List<RegistroEstacionamiento> findRegistrosDePatentePares(Long idEst);
    
    RegistroEstacionamiento save(RegistroEstacionamiento registroEstacionamiento);

    void update(RegistroEstacionamiento registro);

    boolean delete(Long id);
}

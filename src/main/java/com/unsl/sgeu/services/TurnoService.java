// src/main/java/com/unsl/sgeu/services/TurnoService.java
package com.unsl.sgeu.services;

import com.unsl.sgeu.dto.*;
import com.unsl.sgeu.mappers.TurnoMapper;
import com.unsl.sgeu.models.*;
import com.unsl.sgeu.repositories.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TurnoService {

    private final TurnoRepository turnoRepo;
    private final EmpleadoRepository empleadoRepo;         // asumimos existe
    private final EstacionamientoRepository estRepo;       // ya lo tenés

    public TurnoService(TurnoRepository turnoRepo,
                        EmpleadoRepository empleadoRepo,
                        EstacionamientoRepository estRepo) {
        this.turnoRepo = turnoRepo;
        this.empleadoRepo = empleadoRepo;
        this.estRepo = estRepo;
    }

    public Page<TurnoDTO> list(Long empleadoId, Long estId, String fecha, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 20, Sort.by("fechaInicio").descending());
        LocalDate f = (fecha != null && !fecha.isBlank()) ? LocalDate.parse(fecha) : null;

        return turnoRepo.search(empleadoId, estId, f, pageable)
                .map(TurnoMapper::toDTO);
    }

    public Page<TurnoDTO> listRange(Long empleadoId, Long estId, String desde, String hasta, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 20, Sort.by("fechaInicio").descending());
        LocalDate d = (desde != null && !desde.isBlank()) ? LocalDate.parse(desde) : null;
        LocalDate h = (hasta != null && !hasta.isBlank()) ? LocalDate.parse(hasta) : null;

        return turnoRepo.searchRange(empleadoId, estId, d, h, pageable)
                .map(TurnoMapper::toDTO);
    }

    public TurnoDTO create(TurnoCreateDTO dto) {
        Empleado emp = empleadoRepo.findById(dto.getEmpleadoId())
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));
        Estacionamiento est = estRepo.findById(dto.getEstacionamientoId())
                .orElseThrow(() -> new IllegalArgumentException("Estacionamiento no encontrado"));

        Turno t = TurnoMapper.fromCreateDTO(dto, emp, est);
        t = turnoRepo.save(t);
        return TurnoMapper.toDTO(t);
    }

    public TurnoDTO update(Long id, TurnoCreateDTO dto) {
        Turno t = turnoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Turno no encontrado"));

        Empleado emp = empleadoRepo.findById(dto.getEmpleadoId())
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));
        Estacionamiento est = estRepo.findById(dto.getEstacionamientoId())
                .orElseThrow(() -> new IllegalArgumentException("Estacionamiento no encontrado"));

        TurnoMapper.copyFromCreateDTO(t, dto, emp, est);
        t = turnoRepo.save(t);
        return TurnoMapper.toDTO(t);
    }

    public void delete(Long id) {
        if (!turnoRepo.existsById(id)) throw new IllegalArgumentException("Turno no encontrado");
        turnoRepo.deleteById(id);
    }

    public TurnoDTO get(Long id) {
        return turnoRepo.findById(id).map(TurnoMapper::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Turno no encontrado"));
    }
}

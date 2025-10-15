// src/main/java/com/unsl/sgeu/services/TurnoService.java
package com.unsl.sgeu.services;

import com.unsl.sgeu.dto.*;
import com.unsl.sgeu.mappers.TurnoMapper;
import com.unsl.sgeu.models.*;
import com.unsl.sgeu.repositories.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class TurnoService {

        private final TurnoRepository turnoRepo;
        private final EmpleadoRepository empleadoRepo;
        private final EstacionamientoRepository estRepo;

        public TurnoService(TurnoRepository turnoRepo,
                        EmpleadoRepository empleadoRepo,
                        EstacionamientoRepository estRepo) {
                this.turnoRepo = turnoRepo;
                this.empleadoRepo = empleadoRepo;
                this.estRepo = estRepo;
        }

        // ===================== LISTADOS =====================
        public Page<TurnoDTO> list(Long empleadoId, Long estId, String fecha, Integer page, Integer size) {
                Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 20,
                                Sort.by("fechaInicio").descending());
                LocalDate f = (fecha != null && !fecha.isBlank()) ? LocalDate.parse(fecha) : null;
                return turnoRepo.search(empleadoId, estId, f, pageable).map(TurnoMapper::toDTO);
        }

        public Page<TurnoDTO> listRange(Long empleadoId, Long estId, String desde, String hasta, Integer page,
                        Integer size) {
                Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 20,
                                Sort.by("fechaInicio").descending());
                LocalDate d = (desde != null && !desde.isBlank()) ? LocalDate.parse(desde) : null;
                LocalDate h = (hasta != null && !hasta.isBlank()) ? LocalDate.parse(hasta) : null;
                return turnoRepo.searchRange(empleadoId, estId, d, h, pageable).map(TurnoMapper::toDTO);
        }

        // ===================== CREAR =====================
        public TurnoDTO create(TurnoCreateDTO dto) {
                // Zona horaria AR (ajustá si corresponde)
                ZoneId zone = ZoneId.of("America/Argentina/Buenos_Aires");
                LocalDate hoy = LocalDate.now(zone);
                LocalDate manana = hoy.plusDays(1);

                // Convertir fecha del DTO (String → LocalDate)
                LocalDate fechaInicio = LocalDate.parse(dto.getFechaInicio());

                // Validaciones básicas
                if (fechaInicio == null || fechaInicio.isBefore(manana)) {
                        throw new IllegalArgumentException("La fecha de inicio debe ser mañana o posterior.");
                }
                Empleado emp = empleadoRepo.findById(dto.getEmpleadoId())
                                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));
                Estacionamiento est = estRepo.findById(dto.getEstacionamientoId())
                                .orElseThrow(() -> new IllegalArgumentException("Estacionamiento no encontrado"));

                // Cerrar la asignación vigente (si existe) del mismo guardia con fecha_fin =
                // hoy
                turnoRepo.findByEmpleadoIdAndFechaFinIsNull(dto.getEmpleadoId())
                                .ifPresent(prev -> {
                                        prev.setFechaFin(hoy);
                                        turnoRepo.save(prev);
                                });

                // Crear nueva (abierta)
                Turno t = TurnoMapper.fromCreateDTO(dto, emp, est);
                t.setFechaFin(null); // abierta hasta nuevo aviso
                t = turnoRepo.save(t);
                return TurnoMapper.toDTO(t);
        }

        // ===================== ACTUALIZAR =====================
        public TurnoDTO update(Long id, TurnoCreateDTO dto) {
                // Zona horaria AR
                ZoneId zone = ZoneId.of("America/Argentina/Buenos_Aires");
                LocalDate hoy = LocalDate.now(zone);
                LocalDate manana = hoy.plusDays(1);

                Turno existente = turnoRepo.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Turno no encontrado"));

                Empleado emp = empleadoRepo.findById(dto.getEmpleadoId())
                                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));
                Estacionamiento est = estRepo.findById(dto.getEstacionamientoId())
                                .orElseThrow(() -> new IllegalArgumentException("Estacionamiento no encontrado"));

                // Si cambió el guardia, aplico la misma regla: nueva fechaInicio debe ser
                // mañana o posterior
                // y cierro la vigente del NUEVO guardia (si existe) con fecha_fin = hoy
                boolean cambiaGuardia = !existente.getEmpleado().getId().equals(dto.getEmpleadoId());
                if (cambiaGuardia) {
                        LocalDate fechaInicio = LocalDate.parse(dto.getFechaInicio());
                        if (fechaInicio == null || fechaInicio.isBefore(manana)) {
                                throw new IllegalArgumentException(
                                                "Al cambiar de guardia, la nueva fecha de inicio debe ser mañana o posterior.");
                        }

                        turnoRepo.findByEmpleadoIdAndFechaFinIsNull(dto.getEmpleadoId())
                                        .ifPresent(prev -> {
                                                prev.setFechaFin(hoy);
                                                turnoRepo.save(prev);
                                        });
                }

                // Copiar datos editados
                TurnoMapper.copyFromCreateDTO(existente, dto, emp, est);

                // Guardar cambios
                Turno guardado = turnoRepo.save(existente);
                return TurnoMapper.toDTO(guardado);
        }

        public TurnoDTO finalizar(Long id) {
                ZoneId zone = ZoneId.of("America/Argentina/Buenos_Aires");
                LocalDate hoy = LocalDate.now(zone);

                Turno t = turnoRepo.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Turno no encontrado"));

                if (t.getFechaFin() == null || t.getFechaFin().isAfter(hoy)) {
                        t.setFechaFin(hoy);
                        turnoRepo.save(t);
                }
                return TurnoMapper.toDTO(t);
        }

        // ===================== DELETE / GET =====================
        public void delete(Long id) {
                if (!turnoRepo.existsById(id))
                        throw new IllegalArgumentException("Turno no encontrado");
                turnoRepo.deleteById(id);
        }

        public TurnoDTO get(Long id) {
                return turnoRepo.findById(id).map(TurnoMapper::toDTO)
                                .orElseThrow(() -> new IllegalArgumentException("Turno no encontrado"));
        }
}

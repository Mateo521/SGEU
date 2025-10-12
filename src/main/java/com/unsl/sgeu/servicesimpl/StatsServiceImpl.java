package com.unsl.sgeu.servicesimpl;

import com.unsl.sgeu.repositories.StatsRepository;
import com.unsl.sgeu.services.StatsService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    public StatsServiceImpl(StatsRepository statsRepository){
        this.statsRepository = statsRepository;
    }

    @Override
    public List<Map<String, Object>> porcentajeCategorias(LocalDate desde, LocalDate hasta, Long estacionamientoId) {
        return statsRepository.porcentajeCategorias(desde, hasta, estacionamientoId);
    }

    @Override
    public Map<String, Object> estacionamientoConMasIngresos(LocalDate desde, LocalDate hasta, Long estacionamientoId) {
        return statsRepository.estacionamientoConMasIngresos(desde, hasta, estacionamientoId);
    }

    @Override
    public List<Map<String, Object>> diaSemanaMasIngresos(LocalDate desde, LocalDate hasta, Long estacionamientoId) {
        return statsRepository.diaSemanaMasIngresos(desde, hasta, estacionamientoId);
    }

    @Override
    public List<Map<String, Object>> horariosPicoIngresos(LocalDate desde, LocalDate hasta, int topN, Long estacionamientoId) {
        return statsRepository.horariosPicoIngresos(desde, hasta, topN, estacionamientoId);
    }

    @Override
    public List<Map<String, Object>> porcentajeOcupacionPorEstacionamiento(LocalDate desde, LocalDate hasta, Long estacionamientoId) {
        return statsRepository.porcentajeOcupacionPorEstacionamiento(desde, hasta, estacionamientoId);
    }

    @Override
    public List<Map<String, Object>> evolucionIngresosDiarios(LocalDate desde, LocalDate hasta, Long estacionamientoId) {
        return statsRepository.evolucionIngresosDiarios(desde, hasta, estacionamientoId);
    }

    @Override
    public List<Map<String, Object>> conteoManualVsQr(LocalDate desde, LocalDate hasta, Long estacionamientoId) {
        return statsRepository.conteoManualVsQr(desde, hasta, estacionamientoId);
    }

    @Override
    public List<Map<String, Object>> distribucionPorTipoVehiculo(LocalDate desde, LocalDate hasta, Long estacionamientoId) {
        return statsRepository.distribucionPorTipoVehiculo(desde, hasta, estacionamientoId);
    }
}

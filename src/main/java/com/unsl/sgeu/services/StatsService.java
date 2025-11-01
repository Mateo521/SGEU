package com.unsl.sgeu.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StatsService {
    List<Map<String, Object>> diaSemanaMasIngresos(LocalDate desde, LocalDate hasta, Long estacionamientoId);
    
    List<Map<String, Object>> porcentajeCategorias(LocalDate desde, LocalDate hasta, Long estacionamientoId);
    Map<String, Object> estacionamientoConMasIngresos(LocalDate desde, LocalDate hasta, Long estacionamientoId);
    List<Map<String,Object>> horariosPicoIngresos(LocalDate desde, LocalDate hasta, int topN ,Long estId);
    List<Map<String, Object>> porcentajeOcupacionPorEstacionamiento(LocalDate desde, LocalDate hasta, Long estId);
    List<Map<String, Object>> evolucionIngresosDiarios(LocalDate desde, LocalDate hasta, Long estId);
    List<Map<String,Object>> conteoManualVsQr(LocalDate desde, LocalDate hasta, Long estId);
    List<Map<String, Object>> distribucionPorTipoVehiculo(LocalDate desde, LocalDate hasta, Long estId);
}

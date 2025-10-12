package com.unsl.sgeu.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StatsRepository {

    // Porcentaje de cantidad de categorias de personas que ingresan
    List<Map<String, Object>> porcentajeCategorias(LocalDate desde, LocalDate hasta, Long estacionamientoId);

    // Estacionamiento con mayor cantidad de ingresos y su capacidad
    Map<String, Object> estacionamientoConMasIngresos(LocalDate desde, LocalDate hasta, Long estacionamientoId);

    // Dia de semana con mayor cantidad de ingresos
    List<Map<String, Object>> diaSemanaMasIngresos(LocalDate desde, LocalDate hasta, Long estacionamientoId);

    // Horarios pico de ingresos (por hora)
    List<Map<String, Object>> horariosPicoIngresos(LocalDate desde, LocalDate hasta, int topN, Long estacionamientoId);

    // Promedio de estancia de vehiculos (horas)
    Double promedioEstancia(LocalDate desde, LocalDate hasta, Long estacionamientoId);

    // Porcentaje de ocupacion por estacionamiento
    List<Map<String, Object>> porcentajeOcupacionPorEstacionamiento(LocalDate desde, LocalDate hasta, Long estacionamientoId);

    // Evolucion de ingresos diarios
    List<Map<String, Object>> evolucionIngresosDiarios(LocalDate desde, LocalDate hasta, Long estacionamientoId);

    // Cantidad de ingresos/egresos hecho de forma MANUAL o QR
    List<Map<String, Object>> conteoManualVsQr(LocalDate desde, LocalDate hasta, Long estacionamientoId);
}

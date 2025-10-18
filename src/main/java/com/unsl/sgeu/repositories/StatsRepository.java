package com.unsl.sgeu.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StatsRepository {

    // Porcentaje de cantidad de categorias de personas que ingresan
    List<Object[]> getCantidadPorCategoria(LocalDate desde, LocalDate hasta, Long estacionamientoId);

    // Dia de semana con mayor cantidad de ingresos
    List<Map<String, Object>> diaSemanaMasIngresos(LocalDate desde, LocalDate hasta, Long estacionamientoId);

    // DAO: devuelve cantidad de ingresos por hora para un rango de fechas y estacionamiento
    List<Object[]> getIngresosPorHora(LocalDate desde, LocalDate hasta, Long estId, int topN);


    // Porcentaje de ocupacion por estacionamiento
    List<Object[]> getIngresosPorEstacionamiento(LocalDate desde, LocalDate hasta, Long estacionamientoId);

    // DAO: devuelve cantidad de ingresos por día para un rango de fechas y un estacionamiento
    List<Object[]> getIngresosPorDia(LocalDate desde, LocalDate hasta, Long estId);


    // DAO: devuelve la cantidad de registros por modo (MANUAL o QR) para un rango de fechas y un estacionamiento
    List<Object[]> getConteoManualVsQr(LocalDate desde, LocalDate hasta, Long estId);


    // DAO: devuelve la cantidad de registros por tipo de vehículo para un rango de fechas y un estacionamiento
    List<Object[]> getDistribucionPorTipoVehiculo(LocalDate desde, LocalDate hasta, Long estId);
    
}

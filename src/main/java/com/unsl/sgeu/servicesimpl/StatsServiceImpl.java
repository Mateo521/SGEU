package com.unsl.sgeu.servicesimpl;

import com.unsl.sgeu.repositories.StatsRepository;
import com.unsl.sgeu.services.StatsService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    public StatsServiceImpl(StatsRepository statsRepository){
        this.statsRepository = statsRepository;
    }

    @Override
    public List<Map<String, Object>> porcentajeCategorias(LocalDate desde, LocalDate hasta, Long estId) {
        List<Object[]> rows = statsRepository.getCantidadPorCategoria(desde, hasta, estId);
        
        // Calcula totales y porcentajes
        long total = 0;
        for(Object[] r: rows) total += ((Number)r[1]).longValue();

        List<Map<String,Object>> res = new ArrayList<>();
        for(Object[] r: rows){
            String cat = r[0] == null ? "Sin categoria" : r[0].toString();
            long cnt = ((Number)r[1]).longValue();
            double pct = total == 0 ? 0.0 : (cnt * 100.0 / total);

            Map<String,Object> m = new HashMap<>();
            m.put("categoria", cat);
            m.put("cantidad", cnt);
            m.put("porcentaje", Math.round(pct*100.0)/100.0);

            res.add(m);
        }

        return res;
    }


    @Override
    public Map<String, Object> estacionamientoConMasIngresos(LocalDate desde, LocalDate hasta, Long estId) {
        List<Object[]> rows = statsRepository.getIngresosPorEstacionamiento(desde, hasta, estId);

        if(rows.isEmpty()) return Map.of(); // Si no hay resultados, devolvemos mapa vacío

        // Tomamos el primer registro (el que tiene más ingresos)
        Object[] r = rows.get(0);
        Map<String,Object> m = new HashMap<>();
        m.put("id", ((Number)r[0]).longValue());
        m.put("nombre", r[1]);
        m.put("capacidad", r[2] == null ? 0 : ((Number)r[2]).intValue());
        m.put("ingresos", ((Number)r[3]).longValue());

        // Calcula porcentaje de capacidad ocupada
        double pct = m.get("capacidad") == null || ((int)m.get("capacidad")) == 0
                    ? 0.0
                    : ((long)m.get("ingresos") * 100.0 / ((int)m.get("capacidad")));
        m.put("porcentaje_capacidad", Math.round(pct*100.0)/100.0);

        return m;
    }


    @Override
    public List<Map<String, Object>> diaSemanaMasIngresos(LocalDate desde, LocalDate hasta, Long estacionamientoId) {
        return statsRepository.diaSemanaMasIngresos(desde, hasta, estacionamientoId);
    }

    @Override
    public List<Map<String,Object>> horariosPicoIngresos(LocalDate desde, LocalDate hasta, int topN, Long estId) {
        List<Object[]> rows = statsRepository.getIngresosPorHora(desde, hasta, estId, topN);
        List<Map<String,Object>> res = new ArrayList<>();
        for(Object[] r : rows) {
            Map<String,Object> m = new HashMap<>();
            m.put("hora", r[0]);
            m.put("cantidad", ((Number) r[1]).longValue());
            res.add(m);
        }
        return res;
    }


    @Override
    public List<Map<String, Object>> porcentajeOcupacionPorEstacionamiento(LocalDate desde, LocalDate hasta, Long estId) {
        List<Object[]> rows = statsRepository.getIngresosPorEstacionamiento(desde, hasta, estId);
        List<Map<String,Object>> res = new ArrayList<>();

        for(Object[] r: rows){
            long ingresos = ((Number)r[3]).longValue();
            int capacidad = r[2]==null ? 0 : ((Number)r[2]).intValue();
            double pct = capacidad == 0 ? 0.0 : Math.min(ingresos*100.0/capacidad, 100.0);

            Map<String,Object> m = new HashMap<>();
            m.put("id", ((Number)r[0]).longValue());
            m.put("nombre", r[1]);
            m.put("capacidad", capacidad);
            m.put("ingresos", ingresos);
            m.put("porcentaje", Math.round(pct*100.0)/100.0);

            res.add(m);
        }

        return res;
    }

    @Override
    public List<Map<String,Object>> evolucionIngresosDiarios(LocalDate desde, LocalDate hasta, Long estId) {
    List<Object[]> rows = statsRepository.getIngresosPorDia(desde, hasta, estId);
    List<Map<String,Object>> res = new ArrayList<>();
    for(Object[] r : rows) {
        Map<String,Object> m = new HashMap<>();
        m.put("dia", r[0]);
        m.put("cantidad", ((Number) r[1]).longValue());
        res.add(m);
    }
    return res;
}

    @Override
    public List<Map<String,Object>> conteoManualVsQr(LocalDate desde, LocalDate hasta, Long estId) {
        List<Object[]> rows = statsRepository.getConteoManualVsQr(desde, hasta, estId);
        List<Map<String,Object>> res = new ArrayList<>();
        long total = 0;
        for(Object[] r : rows) total += ((Number) r[1]).longValue();

        for(Object[] r : rows) {
            Map<String,Object> m = new HashMap<>();
            String modo = r[0] == null ? "MANUAL" : r[0].toString();
            long cnt = ((Number) r[1]).longValue();
            double pct = total == 0 ? 0.0 : (cnt * 100.0 / total);
            m.put("modo", modo);
            m.put("cantidad", cnt);
            m.put("porcentaje", Math.round(pct*100.0)/100.0);
            res.add(m);
        }
        return res;
    }


    @Override
    public List<Map<String,Object>> distribucionPorTipoVehiculo(LocalDate desde, LocalDate hasta, Long estId) {
        List<Object[]> rows = statsRepository.getDistribucionPorTipoVehiculo(desde, hasta, estId);
        long total = 0;
        for(Object[] r : rows) total += ((Number) r[1]).longValue();

        List<Map<String,Object>> res = new ArrayList<>();
        for(Object[] r : rows){
            Map<String,Object> m = new HashMap<>();
            String tipo = r[0] == null ? "Desconocido" : r[0].toString();
            long cnt = ((Number) r[1]).longValue();
            double pct = total == 0 ? 0.0 : (cnt*100.0/total);
            m.put("tipo", tipo);
            m.put("cantidad", cnt);
            m.put("porcentaje", Math.round(pct*100.0)/100.0);
            res.add(m);
        }
        return res;
    }

}

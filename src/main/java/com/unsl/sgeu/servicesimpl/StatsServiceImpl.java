package com.unsl.sgeu.servicesimpl;

import com.unsl.sgeu.dto.*;
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
        // 1. Obtener todos los estacionamientos activos
        List<Object[]> estacionamientos = statsRepository.getIngresosPorEstacionamiento(desde, hasta, estId);
        // 2. Obtener ingresos diarios por estacionamiento
        List<Object[]> ingresosPorDia = statsRepository.getIngresosPorDia(desde, hasta, estId);

        // Map<idEst, [nombre, capacidad]>
        Map<Long, Object[]> infoEst = new HashMap<>();
        for (Object[] r : estacionamientos) {
            Long id = ((Number) r[0]).longValue();
            infoEst.put(id, r);
        }

        // Map<idEst, Map<fecha, ingresos>>
        Map<Long, Map<String, Long>> ingresosDiariosPorEst = new HashMap<>();
        for (Object[] r : ingresosPorDia) {
            // Si la query de getIngresosPorDia no trae id_est, hay que ajustarla para que lo haga
            // Suponemos: [fecha, cantidad, id_est]
            String fecha = r[0].toString();
            Long cantidad = ((Number) r[1]).longValue();
            Long idEst = r.length > 2 ? ((Number) r[2]).longValue() : null;
            if (idEst == null) continue;
            ingresosDiariosPorEst.putIfAbsent(idEst, new HashMap<>());
            ingresosDiariosPorEst.get(idEst).put(fecha, cantidad);
        }

        List<Map<String, Object>> res = new ArrayList<>();
        for (Map.Entry<Long, Object[]> entry : infoEst.entrySet()) {
            Long id = entry.getKey();
            Object[] datos = entry.getValue();
            String nombre = datos[1].toString();
            int capacidad = datos[2] == null ? 0 : ((Number) datos[2]).intValue();
            long ingresosTotales = ((Number) datos[3]).longValue();

            Map<String, Long> ingresosPorFecha = ingresosDiariosPorEst.getOrDefault(id, new HashMap<>());
            double sumaPct = 0.0;
            int dias = 0;
            for (Long ingresosDia : ingresosPorFecha.values()) {
                if (capacidad > 0) {
                    double pctDia = Math.min(ingresosDia * 100.0 / capacidad, 100.0);
                    sumaPct += pctDia;
                    dias++;
                }
            }
            double promedioPct = (dias > 0) ? (sumaPct / dias) : 0.0;

            Map<String, Object> m = new HashMap<>();
            m.put("id", id);
            m.put("nombre", nombre);
            m.put("capacidad", capacidad);
            m.put("ingresos", ingresosTotales);
            m.put("porcentaje", Math.round(promedioPct * 100.0) / 100.0);
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

    @Override
    public StatsResponseDTO buildStatsDto(LocalDate desde, LocalDate hasta, Long estId) {
        StatsResponseDTO dto = new StatsResponseDTO();

        var pctCats = this.porcentajeCategorias(desde,hasta,estId);
        var lstCats = new java.util.ArrayList<CategoriaStatsDTO>();
        var categoriasLabels = new java.util.ArrayList<String>();
        var categoriasData = new java.util.ArrayList<Number>();
        for(var m : pctCats){
            java.util.Map<String,Object> map = (java.util.Map<String,Object>) m;
            String cat = map.getOrDefault("categoria","Sin categoria").toString();
            long cantidad = ((Number)map.getOrDefault("cantidad",0)).longValue();
            double pct = ((Number)map.getOrDefault("porcentaje",0)).doubleValue();
            lstCats.add(new CategoriaStatsDTO(cat,cantidad,pct));
            categoriasLabels.add(cat);
            categoriasData.add(cantidad);
        }
        dto.setCategorias(lstCats);
        dto.setCategoriasLabels(categoriasLabels);
        dto.setCategoriasData(categoriasData);

        var estTopMap = this.estacionamientoConMasIngresos(desde,hasta,estId);
        if(estTopMap != null && !estTopMap.isEmpty()){
            java.util.Map<String,Object> top = (java.util.Map<String,Object>) estTopMap;
            EstacionamientoStatsDTO est = new EstacionamientoStatsDTO(
                ((Number)top.getOrDefault("id",0)).longValue(),
                top.getOrDefault("nombre","\"").toString(),
                ((Number)top.getOrDefault("capacidad",0)).intValue(),
                ((Number)top.getOrDefault("ingresos",0)).longValue(),
                ((Number)top.getOrDefault("porcentaje_capacidad",0)).doubleValue()
            );
            dto.setEstacionamientoTop(est);
        }

        var ocup = this.porcentajeOcupacionPorEstacionamiento(desde,hasta,estId);
        var ocupLabels = new java.util.ArrayList<String>();
        var ocupData = new java.util.ArrayList<Number>();
        var estList = new java.util.ArrayList<EstacionamientoStatsDTO>();
        for(var m : ocup){
            java.util.Map<String,Object> map = (java.util.Map<String,Object>) m;
            String nombre = map.getOrDefault("nombre","-").toString();
            Integer cap = ((Number)map.getOrDefault("capacidad",0)).intValue();
            Long ingresos = ((Number)map.getOrDefault("ingresos",0)).longValue();
            Double pct = ((Number)map.getOrDefault("porcentaje",0)).doubleValue();
            ocupLabels.add(nombre); ocupData.add(pct);
            estList.add(new EstacionamientoStatsDTO(((Number)map.getOrDefault("id",0)).longValue(), nombre, cap, ingresos, pct));
        }
        dto.setOcupacionLabels(ocupLabels);
        dto.setOcupacionData(ocupData);
        dto.setPorcentajeOcupacion(estList);

        var evo = this.evolucionIngresosDiarios(desde,hasta,estId);
        var evoList = new java.util.ArrayList<EvolucionDTO>();
        var evoLabels = new java.util.ArrayList<String>();
        var evoData = new java.util.ArrayList<Number>();
        for(var m: evo){
            java.util.Map<String,Object> map = (java.util.Map<String,Object>) m;
            String dia = map.getOrDefault("dia","-").toString();
            Long cnt = ((Number)map.getOrDefault("cantidad",0)).longValue();
            evoList.add(new EvolucionDTO(dia, cnt));
            evoLabels.add(dia); evoData.add(cnt);
        }
        dto.setEvolucion(evoList);
        dto.setEvolucionLabels(evoLabels);
        dto.setEvolucionData(evoData);

        var modos = this.conteoManualVsQr(desde,hasta,estId);
        var modoList = new java.util.ArrayList<ModoConteoDTO>();
        for(var m: modos){
            java.util.Map<String,Object> map = (java.util.Map<String,Object>) m;
            String modo = map.getOrDefault("modo","MANUAL").toString();
            Long cnt = ((Number)map.getOrDefault("cantidad",0)).longValue();
            Double pct = ((Number)map.getOrDefault("porcentaje",0)).doubleValue();
            modoList.add(new ModoConteoDTO(modo, cnt, pct));
        }
        dto.setModoConteo(modoList);

        var horarios = this.horariosPicoIngresos(desde,hasta,5,estId);
        var horariosList = new java.util.ArrayList<HorarioDTO>();
        for(var m: horarios){
            java.util.Map<String,Object> map = (java.util.Map<String,Object>) m;
            Integer hora = ((Number)map.getOrDefault("hora",0)).intValue();
            Long cnt = ((Number)map.getOrDefault("cantidad",0)).longValue();
            horariosList.add(new HorarioDTO(hora, cnt));
        }
        dto.setHorariosPico(horariosList);

        var distrib = this.distribucionPorTipoVehiculo(desde,hasta,estId);
        var tipoList = new java.util.ArrayList<TipoVehiculoDTO>();
        for(var m: distrib){
            java.util.Map<String,Object> map = (java.util.Map<String,Object>) m;
            String tipo = map.getOrDefault("tipo","Desconocido").toString();
            Long cnt = ((Number)map.getOrDefault("cantidad",0)).longValue();
            Double pct = ((Number)map.getOrDefault("porcentaje",0)).doubleValue();
            tipoList.add(new TipoVehiculoDTO(tipo, cnt, pct));
        }
        dto.setDistribucionTipoVehiculo(tipoList);

        return dto;
    }

}

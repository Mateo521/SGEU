package com.unsl.sgeu.controllers;

import com.unsl.sgeu.services.StatsService;
import com.unsl.sgeu.repositories.EstacionamientoRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
import com.unsl.sgeu.dto.*;

@Controller
public class StatsController {

    private final StatsService statsService;
    private final EstacionamientoRepository estacionamientoRepository;

    public StatsController(StatsService statsService, EstacionamientoRepository estacionamientoRepository){
        this.statsService = statsService;
        this.estacionamientoRepository = estacionamientoRepository;
    }

    @GetMapping("/stats")
    public String stats(){
        return "redirect:/estadisticas";
    }

    // Vista Thymeleaf final en /estadisticas
    @GetMapping(value = "/estadisticas", produces = MediaType.TEXT_HTML_VALUE)
    public String estadisticasHtml(Model model,
                                   @RequestParam(value = "desde", required = false) String desdeStr,
                                   @RequestParam(value = "hasta", required = false) String hastaStr,
                                   @RequestParam(value = "estId", required = false) Long estId){
        LocalDate desde = (desdeStr==null||desdeStr.isBlank())?null:LocalDate.parse(desdeStr);
        LocalDate hasta = (hastaStr==null||hastaStr.isBlank())?null:LocalDate.parse(hastaStr);

        StatsResponseDTO dto = buildStatsDto(desde, hasta, estId);

        // llenar modelo desde DTO para la vista
        model.addAttribute("porcentajeCategorias", dto.getCategorias());
        model.addAttribute("estacionamientoTop", dto.getEstacionamientoTop());
        model.addAttribute("diaSemanaTop", dto.getHorariosPico()); // nota: reuso para mostrar
        model.addAttribute("horariosPico", dto.getHorariosPico());
        model.addAttribute("promedioEstancia", dto.getPromedioEstancia());
        model.addAttribute("ocupacionPorEst", dto.getEstacionamientoTop()!=null?java.util.List.of(dto.getEstacionamientoTop()):java.util.List.of());
        model.addAttribute("evolucion", dto.getEvolucion());
        model.addAttribute("modoConteo", dto.getModoConteo());
        model.addAttribute("categoriasLabels", dto.getCategoriasLabels());
        model.addAttribute("categoriasData", dto.getCategoriasData());
        model.addAttribute("ocupacionLabels", dto.getOcupacionLabels());
        model.addAttribute("ocupacionData", dto.getOcupacionData());
        model.addAttribute("evolucionLabels", dto.getEvolucionLabels());
        model.addAttribute("evolucionData", dto.getEvolucionData());

        // lista de estacionamientos para el select
        java.util.List<java.util.Map<String,Object>> ests = new java.util.ArrayList<>();
        for(var e: estacionamientoRepository.findAll()){
            java.util.Map<String,Object> m = new java.util.HashMap<>();
            m.put("id", e.getIdEst());
            m.put("nombre", e.getNombre());
            ests.add(m);
        }
        model.addAttribute("estacionamientos", ests);
        model.addAttribute("selectedEstId", estId);

        return "stats";
    }

    // JSON REST en la misma ruta /estadisticas para Accept: application/json
    @GetMapping(value = "/estadisticas", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String,Object>> estadisticasJson(@RequestParam(value = "desde", required = false) String desdeStr,
                                                                 @RequestParam(value = "hasta", required = false) String hastaStr,
                                                                 @RequestParam(value = "estId", required = false) Long estId){
        LocalDate desde = (desdeStr==null||desdeStr.isBlank())?null:LocalDate.parse(desdeStr);
        LocalDate hasta = (hastaStr==null||hastaStr.isBlank())?null:LocalDate.parse(hastaStr);

        StatsResponseDTO dto = buildStatsDto(desde, hasta, estId);
        return ResponseEntity.ok(Map.of("data", dto));
    }

    // Nota: la API de estacionamientos ya está disponible en EstacionamientoController (/api/estacionamientos)

    // Construye y mapea la respuesta a DTO (evita llamadas duplicadas)
    private StatsResponseDTO buildStatsDto(LocalDate desde, LocalDate hasta, Long estId){
        StatsResponseDTO dto = new StatsResponseDTO();

        var pctCats = statsService.porcentajeCategorias(desde,hasta,estId);
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
        var estTopMap = statsService.estacionamientoConMasIngresos(desde,hasta,estId);
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
        var ocup = statsService.porcentajeOcupacionPorEstacionamiento(desde,hasta,estId);
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
        // Exponer la lista detallada para la tabla en el frontend
        dto.setPorcentajeOcupacion(estList);

        var evo = statsService.evolucionIngresosDiarios(desde,hasta,estId);
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

        var modos = statsService.conteoManualVsQr(desde,hasta,estId);
        var modoList = new java.util.ArrayList<ModoConteoDTO>();
        for(var m: modos){
            java.util.Map<String,Object> map = (java.util.Map<String,Object>) m;
            String modo = map.getOrDefault("modo","MANUAL").toString();
            Long cnt = ((Number)map.getOrDefault("cantidad",0)).longValue();
            Double pct = ((Number)map.getOrDefault("porcentaje",0)).doubleValue();
            modoList.add(new ModoConteoDTO(modo, cnt, pct));
        }
        dto.setModoConteo(modoList);

        var horarios = statsService.horariosPicoIngresos(desde,hasta,5,estId);
        var horariosList = new java.util.ArrayList<HorarioDTO>();
        for(var m: horarios){
            java.util.Map<String,Object> map = (java.util.Map<String,Object>) m;
            Integer hora = ((Number)map.getOrDefault("hora",0)).intValue();
            Long cnt = ((Number)map.getOrDefault("cantidad",0)).longValue();
            horariosList.add(new HorarioDTO(hora, cnt));
        }
        dto.setHorariosPico(horariosList);

        dto.setPromedioEstancia(statsService.promedioEstancia(desde,hasta,estId));

        return dto;
    }
}

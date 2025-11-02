package com.unsl.sgeu.controllers;

import com.unsl.sgeu.services.EstacionamientoService;
import com.unsl.sgeu.services.StatsService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.Map;
 
import com.unsl.sgeu.dto.*;

@Controller
public class StatsController {

    private final StatsService statsService;
    private final EstacionamientoService estacionamientoService;

    public StatsController(StatsService statsService, EstacionamientoService estacionamientoService){
        this.statsService = statsService;
        this.estacionamientoService = estacionamientoService;
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

        StatsResponseDTO dto = statsService.buildStatsDto(desde, hasta, estId);

        // llenar modelo desde DTO para la vista
        model.addAttribute("porcentajeCategorias", dto.getCategorias());
        model.addAttribute("estacionamientoTop", dto.getEstacionamientoTop());
        model.addAttribute("diaSemanaTop", dto.getHorariosPico()); // nota: reuso para mostrar
        model.addAttribute("horariosPico", dto.getHorariosPico());
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
        for(var e: estacionamientoService.listarTodos()){
            java.util.Map<String,Object> m = new java.util.HashMap<>();
            m.put("id", e.getId());
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

    StatsResponseDTO dto = statsService.buildStatsDto(desde, hasta, estId);
        return ResponseEntity.ok(Map.of("data", dto));
    }
}

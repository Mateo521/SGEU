// src/main/java/com/unsl/sgeu/controllers/TurnoController.java
package com.unsl.sgeu.controllers;

import com.unsl.sgeu.dto.*;
import com.unsl.sgeu.services.TurnoService;
import com.unsl.sgeu.util.Pagina;
import jakarta.validation.Valid;
import org.springframework.http.*;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@CrossOrigin // si la UI corre en otro host/puerto
@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

    private final TurnoService service;

    public TurnoController(TurnoService service) {
        this.service = service;
    }

    @PutMapping("/{id}/finalizar")
    public TurnoDTO finalizar(@PathVariable Long id) {
        return service.finalizar(id);
    }

    // GET /api/turnos?empleadoId=&estId=&fecha=&page=&size=
    @GetMapping
    public Pagina<TurnoDTO> list(
            @RequestParam(required = false) Long empleadoId,
            @RequestParam(required = false) Long estId,
            @RequestParam(required = false) String fecha,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        List<TurnoDTO> turnos = service.list(empleadoId, estId, fecha, page, size);
        long total = service.count(empleadoId, estId, fecha);
        return new Pagina<>(turnos, page, size, total);
    }

    // GET /api/turnos/range?desde=2025-10-01&hasta=2025-10-31
    @GetMapping("/range")
    public Pagina<TurnoDTO> listRange(
            @RequestParam(required = false) Long empleadoId,
            @RequestParam(required = false) Long estId,
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        List<TurnoDTO> turnos = service.listRange(empleadoId, estId, desde, hasta, page, size);
        long total = service.countRange(empleadoId, estId, desde, hasta);
        return new Pagina<>(turnos, page, size, total);
    }

    @GetMapping("/{id}")
    public TurnoDTO get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public ResponseEntity<TurnoDTO> create(@Valid @RequestBody TurnoCreateDTO dto) {
        TurnoDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created); // 201
    }

    @PutMapping("/{id}")
    public TurnoDTO update(@PathVariable Long id, @Valid @RequestBody TurnoCreateDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build(); // 204
    }
}

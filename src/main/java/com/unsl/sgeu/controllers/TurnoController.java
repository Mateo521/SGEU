// src/main/java/com/unsl/sgeu/controllers/TurnoController.java
package com.unsl.sgeu.controllers;

import com.unsl.sgeu.dto.*;
import com.unsl.sgeu.services.TurnoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@CrossOrigin // si la UI corre en otro host/puerto
@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

    private final TurnoService service;

    public TurnoController(TurnoService service) {
        this.service = service;
    }

    //este finaliza un turno (osea le da valor a fecha fin)
    @PutMapping("/{id}/finalizar")
    public TurnoDTO finalizar(@PathVariable Long id) {
        return service.finalizar(id);
    }

    // GET /api/turnos?empleadoId=&estId=&fecha=&page=&size=
    @GetMapping
    public Page<TurnoDTO> list(
            @RequestParam(required = false) Long empleadoId,
            @RequestParam(required = false) Long estId,
            @RequestParam(required = false) String fecha,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return service.list(empleadoId, estId, fecha, page, size);
    }

    // GET /api/turnos/range?desde=2025-10-01&hasta=2025-10-31
    @GetMapping("/range")
    public Page<TurnoDTO> listRange(
            @RequestParam(required = false) Long empleadoId,
            @RequestParam(required = false) Long estId,
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return service.listRange(empleadoId, estId, desde, hasta, page, size);
    }

    @GetMapping("/{id}")
    public TurnoDTO get(@PathVariable Long id) {
        return service.get(id);
    }

    //Este recibe el formulario de el front
    @PostMapping
    public ResponseEntity<TurnoDTO> create(@Valid @RequestBody TurnoCreateDTO dto) {
        TurnoDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created); // 201
    }
    //este es para editar un turno existente
    @PutMapping("/{id}")
    public TurnoDTO update(@PathVariable Long id, @Valid @RequestBody TurnoCreateDTO dto) {
        return service.update(id, dto);
    }
    // eliminar un turno
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build(); // 204
    }
}

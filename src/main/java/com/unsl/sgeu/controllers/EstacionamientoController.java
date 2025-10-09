// Solo por si lo querés ya listo
package com.unsl.sgeu.controllers;

import com.unsl.sgeu.dto.EstacionamientoDTO;
import com.unsl.sgeu.services.EstacionamientoService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/estacionamientos")
public class EstacionamientoController {

    private final EstacionamientoService service;

    public EstacionamientoController(EstacionamientoService service) {
        this.service = service;
    }

    @GetMapping
    public List<EstacionamientoDTO> listarActivos() {
        return service.listarActivos();
    }

    @GetMapping("/todos")
    public List<EstacionamientoDTO> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public EstacionamientoDTO obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    public ResponseEntity<EstacionamientoDTO> crear(@Valid @RequestBody EstacionamientoDTO dto) {
        EstacionamientoDTO creado = service.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public EstacionamientoDTO editar(@PathVariable Long id, @Valid @RequestBody EstacionamientoDTO dto) {
        return service.editar(id, dto);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        boolean estado = Boolean.TRUE.equals(body.get("estado"));
        service.cambiarEstado(id, estado);
        return ResponseEntity.noContent().build();
    }
}

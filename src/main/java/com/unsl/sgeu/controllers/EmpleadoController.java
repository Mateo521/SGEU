// src/main/java/com/unsl/sgeu/controllers/EmpleadoController.java
package com.unsl.sgeu.controllers;

import com.unsl.sgeu.dto.EmpleadoDTO;
import com.unsl.sgeu.services.EmpleadoServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/empleados")
@CrossOrigin
public class EmpleadoController {

    private final EmpleadoServices empleadoServices;

    public EmpleadoController(EmpleadoServices empleadoServices) {
        this.empleadoServices = empleadoServices;
    }

    @GetMapping("/guardias")
    public ResponseEntity<List<EmpleadoDTO>> listarGuardias() {
        List<EmpleadoDTO> guardias = empleadoServices.listarGuardias();
        return ResponseEntity.ok(guardias);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpleadoDTO> actualizarEmpleado(
            @PathVariable Long id,
            @RequestBody EmpleadoDTO dto) {
        try {
            EmpleadoDTO actualizado = empleadoServices.actualizarEmpleado(id, dto);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}

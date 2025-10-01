package com.unsl.sgeu.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.unsl.sgeu.repositories.*;

import java.util.Map;

import com.unsl.sgeu.models.*;

@RestController
@RequestMapping("/qr")
public class QRController {

    private final VehiculoRepository vehiculoRepo;

    public QRController(VehiculoRepository vehiculoRepo) {
        this.vehiculoRepo = vehiculoRepo;
    }

    @PostMapping("/leer")
    public ResponseEntity<?> leerQR(@RequestBody LeerQR qr) {
        Vehiculo v = vehiculoRepo.findByCodigoQr(qr.getCodigo());

        if (v == null) {
            return ResponseEntity.status(404).body(Map.of("mensaje", "Vehículo desconocido"));
        }

        return ResponseEntity.ok(Map.of(
                "mensaje", "✅ Vehículo encontrado",
                "patente", v.getPatente(),
                "modelo", v.getModelo(),
                "color", v.getColor(),
                "dniDuenio", v.getDuenio().getDni()));
    }

}

package com.unsl.sgeu.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.unsl.sgeu.services.*;
import com.unsl.sgeu.models.*;
import java.util.Map;

@RestController
@RequestMapping("/leerqr")
public class QRController {

    private final VehiculoService vehiculoService;
    private final PersonaVehiculoService personaVehiculoService;

    public QRController(VehiculoService vehiculoService,
                        PersonaVehiculoService personaVehiculoService) {
        this.vehiculoService = vehiculoService;
        this.personaVehiculoService = personaVehiculoService;
    }

    @PostMapping("/leer")
    public ResponseEntity<?> leerQR(@RequestBody Map<String, String> request) {
        String codigo = request.get("codigo");

        Vehiculo v = vehiculoService.buscarPorQr(codigo);
        if (v == null) {
            return ResponseEntity.status(404).body(Map.of("mensaje", "Vehículo desconocido"));
        }

        // dueños por la tabla N:M
        var dnisDuenios = personaVehiculoService.obtenerDnisPorPatente(v.getPatente());
        // opcional: nombres de dueños
        // var personas = personaVehiculoService.obtenerPersonasPorPatente(v.getPatente());

        return ResponseEntity.ok(Map.of(
            "mensaje", "✅ Vehículo encontrado",
            "patente", v.getPatente(),
            "modelo", v.getModelo(),
            "color",  v.getColor(),
            "duenios", dnisDuenios  // o personas si querés devolver objeto completo
        ));
    }
}

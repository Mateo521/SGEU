package com.unsl.sgeu.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.unsl.sgeu.services.*;
import com.unsl.sgeu.models.*;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/leerqr")
public class QRController {

    private static final Logger logger = LoggerFactory.getLogger(QRController.class);
    private final VehiculoService vehiculoService;

    public QRController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
        logger.info("QRController creado correctamente");
    }

    @PostConstruct
    public void init() {
        logger.info("QRController inicializado - Endpoint disponible en: /leerqr/leer");
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        logger.info("Endpoint de test llamado");
        return ResponseEntity.ok("QRController funcionando correctamente");
    }

    @PostMapping("/leer")
    public ResponseEntity<?> leerQR(@RequestBody Map<String, String> request) {
        String codigo = request.get("codigo");
        logger.info("Recibida petición para leer QR: {}", codigo);

        Vehiculo v = vehiculoService.buscarPorQr(codigo);

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

package com.unsl.sgeu.controllers;
import com.unsl.sgeu.models.Estacionamiento;
import com.unsl.sgeu.services.RegistroEstacionamientoService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collections;
import java.util.List;


@RestController
public class AlertaController {


    @Autowired
    private RegistroEstacionamientoService registroEstacionamientoService;

    @GetMapping("/api/alertas/vencidas")
    public ResponseEntity<List<String>> obtenerAlertasVencidas(HttpSession session) {
        Estacionamiento est = (Estacionamiento) session.getAttribute("estacionamiento");

        // Si no hay estacionamiento en la sesión, devolvemos una lista vacía.
        if (est == null) {
            System.out.println("Estacionamiento NULL");
            return ResponseEntity.ok(Collections.emptyList());
        }

        // Obtenemos la lista de matrículas directamente del servicio.
        List<String> patentesVencidas = registroEstacionamientoService.obtenerPatentesAdentroMasDeCuatroHoras(est);

        // Devolvemos la lista. Spring la convertirá en un arreglo JSON.
        // Si la lista está vacía, se enviará un arreglo vacío: []
        System.out.println("Hay estacionamiento, devuelve la lista de tamaño"+ patentesVencidas.size());
        return ResponseEntity.ok(patentesVencidas);
    }
}


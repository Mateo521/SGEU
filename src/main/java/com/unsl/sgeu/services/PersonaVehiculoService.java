package com.unsl.sgeu.services;

import com.unsl.sgeu.models.Persona;
import com.unsl.sgeu.models.Vehiculo;
import com.unsl.sgeu.repositories.PersonaRepository;
import com.unsl.sgeu.repositories.VehiculoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

// PersonaVehiculoService.java
@Service
public class PersonaVehiculoService {

    private final PersonaRepository personaRepository;
    private final DataSource dataSource;

    public PersonaVehiculoService(PersonaRepository personaRepository, DataSource dataSource) {
        this.personaRepository = personaRepository;
        this.dataSource = dataSource;
    }

    @Transactional
    public void vincular(Long dni, String patente) { /* ... como ya te pasé ... */ }

    @Transactional
    public java.util.List<Long> obtenerDnisPorPatente(String patente) {
        java.util.List<Long> dnis = new java.util.ArrayList<>();
        try (var conn = dataSource.getConnection();
             var ps = conn.prepareStatement(
                     "SELECT dni FROM persona_vehiculo WHERE patente = ?")) {
            ps.setString(1, patente);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) dnis.add(rs.getLong(1));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error consultando dueños por patente", e);
        }
        return dnis;
    }

    

    @Transactional
    public java.util.List<Persona> obtenerPersonasPorPatente(String patente) {
        var dnis = obtenerDnisPorPatente(patente);
        if (dnis.isEmpty()) return java.util.List.of();
        return personaRepository.findAllById(dnis);
    }

    
}

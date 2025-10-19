package com.unsl.sgeu.repositories;

import com.unsl.sgeu.config.DatabaseConnection;
import com.unsl.sgeu.models.Persona;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PersonaRepositoryImpl implements PersonaRepository {

    private final DatabaseConnection databaseConnection;

    @Autowired
    public PersonaRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public List<Persona> findAll() {
        List<Persona> personas = new ArrayList<>();
        String sql = "SELECT * FROM persona";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                personas.add(mapPersona(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return personas;
    }

    @Override
    public Optional<Persona> findById(Long id) {
        String sql = "SELECT * FROM persona WHERE dni = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapPersona(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Persona> findByEmailIgnoreCase(String email) {
        String sql = "SELECT * FROM persona WHERE LOWER(email) = LOWER(?)";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapPersona(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Persona> findByTelefono(String telefono) {
        String sql = "SELECT * FROM persona WHERE telefono = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, telefono);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapPersona(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public boolean existsByDni(Long dni) {
        String sql = "SELECT COUNT(*) FROM persona WHERE dni = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, dni);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Persona save(Persona persona) {
        String sql = "INSERT INTO persona(dni, nombre, telefono, email, id_categoria, categoria) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, persona.getDni());
            stmt.setString(2, persona.getNombre());
            stmt.setString(3, persona.getTelefono());
            stmt.setString(4, persona.getEmail());
            stmt.setObject(5, persona.getIdCategoria(), Types.INTEGER);
            stmt.setString(6, persona.getCategoria());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return persona;
    }

    @Override
    public void update(Persona persona) {
        String sql = "UPDATE persona SET nombre = ?, telefono = ?, email = ?, id_categoria = ?, categoria = ? " +
                     "WHERE dni = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, persona.getNombre());
            stmt.setString(2, persona.getTelefono());
            stmt.setString(3, persona.getEmail());
            stmt.setObject(4, persona.getIdCategoria(), Types.INTEGER);
            stmt.setString(5, persona.getCategoria());
            stmt.setLong(6, persona.getDni());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM persona WHERE dni = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Persona mapPersona(ResultSet rs) throws SQLException {
        Persona p = new Persona();
        p.setDni(rs.getLong("dni"));
        p.setNombre(rs.getString("nombre"));
        p.setTelefono(rs.getString("telefono"));
        p.setEmail(rs.getString("email"));
        p.setIdCategoria(rs.getInt("id_categoria"));
        p.setCategoria(rs.getString("categoria"));
        return p;
    }
}

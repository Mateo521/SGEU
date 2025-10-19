package com.unsl.sgeu.repositories;

import com.unsl.sgeu.config.DatabaseConnection;
import com.unsl.sgeu.models.Estacionamiento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class EstacionamientoRepositoryImpl implements EstacionamientoRepository {

    private final DatabaseConnection databaseConnection;

    @Autowired
    public EstacionamientoRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public List<Estacionamiento> findAll() {
        List<Estacionamiento> lista = new ArrayList<>();
        String sql = "SELECT * FROM estacionamiento";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapEstacionamiento(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Estacionamiento> findByEstadoTrue() {
        return findByEstado(true);
    }

    @Override
    public List<Estacionamiento> findByEstadoFalse() {
        return findByEstado(false);
    }

    private List<Estacionamiento> findByEstado(boolean estado) {
        List<Estacionamiento> lista = new ArrayList<>();
        String sql = "SELECT * FROM estacionamiento WHERE estado = ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, estado);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapEstacionamiento(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean existsByNombreIgnoreCase(String nombre) {
        String sql = "SELECT COUNT(*) FROM estacionamiento WHERE LOWER(nombre) = LOWER(?)";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre);
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
    public Optional<Estacionamiento> findById(Long id) {
        String sql = "SELECT * FROM estacionamiento WHERE id_est = ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapEstacionamiento(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public void save(Estacionamiento estacionamiento) {
        if (estacionamiento.getIdEst() == null) {
            // INSERT
            String sql = "INSERT INTO estacionamiento(nombre, direccion, capacidad, estado) VALUES (?, ?, ?, ?)";

            try (Connection conn = databaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                stmt.setString(1, estacionamiento.getNombre());
                stmt.setString(2, estacionamiento.getDireccion());
                stmt.setInt(3, estacionamiento.getCapacidad());
                stmt.setBoolean(4, estacionamiento.getEstado());

                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        estacionamiento.setIdEst(rs.getLong(1));
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            // UPDATE
            String sql = "UPDATE estacionamiento SET nombre = ?, direccion = ?, capacidad = ?, estado = ? WHERE id_est = ?";

            try (Connection conn = databaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, estacionamiento.getNombre());
                stmt.setString(2, estacionamiento.getDireccion());
                stmt.setInt(3, estacionamiento.getCapacidad());
                stmt.setBoolean(4, estacionamiento.getEstado());
                stmt.setLong(5, estacionamiento.getIdEst());

                stmt.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM estacionamiento WHERE id_est = ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Estacionamiento mapEstacionamiento(ResultSet rs) throws SQLException {
        Estacionamiento e = new Estacionamiento();
        e.setIdEst(rs.getLong("id_est"));
        e.setNombre(rs.getString("nombre"));
        e.setDireccion(rs.getString("direccion"));
        e.setCapacidad(rs.getInt("capacidad"));
        e.setEstado(rs.getBoolean("estado"));
        return e;
    }
}

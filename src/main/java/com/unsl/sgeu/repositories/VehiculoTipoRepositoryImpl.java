package com.unsl.sgeu.repositories;

import com.unsl.sgeu.config.DatabaseConnection;
import com.unsl.sgeu.models.VehiculoTipo;
import com.unsl.sgeu.repositories.VehiculoTipoRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class VehiculoTipoRepositoryImpl implements VehiculoTipoRepository {

    private final DatabaseConnection databaseConnection;

    public VehiculoTipoRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    // -------------------- CRUD Básico --------------------

    @Override
    public VehiculoTipo save(VehiculoTipo vehiculoTipo) {
        String sql = "INSERT INTO vehiculo_tipo(nombre) VALUES (?)";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, vehiculoTipo.getNombre());
            stmt.executeUpdate();

            // Recuperar ID generado automáticamente
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                vehiculoTipo.setId(rs.getShort(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vehiculoTipo;
    }

    @Override
    public void update(VehiculoTipo vehiculoTipo) {
        String sql = "UPDATE vehiculo_tipo SET nombre = ? WHERE id_vehiculo_tipo = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, vehiculoTipo.getNombre());
            stmt.setShort(2, vehiculoTipo.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(Short id) {
        String sql = "DELETE FROM vehiculo_tipo WHERE id_vehiculo_tipo = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setShort(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<VehiculoTipo> findById(Short id) {
        String sql = "SELECT * FROM vehiculo_tipo WHERE id_vehiculo_tipo = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setShort(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(mapVehiculoTipo(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<VehiculoTipo> findAll() {
        List<VehiculoTipo> lista = new ArrayList<>();
        String sql = "SELECT * FROM vehiculo_tipo ORDER BY nombre";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) lista.add(mapVehiculoTipo(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // -------------------- Búsquedas específicas --------------------

    @Override
    public Optional<VehiculoTipo> findByNombreIgnoreCase(String nombre) {
        String sql = "SELECT * FROM vehiculo_tipo WHERE LOWER(nombre) = LOWER(?)";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(mapVehiculoTipo(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public boolean existsByNombreIgnoreCase(String nombre) {
        String sql = "SELECT COUNT(*) AS total FROM vehiculo_tipo WHERE LOWER(nombre) = LOWER(?)";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("total") > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // -------------------- Mapper --------------------
    private VehiculoTipo mapVehiculoTipo(ResultSet rs) throws SQLException {
        VehiculoTipo vt = new VehiculoTipo();
        vt.setId(rs.getShort("id_vehiculo_tipo"));
        vt.setNombre(rs.getString("nombre"));
        return vt;
    }
}

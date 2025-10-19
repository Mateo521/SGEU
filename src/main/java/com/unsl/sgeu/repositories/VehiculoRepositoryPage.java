package com.unsl.sgeu.repositories;

import com.unsl.sgeu.config.DatabaseConnection;
import com.unsl.sgeu.models.Vehiculo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class VehiculoRepositoryPage {

    private final DatabaseConnection databaseConnection;

    @Autowired
    public VehiculoRepositoryPage(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    // Todos los vehículos paginados
    public List<Vehiculo> obtenerTodosPaginado(int limit, int offset) {
        List<Vehiculo> vehiculos = new ArrayList<>();
        String sql = "SELECT * FROM vehiculo ORDER BY patente DESC LIMIT ? OFFSET ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            stmt.setInt(2, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vehiculos.add(mapVehiculo(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vehiculos;
    }

    // Vehículos de un guardia (dniDuenio)
    public List<Vehiculo> obtenerTodosPorGuardiaPaginado(Long dniDuenio, int limit, int offset) {
        List<Vehiculo> vehiculos = new ArrayList<>();
        String sql = "SELECT * FROM vehiculo WHERE dni_duenio = ? ORDER BY patente DESC LIMIT ? OFFSET ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, dniDuenio);
            stmt.setInt(2, limit);
            stmt.setInt(3, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vehiculos.add(mapVehiculo(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vehiculos;
    }

    // Buscar por patente
    public List<Vehiculo> buscarVehiculosPorPatentePaginado(String patente, int limit, int offset) {
        List<Vehiculo> vehiculos = new ArrayList<>();
        String sql = "SELECT * FROM vehiculo WHERE LOWER(patente) LIKE ? ORDER BY patente DESC LIMIT ? OFFSET ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + patente.toLowerCase() + "%");
            stmt.setInt(2, limit);
            stmt.setInt(3, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vehiculos.add(mapVehiculo(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vehiculos;
    }

    // Buscar por patente y guardia
    public List<Vehiculo> buscarPorPatenteYGuardiaPaginado(String patente, Long dniDuenio, int limit, int offset) {
        List<Vehiculo> vehiculos = new ArrayList<>();
        String sql = "SELECT * FROM vehiculo WHERE LOWER(patente) LIKE ? AND dni_duenio = ? ORDER BY patente DESC LIMIT ? OFFSET ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + patente.toLowerCase() + "%");
            stmt.setLong(2, dniDuenio);
            stmt.setInt(3, limit);
            stmt.setInt(4, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vehiculos.add(mapVehiculo(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vehiculos;
    }

    // Mapeo del ResultSet a Vehiculo
    private Vehiculo mapVehiculo(ResultSet rs) throws SQLException {
        Vehiculo v = new Vehiculo();
        v.setPatente(rs.getString("patente"));
        v.setCodigoQr(rs.getString("codigo_qr"));
        v.setModelo(rs.getString("modelo"));
        v.setColor(rs.getString("color"));
        v.setIdVehiculoTipo(rs.getInt("id_vehiculo_tipo"));
        v.setDniDuenio(rs.getLong("dni_duenio"));
        v.setTipo(rs.getString("tipo"));
        // Nota: propietario queda null, se podría traer con un JOIN si hace falta
        return v;
    }
}

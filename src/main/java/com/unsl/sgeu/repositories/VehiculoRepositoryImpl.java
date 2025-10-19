package com.unsl.sgeu.repositories;

import com.unsl.sgeu.config.DatabaseConnection;
import com.unsl.sgeu.models.Vehiculo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehiculoRepositoryImpl implements VehiculoRepository {

    private DatabaseConnection databaseConnection;

    public VehiculoRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    // -------------------- CRUD Básico --------------------
    @Override
    public Vehiculo save(Vehiculo vehiculo) {
        String sql = "INSERT INTO vehiculo(patente, codigo_qr, modelo, color, id_vehiculo_tipo, dni_duenio, tipo) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, vehiculo.getPatente());
            stmt.setString(2, vehiculo.getCodigoQr());
            stmt.setString(3, vehiculo.getModelo());
            stmt.setString(4, vehiculo.getColor());
            if (vehiculo.getIdVehiculoTipo() != null) stmt.setInt(5, vehiculo.getIdVehiculoTipo());
            else stmt.setNull(5, Types.INTEGER);
            if (vehiculo.getDniDuenio() != null) stmt.setLong(6, vehiculo.getDniDuenio());
            else stmt.setNull(6, Types.BIGINT);
            stmt.setString(7, vehiculo.getTipo());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehiculo;
    }
    @Override
    public void update(Vehiculo vehiculo) {
        String sql = "UPDATE vehiculo SET codigo_qr = ?, modelo = ?, color = ?, id_vehiculo_tipo = ?, dni_duenio = ?, tipo = ? " +
                     "WHERE patente = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, vehiculo.getCodigoQr());
            stmt.setString(2, vehiculo.getModelo());
            stmt.setString(3, vehiculo.getColor());
            if (vehiculo.getIdVehiculoTipo() != null) stmt.setInt(4, vehiculo.getIdVehiculoTipo());
            else stmt.setNull(4, Types.INTEGER);
            if (vehiculo.getDniDuenio() != null) stmt.setLong(5, vehiculo.getDniDuenio());
            else stmt.setNull(5, Types.BIGINT);
            stmt.setString(6, vehiculo.getTipo());
            stmt.setString(7, vehiculo.getPatente());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void delete(String patente) {
        String sql = "DELETE FROM vehiculo WHERE patente = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patente);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public Vehiculo findByPatente(String patente) {
        String sql = "SELECT * FROM vehiculo WHERE patente = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patente);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapVehiculo(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public List<Vehiculo> findAll() {
        List<Vehiculo> lista = new ArrayList<>();
        String sql = "SELECT * FROM vehiculo ORDER BY patente";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) lista.add(mapVehiculo(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    @Override
    public Vehiculo findByCodigoQr(String codigoQr) {
        String sql = "SELECT * FROM vehiculo WHERE codigo_qr = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigoQr);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapVehiculo(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public List<Vehiculo> findByPatenteContainingIgnoreCase(String patente) {
        List<Vehiculo> lista = new ArrayList<>();
        String sql = "SELECT * FROM vehiculo WHERE UPPER(patente) LIKE UPPER(?) ORDER BY patente";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + patente + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) lista.add(mapVehiculo(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // -------------------- Métodos con RegistroEstacionamiento --------------------
    @Override
    public List<Vehiculo> findVehiculosByEstacionamiento(Long idEstacionamiento) {
        List<Vehiculo> lista = new ArrayList<>();
        String sql = "SELECT DISTINCT v.* FROM vehiculo v " +
                     "JOIN registro_estacionamiento r ON v.patente = r.patente " +
                     "WHERE r.id_estacionamiento = ? ORDER BY v.patente";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idEstacionamiento);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) lista.add(mapVehiculo(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    @Override
    public List<Vehiculo> findVehiculosByEstacionamientos(List<Long> idsEstacionamientos) {
        List<Vehiculo> lista = new ArrayList<>();
        if (idsEstacionamientos == null || idsEstacionamientos.isEmpty()) return lista;

        String inClause = String.join(",", idsEstacionamientos.stream().map(String::valueOf).toArray(String[]::new));
        String sql = "SELECT DISTINCT v.* FROM vehiculo v " +
                     "JOIN registro_estacionamiento r ON v.patente = r.patente " +
                     "WHERE r.id_estacionamiento IN (" + inClause + ") ORDER BY v.patente";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) lista.add(mapVehiculo(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    @Override
    public List<Vehiculo> findVehiculosByEstacionamientoAndPatente(Long idEstacionamiento, String patente) {
        List<Vehiculo> lista = new ArrayList<>();
        String sql = "SELECT DISTINCT v.* FROM vehiculo v " +
                     "JOIN registro_estacionamiento r ON v.patente = r.patente " +
                     "WHERE r.id_estacionamiento = ? AND UPPER(v.patente) LIKE UPPER(?) " +
                     "ORDER BY v.patente";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idEstacionamiento);
            stmt.setString(2, "%" + patente + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) lista.add(mapVehiculo(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    @Override
    public List<Vehiculo> findVehiculosByEstacionamientosAndPatente(List<Long> idsEstacionamientos, String patente) {
        List<Vehiculo> lista = new ArrayList<>();
        if (idsEstacionamientos == null || idsEstacionamientos.isEmpty()) return lista;

        String inClause = String.join(",", idsEstacionamientos.stream().map(String::valueOf).toArray(String[]::new));
        String sql = "SELECT DISTINCT v.* FROM vehiculo v " +
                     "JOIN registro_estacionamiento r ON v.patente = r.patente " +
                     "WHERE r.id_estacionamiento IN (" + inClause + ") " +
                     "AND UPPER(v.patente) LIKE UPPER(?) ORDER BY v.patente";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + patente + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) lista.add(mapVehiculo(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    @Override
    public List<Vehiculo> findAllVehiculosByGuardiaEstacionamientos(List<Long> idsEstacionamientos) {
        List<Vehiculo> lista = new ArrayList<>();
        if (idsEstacionamientos == null || idsEstacionamientos.isEmpty()) return lista;

        String inClause = String.join(",", idsEstacionamientos.stream().map(String::valueOf).toArray(String[]::new));
        String sql = "SELECT DISTINCT v.* FROM vehiculo v " +
                     "WHERE v.patente IN (SELECT DISTINCT re.patente FROM registro_estacionamiento re WHERE re.id_estacionamiento IN (" + inClause + ")) " +
                     "OR v.patente NOT IN (SELECT DISTINCT re2.patente FROM registro_estacionamiento re2) " +
                     "ORDER BY v.patente";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) lista.add(mapVehiculo(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    @Override
    public String findEstacionamientoOrigenByPatente(String patente, List<Long> idsEstacionamientos) {
        if (idsEstacionamientos == null || idsEstacionamientos.isEmpty()) return null;

        String inClause = String.join(",", idsEstacionamientos.stream().map(String::valueOf).toArray(String[]::new));
        String sql = "SELECT e.nombre " +
                    "FROM registro_estacionamiento re " +
                    "JOIN estacionamiento e ON re.id_estacionamiento = e.id_est " +
                    "WHERE re.patente = ? AND re.id_estacionamiento IN (" + inClause + ") " +
                    "ORDER BY re.fecha_hora ASC " +
                    "LIMIT 1";

        try (Connection conn = databaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patente);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("nombre");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    // -------------------- Mapper --------------------
    private Vehiculo mapVehiculo(ResultSet rs) throws SQLException {
        Vehiculo v = new Vehiculo();
        v.setPatente(rs.getString("patente"));
        v.setCodigoQr(rs.getString("codigo_qr"));
        v.setModelo(rs.getString("modelo"));
        v.setColor(rs.getString("color"));
        int tipoId = rs.getInt("id_vehiculo_tipo");
        if (!rs.wasNull()) v.setIdVehiculoTipo(tipoId);
        long dni = rs.getLong("dni_duenio");
        if (!rs.wasNull()) v.setDniDuenio(dni);
        v.setTipo(rs.getString("tipo"));
        return v;
    }
}

package com.unsl.sgeu.repositories;

import com.unsl.sgeu.config.DatabaseConnection;
import com.unsl.sgeu.models.RegistroEstacionamiento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class RegistroEstacionamientoRepositoryImpl implements RegistroEstacionamientoRepository {

    private final DatabaseConnection databaseConnection;

    @Autowired
    public RegistroEstacionamientoRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public List<RegistroEstacionamiento> findAll() {
        List<RegistroEstacionamiento> registros = new ArrayList<>();
        String sql = "SELECT * FROM registro_estacionamiento";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                registros.add(mapRegistro(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return registros;
    }

    @Override
    public Optional<RegistroEstacionamiento> findById(Long id) {
        String sql = "SELECT * FROM registro_estacionamiento WHERE id_registro = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRegistro(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<RegistroEstacionamiento> findByPatenteOrderByFechaHoraDesc(String patente) {
        List<RegistroEstacionamiento> registros = new ArrayList<>();
        String sql = "SELECT * FROM registro_estacionamiento WHERE patente = ? ORDER BY fecha_hora DESC";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    registros.add(mapRegistro(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return registros;
    }

    @Override
    public boolean existsByPatente(String patente) {
        String sql = "SELECT COUNT(*) FROM registro_estacionamiento WHERE patente = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patente);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public long countByPatente(String patente) {
        String sql = "SELECT COUNT(*) FROM registro_estacionamiento WHERE patente = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patente);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public long countByPatenteAndIdEstacionamiento(String patente, Long idEstacionamiento) {
        String sql = "SELECT COUNT(*) FROM registro_estacionamiento WHERE patente = ? AND id_est = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patente);
            stmt.setLong(2, idEstacionamiento);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int deleteByPatente(String patente) {
        String sql = "DELETE FROM registro_estacionamiento WHERE patente = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patente);
            return stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<String> findPatentesAdentroMasDeCuatroHoras(Long idEst) {
        List<String> patentes = new ArrayList<>();
        String sql = "SELECT patente FROM registro_estacionamiento " +
                     "WHERE id_est = ? AND tipo = 'INGRESO' " +
                     "AND fecha_hora <= NOW() - INTERVAL '4 HOURS' " +
                     "AND patente NOT IN (SELECT patente FROM registro_estacionamiento WHERE id_est = ? AND tipo = 'EGRESO')";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idEst);
            stmt.setLong(2, idEst);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    patentes.add(rs.getString("patente"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return patentes;
    }

    @Override
    public List<RegistroEstacionamiento> findByIdEstacionamientoAndTipoAndFechaHoraBetweenOrderByFechaHoraDesc(
            Long idEst, String tipo, LocalDateTime inicio, LocalDateTime fin) {

        List<RegistroEstacionamiento> registros = new ArrayList<>();
        String sql = "SELECT * FROM registro_estacionamiento WHERE id_est = ? AND tipo = ? " +
                     "AND fecha_hora BETWEEN ? AND ? ORDER BY fecha_hora DESC";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idEst);
            stmt.setString(2, tipo);
            stmt.setTimestamp(3, Timestamp.valueOf(inicio));
            stmt.setTimestamp(4, Timestamp.valueOf(fin));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    registros.add(mapRegistro(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return registros;
    }

    @Override
    public List<RegistroEstacionamiento> findByIdEstacionamientoAndFechaHoraBetweenOrderByFechaHoraDesc(
            Long idEst, LocalDateTime inicio, LocalDateTime fin) {

        List<RegistroEstacionamiento> registros = new ArrayList<>();
        String sql = "SELECT * FROM registro_estacionamiento WHERE id_est = ? " +
                     "AND fecha_hora BETWEEN ? AND ? ORDER BY fecha_hora DESC";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idEst);
            stmt.setTimestamp(2, Timestamp.valueOf(inicio));
            stmt.setTimestamp(3, Timestamp.valueOf(fin));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    registros.add(mapRegistro(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return registros;
    }

    @Override
    public List<RegistroEstacionamiento> findRegistrosDePatentesImpares(Long idEst) {
        List<RegistroEstacionamiento> registros = new ArrayList<>();
        String sql = "SELECT * FROM registro_estacionamiento WHERE id_est = ? AND MOD(CAST(patente AS BIGINT), 2) = 1";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idEst);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    registros.add(mapRegistro(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return registros;
    }

    @Override
    public List<RegistroEstacionamiento> findRegistrosDePatentePares(Long idEst) {
        List<RegistroEstacionamiento> registros = new ArrayList<>();
        String sql = "SELECT * FROM registro_estacionamiento WHERE id_est = ? AND MOD(CAST(patente AS BIGINT), 2) = 0";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idEst);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    registros.add(mapRegistro(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return registros;
    }

    @Override
    public RegistroEstacionamiento save(RegistroEstacionamiento registro) {
        String sql = "INSERT INTO registro_estacionamiento (patente, fecha_hora, tipo_movimiento, modo, id_est) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, registro.getPatente());
            stmt.setTimestamp(2, Timestamp.valueOf(registro.getFechaHora()));
            stmt.setString(3, registro.getTipo());
            stmt.setString(4, registro.getModo());
            if (registro.getIdEstacionamiento() != null) {
                stmt.setLong(5, registro.getIdEstacionamiento());
            } else {
                stmt.setNull(5, Types.BIGINT);
            }

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    registro.setId(rs.getLong(1));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return registro;
    }
    @Override
    public void update(RegistroEstacionamiento registro) {
        if (registro.getId() == null) return;

        String sql = "UPDATE registro_estacionamiento SET patente = ?, fecha_hora = ?, tipo_movimiento = ?, modo = ?, id_est = ? WHERE id_registro = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, registro.getPatente());
            stmt.setTimestamp(2, Timestamp.valueOf(registro.getFechaHora()));
            stmt.setString(3, registro.getTipo());
            stmt.setString(4, registro.getModo());
            if (registro.getIdEstacionamiento() != null) {
                stmt.setLong(5, registro.getIdEstacionamiento());
            } else {
                stmt.setNull(5, Types.BIGINT);
            }
            stmt.setLong(6, registro.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM registro_estacionamiento WHERE id_registro = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private RegistroEstacionamiento mapRegistro(ResultSet rs) throws SQLException {
        RegistroEstacionamiento r = new RegistroEstacionamiento();
        r.setId(rs.getLong("id_registro"));
        r.setPatente(rs.getString("patente"));
        r.setFechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime());
        r.setTipo(rs.getString("tipo_movimiento"));
        r.setModo(rs.getString("modo"));
        r.setIdEstacionamiento(rs.getLong("id_est"));
        return r;
    }
}

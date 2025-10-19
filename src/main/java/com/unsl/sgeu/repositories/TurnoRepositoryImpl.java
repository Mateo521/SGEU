package com.unsl.sgeu.repositories;

import com.unsl.sgeu.models.Estacionamiento;
import com.unsl.sgeu.models.Persona;
import com.unsl.sgeu.config.DatabaseConnection;
import com.unsl.sgeu.models.Empleado;
import com.unsl.sgeu.models.Turno;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TurnoRepositoryImpl implements TurnoRepository {

    private final DatabaseConnection databaseConnection;

    @Autowired
    public TurnoRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public Optional<Turno> findById(Long id){
        String sql = "SELECT * FRON turnos WHERE id_turno = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapTurno(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


    @Override
    public List<Turno> findByEmpleadoId(Long empleadoId) {
        List<Turno> turnos = new ArrayList<>();
        String sql = "SELECT * FROM turnos WHERE id_empleado = ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, empleadoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    turnos.add(mapTurno(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return turnos;
    }

    @Override
    public Optional<Turno> findByEmpleadoIdAndFechaFinIsNull(Long empleadoId) {
        String sql = "SELECT * FROM turnos WHERE id_empleado = ? AND fecha_fin IS NULL";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, empleadoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapTurno(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public Estacionamiento findEstacionamientoActivoByEmpleadoUsuario(String usuario) {
        String sql = "SELECT e.id_est, e.nombre, e.direccion, e.capacidad, e.estado " +
                     "FROM turnos t " +
                     "JOIN empleado em ON t.id_empleado = em.id " +
                     "JOIN estacionamiento e ON t.id_est = e.id_est " +
                     "WHERE em.nombre_usuario = ? AND t.fecha_fin IS NULL LIMIT 1";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Estacionamiento e = new Estacionamiento();
                    e.setIdEst(rs.getLong("id_est"));
                    e.setNombre(rs.getString("nombre"));
                    e.setDireccion(rs.getString("direccion"));
                    e.setCapacidad(rs.getInt("capacidad"));
                    e.setEstado(rs.getBoolean("estado"));
                    return e;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean existsByEmpleadoAndEstacionamiento(Long empleadoId, Long estacionamientoId) {
        String sql = "SELECT COUNT(*) FROM turnos WHERE id_empleado = ? AND id_est = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, empleadoId);
            stmt.setLong(2, estacionamientoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public Turno save(Turno turno) {
        String sql = "INSERT INTO turnos(fecha_inicio, fecha_fin, id_empleado, id_est, hora_in, hora_salida) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(turno.getFechaInicio()));
            if (turno.getFechaFin() != null) {
                stmt.setDate(2, Date.valueOf(turno.getFechaFin()));
            } else {
                stmt.setNull(2, Types.DATE);
            }
            stmt.setLong(3, turno.getEmpleado().getId());
            stmt.setLong(4, turno.getEstacionamiento().getIdEst());
            stmt.setTime(5, Time.valueOf(turno.getHoraIngreso()));
            stmt.setTime(6, Time.valueOf(turno.getHoraSalida()));

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return turno;
    }


    @Override
    public void update(Turno turno) {
        String sql = "UPDATE turnos SET fecha_inicio = ?, fecha_fin = ?, id_empleado = ?, id_est = ?, hora_in = ?, hora_salida = ? " +
                     "WHERE id_turno = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(turno.getFechaInicio()));
            if (turno.getFechaFin() != null) {
                stmt.setDate(2, Date.valueOf(turno.getFechaFin()));
            } else {
                stmt.setNull(2, Types.DATE);
            }
            stmt.setLong(3, turno.getEmpleado().getId());
            stmt.setLong(4, turno.getEstacionamiento().getIdEst());
            stmt.setTime(5, Time.valueOf(turno.getHoraIngreso()));
            stmt.setTime(6, Time.valueOf(turno.getHoraSalida()));
            stmt.setLong(7, turno.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM turnos WHERE id_turno = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Mapeo de ResultSet → Turno
    private Turno mapTurno(ResultSet rs) throws SQLException {
        Turno t = new Turno();
        t.setId(rs.getLong("id_turno"));
        t.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
        Date fechaFin = rs.getDate("fecha_fin");
        if (fechaFin != null) t.setFechaFin(fechaFin.toLocalDate());

        Empleado e = new Empleado();
        e.setId(rs.getLong("id_empleado"));
        t.setEmpleado(e);

        Estacionamiento est = new Estacionamiento();
        est.setIdEst(rs.getLong("id_est"));
        t.setEstacionamiento(est);

        t.setHoraIngreso(rs.getTime("hora_in").toLocalTime());
        t.setHoraSalida(rs.getTime("hora_salida").toLocalTime());
        return t;
    }
}

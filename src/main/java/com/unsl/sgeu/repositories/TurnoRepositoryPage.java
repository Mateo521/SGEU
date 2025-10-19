package com.unsl.sgeu.repositories;

import com.unsl.sgeu.models.Estacionamiento;
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

@Repository
public class TurnoRepositoryPage {

    private final DatabaseConnection databaseConnection;

    @Autowired
    public TurnoRepositoryPage(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    /**
     * Simula search() con filtros opcionales y paginación.
     *
     * @param empleadoId Id del empleado (puede ser null)
     * @param estId      Id del estacionamiento (puede ser null)
     * @param fecha      Fecha exacta (puede ser null)
     * @param limit      Cantidad de registros por página
     * @param offset     Desplazamiento (para paginación)
     * @return lista de turnos
     */
    public List<Turno> search(Long empleadoId, Long estId, LocalDate fecha, int limit, int offset) {
        List<Turno> turnos = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM turnos WHERE 1=1 ");

        if (empleadoId != null) sql.append("AND id_empleado = ? ");
        if (estId != null) sql.append("AND id_est = ? ");
        if (fecha != null) sql.append("AND fecha_inicio = ? ");

        sql.append("ORDER BY fecha_inicio DESC LIMIT ? OFFSET ?");

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            if (empleadoId != null) stmt.setLong(idx++, empleadoId);
            if (estId != null) stmt.setLong(idx++, estId);
            if (fecha != null) stmt.setDate(idx++, Date.valueOf(fecha));

            stmt.setInt(idx++, limit);
            stmt.setInt(idx, offset);

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

    /**
     * Simula searchRange() con filtros opcionales y rango de fechas.
     *
     * @param empleadoId Id del empleado (puede ser null)
     * @param estId      Id del estacionamiento (puede ser null)
     * @param desde      Fecha desde (puede ser null)
     * @param hasta      Fecha hasta (puede ser null)
     * @param limit      Cantidad de registros por página
     * @param offset     Desplazamiento (para paginación)
     * @return lista de turnos
     */
    public List<Turno> searchRange(Long empleadoId, Long estId, LocalDate desde, LocalDate hasta, int limit, int offset) {
        List<Turno> turnos = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM turnos WHERE 1=1 ");

        if (empleadoId != null) sql.append("AND id_empleado = ? ");
        if (estId != null) sql.append("AND id_est = ? ");
        if (desde != null) sql.append("AND fecha_inicio >= ? ");
        if (hasta != null) sql.append("AND fecha_inicio <= ? ");

        sql.append("ORDER BY fecha_inicio DESC LIMIT ? OFFSET ?");

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            if (empleadoId != null) stmt.setLong(idx++, empleadoId);
            if (estId != null) stmt.setLong(idx++, estId);
            if (desde != null) stmt.setDate(idx++, Date.valueOf(desde));
            if (hasta != null) stmt.setDate(idx++, Date.valueOf(hasta));

            stmt.setInt(idx++, limit);
            stmt.setInt(idx, offset);

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

    // Reutilizamos el método de mapeo
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

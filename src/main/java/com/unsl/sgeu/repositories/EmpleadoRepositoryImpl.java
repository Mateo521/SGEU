package com.unsl.sgeu.repositories;

import com.unsl.sgeu.config.DatabaseConnection;
import com.unsl.sgeu.models.Empleado;
import com.unsl.sgeu.models.Rol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class EmpleadoRepositoryImpl implements EmpleadoRepository {

    private final DatabaseConnection databaseConnection;

    @Autowired
    public EmpleadoRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public List<Empleado> findAll() {
        List<Empleado> empleados = new ArrayList<>();
        String sql = "SELECT * FROM empleado";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                empleados.add(mapEmpleado(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return empleados;
    }

    @Override
    public Optional<Empleado> findById(Long id) {
        String sql = "SELECT * FROM empleado WHERE id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapEmpleado(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public Empleado save(Empleado empleado) {
        if (empleado.getId() == null) {
            // INSERT
            String sql = "INSERT INTO empleado(nombre, apellido, correo, nombre_usuario, contrasenia, rol) " +
                         "VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = databaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                stmt.setString(1, empleado.getNombre());
                stmt.setString(2, empleado.getApellido());
                stmt.setString(3, empleado.getCorreo());
                stmt.setString(4, empleado.getNombreUsuario());
                stmt.setString(5, empleado.getContrasenia());
                stmt.setString(6, empleado.getRol().name());

                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        empleado.setId(rs.getLong(1));
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            // UPDATE
            String sql = "UPDATE empleado SET nombre = ?, apellido = ?, correo = ?, nombre_usuario = ?, " +
                         "contrasenia = ?, rol = ? WHERE id = ?";
            try (Connection conn = databaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, empleado.getNombre());
                stmt.setString(2, empleado.getApellido());
                stmt.setString(3, empleado.getCorreo());
                stmt.setString(4, empleado.getNombreUsuario());
                stmt.setString(5, empleado.getContrasenia());
                stmt.setString(6, empleado.getRol().name());
                stmt.setLong(7, empleado.getId());

                stmt.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return empleado;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM empleado WHERE id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Empleado findByNombreUsuario(String nombreUsuario) {
        String sql = "SELECT * FROM empleado WHERE nombre_usuario = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombreUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapEmpleado(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Optional<Empleado> findByNombreUsuarioIgnoreCase(String nombreUsuario) {
        String sql = "SELECT * FROM empleado WHERE LOWER(nombre_usuario) = LOWER(?)";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombreUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapEmpleado(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Empleado> findByNombreUsuarioAndContrasenia(String nombreUsuario, String contrasenia) {
        String sql = "SELECT * FROM empleado WHERE nombre_usuario = ? AND contrasenia = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombreUsuario);
            stmt.setString(2, contrasenia);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapEmpleado(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Empleado> findByRol(Rol rol) {
        List<Empleado> empleados = new ArrayList<>();
        String sql = "SELECT * FROM empleado WHERE rol = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rol.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    empleados.add(mapEmpleado(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return empleados;
    }

    @Override
    public boolean existsByNombreUsuario(String nombreUsuario) {
        String sql = "SELECT COUNT(*) FROM empleado WHERE nombre_usuario = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombreUsuario);
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
    public Optional<Empleado> findByCorreoIgnoreCase(String correo) {
        String sql = "SELECT * FROM empleado WHERE LOWER(correo) = LOWER(?)";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, correo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapEmpleado(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // Mapeo auxiliar de ResultSet a Empleado
    private Empleado mapEmpleado(ResultSet rs) throws SQLException {
        try {
            Empleado e = new Empleado();
            e.setId(rs.getLong("id"));
            e.setNombre(rs.getString("nombre"));
            e.setApellido(rs.getString("apellido"));
            e.setCorreo(rs.getString("correo"));
            e.setNombreUsuario(rs.getString("nombre_usuario"));
            e.setContrasenia(rs.getString("contrasenia"));
            
            String rolStr = rs.getString("rol");
            if (rolStr != null) {
                rolStr = rolStr.trim();
                // Los valores en la BD deben ser exactamente "Administrador" o "Guardia"
                if ("Administrador".equals(rolStr)) {
                    e.setRol(Rol.Administrador);
                } else if ("Guardia".equals(rolStr)) {
                    e.setRol(Rol.Guardia);
                } else {
                    // Fallback: intentar valueOf con el contenido tal cual y, si falla, usar Guardia
                    try {
                        e.setRol(Rol.valueOf(rolStr));
                    } catch (IllegalArgumentException ex) {
                        System.err.println("Rol inválido en BD: " + rolStr + ". Usando Guardia por defecto");
                        e.setRol(Rol.Guardia);
                    }
                }
            } else {
                e.setRol(Rol.Guardia);
            }
            
            return e;
        } catch (SQLException e) {
            System.err.println("Error mapeando empleado: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}

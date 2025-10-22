package com.unsl.sgeu.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.stereotype.Component;

@Component
public class DatabaseConnection {

    // Instancia única del Singleton
    private static DatabaseConnection instance;

    // En esta versión no mantenemos una Connection única; pedimos una por llamada
    private Connection connection;

    // Datos de conexión (podés ajustarlos según tu config)
    private static final String URL = "jdbc:mysql://190.122.236.218:3306/sgeu_db?useSSL=false&serverTimezone=America/Argentina/San_Luis";
    private static final String USER = "dbtester";
    private static final String PASSWORD = "igsoftware1234__";

    // Constructor privado — evita que se cree más de una instancia
    private DatabaseConnection() {
        try {
            // Carga del driver (opcional desde JDBC 4, pero útil para claridad)
            Class.forName("com.mysql.cj.jdbc.Driver");
            // No abrimos la conexión aquí de forma persistente. Se abrirá por llamada en getConnection().
            System.out.println("DatabaseConnection inicializado (las conexiones se abrirán por llamada).");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al inicializar el driver JDBC: " + e.getMessage());
        }
    }

    // Método público estático para obtener la instancia única
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    // Devuelve una conexión nueva. El llamador debe cerrarla cuando termine.
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Cierra la conexión manualmente (si se necesita)
    // Método existente dejado por compatibilidad; cierra la conexión almacenada si existiera
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión cerrada.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

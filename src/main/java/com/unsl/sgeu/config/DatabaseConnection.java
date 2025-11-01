package com.unsl.sgeu.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.stereotype.Component;

@Component
public class DatabaseConnection {

    // Instancia única del Singleton
    private static DatabaseConnection instance;

    // Objeto Connection
    private Connection connection;

    // Datos de conexión
    private static final String URL = "jdbc:mysql://190.122.236.218:3306/sgeu_db?useSSL=false&serverTimezone=America/Argentina/San_Luis";
    private static final String USER = "dbtester";
    private static final String PASSWORD = "igsoftware1234__";

    // Constructor privado — evita que se cree más de una instancia
    private DatabaseConnection() {
        try {
            // Carga del driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión establecida con la base de datos.");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al conectar a la base de datos: " + e.getMessage());
        }
    }

    // Método público estático para obtener la instancia única
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        } else {
            try {
                // Si la conexión está cerrada, la reabre
                if (instance.getConnection().isClosed()) {
                    instance = new DatabaseConnection();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                instance = new DatabaseConnection();
            }
        }
        return instance;
    }

    // Devuelve la conexión
    public Connection getConnection() {
        return connection;
    }

    // Cierra la conexión manualmente si es necesario
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

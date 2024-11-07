package com.intissar.olimpiadas.db;

import java.io.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnect {
    private final Connection connection;

    public DBConnect() throws SQLException {
        Properties configuracion = getConfiguration();
        Properties connConfig = new Properties();
        connConfig.setProperty("user", configuracion.getProperty("user"));
        connConfig.setProperty("password", configuracion.getProperty("password"));

        String url = "jdbc:mysql://" + configuracion.getProperty("address") + ":" +
                configuracion.getProperty("port") + "/" +
                configuracion.getProperty("database") +
                "?serverTimezone=Europe/Madrid";

        connection = DriverManager.getConnection(url, connConfig);
        connection.setAutoCommit(true);

        DatabaseMetaData databaseMetaData = connection.getMetaData();
        System.out.println("--- Datos de conexión ------------------------------------------");
        System.out.printf("Base de datos: %s%n", databaseMetaData.getDatabaseProductName());
        System.out.printf("  Versión: %s%n", databaseMetaData.getDatabaseProductVersion());
        System.out.printf("Driver: %s%n", databaseMetaData.getDriverName());
        System.out.printf("  Versión: %s%n", databaseMetaData.getDriverVersion());
        System.out.println("----------------------------------------------------------------");
    }

    public static Properties getConfiguration() {
        File f = new File("configuration.properties");
        Properties properties = new Properties();
        try (FileInputStream configFileReader = new FileInputStream(f)) {
            properties.load(configFileReader);
        } catch (IOException e) {
            System.out.println("Error al cargar la configuración: " + e.getMessage());
            throw new RuntimeException("configuration.properties no encontrado en la ruta: " + f.getPath());
        }
        return properties;
    }

    public Connection getConnection() {
        return connection;
    }

    public Connection closeConnection() throws SQLException {
        connection.close();
        return connection;
    }

}
package org.example.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class PostgresConfig {
    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try (InputStream input = PostgresConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
                Properties properties = new Properties();
                if (input == null) {
                    throw new RuntimeException("application.properties file not found.");
                }
                properties.load(input);

                String url = properties.getProperty("postgres.url");
                String user = properties.getProperty("postgres.user");
                String password = properties.getProperty("postgres.password");

                connection = DriverManager.getConnection(url, user, password);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create PostgreSQL connection.", e);
            }
        }
        return connection;
    }
}

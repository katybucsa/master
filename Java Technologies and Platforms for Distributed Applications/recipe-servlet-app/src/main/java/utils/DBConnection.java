package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private final Properties properties;
    private static final Logger logger = LogManager.getLogger();

    public DBConnection() {

        String url = "jdbc:postgresql://localhost:5432/recipe-db";
        properties = new Properties();
        properties.setProperty("user", "postgres");
        properties.setProperty("password", "postgres");
        properties.setProperty("driver", "org.postgresql.Driver");
        properties.setProperty("url", url);
    }

    private static Connection connection = null;

    private Connection getNewConnection() {

        logger.traceEntry();
        logger.info("trying to connect to database... {}", properties);
        Connection conn = null;
        try {
            Class.forName(properties.getProperty("driver"));
            logger.info("Loaded driver ...{}", properties.getProperty("driver"));
            conn = DriverManager.getConnection(properties.getProperty("url"), properties);
        } catch (ClassNotFoundException cnfe) {
            logger.error(cnfe);
            System.out.println("Error loading driver " + cnfe);
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error getting connection " + e);
        }
        return conn;
    }

    public Connection getConnection() {
        logger.traceEntry();
        try {
            if (connection == null || connection.isClosed())
                connection = getNewConnection();
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error " + e);
        }
        logger.traceExit(connection);
        return connection;
    }

    public static void closeConnection() {
        logger.traceEntry("Close database connection");
        try {
            connection.close();
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.traceExit("Database connection closed successfully");
    }
}

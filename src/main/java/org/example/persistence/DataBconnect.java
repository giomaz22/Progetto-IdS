package org.example.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBconnect {
    private static final String URL = "jdbc:h2:./database/trenical;AUTO_SERVER=TRUE";
    // salva file trenical.mv.db nella cartella /database
    private static final String USER = "giovannim";
    private static final String PASSWORD = "";

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }
}

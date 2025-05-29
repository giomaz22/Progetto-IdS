package org.example.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionSingleton {
    private static final String URL = "jdbc:h2:./database/trenical;AUTO_SERVER=TRUE";
    // salva file trenical.mv.db nella cartella /database
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private static Connection connection;
    /*
    public static Singleton getSingleton(){
        if(this.singleton = null){
            this.singleton = new Singleton(args);
            }
            return singleton;
    }
    Il singleton si utilizza quando si vuole assicurare che ci sia solo UNA istanza di questo
    oggetto in memoria che si vuole riutilazzare.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }
}

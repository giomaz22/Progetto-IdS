package org.example.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBcreaTabelle {
    public static void init() {
        try (Connection conn = DataBconnect.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS clienti (
                    id INT PRIMARY KEY,
                    nome VARCHAR(100),
                    email VARCHAR(100),
                    isFedelta BOOLEAN
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS treni (
                    id INT PRIMARY KEY,
                    partenza VARCHAR(100),
                    arrivo VARCHAR(100),
                    data_partenza DATE,
                    posti_disponibili INT
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS biglietti (
                    id INT PRIMARY KEY,
                    cliente_id INT,
                    treno_id INT,
                    classe_servizio VARCHAR(20),
                    prezzo DECIMAL(10,2),
                    FOREIGN KEY (cliente_id) REFERENCES clienti(id),
                    FOREIGN KEY (treno_id) REFERENCES treni(id)
                );
            """);

            // Puoi aggiungere anche: prenotazioni, promozioni, notifiche...

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

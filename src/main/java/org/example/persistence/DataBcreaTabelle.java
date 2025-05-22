package org.example.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBcreaTabelle {
    public static void init() {
        try (Connection conn = DataBconnect.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS utenti (
                    cf VARCHAR PRIMARY KEY,
                    nome VARCHAR(100),
                    cognome VARCHAR(100),
                    dataNascita VARCHAR(100),
                    isFedelta BOOLEAN
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS treni (
                    id VARCHAR PRIMARY KEY,
                    tipoTreno VARCHAR(100),
                    oraArrivo VARCHAR(100),
                    oraPartenza VARCHAR(100),
                    statoTreno VARCHAR(100),
                    postiDisponibili INT
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS biglietti (
                    codBiglietto VARCHAR PRIMARY KEY,
                    partenza VARCHAR(100),
                    destinazione VARCHAR(100),
                    treno VARCHAR(100),
                    classe VARCHAR(100),
                    posto VARCHAR(100),
                    PNR VARCHAR(100),
                    nomeU VARCHAR(100),
                    cognomeU VARCHAR(100),
                    cf VARCHAR(100),
                    dataPart VARCHAR(100),
                    dataArr VARCHAR(100),
                    numCarrozza INT,
                    idFedelta INT,
                    prezzo DOUBLE,
                    
                    FOREIGN KEY (cf) REFERENCES clienti(id),
                    FOREIGN KEY (treno) REFERENCES treni(id)
                );
            """);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

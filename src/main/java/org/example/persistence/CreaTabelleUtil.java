package org.example.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreaTabelleUtil {
    public static void init() {
        try (Connection conn = DBConnectionSingleton.getConnection();
             Statement stmt = conn.createStatement()) {


            stmt.execute("""
                CREATE TABLE IF NOT EXISTS utenti (
                    cf VARCHAR PRIMARY KEY,
                    nome VARCHAR(100),
                    cognome VARCHAR(100),
                    dataNascita VARCHAR(100),
                    email VARCHAR(100),
                    password VARCHAR,
                    isAdmin BOOLEAN
                );  
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS treni (
                    id VARCHAR PRIMARY KEY,
                    tipoTreno VARCHAR(100),
                    statoTreno VARCHAR(100),
                    numCarrozze INT
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS fedelta(
                    id VARCHAR PRIMARY KEY,
                    cf VARCHAR (100),
                    puntiFedelta INT,
                                                  
                    FOREIGN KEY (cf) REFERENCES utenti(cf)                               
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS viaggi (
                    id INT PRIMARY KEY,
                    idTreno VARCHAR(100),
                    numPostiDisponibili INT,
                    oraPartenza VARCHAR(100),
                    oraArrivo VARCHAR(100),
                    data VARCHAR(100),
                    
                    stazionePartenza VARCHAR(100),
                    stazioneArrivo VARCHAR(100),
                    prezzo DOUBLE,
                    classiDisponibili VARCHAR(100),
                    
                    FOREIGN KEY (idTreno) REFERENCES treni(id)
                ) 
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS prenotazioni (
                    PNR VARCHAR(100) PRIMARY KEY,
                    cf VARCHAR(100),
                    idViaggio INT,
                    dataScadenza VARCHAR(100),
                    posto INT,
                    numCarrozza INT,
                    
                    FOREIGN KEY (idViaggio) REFERENCES viaggi(id),
                    FOREIGN KEY (cf) REFERENCES utenti(cf)
                )
            """);


            stmt.execute("""
                CREATE TABLE IF NOT EXISTS biglietti (
                    codBiglietto VARCHAR PRIMARY KEY,
                    idViaggio INT,
                    classe VARCHAR(100),
                    posto INT,
                    PNR VARCHAR(100),
                    cf VARCHAR(100),
                    numCarrozza INT,
                    
                    FOREIGN KEY (cf) REFERENCES utenti(cf),
                    FOREIGN KEY (idViaggio) REFERENCES viaggi(id),
                    FOREIGN KEY (PNR) REFERENCES prenotazioni(PNR)
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS promozioni (
                    CodicePromozione VARCHAR(100) PRIMARY KEY,
                    PercentualeSconto INT,
                    soloFedelta BOOLEAN,
                    tipoTreno VARCHAR,
                    inizioPromo DATE,
                    finePromo DATE
                )
            """);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

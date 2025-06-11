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
                    isAdmin BOOLEAN,
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

            // Logica di business: ogni tot si controllano tutte le prenotazioni
            // Le prenotazioni a cui manca un giorno per la scadenza implicano una notifica al cliente
            // che ricorda di acquistare il biglietto
            // Le prenotazioni scadute vengono eliminate dal DB e il cliente viene notificato
            // dell'operazione
            // Data scadenza inoltre è uguale a idViaggio(oraPartenza) -1 giorno
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


            // logica di business gestisce il calcolo dei posti disponibili per carrozza:
            // per semplicità si presuppone che ogni carrozza abbia 100 posti disponibili
            // quindi
            // 0. L'utente vuole sapere quali posti sono disponibili per una determinata carrozza in un determinato treno
            // 1. Implemento metodo che mi permette di fare seguente query:
            /**
             * SELECT p.posto
             * FROM prenotazioni p
             * JOIN viaggi v ON p.idViaggio = v.id
             * WHERE v.idTreno = ? (parametro inserito dal cliente) AND p.numCarrozza = ? (parametro inserito dal cliente)
             */
            // Questo metodo ritorna una lista di int che rappresentano i posti prenotati in quella carrozza
            // 2. Implemento un metodo che mi permette di fare seguente query:
            /**
             * SELECT b.posto
             * FROM biglietti b
             * JOIN viaggi v ON b.idViaggio = v.id
             * WHERE v.idTreno = ? AND b.numCarrozza = ?
             */
            // Che ritornerà una lista come il metodo precedente
            // 3. Creo una lista di tutti i posti occupati come somma delle due liste create in precedenza
            // 4. Sottraggo questa lista alla lista di tutti i posti della carrozza
            // 5. ritorno la lista ottenuta dalla sottrazione che rappresenta i posti disponibili


            // ricorda di implementare check all'interno di logica di business che si assicuri che
            // una prenotazione/acquisto biglietto NON possa essere effettuato
            // se si cerca di prenotare/acquistare posto già occupato.
            // tutta questa operazione di acquisto deve essere TRANSAZIONALE
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
                    inizioPromo VARCHAR,
                    finePromo VARCHAR
                )
            """);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

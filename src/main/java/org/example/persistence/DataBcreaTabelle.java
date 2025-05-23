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
                    IDfedelta VARCHAR(100),
                    
                    FOREIGN KEY (IDfedelta) REFERENCES fedelta(id)
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

            // Quando poi andremo a lavorare sulle prenotazioni/acquisto biglietto
            // Mi devo ricordare che qualunque UPDATE venga fatto su questa tabella
            // Per quanto riguarda numPostiDisponibili, oraPartenza/oraArrivo (In caso di ritardi ecc. ecc.)
            // Vanno tutte fatte in TRANSAZIONE quindi accertarsi che l'operazione risulti atomica
            // e che rispetto le proprietà ACID

            //classiDisponibili è una stringa formattata come segue:
            // "economy business luxury"
            // quindi simula una lista, quando devo fare il parsing uso StringTokenizer con divisore di base


            //oraPartenza, oraArrivo definite secondo pattern dd/MM/yyyy HH:mm:ss es 13/08/2024 13:30:00
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS viaggi (
                    id INT PRIMARY KEY,
                    idTreno VARCHAR(100),
                    numPostiDisponibili INT,
                    oraPartenza VARCHAR(100),
                    oraArrivo VARCHAR(100),
                    stazionePartenza VARCHAR(100),
                    stazioneArrivo VARCHAR(100),
                    prezzo DOUBLE,
                    classiDisponibili VARCHAR(100),
                    
                    FOREIGN KEY (idTreno) REFERENCES treni(id)
                ) 
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS treni (
                    id VARCHAR PRIMARY KEY,
                    tipoTreno VARCHAR(100),
                    statoTreno VARCHAR(100),
                    numCarrozze INT
                );
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
                    posto INT(100),
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
                    posto INT(100),
                    PNR VARCHAR(100),
                    cf VARCHAR(100),
                    numCarrozza INT,
                    
                    FOREIGN KEY (cf) REFERENCES utenti(id),
                    FOREIGN KEY (idViaggio) REFERENCES viaggi(id),
                    FOREIGN KEY (PNR) REFERENCES prenotazioni(PNR)
                );
            """);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

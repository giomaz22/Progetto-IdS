package org.example.servizi;

import org.example.model.Biglietto;
import org.example.model.Prenotazione;
import org.example.model.Viaggio;
import org.example.persistence.DataBconnect;
import org.example.persistence.gestisciBigliettoDB;
import org.example.persistence.gestisciPrenotazioneDB;
import org.example.persistence.gestisciViaggioDB;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PrenotaS {
    private final gestisciPrenotazioneDB prenotazioniDB = new gestisciPrenotazioneDB();
    private final gestisciViaggioDB viaggiDB = new gestisciViaggioDB();
    private final gestisciBigliettoDB bigliettoDB = new gestisciBigliettoDB();
    private final BiglietteriaS biglietteria = new BiglietteriaS(bigliettoDB);

    /**
     * Aggiunge una prenotazione per un viaggio e blocca il posto.
     */
    public boolean creaPrenotazione(Prenotazione p) {
        Connection conn = null;
        try {
            conn = DataBconnect.getConnection();
            conn.setAutoCommit(false);

            Viaggio v = viaggiDB.trovaViaggioPerID(p.getId_viaggio(), conn);
            if (v == null || v.getNumPostiDisponibili() <= 0) {
                System.out.println("Posti non disponibili.");
                return false;
            }

            // Riduce i posti disponibili
            viaggiDB.aggiornaDisponibilitaPosti(v.getIDViaggio(), v.getNumPostiDisponibili() - 1, conn);
            // Inserisce la prenotazione
            prenotazioniDB.addPrenotazione(p, conn);

            conn.commit();
            System.out.println("Prenotazione creata con successo.");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("Transazione annullata.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Conferma una prenotazione trasformandola in biglietto (e rimuovendola dal DB).
     */
    public boolean confermaPrenotazione(String pnr, String classe) {
        Prenotazione p = prenotazioniDB.trovaPerPNR(pnr);
        if (p == null) {
            System.out.println("Prenotazione non trovata.");
            return false;
        }

        // Crea biglietto dal contenuto della prenotazione
        Biglietto b = new Biglietto();
        b.setCf(p.getCf());
        b.setPNR(p.getPNR());
        b.setIdViaggio(p.getId_viaggio());
        b.setClasse(classe);
        b.setNumCarrozza(p.getNumCarrozza());
        b.setPosto(p.getPosto());
        b.setCodBiglietto("B-" + p.getPNR());

        boolean successo = biglietteria.acquistaBiglietto(b, 1);
        if (successo) {
            prenotazioniDB.eliminaPrenotazione(pnr);
        }

        return successo;
    }

    /**
     * Verifica se la prenotazione è scaduta e, in caso, la cancella e rilascia il posto.
     */
    public void verificaPrenotazioniScadute() {
        List<Prenotazione> tutte = prenotazioniDB.tutteLePrenotazioni();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Prenotazione p : tutte) {
            try (Connection conn = DataBconnect.getConnection()) {
                conn.setAutoCommit(false);

                LocalDateTime scadenza = LocalDateTime.parse(p.getDataScadenza(), formatter);
                if (now.isAfter(scadenza)) {
                    System.out.println("Prenotazione scaduta: " + p.getPNR());

                    // Libera il posto e cancella la prenotazione
                    viaggiDB.incrementaPostiDisponibili(p.getId_viaggio(), 1, conn);
                    prenotazioniDB.eliminaPrenotazione(p.getPNR(), conn);

                    conn.commit();
                } else if (now.plusMinutes(30).isAfter(scadenza)) {
                    System.out.println("ATTENZIONE: la prenotazione " + p.getPNR() + " sta per scadere.");
                }

            } catch (Exception e) {
                e.printStackTrace();
                // rollback in caso di errore
                try (Connection conn = DataBconnect.getConnection()) {
                    conn.rollback();
                    conn.setAutoCommit(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

}

package org.example.servizi;

import org.example.model.Biglietto;
import org.example.model.Viaggio;
import org.example.persistence.DataBconnect;
import org.example.persistence.gestisciBigliettoDB;
import org.example.persistence.gestisciViaggioDB;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

/*
Questa è una classe di servizio. Utile per la gestione dell'acquisto
di uno o più biglietti da parte degli utenti/clienti, modifica dei
biglietti e/o eventuale cancellazione.
 */

public class BiglietteriaS {
    private final gestisciBigliettoDB bigliettiDataBase; //classe di gestione DB per i biglietti
    private final gestisciViaggioDB viaggioDataBase; //classe di gestione DB per i biglietti

    public BiglietteriaS(gestisciBigliettoDB bigliettiDataBase) {
        this.bigliettiDataBase = bigliettiDataBase;
        viaggioDataBase = new gestisciViaggioDB();
    }

    /*
    Metodo per l'acquisto di uno o più biglietti.
     */
    public boolean acquistaBiglietto(Biglietto biglietto, int numBiglietti) {

        Connection conn = null;
        try {
            conn = DataBconnect.getConnection();
            conn.setAutoCommit(false); // inizio transazione
            // numBiglietti è la quantità di biglietti che l'utente desidera acquistare
            Viaggio viaggio = viaggioDataBase.trovaViaggioPerID(biglietto.getIdViaggio(), conn);

            if (viaggio == null) {
                System.out.println("[Errore]: Viaggio non trovato");
                return false;
            }

            if (viaggio.getNumPostiDisponibili() < numBiglietti) {
                return false;
                System.out.println("[Errore]: I posti disponibili sul treno sono " +
                        "inferiori rispetto al numero dei biglietti che si vogliono acquistare");
            }

            // calcolo del prezzo totale
            double prezzoTotale = calcolaPrezzo(biglietto.getClasse(), viaggio.getPrezzo(), numBiglietti);

            // simuliamo il pagamento
            if(!pagamentoEffettuato(biglietto.getCf(), prezzoTotale)) {
                System.out.println("[Errore]: Pagamento non effettuato. Acquisto fallito!");
                return false;
            }

            /*
            Se il pagamento risulta confermato, e quindi viene acquistato
            il biglietto, è necessario inserire i biglietti acquistati nel
            database, ed in più vanno gestiti i posti disponibili per quel
            determinato viaggio.
             */
            viaggioDataBase.aggiornaDisponibilitaPosti(biglietto.getIdViaggio(),
                    viaggio.getNumPostiDisponibili() - numBiglietti, conn);

            for (int i = 0; i < numBiglietti; i++) {
                Biglietto nuovoBiglietto = new Biglietto();
                nuovoBiglietto.setIdViaggio(biglietto.getIdViaggio());
                nuovoBiglietto.setClasse(biglietto.getClasse());
                nuovoBiglietto.setPNR(biglietto.getPNR());
                nuovoBiglietto.setCf(biglietto.getCf());
                nuovoBiglietto.setNumCarrozza(biglietto.getNumCarrozza());
                nuovoBiglietto.setPosto(biglietto.getPosto() + i);
                nuovoBiglietto.setCodBiglietto(biglietto.getCodBiglietto());

                bigliettiDataBase.addBiglietto(nuovoBiglietto, conn);
            }
            conn.commit();
            System.out.println("Il cliente " + biglietto.getCf() + " ha acquistato con successo il/i biglietto/i.");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("Transazione annullata.");
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean modificaBiglietto(String codBiglietto, int nuovoIdViaggio, String nuovaClasse) {
        Connection conn = null;

        try {
            conn = DataBconnect.getConnection();
            conn.setAutoCommit(false); // inizio transazione

            // Recupero biglietto attuale
            Biglietto b = bigliettiDataBase.trovaBigliettoPerCodice(codBiglietto);
            if (b == null) {
                System.out.println("Biglietto non trovato.");
                return false;
            }

            // Recupero viaggio attuale e nuovo
            Viaggio viaggioVecchio = viaggioDataBase.trovaViaggioPerID(b.getIdViaggio(), conn);
            Viaggio viaggioNuovo = viaggioDataBase.trovaViaggioPerID(nuovoIdViaggio, conn);

            if (viaggioNuovo == null || viaggioNuovo.getNumPostiDisponibili() <= 0) {
                System.out.println("Nuovo viaggio non disponibile.");
                return false;
            }

            // Calcolo dei prezzi
            double prezzoVecchio = calcolaPrezzo(b.getClasse(), viaggioVecchio.getPrezzo(), 1);
            double prezzoNuovo = calcolaPrezzo(nuovaClasse, viaggioNuovo.getPrezzo(), 1);

            double daPagare = 0.0;
            if (prezzoNuovo > prezzoVecchio) {
                daPagare = prezzoNuovo - prezzoVecchio; // differenza tariffaria
            } else {
                daPagare = 10.0; // penale fissa
            }

            if (!pagamentoEffettuato(b.getCf(), daPagare)) {
                System.out.println("Pagamento fallito.");
                return false;
            }

            // Ripristina il posto del vecchio viaggio
            viaggioDataBase.incrementaPostiDisponibili(viaggioVecchio.getIDViaggio(), 1, conn);

            // Riduce i posti del nuovo viaggio
            viaggioDataBase.aggiornaDisponibilitaPosti(viaggioNuovo.getIDViaggio(), viaggioNuovo.getNumPostiDisponibili() - 1, conn);

            // Aggiorna il biglietto
            b.setIdViaggio(nuovoIdViaggio);
            b.setClasse(nuovaClasse);
            bigliettiDataBase.aggiornaBiglietto(b, conn);

            conn.commit(); // fine transazione
            System.out.println("Biglietto modificato con successo.");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // annulla tutto
                    System.out.println("Transazione annullata.");
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /* La logica di business si basa sul prezzo per classe.
    Si parte da un prezzo base con classe standard (2° classe).
        2° classe, tariffa 15€;
        1° classe, tariffa 20€;
    Business class, tariffa 30€;
    Luxury class, tariffa 50€;
    Exclusive class, tariffa 70€;

    Si noti che non tutti i treni offrono la possibilità di avere più classi.
    */

    private double calcolaPrezzo(String classe, double prezzoBase, int numBiglietti) {
        return switch (classe) {
            case "Prima" -> (prezzoBase + 5.0) * numBiglietti;
            case "Business" -> (prezzoBase + 15.0) * numBiglietti;
            case "Luxury" -> (prezzoBase + 35.0) * numBiglietti;
            case "Exclusive" -> (prezzoBase + 55.0) * numBiglietti;
            default -> (prezzoBase) * numBiglietti; //seconda classe, di base
        };
    }

    /*
    Metodo utile per la simulazione del pagamento lato utente.
    Come da traccia il pagamento è effettuabile tramite carta o wallet.
    Per controllo del numero di carta si sfrutta il noto "algoritmo di Luhn",
    algoritmo di checksum che convalida i numeri della carta di credito.
     */

    private boolean pagamentoEffettuato(String cf, double prezzoTotale) {
        Scanner scan = new Scanner(System.in);
        System.out.println("I biglietti sono nel carrello. Paga ora €" + prezzoTotale);
        System.out.println("Scegli il metodo di pagamento. Digita (1) per Carta | (2) per Wallet");
        String option = scan.nextLine();

        if (option.equals("1")) {
            System.out.println("Inserire un numero di carta valido (16 cifre)");
            String numCarta = scan.nextLine();
            if(!validaCartaLuhn(numCarta)){
                System.out.println("Il numero della carta non è valido. Riprovare.");
                return false;
            }
        } else if (option.equals("2")) {
            System.out.println("Pagamento con Wallet. Attendere...");
        } else {
            System.out.println("Non sono riconosciuti altri metodi");
        }

        System.out.println("Pagamento riuscito!");
        return true;
    }

    private boolean validaCartaLuhn(String numCarta) {
        if (numCarta == null || numCarta.length() != 16 || !numCarta.matches("\\d+")) return false;

        int somma = 0;
        for (int i = 0; i < 16; i++) {
            int cifra = Character.getNumericValue(numCarta.charAt(15 - i));
            if (i % 2 == 1) { // ogni seconda cifra da destra
                cifra *= 2;
                if (cifra > 9) cifra -= 9;
            }
            somma += cifra;
        }
        return somma % 10 == 0;
    }
}


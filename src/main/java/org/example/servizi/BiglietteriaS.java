package org.example.servizi;

import org.example.model.Biglietto;
import org.example.model.Viaggio;
import org.example.persistence.gestisciBigliettoDB;
import org.example.persistence.gestisciViaggioDB;

/*
Questa è una classe di servizio. Utile per la gestione dell'acquisto
di uno o più biglietti da parte degli utenti/clienti, modifica dei
biglietti e/o eventuale cancellazione.
 */

public class BiglietteriaS {
    private final gestisciBigliettoDB bigliettiDataBase; //classe di gestione DB per i biglietti
    private final gestisciViaggioDB viaggioDataBase; //classe di gestione DB per i biglietti

    public BiglietteriaS(gestisciBigliettoDB bigliettiDataBase, gestisciViaggioDB viaggioDataBase) {
        this.bigliettiDataBase = bigliettiDataBase;
        this.viaggioDataBase = viaggioDataBase;
    }

    /*
    Metodo per l'acquisto di uno o più biglietti.
     */
    public boolean acquistaBiglietto(Biglietto biglietto, int numBiglietti) {
        // numBiglietti è la quantità di biglietti che l'utente desidera acquistare
        Viaggio viaggio = viaggioDataBase.trovaViaggioPerID(biglietto.getIdViaggio());

        if(viaggio == null) {
            return false;
            System.out.println("[Errore]: Viaggio non trovato");
        }

        if(viaggio.getNumPostiDisponibili() < numBiglietti) {
            return false;
            System.out.println("[Errore]: I posti disponibili sul treno sono " +
                    "inferiori rispetto al numero dei biglietti che si vogliono acquistare");
        }

        /*
        Ora si rimanda ad un sottometodo per il calcolo del prezzo dei biglietti. La logica
        di business si basa sul prezzo per classe. Si parte da un prezzo base con classe
        standard (2° classe).
        2° classe, tariffa 15€;
        1° classe, tariffa 20€;
        Business class, tariffa 30€;
        Luxury class, tariffa 50€;
        Exclusive class, tariffa 70€;

        Si noti che non tutti i treni offrono la possibilità di avere più classi.
         */
        double prezzoTotale = calcolaPrezzo(biglietto.getClasse(), viaggio.getPrezzo(), numBiglietti);
    }

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
    Poichè nella ricerca del biglietto deve essere possibile consultare l'ora e la
    disponibilità dei posti con stazione di partenza, arrivo e dati (OBBLIGATORIO).
    Le evntuali preferenze, come classe e tipoTreno, sono FACOLTATIVE nella ricerca.
    Ricordiamo che lo stesso utente può acquistare uno o più biglietti per lo stesso
    treno.
     */

    /*
    Un metodo modifica biglietto mi dà la possibilità di variare la data, l'orario o la classe di servizio per tutti
    i biglietti acquistati in precedenza dal cliente. Ovviamente tele modifica comporta il pagamento di una differenza
    tariffaria oppure il pagamento di una penale.
    I PAGAMENTI AVVENGONO ELETTRONICAMENTE (SIMULAZIONE VIRTUALE).
     */
}


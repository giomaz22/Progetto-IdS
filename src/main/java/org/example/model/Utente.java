package org.example.model;

import org.example.persistence.gestisciBigliettoDB;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.persistence.gestisciPrenotazioneDB;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Utente {
    private String nome, cognome, codiceFiscale, dataNascita, fedelta;

    private List<Biglietto> bigliettiUtente = new ArrayList<>();
    private List<Prenotazione> prenotazioniUtente = new ArrayList<>();

    private List<Biglietto> aggiungiBigliettiUtente (gestisciBigliettoDB bigliettoU) {
        bigliettiUtente = bigliettoU.trovaBigliettiPerUtente(this.codiceFiscale);
        return bigliettiUtente;
    }

    private List<Prenotazione> aggiungiPrenotazioniUtente (gestisciPrenotazioneDB p) {
        prenotazioniUtente = p.trovaPrenotazioniPerUtente(this.codiceFiscale);
        return prenotazioniUtente;
    }

}

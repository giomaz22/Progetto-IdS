package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Utente {
    String nome, cognome, codiceFiscale, dataNscita, indirizzo;
    Fedelta cartaUtente;
    Biglietto bigliettoUtente;
    Prenotazione prenotazioneEffettuata;
}

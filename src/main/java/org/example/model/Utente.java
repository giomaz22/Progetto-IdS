package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Utente {
    private String nome, cognome, codiceFiscale, dataNascita;
    private boolean cFedelta;
    private Fedelta cartaUtente;

    private List<Biglietto> bigliettiUtente = new ArrayList<>();
    private List<Prenotazione> prenotazionesUtente = new ArrayList<>();

}

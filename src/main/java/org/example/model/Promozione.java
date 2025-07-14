package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Data
@AllArgsConstructor
@NoArgsConstructor
/*
Gestione delle Promozioni
Il sistema deve supportare la creazione e la gestione di promozioni.
Le promozioni possono essere applicabili a tutti i clienti o specificamente ai membri del programma "FedeltàTreno".
Le promozioni possono essere associate a specifiche tratte ferroviarie, periodi dell'anno, tipologie di treno.
 */
public class Promozione {
    String codicePromozione;
    int percentualeSconto;

    boolean soloFedelta;
    String tipoTreno;
    String inizioPromo, finePromo;

    private static final DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter sqlFormatter = DateTimeFormatter.ISO_LOCAL_DATE;  // yyyy-MM-dd

    public void setInizioPromoDaInput(String dataInput) {
        try {
            LocalDate date = LocalDate.parse(dataInput, inputFormatter);
            this.inizioPromo = date.format(sqlFormatter);
        } catch (DateTimeParseException e) {
            this.inizioPromo = null;
        }
    }

    public void setFinePromoDaInput(String dataInput) {
        try {
            LocalDate date = LocalDate.parse(dataInput, inputFormatter);
            this.finePromo = date.format(sqlFormatter);
        } catch (DateTimeParseException e) {
            this.finePromo = null;
        }
    }
}

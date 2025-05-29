package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}

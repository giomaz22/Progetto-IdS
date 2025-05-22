package org.example.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Biglietto {
    String IDbiglietto;
    String stazionePartenza, stazioneArrivo, ID_treno, classe, posto, PNR;
    String nomeUtente, cognomeUtente, CF;
    String dataPartenza, dataArrivo;
    int carrozza, IDfedelta;
    double prezzo;
}

package org.example.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Biglietto {
    String stazionePartenza, stazioneArrivo, treno, classe, posto, PNR;
    String nomeUtente, cognomeUtente, CF;
    String dataPartenza, dataArrivo;
    int carrozza, codiceTreno, IDfedelta;
}

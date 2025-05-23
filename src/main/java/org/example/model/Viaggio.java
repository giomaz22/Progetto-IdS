package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Viaggio {
    private int IDViaggio, numPostiDisponibili;
    private String IDtreno, oraPartenza, oraArrivo, stazionePartenza, stazioneArrivo, classiDisponibili;
    private double prezzo;
}

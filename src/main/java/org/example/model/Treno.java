package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Treno {
    String IDtreno, tipologia, oraPartenza, oraArrivo, statoTreno;
    private int disponibilitaPosti;
    private List<Biglietto> bigliettiVendutiPerTreno = new ArrayList<>();
}

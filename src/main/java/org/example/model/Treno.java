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
    private String IDtreno, tipologia, statoTreno;
    private int numCarrozze;
    private List<Biglietto> bigliettiVendutiPerTreno = new ArrayList<>();

    public Treno(String IDtreno, String tipologia, String statoTreno, int numCarrozze) {
        this.IDtreno = IDtreno;
        this.tipologia = tipologia;
        this.statoTreno = statoTreno;
        this.numCarrozze = numCarrozze;
    }
}

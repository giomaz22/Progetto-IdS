package org.example.servizi;

import org.example.model.Biglietto;
import org.example.persistence.gestisciBigliettoDB;

public class BiglietteriaS {
    private final gestisciBigliettoDB biglietti;

    public BiglietteriaS(gestisciBigliettoDB biglietto) {
        this.biglietti = biglietto;
    }

    public void acquistaBiglietti(Biglietto b) {
        biglietti.addBiglietto(b);
        System.out.println(b.getNomeUtente() + "ha acquistato" + b.getIDbiglietto());
    }
}

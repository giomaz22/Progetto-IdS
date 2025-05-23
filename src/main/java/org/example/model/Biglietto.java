package org.example.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Biglietto {
    private String codBiglietto;
    private String classe, PNR, cf;
    private int idViaggio, numCarrozza, posto;
}

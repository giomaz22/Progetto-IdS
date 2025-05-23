package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Prenotazione {
    private String PNR, dataScadenza, cf;
    private int id_viaggio, posto, numCarrozza;
}

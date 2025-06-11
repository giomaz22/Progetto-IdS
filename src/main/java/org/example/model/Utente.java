package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Utente {
    private String nome, cognome, codiceFiscale, dataNascita;
    private String password;
    private String email;
    private boolean isAmministratore;
}

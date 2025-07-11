package org.example.util;

import java.security.SecureRandom;

public class CodiceCartaFedelta {

    /*
    In questa classe è implementato un metodo che, dato un insieme di caratteri,
    genera un codice casuale che fungerà da ID per la carta fedeltà di un utente.
     */

    private static final String CARATTERI = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom random = new SecureRandom();

    public static String generaCodice() {
        StringBuilder codice = new StringBuilder(36);
        for (int i = 0; i < 36; i++) {
            int index = random.nextInt(CARATTERI.length());
            codice.append(CARATTERI.charAt(index));
        }
        return codice.toString();
    }

}

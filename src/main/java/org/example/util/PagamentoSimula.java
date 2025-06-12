package org.example.util;

import java.util.Scanner;

public class PagamentoSimula {
    public static boolean checkLuhn(String numCarta) {
        if (numCarta == null || numCarta.length() != 16 || !numCarta.matches("\\d+")) return false;

        int somma = 0;
        for (int i = 0; i < 16; i++) {
            int cifra = Character.getNumericValue(numCarta.charAt(15 - i));
            if (i % 2 == 1) { // ogni seconda cifra da destra
                cifra *= 2;
                if (cifra > 9) cifra -= 9;
            }
            somma += cifra;
        }
        return somma % 10 == 0;
    }
}

package org.example.util;

public class PagamentoSimula {
    /*
    In questa classe è implementato un metodo che serve per verificare se
    il numero di una carta di credito usata per l'acquisto di un biglietto
    è valida o meno. (Algoritmo di Luhn)
     */
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

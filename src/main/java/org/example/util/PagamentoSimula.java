package org.example.util;

import java.util.Scanner;

public class PagamentoSimula {
    /*
    Metodo utile per la simulazione del pagamento lato utente.
    Come da traccia il pagamento è effettuabile tramite carta o wallet.
    Per controllo del numero di carta si sfrutta il noto "algoritmo di Luhn",
    algoritmo di checksum che convalida i numeri della carta di credito.
     */

    private static boolean pagamentoEffettuato(String cf, double prezzoTotale) {
        Scanner scan = new Scanner(System.in);
        System.out.println("I biglietti sono nel carrello. Paga ora €" + prezzoTotale);
        System.out.println("Scegli il metodo di pagamento. Digita (1) per Carta | (2) per Wallet");
        String option = scan.nextLine();

        if (option.equals("1")) {
            System.out.println("Inserire un numero di carta valido (16 cifre)");
            String numCarta = scan.nextLine();
            if(!checkLuhn(numCarta)){
                System.out.println("Il numero della carta non è valido. Riprovare.");
                return false;
            }
        } else if (option.equals("2")) {
            System.out.println("Pagamento con Wallet. Attendere...");
        } else {
            System.out.println("Non sono riconosciuti altri metodi");
        }

        System.out.println("Pagamento riuscito!");
        return true;
    }

    private static boolean checkLuhn(String numCarta) {
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

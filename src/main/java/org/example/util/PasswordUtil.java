package org.example.util;


import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    /*
    In questa classe sono presenti metodi utili per garantire la crittografia
    delle password inserite dagli utenti in fase di registrazione.
     */

    // Hashing della password
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // Verifica della password
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}


package org.example.util;

import java.util.concurrent.ThreadLocalRandom;

public class ClassiTrenoUtil {

    public static String scegliClasseBaseCasuale() {
        String[] classi = {"PRIMA", "SECONDA"};
        int indice = ThreadLocalRandom.current().nextInt(classi.length);
        return classi[indice];
    }
}

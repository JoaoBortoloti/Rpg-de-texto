package sistema;

import java.util.Random;

public class Dado {
    private static Random random = new Random();
    private static Long seed = null;

    // Permite definir seed para testes determinísticos
    public static void setSeed(long seed) {
        Dado.seed = seed;
        random = new Random(seed);
    }

    public static void resetSeed() {
        seed = null;
        random = new Random();
    }

    public static int rolar(int faces) {
        if (faces <= 0) {
            throw new IllegalArgumentException("Número de faces deve ser positivo");
        }
        return random.nextInt(faces) + 1;
    }

    public static int rolarD6() {
        return rolar(6);
    }

    public static int rolarD20() {
        return rolar(20);
    }

    public static Long getSeed() {
        return seed;
    }
}
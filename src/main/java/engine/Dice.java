package engine;

import java.util.Random;

/**
 * Stateless dice-rolling utility.
 *
 * <p>Supports an optional seed for deterministic behaviour in tests and a
 * silent mode that suppresses console output.
 */
public class Dice {

    private static Random random   = new Random();
    private static Long   seed     = null;
    private static boolean silent  = false;

    public static void setSeed(long seed) {
        Dice.seed  = seed;
        random     = new Random(seed);
    }

    public static void resetSeed() {
        seed   = null;
        random = new Random();
    }

    public static Long getSeed() { return seed; }

    /**
     * Activates or deactivates silent mode.
     * When silent, roll results are not printed — useful in automated tests.
     */
    public static void setSilent(boolean value) { silent = value; }

    /**
     * Rolls a die with the given number of faces.
     *
     * @param faces number of faces (must be &gt; 0)
     * @return a value in [1, faces]
     * @throws IllegalArgumentException if faces &le; 0
     */
    public static int roll(int faces) {
        if (faces <= 0) throw new IllegalArgumentException("Number of faces must be positive");
        int result = random.nextInt(faces) + 1;
        if (!silent) System.out.printf("[DICE] d%d → %d%n", faces, result);
        return result;
    }

    public static int rollD6()  { return roll(6); }
    public static int rollD20() { return roll(20); }
}

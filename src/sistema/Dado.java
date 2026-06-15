package sistema;

import java.util.Random;

/**
 * Utilitário de rolagem de dados.
 * <p>
 * Possui suporte a seed para permitir testes determinísticos.
 * Sempre imprime o resultado da rolagem para que o jogador
 * veja o valor tirado, como em um RPG de mesa.
 */
public class Dado {
    private static Random random = new Random();
    private static Long seed = null;

    /**
     * Define a seed do gerador de números aleatórios.
     * Útil para testes reproduzíveis.
     *
     * @param seed valor da seed
     */
    public static void setSeed(long seed) {
        Dado.seed = seed;
        random = new Random(seed);
    }

    /**
     * Reseta a seed, voltando a um gerador aleatório imprevisível.
     */
    public static void resetSeed() {
        seed = null;
        random = new Random();
    }

    /**
     * Rola um dado com o número de faces indicado.
     *
     * @param faces número de faces (deve ser &gt; 0)
     * @return valor entre 1 e {@code faces} (inclusive)
     * @throws IllegalArgumentException se faces &lt;= 0
     */
    public static int rolar(int faces) {
        if (faces <= 0) {
            throw new IllegalArgumentException("Número de faces deve ser positivo");
        }
        int resultado = random.nextInt(faces) + 1;
        System.out.println(String.format("[DADO] d%d -> %d", faces, resultado));
        return resultado;
    }

    /**
     * Rola um d6 (dado de 6 faces).
     */
    public static int rolarD6() {
        return rolar(6);
    }

    /**
     * Rola um d20 (dado de 20 faces).
     */
    public static int rolarD20() {
        return rolar(20);
    }

    /**
     * @return seed atual (se definida) ou null se aleatório padrão.
     */
    public static Long getSeed() {
        return seed;
    }
}
package interfaces;

/**
 * Interface que representa algo que pode participar de combate,
 * causando e recebendo dano.
 */
public interface Atacavel {

    /**
     * Calcula o dano causado em um ataque, dado o valor da rolagem de dado.
     *
     * @param rolagemDado valor da rolagem de dado
     * @return dano calculado
     */
    int calcularDano(int rolagemDado);

    /**
     * Aplica dano recebido, permitindo que a implementação faça
     * cálculos de defesa, redução de dano, etc.
     *
     * @param dano dano bruto recebido
     */
    void receberDano(int dano);

    /**
     * Indica se o alvo ainda está vivo (por exemplo, HP &gt; 0).
     */
    boolean estaVivo();
}
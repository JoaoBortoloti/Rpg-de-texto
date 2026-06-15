package sistema;

/**
 * Centraliza as constantes de balanceamento do jogo.
 * <p>
 * Qualquer valor numérico que afete mecânicas de jogo deve ser definido
 * aqui — facilita ajuste fino sem caçar literais espalhadas no código.
 */
public final class GameConfig {

    // Progressão de XP
    public static final int XP_INICIAL_PROXIMO_NIVEL = 100;
    public static final double MULTIPLICADOR_XP_NIVEL  = 1.5;
    public static final int XP_BOSS_MULTIPLICADOR      = 3;

    // Bônus de level up
    public static final int BONUS_HP_LEVEL_UP     = 20;
    public static final int BONUS_ATAQUE_LEVEL_UP = 3;
    public static final int BONUS_DEFESA_LEVEL_UP = 2;

    // Exploração — limites da rolagem d10
    public static final int LIMIAR_INIMIGO    = 5;
    public static final int LIMIAR_ITEM       = 7;
    public static final int LIMIAR_ARMADILHA  = 8;
    public static final int CADENCIA_HISTORIA = 2;

    // Armadilha (Dado.rolar(X))
    public static final int FACES_DADO_ARMADILHA = 15;

    // Fuga (rolagem d20 >= X)
    public static final int LIMIAR_FUGA = 12;

    // Boss — fórmulas de atributo
    public static final int BOSS_HP_BASE          = 200;
    public static final int BOSS_HP_POR_NIVEL     = 20;
    public static final int BOSS_ATAQUE_BASE      = 20;
    public static final int BOSS_ATAQUE_POR_NIVEL = 2;
    public static final int BOSS_DEFESA_BASE      = 15;
    public static final int BOSS_NIVEL_BONUS      = 2;

    // Boss — limiar do ataque sombrio (Dado.rolar(10) >= X)
    public static final int BOSS_LIMIAR_ESPECIAL = 7;

    private GameConfig() {}
}

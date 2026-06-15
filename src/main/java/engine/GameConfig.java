package engine;

/**
 * Central registry for all numeric game-balance constants.
 * Adjust values here to tune difficulty without hunting down literals.
 */
public final class GameConfig {

    // XP progression
    public static final int    INITIAL_NEXT_LEVEL_XP = 100;
    public static final double LEVEL_XP_MULTIPLIER   = 1.5;
    public static final int    BOSS_XP_MULTIPLIER    = 3;

    // Level-up stat bonuses
    public static final int LEVEL_UP_HP_BONUS      = 20;
    public static final int LEVEL_UP_ATTACK_BONUS  = 3;
    public static final int LEVEL_UP_DEFENSE_BONUS = 2;

    // Exploration — thresholds on a d10 roll
    public static final int ENEMY_THRESHOLD = 5;
    public static final int ITEM_THRESHOLD  = 7;
    public static final int TRAP_THRESHOLD  = 8;
    public static final int STORY_CADENCE   = 2;

    // Trap damage (Dice.roll(X))
    public static final int TRAP_DICE_FACES = 15;

    // Flee threshold (d20 roll >= X)
    public static final int FLEE_THRESHOLD = 12;

    // Boss stat formulae
    public static final int BOSS_BASE_HP         = 200;
    public static final int BOSS_HP_PER_LEVEL    = 20;
    public static final int BOSS_BASE_ATTACK     = 20;
    public static final int BOSS_ATTACK_PER_LEVEL = 2;
    public static final int BOSS_BASE_DEFENSE    = 15;
    public static final int BOSS_LEVEL_BONUS     = 2;

    // Boss shadow attack threshold (Dice.roll(10) >= X)
    public static final int BOSS_SPECIAL_THRESHOLD = 7;

    private GameConfig() {}
}

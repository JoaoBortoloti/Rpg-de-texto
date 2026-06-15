package characters;

import items.Item;
import items.Effect;
import engine.Dice;

/**
 * An enemy character generated during exploration.
 *
 * <p>Uses a static factory ({@link #createRandom(int)}) to produce level-scaled
 * opponents and generates random loot on creation.
 */
public class Enemy extends GameCharacter {

    private final int xpReward;
    private final String type;

    public Enemy(String name, int hitPoints, int attack, int defense, int level, String type) {
        super(name, hitPoints, attack, defense, level);
        this.type     = type;
        this.xpReward = level * 50;
        generateLoot();
    }

    public Enemy() {
        this("Goblin", 50, 8, 3, 1, "Common");
    }

    public Enemy(Enemy other) {
        super(other);
        this.xpReward = other.xpReward;
        this.type     = other.type;
    }

    private void generateLoot() {
        int count = Dice.roll(3);
        for (int i = 0; i < count; i++) {
            Item item = switch (Dice.roll(4)) {
                case 1 -> new Item("Health Potion",   "Restores HP",           Effect.HEAL,          1, 30);
                case 2 -> new Item("Strength Potion", "Increases attack",      Effect.ATTACK_BUFF,   1, 5);
                case 3 -> new Item("Defense Potion",  "Increases defense",     Effect.DEFENSE_BUFF,  1, 5);
                default-> new Item("Bomb",            "Deals damage",          Effect.DAMAGE,        1, 25);
            };
            getInventory().add(item);
        }
    }

    @Override
    public String useSpecialAbility(GameCharacter target) {
        return getName() + " uses a special attack!";
    }

    public int    getXpReward() { return xpReward; }
    public String getType()     { return type; }

    /**
     * Produces an enemy whose stats scale with the player's level.
     *
     * @param playerLevel current player level
     * @return a new {@link Enemy} instance
     */
    public static Enemy createRandom(int playerLevel) {
        int level       = Math.max(1, playerLevel + Dice.roll(3) - 2);
        int enemyType   = Dice.roll(5);
        return switch (enemyType) {
            case 1  -> new Enemy("Goblin",       40 + level * 10, 6  + level * 2, 2 + level,           level, "Common");
            case 2  -> new Enemy("Orc",          60 + level * 15, 8  + level * 3, 4 + level,           level, "Strong");
            case 3  -> new Enemy("Skeleton",     35 + level * 8,  7  + level * 2, 3 + level,           level, "Undead");
            case 4  -> new Enemy("Wild Wolf",    45 + level * 12, 9  + level * 2, 2 + level,           level, "Beast");
            default -> new Enemy("Young Dragon", 80 + level * 20, 10 + level * 4, 6 + level * 2,      level, "Boss");
        };
    }
}

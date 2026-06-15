package characters;

import items.Inventory;
import contracts.Attackable;

/**
 * Abstract base class for every entity that participates in combat.
 *
 * <p>Implements {@link Attackable} to allow polymorphic use in combat routines.
 * Concrete subclasses override {@link #calculateDamage(int)} and
 * {@link #useSpecialAbility(GameCharacter)} to express their unique mechanics.
 */
public abstract class GameCharacter implements Attackable {

    private String name;
    private int hitPoints;
    private int maxHitPoints;
    private int attack;
    private int defense;
    private int level;
    private Inventory inventory;

    /**
     * @param name      character name (must not be blank)
     * @param hitPoints starting and maximum HP (must be &gt; 0)
     * @param attack    base attack value (must be &ge; 0)
     * @param defense   base defense value (must be &ge; 0)
     * @param level     starting level (must be &ge; 1)
     * @throws IllegalArgumentException if any constraint is violated
     */
    public GameCharacter(String name, int hitPoints, int attack, int defense, int level) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Character name cannot be blank");
        if (hitPoints <= 0)
            throw new IllegalArgumentException("Hit points must be positive");
        if (attack < 0 || defense < 0)
            throw new IllegalArgumentException("Attack and defense cannot be negative");
        if (level < 1)
            throw new IllegalArgumentException("Level must be at least 1");

        this.name         = name;
        this.hitPoints    = hitPoints;
        this.maxHitPoints = hitPoints;
        this.attack       = attack;
        this.defense      = defense;
        this.level        = level;
        this.inventory    = new Inventory(20);
    }

    /** Deep-copy constructor. */
    public GameCharacter(GameCharacter other) {
        this.name         = other.name;
        this.hitPoints    = other.hitPoints;
        this.maxHitPoints = other.maxHitPoints;
        this.attack       = other.attack;
        this.defense      = other.defense;
        this.level        = other.level;
        this.inventory    = new Inventory(other.inventory);
    }

    // ── Getters and setters ───────────────────────────────────────────────────

    public String    getName()         { return name; }
    public int       getHitPoints()    { return hitPoints; }
    public int       getMaxHitPoints() { return maxHitPoints; }
    public int       getAttack()       { return attack; }
    public int       getDefense()      { return defense; }
    public int       getLevel()        { return level; }
    public Inventory getInventory()    { return inventory; }

    /** Clamps HP to [0, maxHitPoints]. */
    public void setHitPoints(int hitPoints) {
        this.hitPoints = Math.max(0, Math.min(hitPoints, maxHitPoints));
    }

    /**
     * @throws IllegalArgumentException if maxHitPoints &le; 0
     */
    public void setMaxHitPoints(int maxHitPoints) {
        if (maxHitPoints <= 0) throw new IllegalArgumentException("Max HP must be positive");
        this.maxHitPoints = maxHitPoints;
        this.hitPoints    = Math.min(this.hitPoints, maxHitPoints);
    }

    /** Clamps to 0 — attack can never go negative. */
    public void setAttack(int attack)   { this.attack  = Math.max(0, attack); }

    /** Clamps to 0 — defense can never go negative. */
    public void setDefense(int defense) { this.defense = Math.max(0, defense); }

    /** Clamps to 1 — level can never go below 1. */
    public void setLevel(int level)     { this.level   = Math.max(1, level); }

    // ── Combat ────────────────────────────────────────────────────────────────

    /**
     * Base damage formula: {@code attack + diceRoll}.
     * Subclasses override this to apply class-specific modifiers.
     */
    @Override
    public int calculateDamage(int diceRoll) {
        return attack + diceRoll;
    }

    /**
     * Applies damage reduced by defense. HP never drops below 0.
     * Real damage = max(0, damage − defense).
     */
    @Override
    public void takeDamage(int damage) {
        int realDamage = Math.max(0, damage - defense);
        hitPoints = Math.max(0, hitPoints - realDamage);
    }

    @Override
    public boolean isAlive() { return hitPoints > 0; }

    /**
     * Heals the character up to {@link #getMaxHitPoints()}.
     *
     * @param amount amount to heal; non-positive values are silently ignored
     */
    public void heal(int amount) {
        if (amount <= 0) return;
        hitPoints = Math.min(hitPoints + amount, maxHitPoints);
    }

    /**
     * Activates this character's signature ability.
     *
     * @param target the ability's target
     * @return a message describing the outcome
     */
    public abstract String useSpecialAbility(GameCharacter target);

    // ── Display ───────────────────────────────────────────────────────────────

    public String getStatus() {
        return String.format("%s (Lv %d) — HP: %d/%d | ATK: %d | DEF: %d",
                name, level, hitPoints, maxHitPoints, attack, defense);
    }

    @Override
    public String toString() { return getStatus(); }
}

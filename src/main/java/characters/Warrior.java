package characters;

import engine.Dice;

/**
 * Melee fighter with a 20 % critical-hit chance and a rage state
 * that boosts damage at the cost of defense.
 */
public class Warrior extends GameCharacter {

    private final int criticalChance;
    private boolean enraged;

    public Warrior(String name, int hitPoints, int attack, int defense, int level) {
        super(name, hitPoints, attack, defense, level);
        this.criticalChance = 20;
        this.enraged        = false;
    }

    public Warrior() {
        this("Warrior", 120, 15, 10, 1);
    }

    public Warrior(Warrior other) {
        super(other);
        this.criticalChance = other.criticalChance;
        this.enraged        = other.enraged;
    }

    /**
     * Rolls for a critical hit (double damage) and applies the rage multiplier
     * if active (+50 % damage).
     */
    @Override
    public int calculateDamage(int diceRoll) {
        int damage = super.calculateDamage(diceRoll);
        if (Dice.roll(100) <= criticalChance) {
            System.out.println("CRITICAL HIT!");
            damage *= 2;
        }
        if (enraged) {
            System.out.println("Rage active! Damage increased!");
            damage = (int) (damage * 1.5);
        }
        return damage;
    }

    /**
     * Enters rage: +50 % damage modifier, −3 defense.
     */
    @Override
    public String useSpecialAbility(GameCharacter target) {
        enraged = true;
        setDefense(getDefense() - 3);
        return "Warrior enters a rage. Attack increased, defense reduced.";
    }

    /**
     * Ends rage and restores the defense penalty.
     */
    public void deactivateRage() {
        if (enraged) {
            enraged = false;
            setDefense(getDefense() + 3);
        }
    }

    public boolean isEnraged() { return enraged; }
}

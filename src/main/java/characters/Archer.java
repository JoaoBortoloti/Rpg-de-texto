package characters;

import engine.Dice;

/**
 * Ranged fighter with 30 arrows and a 75 % precision bonus.
 * Falls back to melee (half damage) when arrows run out.
 */
public class Archer extends GameCharacter {

    private int arrows;
    private final int accuracy;

    public Archer(String name, int hitPoints, int attack, int defense, int level) {
        super(name, hitPoints, attack, defense, level);
        this.arrows   = 30;
        this.accuracy = 75;
    }

    public Archer() {
        this("Archer", 100, 12, 7, 1);
    }

    public Archer(Archer other) {
        super(other);
        this.arrows   = other.arrows;
        this.accuracy = other.accuracy;
    }

    /**
     * Consumes 1 arrow per shot. Without arrows falls back to a melee
     * attack at 50 % effectiveness. A precision roll may add a +10 bonus.
     */
    @Override
    public int calculateDamage(int diceRoll) {
        if (arrows <= 0) {
            System.out.println("No arrows left! Using melee...");
            return super.calculateDamage(diceRoll) / 2;
        }
        arrows--;
        int damage = super.calculateDamage(diceRoll);
        if (Dice.roll(100) <= accuracy) {
            System.out.println("Precise shot!");
            damage += 10;
        }
        return damage;
    }

    /**
     * Arrow Barrage: costs 3 arrows, fires three d6 shots at the target.
     */
    @Override
    public String useSpecialAbility(GameCharacter target) {
        if (arrows < 3) return "Not enough arrows for Arrow Barrage.";
        arrows -= 3;
        int r1    = Dice.rollD6();
        int r2    = Dice.rollD6();
        int r3    = Dice.rollD6();
        int total = super.calculateDamage(r1) + super.calculateDamage(r2) + super.calculateDamage(r3);
        target.takeDamage(total);
        return String.format("Arrow Barrage! Arrows: −3 | Rolls: %d, %d, %d | Total damage: %d",
                r1, r2, r3, total);
    }

    public void reloadArrows(int amount) { arrows += amount; }
    public int  getArrows()             { return arrows; }

    @Override
    public String getStatus() {
        return super.getStatus() + String.format(" | Arrows: %d", arrows);
    }
}

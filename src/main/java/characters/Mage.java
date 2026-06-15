package characters;

import engine.Dice;

/**
 * Spellcaster who channels mana to amplify attacks and cast Fireball.
 *
 * <p>Mana pool and magic power both scale with level so the class remains
 * competitive at higher difficulties.
 */
public class Mage extends GameCharacter {

    private int mana;
    private final int maxMana;
    private final int magicPower;

    public Mage(String name, int hitPoints, int attack, int defense, int level) {
        super(name, hitPoints, attack, defense, level);
        this.maxMana    = 50 + (level * 10);
        this.mana       = maxMana;
        this.magicPower = 10 + (level * 5);
    }

    public Mage() {
        this("Mage", 80, 10, 5, 1);
    }

    public Mage(Mage other) {
        super(other);
        this.mana       = other.mana;
        this.maxMana    = other.maxMana;
        this.magicPower = other.magicPower;
    }

    /**
     * Spends 10 mana to add {@code magicPower} to the base hit.
     * Falls back to mundane damage when mana is insufficient.
     */
    @Override
    public int calculateDamage(int diceRoll) {
        int damage = super.calculateDamage(diceRoll);
        if (mana >= 10) {
            damage += magicPower;
            mana   -= 10;
            System.out.println("Spell cast! (−10 mana)");
        }
        return damage;
    }

    /**
     * Fireball: costs 30 mana, deals {@code magicPower + 10 + d6} damage directly.
     */
    @Override
    public String useSpecialAbility(GameCharacter target) {
        if (mana < 30) return "Not enough mana to cast Fireball.";
        mana -= 30;
        int roll   = Dice.rollD6();
        int damage = magicPower + 10 + roll;
        target.takeDamage(damage);
        return String.format("Fireball! Mana: −30 | Roll: %d | Damage: %d", roll, damage);
    }

    public void regenerateMana(int amount) {
        mana = Math.min(mana + amount, maxMana);
    }

    public int getMana()    { return mana; }
    public int getMaxMana() { return maxMana; }

    @Override
    public String getStatus() {
        return super.getStatus() + String.format(" | MANA: %d/%d", mana, maxMana);
    }
}

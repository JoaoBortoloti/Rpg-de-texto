package characters;

import engine.Dice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MageTest {

    @BeforeEach  void setUp()    { Dice.setSilent(true); }
    @AfterEach   void tearDown() { Dice.resetSeed(); Dice.setSilent(false); }

    private Mage mage() { return new Mage("Merlin", 80, 10, 5, 1); }

    // ── mana pool & scaling ───────────────────────────────────────────────────

    @Test
    void manaScalesWithLevel() {
        Mage lvl1 = new Mage("A", 80, 10, 5, 1);
        Mage lvl5 = new Mage("B", 80, 10, 5, 5);
        assertEquals(60, lvl1.getMaxMana());
        assertEquals(100, lvl5.getMaxMana());
    }

    @Test
    void startsManaAtMax() {
        Mage m = mage();
        assertEquals(m.getMaxMana(), m.getMana());
    }

    // ── calculateDamage ───────────────────────────────────────────────────────

    @Test
    void damageIncludesMagicPowerWhenManaAvailable() {
        Mage m = mage();
        int manaBefore = m.getMana();
        int dmg = m.calculateDamage(5);
        int magicPower = 10 + (1 * 5);
        assertEquals(10 + 5 + magicPower, dmg);
        assertEquals(manaBefore - 10, m.getMana());
    }

    @Test
    void damageSkipsMagicPowerWhenManaInsufficient() {
        Mage m = mage();
        while (m.getMana() >= 10) m.calculateDamage(0);
        int manaAfterDepletion = m.getMana();
        int dmg = m.calculateDamage(5);
        assertEquals(10 + 5, dmg);
        assertEquals(manaAfterDepletion, m.getMana());
    }

    @Test
    void eachAttackCostsTenMana() {
        Mage m = mage();
        int before = m.getMana();
        m.calculateDamage(1);
        assertEquals(before - 10, m.getMana());
    }

    // ── Fireball ──────────────────────────────────────────────────────────────

    @Test
    void fireballReducesTargetHp() {
        Mage m = mage();
        Warrior target = new Warrior("Target", 200, 5, 0, 1);
        int hpBefore = target.getHitPoints();
        m.useSpecialAbility(target);
        assertTrue(target.getHitPoints() < hpBefore);
    }

    @Test
    void fireballCostsThirtyMana() {
        Mage m = mage();
        int before = m.getMana();
        m.useSpecialAbility(new Warrior("Target", 200, 5, 0, 1));
        assertEquals(before - 30, m.getMana());
    }

    @Test
    void fireballRefusedWhenManaInsufficient() {
        Mage m = mage();
        while (m.getMana() >= 10) m.calculateDamage(0);
        String result = m.useSpecialAbility(new Warrior("Target", 200, 5, 0, 1));
        assertEquals("Not enough mana to cast Fireball.", result);
    }

    // ── regenerateMana ────────────────────────────────────────────────────────

    @Test
    void regenerateManaRestoresMana() {
        Mage m = mage();
        m.calculateDamage(1);
        int before = m.getMana();
        m.regenerateMana(5);
        assertEquals(before + 5, m.getMana());
    }

    @Test
    void regenerateManaCannotExceedMaxMana() {
        Mage m = mage();
        m.regenerateMana(9999);
        assertEquals(m.getMaxMana(), m.getMana());
    }

    // ── copy constructor ──────────────────────────────────────────────────────

    @Test
    void copyConstructorPreservesMana() {
        Mage m = mage();
        m.calculateDamage(0);
        Mage copy = new Mage(m);
        assertEquals(m.getMana(), copy.getMana());
    }

    @Test
    void copyConstructorIsIndependent() {
        Mage original = mage();
        Mage copy     = new Mage(original);
        copy.calculateDamage(0);
        assertEquals(original.getMana(), copy.getMana() + 10);
    }
}

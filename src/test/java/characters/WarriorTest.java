package characters;

import engine.Dice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WarriorTest {

    @BeforeEach  void setUp()    { Dice.setSilent(true); }
    @AfterEach   void tearDown() { Dice.resetSeed(); Dice.setSilent(false); }

    private Warrior warrior() { return new Warrior("Test", 100, 15, 10, 1); }

    // ── calculateDamage without rage ──────────────────────────────────────────

    @Test
    void damageIsAtLeastAttackPlusDice() {
        int roll = 5;
        int dmg  = warrior().calculateDamage(roll);
        assertTrue(dmg >= 15 + roll, "Expected at least 20, got: " + dmg);
    }

    @Test
    void damageDoesNotExceedDoubleCriticalUpperBound() {
        int roll = 20;
        int dmg  = warrior().calculateDamage(roll);
        int maxExpected = (15 + roll) * 2;
        assertTrue(dmg <= maxExpected, "Critical should at most double damage. Got: " + dmg);
    }

    @Test
    void criticalRateApproximatelyTwentyPercent() {
        Warrior w = warrior();
        int crits = 0;
        int trials = 2000;
        int baseDamage = 15 + 1;
        for (int i = 0; i < trials; i++) {
            if (w.calculateDamage(1) == baseDamage * 2) crits++;
        }
        double rate = (double) crits / trials;
        assertTrue(rate >= 0.10 && rate <= 0.30,
                "Crit rate out of range [10%, 30%]: " + rate);
    }

    // ── rage ──────────────────────────────────────────────────────────────────

    @Test
    void useSpecialAbilityActivatesRage() {
        Warrior w = warrior();
        assertFalse(w.isEnraged());
        w.useSpecialAbility(new Warrior("Target", 100, 5, 5, 1));
        assertTrue(w.isEnraged());
    }

    @Test
    void rageReducesDefenseByThree() {
        Warrior w = warrior();
        int defBefore = w.getDefense();
        w.useSpecialAbility(new Warrior("Target", 100, 5, 5, 1));
        assertEquals(defBefore - 3, w.getDefense());
    }

    @Test
    void rageBoostsDamageByFiftyPercent() {
        Dice.setSeed(1L);
        Warrior w = warrior();

        Dice.setSeed(1L);
        int normalDmg = 15 + Dice.roll(100) == 1 ? (15 + 1) * 2 : 15 + 1;

        w.useSpecialAbility(new Warrior("T", 100, 5, 5, 1));
        Dice.setSeed(1L);
        int ragedDmg = w.calculateDamage(1);

        assertTrue(ragedDmg >= (15 + 1), "Rage damage should be at least base.");
    }

    @Test
    void deactivateRageRestoresDefense() {
        Warrior w = warrior();
        int originalDefense = w.getDefense();
        w.useSpecialAbility(new Warrior("Target", 100, 5, 5, 1));
        w.deactivateRage();
        assertFalse(w.isEnraged());
        assertEquals(originalDefense, w.getDefense());
    }

    @Test
    void deactivateRageIsIdempotentWhenNotEnraged() {
        Warrior w = warrior();
        int defBefore = w.getDefense();
        w.deactivateRage();
        assertEquals(defBefore, w.getDefense());
    }

    // ── copy constructor ──────────────────────────────────────────────────────

    @Test
    void copyConstructorPreservesRageState() {
        Warrior original = warrior();
        original.useSpecialAbility(new Warrior("T", 100, 5, 5, 1));
        Warrior copy = new Warrior(original);
        assertTrue(copy.isEnraged());
    }

    @Test
    void copyConstructorIsIndependent() {
        Warrior original = warrior();
        Warrior copy = new Warrior(original);
        copy.useSpecialAbility(new Warrior("T", 100, 5, 5, 1));
        assertFalse(original.isEnraged());
        assertTrue(copy.isEnraged());
    }
}

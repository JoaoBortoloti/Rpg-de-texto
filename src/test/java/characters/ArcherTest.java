package characters;

import engine.Dice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArcherTest {

    @BeforeEach  void setUp()    { Dice.setSilent(true); }
    @AfterEach   void tearDown() { Dice.resetSeed(); Dice.setSilent(false); }

    private Archer archer() { return new Archer("Robin", 100, 12, 7, 1); }

    // ── arrows ────────────────────────────────────────────────────────────────

    @Test
    void startsWithThirtyArrows() {
        assertEquals(30, archer().getArrows());
    }

    @Test
    void shotDecrementsArrows() {
        Archer a = archer();
        a.calculateDamage(5);
        assertEquals(29, a.getArrows());
    }

    @Test
    void arrowsFallsBackToMeleeWhenEmpty() {
        Archer a = archer();
        while (a.getArrows() > 0) a.calculateDamage(1);
        int meleeDmg = a.calculateDamage(10);
        int expected = (12 + 10) / 2;
        assertEquals(expected, meleeDmg);
    }

    @Test
    void meleeFallbackDoesNotConsumeArrows() {
        Archer a = archer();
        while (a.getArrows() > 0) a.calculateDamage(1);
        a.calculateDamage(1);
        assertEquals(0, a.getArrows());
    }

    @Test
    void reloadArrowsAddsArrows() {
        Archer a = archer();
        a.calculateDamage(1);
        a.reloadArrows(5);
        assertEquals(34, a.getArrows());
    }

    // ── precision bonus ───────────────────────────────────────────────────────

    @Test
    void precisionBonusRateApproximatelySeventyFivePercent() {
        Archer a = archer();
        a.reloadArrows(5000);
        int bonusHits = 0;
        int baseDmg = 12 + 1;
        int trials = 2000;
        for (int i = 0; i < trials; i++) {
            int dmg = a.calculateDamage(1);
            if (dmg == baseDmg + 10) bonusHits++;
        }
        double rate = (double) bonusHits / trials;
        assertTrue(rate >= 0.60 && rate <= 0.90,
                "Precision rate out of [60%, 90%]: " + rate);
    }

    // ── Arrow Barrage ─────────────────────────────────────────────────────────

    @Test
    void arrowBarrageCostsThreeArrows() {
        Archer a = archer();
        a.useSpecialAbility(new Warrior("Target", 200, 5, 0, 1));
        assertEquals(27, a.getArrows());
    }

    @Test
    void arrowBarrageDealsDamageToTarget() {
        Archer a = archer();
        Warrior target = new Warrior("Target", 200, 5, 0, 1);
        int hpBefore = target.getHitPoints();
        a.useSpecialAbility(target);
        assertTrue(target.getHitPoints() < hpBefore);
    }

    @Test
    void arrowBarrageRefusedWithFewerThanThreeArrows() {
        Archer a = archer();
        while (a.getArrows() >= 3) a.calculateDamage(1);
        String result = a.useSpecialAbility(new Warrior("Target", 200, 5, 0, 1));
        assertEquals("Not enough arrows for Arrow Barrage.", result);
    }

    // ── copy constructor ──────────────────────────────────────────────────────

    @Test
    void copyConstructorPreservesArrows() {
        Archer original = archer();
        original.calculateDamage(1);
        Archer copy = new Archer(original);
        assertEquals(original.getArrows(), copy.getArrows());
    }

    @Test
    void copyConstructorIsIndependent() {
        Archer original = archer();
        Archer copy     = new Archer(original);
        copy.calculateDamage(1);
        assertEquals(30,  original.getArrows());
        assertEquals(29,  copy.getArrows());
    }
}

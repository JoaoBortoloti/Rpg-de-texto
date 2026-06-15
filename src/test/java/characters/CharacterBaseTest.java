package characters;

import engine.Dice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests base-class behaviour of GameCharacter through the Warrior concrete class.
 */
class CharacterBaseTest {

    @BeforeEach  void setUp()    { Dice.setSilent(true); }
    @AfterEach   void tearDown() { Dice.resetSeed(); Dice.setSilent(false); }

    private Warrior hero() { return new Warrior("Hero", 100, 10, 5, 1); }

    // ── Constructor validation ─────────────────────────────────────────────────

    @Test
    void blankNameThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Warrior("  ", 100, 10, 5, 1));
    }

    @Test
    void nullNameThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Warrior(null, 100, 10, 5, 1));
    }

    @Test
    void zeroHpThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Warrior("X", 0, 10, 5, 1));
    }

    @Test
    void negativeAttackThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Warrior("X", 100, -1, 5, 1));
    }

    @Test
    void negativeDefenseThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Warrior("X", 100, 10, -1, 1));
    }

    @Test
    void levelZeroThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Warrior("X", 100, 10, 5, 0));
    }

    // ── takeDamage ────────────────────────────────────────────────────────────

    @Test
    void takeDamageReducedByDefense() {
        Warrior warrior = hero();
        warrior.takeDamage(15);
        assertEquals(100 - (15 - 5), warrior.getHitPoints());
    }

    @Test
    void takeDamageNeverGoesBelowZero() {
        Warrior warrior = hero();
        warrior.takeDamage(9999);
        assertEquals(0, warrior.getHitPoints());
    }

    @Test
    void damageHigherThanHpSetToZero() {
        Warrior warrior = hero();
        warrior.takeDamage(200);
        assertEquals(0, warrior.getHitPoints());
    }

    @Test
    void damageAbsorbedCompletelyByDefense() {
        Warrior warrior = hero();
        warrior.takeDamage(3);
        assertEquals(100, warrior.getHitPoints());
    }

    // ── heal ──────────────────────────────────────────────────────────────────

    @Test
    void healRestoresHp() {
        Warrior warrior = hero();
        warrior.takeDamage(20);
        warrior.heal(10);
        assertEquals(100 - (20 - 5) + 10, warrior.getHitPoints());
    }

    @Test
    void healDoesNotExceedMaxHp() {
        Warrior warrior = hero();
        warrior.heal(9999);
        assertEquals(warrior.getMaxHitPoints(), warrior.getHitPoints());
    }

    @Test
    void healIgnoresNonPositiveAmount() {
        Warrior warrior = hero();
        warrior.takeDamage(20);
        int hpBefore = warrior.getHitPoints();
        warrior.heal(0);
        warrior.heal(-5);
        assertEquals(hpBefore, warrior.getHitPoints());
    }

    // ── isAlive ───────────────────────────────────────────────────────────────

    @Test
    void isAliveTrueWhenHpPositive() {
        assertTrue(hero().isAlive());
    }

    @Test
    void isAliveFalseAtZeroHp() {
        Warrior warrior = hero();
        warrior.takeDamage(9999);
        assertFalse(warrior.isAlive());
    }

    // ── setters ───────────────────────────────────────────────────────────────

    @Test
    void setHitPointsClampsToMax() {
        Warrior warrior = hero();
        warrior.setHitPoints(9999);
        assertEquals(warrior.getMaxHitPoints(), warrior.getHitPoints());
    }

    @Test
    void setHitPointsClampsToZero() {
        Warrior warrior = hero();
        warrior.setHitPoints(-50);
        assertEquals(0, warrior.getHitPoints());
    }

    @Test
    void setMaxHitPointsCapsCurrentHp() {
        Warrior warrior = hero();
        warrior.setMaxHitPoints(50);
        assertEquals(50, warrior.getHitPoints());
    }

    @Test
    void setMaxHitPointsZeroThrows() {
        assertThrows(IllegalArgumentException.class, () -> hero().setMaxHitPoints(0));
    }

    @Test
    void setAttackClampsToZero() {
        Warrior warrior = hero();
        warrior.setAttack(-99);
        assertEquals(0, warrior.getAttack());
    }

    @Test
    void setDefenseClampsToZero() {
        Warrior warrior = hero();
        warrior.setDefense(-99);
        assertEquals(0, warrior.getDefense());
    }

    @Test
    void setLevelClampsToOne() {
        Warrior warrior = hero();
        warrior.setLevel(0);
        assertEquals(1, warrior.getLevel());
    }

    // ── calculateDamage (base) ────────────────────────────────────────────────

    @Test
    void calculateDamageIsAtLeastAttackPlusDice() {
        Dice.setSeed(0L);
        Warrior warrior = new Warrior("X", 100, 10, 5, 1) {
            @Override public int calculateDamage(int diceRoll) {
                return getAttack() + diceRoll;
            }
        };
        assertTrue(warrior.calculateDamage(5) >= 10 + 5);
    }
}

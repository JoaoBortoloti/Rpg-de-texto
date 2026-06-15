package engine;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DiceTest {

    @BeforeEach  void setUp()    { Dice.setSilent(true); }
    @AfterEach   void tearDown() { Dice.resetSeed(); Dice.setSilent(false); }

    @Test
    void rollAlwaysReturnsValueInRange() {
        for (int i = 0; i < 1000; i++) {
            int result = Dice.roll(6);
            assertTrue(result >= 1 && result <= 6, "Expected 1–6, got: " + result);
        }
    }

    @Test
    void rollD6UsesSixFaces() {
        for (int i = 0; i < 500; i++) assertTrue(Dice.rollD6() >= 1 && Dice.rollD6() <= 6);
    }

    @Test
    void rollD20UsesTwentyFaces() {
        for (int i = 0; i < 500; i++) assertTrue(Dice.rollD20() >= 1 && Dice.rollD20() <= 20);
    }

    @Test
    void seedProducesDeterministicSequence() {
        Dice.setSeed(42L);
        int a1 = Dice.roll(20), a2 = Dice.roll(20);
        Dice.setSeed(42L);
        int b1 = Dice.roll(20), b2 = Dice.roll(20);
        assertEquals(a1, b1);
        assertEquals(a2, b2);
    }

    @Test
    void resetSeedRestoresRandomMode() {
        Dice.setSeed(42L);
        assertNotNull(Dice.getSeed());
        Dice.resetSeed();
        assertNull(Dice.getSeed());
    }

    @Test
    void invalidFacesThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> Dice.roll(0));
        assertThrows(IllegalArgumentException.class, () -> Dice.roll(-1));
    }
}

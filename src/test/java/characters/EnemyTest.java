package characters;

import engine.Dice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnemyTest {

    @BeforeEach  void setUp()    { Dice.setSilent(true); }
    @AfterEach   void tearDown() { Dice.resetSeed(); Dice.setSilent(false); }

    // ── constructor & basic getters ───────────────────────────────────────────

    @Test
    void xpRewardIsLevelTimedFifty() {
        Enemy e = new Enemy("Goblin", 50, 8, 3, 3, "Common");
        assertEquals(3 * 50, e.getXpReward());
    }

    @Test
    void typeIsPreserved() {
        Enemy e = new Enemy("Skeleton", 40, 7, 3, 2, "Undead");
        assertEquals("Undead", e.getType());
    }

    @Test
    void defaultConstructorCreatesGoblinAtLevelOne() {
        Enemy e = new Enemy();
        assertEquals("Goblin", e.getName());
        assertEquals(1 * 50, e.getXpReward());
    }

    // ── loot generation ───────────────────────────────────────────────────────

    @Test
    void inventoryIsGeneratedOnCreation() {
        Dice.setSeed(1L);
        Enemy e = new Enemy("Goblin", 50, 8, 3, 1, "Common");
        assertFalse(e.getInventory().isEmpty(), "Enemy should generate at least one loot item");
    }

    // ── useSpecialAbility ─────────────────────────────────────────────────────

    @Test
    void useSpecialAbilityReturnsFlavorText() {
        Enemy e = new Enemy("Goblin", 50, 8, 3, 1, "Common");
        String result = e.useSpecialAbility(new Warrior("Target", 100, 10, 5, 1));
        assertTrue(result.contains("Goblin"), "Message should mention enemy name");
    }

    // ── createRandom ──────────────────────────────────────────────────────────

    @Test
    void createRandomReturnsNonNullEnemy() {
        Enemy e = Enemy.createRandom(1);
        assertNotNull(e);
        assertTrue(e.isAlive());
    }

    @Test
    void createRandomLevelIsAtLeastOne() {
        for (int i = 0; i < 100; i++) {
            Enemy e = Enemy.createRandom(1);
            assertTrue(e.getLevel() >= 1, "Enemy level should be at least 1");
        }
    }

    @Test
    void createRandomProducesKnownEnemyNames() {
        java.util.Set<String> knownNames = java.util.Set.of(
            "Goblin", "Orc", "Skeleton", "Wild Wolf", "Young Dragon"
        );
        for (int i = 0; i < 200; i++) {
            assertTrue(knownNames.contains(Enemy.createRandom(5).getName()));
        }
    }

    // ── copy constructor ──────────────────────────────────────────────────────

    @Test
    void copyConstructorPreservesXpAndType() {
        Enemy original = new Enemy("Orc", 60, 10, 4, 2, "Strong");
        Enemy copy     = new Enemy(original);
        assertEquals(original.getXpReward(), copy.getXpReward());
        assertEquals(original.getType(),     copy.getType());
    }

    @Test
    void copyConstructorIsIndependent() {
        Enemy original = new Enemy("Orc", 60, 10, 4, 2, "Strong");
        Enemy copy     = new Enemy(original);
        copy.takeDamage(9999);
        assertTrue(original.isAlive());
        assertFalse(copy.isAlive());
    }
}

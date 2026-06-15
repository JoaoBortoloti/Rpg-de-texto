package items;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    private Inventory inventory;

    @BeforeEach
    void setUp() { inventory = new Inventory(3); }

    private Item potion(int qty) { return new Item("Health Potion", "Heals HP", Effect.HEAL, qty, 30); }
    private Item bomb()          { return new Item("Bomb",           "Damage",   Effect.DAMAGE, 1, 25); }
    private Item strPotion()     { return new Item("Strength Potion","ATK buff", Effect.ATTACK_BUFF, 1, 5); }

    // ── add ───────────────────────────────────────────────────────────────────

    @Test
    void addItemIncreasesSize() {
        inventory.add(potion(1));
        assertEquals(1, inventory.getSize());
    }

    @Test
    void addSameItemTypeStacksQuantity() {
        inventory.add(potion(2));
        inventory.add(potion(3));
        assertEquals(1, inventory.getSize());
        assertEquals(5, inventory.findByName("Health Potion").getQuantity());
    }

    @Test
    void addNullThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> inventory.add(null));
    }

    @Test
    void addBeyondCapacityThrowsIllegalState() {
        inventory.add(potion(1));
        inventory.add(bomb());
        inventory.add(strPotion());
        Item extra = new Item("Defense Potion", "DEF buff", Effect.DEFENSE_BUFF, 1, 5);
        assertThrows(IllegalStateException.class, () -> inventory.add(extra));
    }

    @Test
    void addDoesNotExceedCapacityForExistingItem() {
        inventory.add(potion(1));
        inventory.add(bomb());
        inventory.add(strPotion());
        assertDoesNotThrow(() -> inventory.add(potion(5)));
        assertEquals(6, inventory.findByName("Health Potion").getQuantity());
    }

    // ── remove ────────────────────────────────────────────────────────────────

    @Test
    void removeDecreasesQuantity() {
        inventory.add(potion(5));
        assertTrue(inventory.remove("Health Potion", 3));
        assertEquals(2, inventory.findByName("Health Potion").getQuantity());
    }

    @Test
    void removeAllQuantityDeletesSlot() {
        inventory.add(potion(2));
        inventory.remove("Health Potion", 2);
        assertTrue(inventory.isEmpty());
    }

    @Test
    void removeInsufficientQuantityReturnsFalse() {
        inventory.add(potion(1));
        assertFalse(inventory.remove("Health Potion", 99));
        assertEquals(1, inventory.findByName("Health Potion").getQuantity());
    }

    @Test
    void removeNonExistentItemReturnsFalse() {
        assertFalse(inventory.remove("Ghost Item", 1));
    }

    @Test
    void removeZeroQuantityThrows() {
        inventory.add(potion(1));
        assertThrows(IllegalArgumentException.class, () -> inventory.remove("Health Potion", 0));
    }

    // ── findByName ────────────────────────────────────────────────────────────

    @Test
    void findByNameIsCaseInsensitive() {
        inventory.add(potion(1));
        assertNotNull(inventory.findByName("HEALTH POTION"));
        assertNotNull(inventory.findByName("health potion"));
    }

    @Test
    void findByNameReturnsNullWhenMissing() {
        assertNull(inventory.findByName("Missing Item"));
    }

    @Test
    void findByNameNullOrBlankReturnsNull() {
        assertNull(inventory.findByName(null));
        assertNull(inventory.findByName("   "));
    }

    // ── findByIndex ───────────────────────────────────────────────────────────

    @Test
    void findByIndexReturnsCorrectItem() {
        inventory.add(bomb());
        inventory.add(potion(1));
        List<Item> sorted = inventory.listSorted();
        for (int i = 0; i < sorted.size(); i++) {
            assertEquals(sorted.get(i).getName(), inventory.findByIndex(i).getName());
        }
    }

    @Test
    void findByIndexOutOfBoundsReturnsNull() {
        inventory.add(potion(1));
        assertNull(inventory.findByIndex(-1));
        assertNull(inventory.findByIndex(99));
    }

    // ── listSorted ────────────────────────────────────────────────────────────

    @Test
    void listSortedReturnsItemsInAlphabeticalOrder() {
        inventory.add(strPotion());
        inventory.add(bomb());
        inventory.add(potion(1));
        List<Item> sorted = inventory.listSorted();
        for (int i = 1; i < sorted.size(); i++) {
            assertTrue(sorted.get(i - 1).compareTo(sorted.get(i)) <= 0);
        }
    }

    @Test
    void listSortedReturnsCopies() {
        inventory.add(potion(5));
        List<Item> copies = inventory.listSorted();
        copies.get(0).increaseQuantity(100);
        assertEquals(5, inventory.findByName("Health Potion").getQuantity());
    }

    // ── deep-copy constructor ─────────────────────────────────────────────────

    @Test
    void copyConstructorIsIndependent() {
        inventory.add(potion(3));
        Inventory copy = new Inventory(inventory);
        copy.remove("Health Potion", 2);
        assertEquals(3, inventory.findByName("Health Potion").getQuantity());
        assertEquals(1, copy.findByName("Health Potion").getQuantity());
    }

    // ── misc ──────────────────────────────────────────────────────────────────

    @Test
    void clearRemovesAllItems() {
        inventory.add(potion(1));
        inventory.add(bomb());
        inventory.clear();
        assertTrue(inventory.isEmpty());
        assertEquals(0, inventory.getSize());
    }

    @Test
    void constructorZeroCapacityThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Inventory(0));
    }
}

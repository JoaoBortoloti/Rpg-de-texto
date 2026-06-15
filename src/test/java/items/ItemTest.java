package items;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    private Item healPotion() { return new Item("Health Potion", "Restores HP", Effect.HEAL, 3, 30); }

    @Test
    void itemsWithSameNameAndEffectAreEqual() {
        Item a = new Item("Health Potion", "desc A", Effect.HEAL, 1, 30);
        Item b = new Item("Health Potion", "desc B", Effect.HEAL, 99, 50);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void itemsWithDifferentEffectsAreNotEqual() {
        Item heal   = new Item("Elixir", "desc", Effect.HEAL,   1, 10);
        Item damage = new Item("Elixir", "desc", Effect.DAMAGE, 1, 10);
        assertNotEquals(heal, damage);
    }

    @Test
    void compareToOrdersAlphabeticallyByName() {
        Item a = new Item("Antidote", "desc", Effect.HEAL, 1, 10);
        Item b = new Item("Potion",   "desc", Effect.HEAL, 1, 10);
        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
    }

    @Test
    void compareToBreaksTiesByEffect() {
        Item buff = new Item("Elixir", "desc", Effect.ATTACK_BUFF, 1, 10);
        Item heal = new Item("Elixir", "desc", Effect.HEAL,        1, 10);
        assertNotEquals(0, buff.compareTo(heal));
    }

    @Test
    void compareToReturnsZeroForEqualItems() {
        Item a = new Item("Potion", "d1", Effect.HEAL, 5, 20);
        Item b = new Item("Potion", "d2", Effect.HEAL, 1, 99);
        assertEquals(0, a.compareTo(b));
    }

    @Test
    void increaseQuantityAddsAmount() {
        Item item = healPotion();
        item.increaseQuantity(2);
        assertEquals(5, item.getQuantity());
    }

    @Test
    void increaseQuantityWithNegativeThrows() {
        assertThrows(IllegalArgumentException.class, () -> healPotion().increaseQuantity(-1));
    }

    @Test
    void decreaseQuantityReturnsTrueOnSuccess() {
        Item item = healPotion();
        assertTrue(item.decreaseQuantity(2));
        assertEquals(1, item.getQuantity());
    }

    @Test
    void decreaseQuantityReturnsFalseOnInsufficientStock() {
        Item item = healPotion();
        assertFalse(item.decreaseQuantity(10));
        assertEquals(3, item.getQuantity());
    }

    @Test
    void decreaseQuantityNeverGoesBelowZero() {
        Item item = healPotion();
        item.decreaseQuantity(3);
        assertEquals(0, item.getQuantity());
        assertFalse(item.decreaseQuantity(1));
    }

    @Test
    void copyConstructorIsIndependent() {
        Item original = healPotion();
        Item copy     = new Item(original);
        copy.increaseQuantity(10);
        assertEquals(3,  original.getQuantity());
        assertEquals(13, copy.getQuantity());
    }

    @Test
    void negativeQuantityInConstructorThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Item("X", "desc", Effect.HEAL, -1, 10));
    }
}

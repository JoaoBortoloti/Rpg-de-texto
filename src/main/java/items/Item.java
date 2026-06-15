package items;

import java.util.Objects;

/**
 * Represents a game item that can stack multiple units.
 *
 * <p><b>Logical equality:</b> two items are equal when they share the same
 * name and effect — quantity is intentionally excluded so that stacking
 * logic treats them as the same slot.
 *
 * <p><b>Natural ordering:</b> alphabetical by name, breaking ties by effect.
 */
public class Item implements Comparable<Item> {

    private final String name;
    private final String description;
    private final Effect effect;
    private int quantity;
    private final int effectValue;

    /**
     * Creates a new item.
     *
     * @param name        logical identity together with {@code effect}
     * @param description flavour text shown in the inventory
     * @param effect      type of effect this item applies
     * @param quantity    initial stack size (must be &ge; 0)
     * @param effectValue numeric magnitude of the effect (HP restored, attack added, etc.)
     * @throws IllegalArgumentException if quantity is negative
     */
    public Item(String name, String description, Effect effect, int quantity, int effectValue) {
        if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative");
        this.name        = name;
        this.description = description;
        this.effect      = effect;
        this.quantity    = quantity;
        this.effectValue = effectValue;
    }

    /** Copy constructor — produces an independent clone. */
    public Item(Item other) {
        this.name        = other.name;
        this.description = other.description;
        this.effect      = other.effect;
        this.quantity    = other.quantity;
        this.effectValue = other.effectValue;
    }

    public String getName()       { return name; }
    public String getDescription(){ return description; }
    public Effect  getEffect()    { return effect; }
    public int     getQuantity()  { return quantity; }
    public int     getEffectValue(){ return effectValue; }

    /**
     * Directly sets the quantity. Prefer {@link #increaseQuantity} /
     * {@link #decreaseQuantity} in normal game flow.
     *
     * @throws IllegalArgumentException if quantity is negative
     */
    public void setQuantity(int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative");
        this.quantity = quantity;
    }

    public void increaseQuantity(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Increase amount cannot be negative");
        this.quantity += amount;
    }

    /**
     * Decreases quantity by {@code amount} if there is enough stock.
     *
     * @return {@code true} if the decrease was applied; {@code false} if stock was insufficient
     * @throws IllegalArgumentException if amount is negative
     */
    public boolean decreaseQuantity(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Decrease amount cannot be negative");
        if (this.quantity < amount) return false;
        this.quantity -= amount;
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(name, item.name) && effect == item.effect;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, effect);
    }

    @Override
    public int compareTo(Item other) {
        int byName = this.name.compareTo(other.name);
        return byName != 0 ? byName : this.effect.compareTo(other.effect);
    }

    @Override
    public String toString() {
        return String.format("%s (x%d) - %s [%s: %d]",
                name, quantity, description, effect.name(), effectValue);
    }
}

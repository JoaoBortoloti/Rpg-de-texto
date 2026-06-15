package items;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages a character's item collection.
 *
 * <p>Items are keyed by {@code (name + effect)} so identical items are
 * automatically stacked instead of occupying separate slots.
 * Capacity limits the number of distinct item types, not total units.
 */
public class Inventory {

    private final Map<String, Item> items;
    private final int maxCapacity;

    /**
     * @param maxCapacity maximum number of distinct item types (slots)
     * @throws IllegalArgumentException if maxCapacity &le; 0
     */
    public Inventory(int maxCapacity) {
        if (maxCapacity <= 0) throw new IllegalArgumentException("Max capacity must be positive");
        this.maxCapacity = maxCapacity;
        this.items = new HashMap<>();
    }

    /** Deep-copy constructor — changes to either inventory do not affect the other. */
    public Inventory(Inventory other) {
        this.maxCapacity = other.maxCapacity;
        this.items = new HashMap<>();
        for (Map.Entry<String, Item> entry : other.items.entrySet()) {
            this.items.put(entry.getKey(), new Item(entry.getValue()));
        }
    }

    /**
     * Adds an item. If an identical item already exists, its quantity is merged.
     *
     * @throws IllegalArgumentException if item is null
     * @throws IllegalStateException    if the inventory is full and this is a new item type
     */
    public void add(Item item) {
        if (item == null) throw new IllegalArgumentException("Item cannot be null");
        String key = buildKey(item);
        if (items.containsKey(key)) {
            items.get(key).increaseQuantity(item.getQuantity());
        } else {
            if (items.size() >= maxCapacity) throw new IllegalStateException("Inventory is full!");
            items.put(key, new Item(item));
        }
    }

    /**
     * Removes a given quantity of an item by name.
     *
     * @return {@code true} if the removal succeeded; {@code false} if the item
     *         was not found or had insufficient quantity
     * @throws IllegalArgumentException if quantity &le; 0
     */
    public boolean remove(String name, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        Item item = findByName(name);
        if (item == null || item.getQuantity() < quantity) return false;
        item.decreaseQuantity(quantity);
        if (item.getQuantity() == 0) items.remove(buildKey(item));
        return true;
    }

    /**
     * Finds an item by name (case-insensitive).
     *
     * @return a reference to the internal item, or {@code null} if not found
     */
    public Item findByName(String name) {
        if (name == null || name.isBlank()) return null;
        String target = name.trim();
        return items.values().stream()
                .filter(i -> i.getName().trim().equalsIgnoreCase(target))
                .findFirst().orElse(null);
    }

    /**
     * Finds an item by its position in the sorted list (0-based).
     *
     * @return a reference to the internal item, or {@code null} if index is invalid
     */
    public Item findByIndex(int index) {
        List<Item> sorted = listSorted();
        if (index < 0 || index >= sorted.size()) return null;
        return findByName(sorted.get(index).getName());
    }

    /**
     * Returns a sorted list of item copies (does not expose internal references).
     */
    public List<Item> listSorted() {
        return items.values().stream()
                .map(Item::new)
                .sorted()
                .collect(Collectors.toList());
    }

    public boolean isEmpty()  { return items.isEmpty(); }
    public int     getSize()  { return items.size(); }
    public void    clear()    { items.clear(); }

    private String buildKey(Item item) {
        return item.getName().toLowerCase() + "_" + item.getEffect().name();
    }

    @Override
    public String toString() {
        if (isEmpty()) return "Inventory is empty";
        StringBuilder sb = new StringBuilder("=== INVENTORY ===\n");
        List<Item> sorted = listSorted();
        for (int i = 0; i < sorted.size(); i++) {
            sb.append(String.format("%d. %s%n", i + 1, sorted.get(i)));
        }
        return sb.toString();
    }
}

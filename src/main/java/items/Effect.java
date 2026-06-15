package items;

/**
 * Enumerates the possible effects an {@link Item} can carry.
 */
public enum Effect {
    HEAL("Restores hit points"),
    ATTACK_BUFF("Increases attack"),
    DEFENSE_BUFF("Increases defense"),
    DAMAGE("Deals damage to a target"),
    OTHER("Special effect");

    private final String description;

    Effect(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

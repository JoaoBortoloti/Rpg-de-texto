package contracts;

/**
 * Contract that every entity capable of participating in combat must implement.
 */
public interface Attackable {

    /**
     * Calculates the damage dealt based on a dice roll.
     *
     * @param diceRoll the value rolled
     * @return damage to be applied to the target
     */
    int calculateDamage(int diceRoll);

    /**
     * Applies incoming damage, reduced by the entity's defense.
     *
     * @param damage raw damage before defense reduction
     */
    void takeDamage(int damage);

    /**
     * @return {@code true} if the entity still has hit points remaining
     */
    boolean isAlive();
}

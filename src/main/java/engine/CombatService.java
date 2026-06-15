package engine;

import characters.Enemy;
import characters.GameCharacter;
import items.Item;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Encapsulates all combat-turn logic.
 *
 * <p>A single {@link #fight} method handles both regular encounters and
 * the boss fight, parameterised by {@code allowFlee} and {@code isBoss}.
 */
public class CombatService {

    private final BufferedReader reader;

    public CombatService(BufferedReader reader) {
        this.reader = reader;
    }

    /**
     * Runs the combat loop until one side falls or the player flees.
     *
     * @param player    the player's character
     * @param enemy     the opposing enemy
     * @param allowFlee whether the player may attempt to flee
     * @param isBoss    if {@code true}, the enemy can use a shadow attack (double damage)
     * @return the {@link CombatResult} describing the outcome
     */
    public CombatResult fight(GameCharacter player, Enemy enemy,
                              boolean allowFlee, boolean isBoss) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println(isBoss ? "FINAL BATTLE!" : "COMBAT STARTED!");
        System.out.println("=".repeat(50));

        while (player.isAlive() && enemy.isAlive()) {
            System.out.println("\n--- Your turn ---");
            printCombatMenu(allowFlee);

            int maxOption = allowFlee ? 4 : 3;
            boolean skippedEnemyTurn = false;

            switch (readOption(1, maxOption)) {
                case 1 -> {
                    int roll   = Dice.rollD6();
                    int damage = player.calculateDamage(roll);
                    enemy.takeDamage(damage);
                    System.out.printf("You rolled %d and dealt %d damage!%n", roll, damage);
                    System.out.println(enemy.getStatus());
                }
                case 2 -> {
                    useItem(player, enemy);
                    skippedEnemyTurn = true;
                }
                case 3 -> {
                    if (allowFlee) {
                        if (attemptFlee()) { System.out.println("You fled successfully!"); return CombatResult.FLED; }
                        System.out.println("Failed to flee!");
                    } else {
                        System.out.println(player.useSpecialAbility(enemy));
                        System.out.println(enemy.getStatus());
                    }
                }
                case 4 -> {
                    System.out.println(player.useSpecialAbility(enemy));
                    System.out.println(enemy.getStatus());
                }
            }

            if (!enemy.isAlive()) return CombatResult.VICTORY;
            if (skippedEnemyTurn)  continue;

            // Enemy turn
            System.out.println("\n--- " + enemy.getName() + "'s turn ---");
            int roll = Dice.rollD6();
            System.out.println(enemy.getName() + " rolled: " + roll);

            if (isBoss && Dice.roll(10) >= GameConfig.BOSS_SPECIAL_THRESHOLD) {
                System.out.println(enemy.getName() + " uses SHADOW STRIKE!");
                int damage = enemy.calculateDamage(roll) * 2;
                player.takeDamage(damage);
                System.out.println("You took " + damage + " devastating damage!");
            } else {
                int damage = enemy.calculateDamage(roll);
                player.takeDamage(damage);
                System.out.println("You took " + damage + " damage!");
            }

            System.out.println(player.getStatus());
            waitForEnter();
        }

        return player.isAlive() ? CombatResult.VICTORY : CombatResult.DEFEAT;
    }

    /**
     * Lets the player select and consume an item from their inventory.
     * Can be called during combat (with a live {@code target}) or from
     * the main menu (pass {@code null} for out-of-combat use).
     */
    public void useItem(GameCharacter player, Enemy target) {
        if (player.getInventory().isEmpty()) {
            System.out.println("Your inventory is empty!");
            return;
        }
        System.out.println("\n" + player.getInventory());
        System.out.print("Enter item number (0 to cancel): ");

        int choice = readOption(0, player.getInventory().getSize());
        if (choice == 0) return;

        Item item = player.getInventory().findByIndex(choice - 1);
        if (item == null) { System.out.println("Invalid item!"); return; }

        if (applyItemEffect(player, item, target)) {
            player.getInventory().remove(item.getName(), 1);
            System.out.println(item.getName() + " used!");
        }
    }

    private boolean applyItemEffect(GameCharacter player, Item item, Enemy target) {
        return switch (item.getEffect()) {
            case HEAL -> {
                player.heal(item.getEffectValue());
                System.out.println("You recovered " + item.getEffectValue() + " HP!");
                yield true;
            }
            case ATTACK_BUFF -> {
                player.setAttack(player.getAttack() + item.getEffectValue());
                System.out.println("Attack increased by " + item.getEffectValue() + "!");
                yield true;
            }
            case DEFENSE_BUFF -> {
                player.setDefense(player.getDefense() + item.getEffectValue());
                System.out.println("Defense increased by " + item.getEffectValue() + "!");
                yield true;
            }
            case DAMAGE -> {
                if (target == null) {
                    System.out.println("This item can only be used in combat!");
                    yield false;
                }
                int variation = Dice.rollD6() - 3;
                int damage    = Math.max(1, item.getEffectValue() + variation);
                target.takeDamage(damage);
                System.out.printf("Used %s and dealt %d damage to %s!%n",
                        item.getName(), damage, target.getName());
                System.out.println(target.getStatus());
                yield true;
            }
            default -> {
                System.out.println("Special effect applied!");
                yield true;
            }
        };
    }

    private void printCombatMenu(boolean allowFlee) {
        System.out.println("1. Attack");
        System.out.println("2. Use item");
        if (allowFlee) {
            System.out.println("3. Attempt to flee");
            System.out.println("4. Special ability");
        } else {
            System.out.println("3. Special ability");
        }
    }

    private boolean attemptFlee() {
        return Dice.rollD20() >= GameConfig.FLEE_THRESHOLD;
    }

    private int readOption(int min, int max) {
        while (true) {
            try {
                int option = Integer.parseInt(reader.readLine().trim());
                if (option >= min && option <= max) return option;
                System.out.print("Invalid option! Enter " + min + "–" + max + ": ");
            } catch (IOException | NumberFormatException e) {
                System.out.print("Invalid input! Enter a number: ");
            }
        }
    }

    private void waitForEnter() {
        System.out.println("\n[Press ENTER to continue]");
        try { reader.readLine(); } catch (IOException ignored) {}
    }
}

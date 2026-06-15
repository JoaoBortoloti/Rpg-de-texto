package engine;

import characters.Enemy;
import characters.GameCharacter;
import items.Effect;
import items.Item;

/**
 * Processes random exploration events: enemy encounters, item finds, and traps.
 *
 * <p>Returns the defeated {@link Enemy} when combat is won so the caller
 * can award XP and loot; returns {@code null} in all other cases.
 */
public class ExplorationService {

    private final CombatService combatService;

    public ExplorationService(CombatService combatService) {
        this.combatService = combatService;
    }

    /**
     * Rolls a d10 and dispatches to the appropriate encounter.
     *
     * @param player the exploring character
     * @return the defeated enemy, or {@code null} if no combat victory occurred
     */
    public Enemy processEvent(GameCharacter player) {
        int roll = Dice.roll(10);
        if      (roll <= GameConfig.ENEMY_THRESHOLD) return fightEnemy(player);
        else if (roll <= GameConfig.ITEM_THRESHOLD)  { findItem(player); }
        else if (roll == GameConfig.TRAP_THRESHOLD)  { triggerTrap(player); }
        else System.out.println("You explore the area but find nothing of interest.");
        return null;
    }

    private Enemy fightEnemy(GameCharacter player) {
        Enemy enemy = Enemy.createRandom(player.getLevel());
        System.out.println("\nA " + enemy.getName() + " appears!");
        System.out.println(enemy.getStatus());
        CombatResult result = combatService.fight(player, enemy, true, false);
        return result == CombatResult.VICTORY ? enemy : null;
    }

    private void findItem(GameCharacter player) {
        Item item = switch (Dice.roll(4)) {
            case 1  -> new Item("Health Potion",   "Restores 30 HP",    Effect.HEAL,         1, 30);
            case 2  -> new Item("Strength Potion", "Increases attack",  Effect.ATTACK_BUFF,  1, 5);
            case 3  -> new Item("Defense Potion",  "Increases defense", Effect.DEFENSE_BUFF, 1, 5);
            default -> new Item("Rare Elixir",     "Restores 50 HP",    Effect.HEAL,         1, 50);
        };
        System.out.println("You found: " + item.getName() + "!");
        try {
            player.getInventory().add(item);
        } catch (IllegalStateException e) {
            System.out.println("Inventory full! Could not pick up: " + item.getName());
        }
    }

    private void triggerTrap(GameCharacter player) {
        System.out.println("You fell into a trap!");
        int damage = Dice.roll(GameConfig.TRAP_DICE_FACES);
        player.takeDamage(damage);
        System.out.println("You took " + damage + " damage!");
        System.out.println(player.getStatus());
    }
}

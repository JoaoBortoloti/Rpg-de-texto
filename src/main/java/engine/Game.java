package engine;

import characters.*;
import items.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.nio.charset.StandardCharsets;

/**
 * Main game orchestrator.
 *
 * <p>Manages the game loop, menus, and story progression, delegating
 * specialised responsibilities to:
 * <ul>
 *   <li>{@link CombatService} — turn-based combat</li>
 *   <li>{@link ExplorationService} — random exploration events</li>
 *   <li>{@link SaveSystem} — disk persistence</li>
 * </ul>
 */
public class Game {

    private GameCharacter player;
    private final BufferedReader reader;
    private final CombatService    combatService;
    private final ExplorationService explorationService;
    private final SaveSystem         saveSystem;

    private int     currentXp;
    private int     nextLevelXp;
    private boolean gameActive;
    private int     explorationCount;
    private int     currentChapter;
    private boolean bossDefeated;

    public Game() {
        this.reader             = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        this.combatService      = new CombatService(reader);
        this.explorationService = new ExplorationService(combatService);
        this.saveSystem         = new SaveSystem();

        this.currentXp        = 0;
        this.nextLevelXp      = GameConfig.INITIAL_NEXT_LEVEL_XP;
        this.gameActive       = true;
        this.explorationCount = 0;
        this.currentChapter   = 1;
        this.bossDefeated     = false;
    }

    public void start() {
        printBanner();
        startScreen();
        if (player == null) { System.out.println("Exiting game."); return; }
        printIntroIfNewGame();
        gameLoop();
    }

    // ── Start screen ──────────────────────────────────────────────────────────

    private void startScreen() {
        boolean choosing = true;
        while (choosing) {
            System.out.println("1. New game");
            System.out.println("2. Load game");
            System.out.println("0. Quit");
            System.out.print("Choice: ");
            switch (readOption(0, 2)) {
                case 1 -> { createCharacter(); choosing = false; }
                case 2 -> { if (loadGame())    choosing = false; }
                case 0 -> { gameActive = false; choosing = false; }
            }
        }
    }

    private void printBanner() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║     TEXT RPG — JAVA OOP SHOWCASE     ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.println();
    }

    private void createCharacter() {
        System.out.println("Choose your class:");
        System.out.println("1. Warrior — high HP and defense, critical hits");
        System.out.println("2. Mage    — powerful spells, low defense");
        System.out.println("3. Archer  — precise ranged attacks");

        int choice = readOption(1, 3);
        System.out.print("\nEnter your character's name: ");
        String name = readLine();

        player = switch (choice) {
            case 1 -> new Warrior(name, 120, 15, 10, 1);
            case 2 -> new Mage(name, 80, 10, 5, 1);
            default -> new Archer(name, 100, 12, 7, 1);
        };

        player.getInventory().add(new Item("Health Potion",   "Restores 30 HP",      Effect.HEAL,        3, 30));
        player.getInventory().add(new Item("Strength Potion", "Increases attack by 5", Effect.ATTACK_BUFF, 1, 5));

        System.out.println("\nCharacter created!");
        System.out.println(player.getStatus());
    }

    // ── Game loop ─────────────────────────────────────────────────────────────

    private void gameLoop() {
        while (gameActive && player.isAlive() && !bossDefeated) {
            printMenu();
            switch (readOption(1, 6)) {
                case 1 -> explore();
                case 2 -> combatService.useItem(player, null);
                case 3 -> System.out.println("\n" + player.getInventory());
                case 4 -> printStatus();
                case 5 -> saveSystem.save(player, currentXp, nextLevelXp,
                                          currentChapter, explorationCount,
                                          bossDefeated, reader);
                case 6 -> quit();
            }
        }
        if (!player.isAlive()) gameOver();
        else if (bossDefeated) victoryEnding();
    }

    private void printMenu() {
        System.out.println("\n" + "─".repeat(50));
        System.out.printf("Chapter %d | Explorations: %d%n", currentChapter, explorationCount);
        System.out.println("What would you like to do?");
        System.out.println("1. Explore");
        System.out.println("2. Use item");
        System.out.println("3. View inventory");
        System.out.println("4. View status");
        System.out.println("5. Save game");
        System.out.println("6. Quit");
        System.out.print("Choice: ");
    }

    // ── Exploration & story ───────────────────────────────────────────────────

    private void explore() {
        System.out.println("\nExploring...");
        explorationCount++;

        if (explorationCount % GameConfig.STORY_CADENCE == 0) {
            advanceStory();
            return;
        }

        Enemy defeated = explorationService.processEvent(player);
        if (defeated != null) onVictory(defeated);
    }

    private void advanceStory() {
        currentChapter++;
        System.out.println("\n" + "=".repeat(50));
        switch (currentChapter) {
            case 2 -> {
                System.out.println("CHAPTER 2: THE ABANDONED VILLAGE");
                System.out.println("=".repeat(50));
                System.out.println("You find an abandoned village.");
                System.out.println("Signs of battle are everywhere.");
                System.out.println("Written in blood on the walls: 'It comes at night'.");
                System.out.println("You sense you are drawing close to the castle...");
            }
            case 3 -> {
                System.out.println("CHAPTER 3: THE CURSED GRAVEYARD");
                System.out.println("=".repeat(50));
                System.out.println("A graveyard stretches before you.");
                System.out.println("Undead wander between the gravestones.");
                System.out.println("A dark energy seeps from the earth.");
                System.out.println("The castle looms ever closer...");
            }
            case 4 -> {
                System.out.println("CHAPTER 4: THE BROKEN BRIDGE");
                System.out.println("=".repeat(50));
                System.out.println("You reach a bridge spanning a vast chasm.");
                System.out.println("The castle rises on the other side.");
                System.out.println("Creatures guard the crossing.");
                System.out.println("You are almost there...");
            }
            case 5 -> {
                System.out.println("CHAPTER 5: THE CASTLE GATES");
                System.out.println("=".repeat(50));
                System.out.println("At last, you stand before the castle gates.");
                System.out.println("They creak open slowly.");
                System.out.println("A voice echoes: 'Welcome, adventurer...'");
                System.out.println("Prepare yourself for the final confrontation!");
            }
            default -> {
                if (currentChapter >= 6) { startBossFight(); return; }
            }
        }
        System.out.println("=".repeat(50));
        waitForEnter();
    }

    // ── Boss fight ────────────────────────────────────────────────────────────

    private void startBossFight() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("FINAL CHAPTER: VORATH, THE ETERNAL");
        System.out.println("=".repeat(50));
        System.out.println("You enter the castle's grand hall.");
        System.out.println("A shadowy figure rises from the throne.");
        System.out.println("'You have come far, " + player.getName() + "...'");
        System.out.println("'But your journey ends here!'");
        System.out.println("=".repeat(50));
        waitForEnter();

        Enemy boss = new Enemy(
            "Vorath, the Eternal",
            GameConfig.BOSS_BASE_HP      + (player.getLevel() * GameConfig.BOSS_HP_PER_LEVEL),
            GameConfig.BOSS_BASE_ATTACK  + (player.getLevel() * GameConfig.BOSS_ATTACK_PER_LEVEL),
            GameConfig.BOSS_BASE_DEFENSE + player.getLevel(),
            player.getLevel()            + GameConfig.BOSS_LEVEL_BONUS,
            "Final Boss"
        );
        boss.getInventory().add(new Item("Legendary Elixir", "Restores 100 HP",      Effect.HEAL,        2, 100));
        boss.getInventory().add(new Item("Dark Essence",     "Increases attack by 10", Effect.ATTACK_BUFF, 1, 10));

        System.out.println("\n" + boss.getStatus());
        waitForEnter();

        if (combatService.fight(player, boss, false, true) == CombatResult.VICTORY) {
            onBossVictory(boss);
        }
    }

    private void onBossVictory(Enemy boss) {
        bossDefeated = true;
        System.out.println("\n" + "=".repeat(50));
        System.out.println("EPIC VICTORY!");
        System.out.println("=".repeat(50));
        System.out.println("You defeated " + boss.getName() + "!");
        System.out.println("The castle begins to crumble...");

        int xp = boss.getXpReward() * GameConfig.BOSS_XP_MULTIPLIER;
        currentXp += xp;
        System.out.println("\n" + xp + " XP gained!");
        lootEnemy(boss);
        waitForEnter();
    }

    private void victoryEnding() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("ENDING — THE LIGHT RETURNS");
        System.out.println("=".repeat(50));
        System.out.println("With Vorath, the Eternal defeated,");
        System.out.println("peace returns to the land.");
        System.out.println("You are hailed as a hero!");
        System.out.printf("%nFinal stats:%n  Level reached: %d%n  Total XP: %d%n  Explorations: %d%n",
                player.getLevel(), currentXp, explorationCount);
        System.out.println("\nCongratulations, " + player.getName() + "!");
        System.out.println("=".repeat(50));
    }

    // ── Post-combat ───────────────────────────────────────────────────────────

    private void onVictory(Enemy enemy) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("VICTORY!");
        System.out.println("=".repeat(50));
        System.out.println("You defeated " + enemy.getName() + "!");

        currentXp += enemy.getXpReward();
        System.out.println(enemy.getXpReward() + " XP");

        if (currentXp >= nextLevelXp) levelUp();
        lootEnemy(enemy);
        waitForEnter();
    }

    private void levelUp() {
        player.setLevel(player.getLevel() + 1);
        currentXp  -= nextLevelXp;
        nextLevelXp = (int) (nextLevelXp * GameConfig.LEVEL_XP_MULTIPLIER);

        player.setMaxHitPoints(player.getMaxHitPoints() + GameConfig.LEVEL_UP_HP_BONUS);
        player.setHitPoints(player.getMaxHitPoints());
        player.setAttack(player.getAttack()   + GameConfig.LEVEL_UP_ATTACK_BONUS);
        player.setDefense(player.getDefense() + GameConfig.LEVEL_UP_DEFENSE_BONUS);

        System.out.println("\nLEVEL UP! Now level " + player.getLevel());
        System.out.println("Max HP, attack and defense increased!");
    }

    private void lootEnemy(Enemy enemy) {
        List<Item> loot = enemy.getInventory().listSorted();
        if (loot.isEmpty()) { System.out.println("The enemy had no items."); return; }
        System.out.println("\nItems found:");
        for (Item item : loot) {
            System.out.println("  - " + item.getName() + " (x" + item.getQuantity() + ")");
            try { player.getInventory().add(item); }
            catch (IllegalStateException e) {
                System.out.println("Inventory full! Could not pick up: " + item.getName());
            }
        }
    }

    // ── Save / load ───────────────────────────────────────────────────────────

    private boolean loadGame() {
        SaveSystem.SavedState state = saveSystem.load(reader);
        if (state == null) return false;

        this.player           = state.player;
        this.currentXp        = state.currentXp;
        this.nextLevelXp      = state.nextLevelXp;
        this.currentChapter   = state.currentChapter;
        this.explorationCount = state.explorationCount;
        this.bossDefeated     = state.bossDefeated;

        System.out.println("\nSave loaded successfully!");
        System.out.println(player.getStatus());
        System.out.printf("Chapter: %d | Explorations: %d%n", currentChapter, explorationCount);
        return true;
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    private void printIntroIfNewGame() {
        if (currentChapter <= 1 && explorationCount == 0 && !bossDefeated) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("CHAPTER 1: THE AWAKENING");
            System.out.println("=".repeat(50));
            System.out.println("You wake up in a dark forest...");
            System.out.println("You have no memory of how you got here.");
            System.out.println("In the distance, the ruins of an ancient castle loom.");
            System.out.println("They say a powerful being dwells within...");
            System.out.println("Your journey begins now!");
            System.out.println("=".repeat(50) + "\n");
            waitForEnter();
        }
    }

    private void printStatus() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println(player.getStatus());
        System.out.printf("XP: %d/%d | Chapter: %d | Explorations: %d%n",
                currentXp, nextLevelXp, currentChapter, explorationCount);
        System.out.println("=".repeat(50));
    }

    private void quit() {
        System.out.println("\nAre you sure you want to quit? (y/n)");
        if (readLine().equalsIgnoreCase("y")) {
            gameActive = false;
            System.out.println("\nThanks for playing!");
        }
    }

    private void gameOver() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("GAME OVER");
        System.out.println("=".repeat(50));
        System.out.printf("You were defeated...%nLevel: %d | XP: %d | Chapter: %d%n",
                player.getLevel(), currentXp, currentChapter);
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

    private String readLine() {
        try { String l = reader.readLine(); return l == null ? "" : l.trim(); }
        catch (IOException e) { return ""; }
    }

    private void waitForEnter() {
        System.out.println("\n[Press ENTER to continue]");
        try { reader.readLine(); } catch (IOException ignored) {}
    }
}

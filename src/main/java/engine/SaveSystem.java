package engine;

import characters.Archer;
import characters.GameCharacter;
import characters.Mage;
import characters.Warrior;
import items.Effect;
import items.Item;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all save/load file I/O.
 *
 * <p>Save format: key=value pairs per line with an ITEMS_START / ITEMS_END
 * block for the inventory.
 */
public class SaveSystem {

    private static final String SAVES_DIR = "src/saves";

    /**
     * Serialises the current game state to a named file chosen by the player.
     */
    public void save(GameCharacter player, int currentXp, int nextLevelXp,
                     int currentChapter, int explorationCount, boolean bossDefeated,
                     BufferedReader reader) {
        if (player == null) { System.out.println("No active game to save."); return; }

        System.out.print("Enter save name: ");
        String saveName = readLine(reader);
        if (saveName == null || saveName.isBlank()) { System.out.println("Invalid save name!"); return; }
        saveName = saveName.trim();

        try {
            File dir  = new File(SAVES_DIR);
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, saveName + ".txt");

            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                pw.println("CLASS="            + player.getClass().getSimpleName());
                pw.println("NAME="             + player.getName());
                pw.println("LEVEL="            + player.getLevel());
                pw.println("HP_CURRENT="       + player.getHitPoints());
                pw.println("HP_MAX="           + player.getMaxHitPoints());
                pw.println("ATTACK="           + player.getAttack());
                pw.println("DEFENSE="          + player.getDefense());
                pw.println("XP_CURRENT="       + currentXp);
                pw.println("XP_NEXT="          + nextLevelXp);
                pw.println("CHAPTER="          + currentChapter);
                pw.println("EXPLORATIONS="     + explorationCount);
                pw.println("BOSS_DEFEATED="    + bossDefeated);
                pw.println("ITEMS_START");
                for (Item item : player.getInventory().listSorted()) {
                    pw.println(item.getName()         + ";" +
                               item.getDescription()  + ";" +
                               item.getEffect().name()+ ";" +
                               item.getQuantity()     + ";" +
                               item.getEffectValue());
                }
                pw.println("ITEMS_END");
            }
            System.out.println("Game saved to: " + file.getPath());
        } catch (Exception e) {
            System.out.println("Error saving game: " + e.getMessage());
        }
    }

    /**
     * Displays available saves and restores the one selected by the player.
     *
     * @return a {@link SavedState} with the loaded data, or {@code null} if cancelled/failed
     */
    public SavedState load(BufferedReader reader) {
        File dir = new File(SAVES_DIR);
        if (!dir.exists() || !dir.isDirectory()) { System.out.println("No saves found."); return null; }

        File[] files = dir.listFiles((d, n) -> n.toLowerCase().endsWith(".txt"));
        if (files == null || files.length == 0) { System.out.println("No save files found."); return null; }

        List<File> list = new ArrayList<>(List.of(files));
        list.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));

        System.out.println("\n=== Available Saves ===");
        for (int i = 0; i < list.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, list.get(i).getName().replaceAll("(?i)\\.txt$", ""));
        }
        System.out.println("0. Cancel");
        System.out.print("Choose save: ");

        int choice = readOption(0, list.size(), reader);
        if (choice == 0) { System.out.println("Load cancelled."); return null; }

        return parseFile(list.get(choice - 1));
    }

    private SavedState parseFile(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String  clazz = null, name = null;
            int level = 1, hpCurrent = 0, hpMax = 0, attack = 0, defense = 0;
            int xpCurrent = 0, xpNext = GameConfig.INITIAL_NEXT_LEVEL_XP;
            int chapter = 1, explorations = 0;
            boolean bossDefeated = false;
            List<Item> items = new ArrayList<>();
            boolean readingItems = false;

            String line;
            while ((line = br.readLine()) != null) {
                if ("ITEMS_START".equals(line)) { readingItems = true;  continue; }
                if ("ITEMS_END".equals(line))   { readingItems = false; continue; }

                if (readingItems) {
                    String[] p = line.split(";");
                    if (p.length == 5) {
                        items.add(new Item(p[0], p[1], Effect.valueOf(p[2]),
                                Integer.parseInt(p[3]), Integer.parseInt(p[4])));
                    }
                } else {
                    String[] p = line.split("=", 2);
                    if (p.length != 2) continue;
                    switch (p[0]) {
                        case "CLASS"         -> clazz       = p[1];
                        case "NAME"          -> name        = p[1];
                        case "LEVEL"         -> level       = Integer.parseInt(p[1]);
                        case "HP_CURRENT"    -> hpCurrent   = Integer.parseInt(p[1]);
                        case "HP_MAX"        -> hpMax       = Integer.parseInt(p[1]);
                        case "ATTACK"        -> attack      = Integer.parseInt(p[1]);
                        case "DEFENSE"       -> defense     = Integer.parseInt(p[1]);
                        case "XP_CURRENT"    -> xpCurrent   = Integer.parseInt(p[1]);
                        case "XP_NEXT"       -> xpNext      = Integer.parseInt(p[1]);
                        case "CHAPTER"       -> chapter     = Integer.parseInt(p[1]);
                        case "EXPLORATIONS"  -> explorations= Integer.parseInt(p[1]);
                        case "BOSS_DEFEATED" -> bossDefeated= Boolean.parseBoolean(p[1]);
                    }
                }
            }

            GameCharacter player = buildCharacter(clazz, name, hpMax, attack, defense, level);
            if (player == null) { System.out.println("Invalid class in save file!"); return null; }

            player.setMaxHitPoints(hpMax);
            player.setHitPoints(hpCurrent);
            player.setAttack(attack);
            player.setDefense(defense);
            player.setLevel(level);
            player.getInventory().clear();
            items.forEach(player.getInventory()::add);

            return new SavedState(player, xpCurrent, xpNext, chapter, explorations, bossDefeated);

        } catch (Exception e) {
            System.out.println("Error loading game: " + e.getMessage());
            return null;
        }
    }

    private GameCharacter buildCharacter(String clazz, String name,
                                         int hp, int attack, int defense, int level) {
        return switch (clazz) {
            case "Warrior" -> new Warrior(name, hp, attack, defense, level);
            case "Mage"    -> new Mage(name, hp, attack, defense, level);
            case "Archer"  -> new Archer(name, hp, attack, defense, level);
            default        -> null;
        };
    }

    private int readOption(int min, int max, BufferedReader reader) {
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

    private String readLine(BufferedReader reader) {
        try { String l = reader.readLine(); return l == null ? "" : l.trim(); }
        catch (IOException e) { return ""; }
    }

    /** Value object carrying the full state restored from a save file. */
    public static class SavedState {
        public final GameCharacter player;
        public final int  currentXp;
        public final int  nextLevelXp;
        public final int  currentChapter;
        public final int  explorationCount;
        public final boolean bossDefeated;

        public SavedState(GameCharacter player, int currentXp, int nextLevelXp,
                          int currentChapter, int explorationCount, boolean bossDefeated) {
            this.player           = player;
            this.currentXp        = currentXp;
            this.nextLevelXp      = nextLevelXp;
            this.currentChapter   = currentChapter;
            this.explorationCount = explorationCount;
            this.bossDefeated     = bossDefeated;
        }
    }
}

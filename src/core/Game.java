package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import utils.FileUtils;

import java.awt.*;

public class Game {

    private World world;
    private long DEFAULT_SEED = 2990;

    private TERenderer ter;

    private String SAVE_FILE = "save.txt";

    private String commands;
    private char firstKey = '1';
    char character;
    public Game() {
        world = new World();
        world.setGame(this);
        commands = "";
    }


    public void runGame() {
        ter = new TERenderer();
        ter.initialize(world.getWidth(), world.getHeight());
        StdDraw.enableDoubleBuffering();
        ter.drawTiles(world.getWorld());
        renderMenu();
        StdDraw.show();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                updateBoard();
            }

        }
    }

    public void updateBoard() {
        while (StdDraw.hasNextKeyTyped()) {
            char key = StdDraw.nextKeyTyped();
            if (key == 'n' || key == 'N') {
                commands += key;
                seedScreen();
            } else if (key == 'l' || key == 'L') {
                commands += key;
                String existCommand = FileUtils.readFile(SAVE_FILE);
                existCommand += commands;
                FileUtils.writeFile(SAVE_FILE, existCommand);
                loadGame(false);
            } else if (key == 'q' || key == 'Q') {
                quitGame();
            } else if (key == 'c' || key == 'C') {
                commands += key;
                characterScreen();
                StdDraw.show();
            } else if (key == 'x' || key == 'X') {
                commands += key;
                character = 'x';
                renderMenu();
                StdDraw.show();
            } else if (key == 'r' || key == 'R') {
                commands += key;
                character = 'r';
                renderMenu();
                StdDraw.show();
            }
        }
    }
    public void seedScreen() {
        newWorldScreen();
        String input = inputSeed();
        long seed = Long.parseLong(input);
        commands += input;
        commands += 's';
        FileUtils.writeFile(SAVE_FILE, commands);
        runNewWorld(seed);
    }

    public void newWorldScreen() {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        Font font = new Font("Arial", Font.PLAIN, 40);
        StdDraw.setFont(font);
        StdDraw.text(35, 25, "Input Seed: ");
        StdDraw.text(45, 20, "(Press 'S' to continue)");
        StdDraw.show();
    }

    public void characterScreen() {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.picture(30, 25, "./src/images/x.png");
        StdDraw.picture(60, 25, "./src/images/r.png");
        StdDraw.setPenColor(StdDraw.WHITE);
        Font font = new Font("Arial", Font.PLAIN, 50);
        StdDraw.setFont(font);
        StdDraw.text(45, 40, "Choose your character");
        StdDraw.text(30, 10, "(X)");
        StdDraw.text(60, 10, "(R)");
    }

    public String inputSeed() {
        String seed = "";
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (key == 's' || key == 'S') {
                    break;
                } else if (key == '\b' && seed.length() > 0) {
                    seed = seed.substring(0, seed.length() - 1);
                } else if (key == '\n' || !Character.isDigit(key)) {
                    continue;
                } else {
                    seed += key;
                }
                displaySeed(seed);
            }
        }
        return seed;
    }

    public void displaySeed(String seed) {
        newWorldScreen();
        StdDraw.setPenColor(StdDraw.WHITE);
        Font font = new Font("Arial", Font.PLAIN, 40);
        StdDraw.setFont(font);
        StdDraw.text(50, 25, seed);
        StdDraw.show();
    }

    public void runNewWorld(long seed) {
        World newWorld = new World(seed, character);
        world = newWorld;
        world.setGame(this);
        world.runGame();
    }

    public void renderMenu() {
        StdDraw.clear(StdDraw.BLACK);
        String name = "CS61B Game";
        StdDraw.setPenColor(StdDraw.WHITE);
        Font font = new Font("Arial", Font.BOLD, 75);
        StdDraw.setFont(font);
        StdDraw.text(45, 40, name);

        Font font2 = new Font("Arial", Font.PLAIN, 40);
        StdDraw.setFont(font2);
        StdDraw.text(45, 30, "Choose Character (C)");
        StdDraw.text(45, 25, "New Game (N)");
        StdDraw.text(45, 20, "Load Game (L)");
        StdDraw.text(45, 15, "Quit (Q)");
    }

    public void loadGame(boolean fromInput) {
        String input = FileUtils.readFile(SAVE_FILE);
        runGameFromInput(input, fromInput);
    }

    public void quitGame() {
        System.exit(0);
    }

    public void runGameFromInput(String input, boolean fromInput) {
        String seed = "";

        for (int i = 0; i < input.length(); i++) {
            char key = input.charAt(i);
            if (key == 'n' || key == 'N') {
                continue;
            } else if (key == 's' || key == 'S') {
                if (fromInput) {
                    FileUtils.writeFile(SAVE_FILE, input.substring(0, i + 1));
                }
                long worldSeed = Long.parseLong(seed);
                String newInput = input.substring(i + 1);
                runNewWorldFromInput(worldSeed, newInput, fromInput);
                break;
            } else if (key == 'l' || key == 'L') {
                loadGame(fromInput);
                world.runWorldFromInput(input.substring(i), fromInput);
                break;
            } else if (Character.isDigit(key)) {
                seed += key;
            } else if (key == 'q' || key == 'Q') {
                System.exit(0);
            } else if (key == 'c' || key == 'C') {
                characterScreen();
            } else if (key == 'x' || key == 'X') {
                character = 'x';
                renderMenu();
            } else if (key == 'r' || key == 'R') {
                character = 'r';
                renderMenu();
            }
        }
    }

    public void runNewWorldFromInput(long seed, String input, boolean fromInput) {
        World newWorld = new World(seed, character);
        world = newWorld;
        world.setGame(this);
        world.runWorldFromInput(input, fromInput);
    }

    public TETile[][] getWorld() {
        return world.getWorld();
    }

}

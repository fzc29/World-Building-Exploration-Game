package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import utils.FileUtils;

import java.awt.*;
import java.util.Random;

public class Puzzle {

    int level; // 1 = easy, 2 = medium, 3 = hard
    boolean completed;
    int collectedStars = 0;
    TETile[][] puzzleTiles;
    TETile[][] hiddenPuzzleTiles;
    long prevActionTimestamp;
    Player playerS;
    Movement move;
    Random rand;

    String commands;
    int totalPoint;
    int totalStars;
    private String SAVE_FILE = "save.txt";

    private TETile charFront;
    private TETile charBack;
    private TETile charLeft;
    private TETile charRight;
    private boolean hidden;
    private int time;

    public Puzzle(int l, int seed, char character, boolean hidden) {
        completed = false;
        this.hidden = hidden;
        level = l;
        if (level == 1) {
            totalPoint = 100;
        } else if (level == 2) {
            totalPoint = 200;
        } else {
            totalPoint = 300;
        }

        rand = new Random(seed);

        if (this.hidden) {
            totalPoint *= 2;
        }

        if (level == 1) {
            totalStars = 7;
        } else if (level == 2) {
            totalStars = 12;
        } else {
            totalStars = 17;
        }

        if (this.hidden) {
            time = 60000;
        } else {
            time = 30000;
        }

        commands = "";
        puzzleTiles = new TETile[90][50];
        hiddenPuzzleTiles = new TETile[90][50];
        createPuzzle();
        placePoints(totalStars, puzzleTiles);
        setCharacter(character);
        playerS = new Player(21, 11, puzzleTiles, charBack);
        playerS.placePlayer();
        move = new Movement(90, 50, playerS, puzzleTiles);
    }


    public void createPuzzle() {
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 90; j++) {
                if ((j == 20 || j == 70) & (i >= 10 && i <= 40)) {
                    puzzleTiles[j][i] = Tileset.WALL2;
                } else if ((j >= 20 && j <= 70) & (i == 10 || i == 40)) {
                    puzzleTiles[j][i] = Tileset.WALL2;
                } else if ((j > 20 && j < 70) && (i > 10 && i < 40)) {
                    puzzleTiles[j][i] = Tileset.FLOOR2;
                } else {
                    puzzleTiles[j][i] = Tileset.NOTHING;
                }
            }
        }
    }

    public void saveGame() {
        String existCommand = FileUtils.readFile(SAVE_FILE);
        existCommand += commands;
        FileUtils.writeFile(SAVE_FILE, existCommand);
    }

    public void placePoints(int n, TETile[][] worldTiles) {
        while (n > 0) {
            int xPoint = rand.nextInt(22, 69);
            int yPoint = rand.nextInt(12, 39);
            if (worldTiles[xPoint][yPoint] != Tileset.STAR) {
                worldTiles[xPoint][yPoint] = Tileset.STAR;
                n--;
            }
        }
    }


    public void runPuzzle() {
        TERenderer ter = new TERenderer();
        ter.initialize(90, 50);
        loadScreen("You have " + (time / 1000) +  " seconds to collect all the stars!", 1000);
        loadScreen("3", 500);
        loadScreen("2", 500);
        loadScreen("1", 500);
        loadScreen("START!", 400);
        prevActionTimestamp = System.currentTimeMillis();
        while (actionDeltaTime() < time) {
            if (hidden) {
                hiddenView();
                ter.drawTiles(hiddenPuzzleTiles);
            } else {
                ter.drawTiles(puzzleTiles);
            }
            updateBoard();
            complete();
            StdDraw.show();
            if (completed) {
                loadScreen("PUZZLES COMPLETED!", 900);
                loadScreen("Points Earned: " + totalPoint, 700);
                break;
            }
        }
        commands += 'p';
        saveGame();
        if (!completed) {
            loadScreen("GAME OVER :(", 900);

        }
    }

    public void complete() {
        if (collectedStars == totalStars) {
            completed = true;
        }
    }

    public void loadScreen(String result, int t) {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        Font font = new Font("Arial", Font.PLAIN, 40);
        StdDraw.setFont(font);
        StdDraw.text(45, 25, result);
        StdDraw.show();
        StdDraw.pause(t);
    }


    private long actionDeltaTime() {
        return System.currentTimeMillis() - prevActionTimestamp;
    }

    public void updateBoard() {
        if (StdDraw.hasNextKeyTyped()) {
            char key = StdDraw.nextKeyTyped();

            if (key == 'w' || key == 'W') {
                commands += key;
                playerS.changeOrientation(charBack);
                checkPoint(playerS.getPosX(), playerS.getPosY() + 1);
                move.tryMove(playerS.getPosX(), playerS.getPosY() + 1);
            } else if (key == 's' || key == 'S') {
                commands += key;
                playerS.changeOrientation(charFront);
                checkPoint(playerS.getPosX(), playerS.getPosY() - 1);
                move.tryMove(playerS.getPosX(), playerS.getPosY() - 1);
            } else if (key == 'a' || key == 'A') {
                commands += key;
                playerS.changeOrientation(charLeft);
                checkPoint(playerS.getPosX() - 1, playerS.getPosY());
                move.tryMove(playerS.getPosX() - 1, playerS.getPosY());
            } else if (key == 'd' || key == 'D') {
                commands += key;
                playerS.changeOrientation(charRight);
                checkPoint(playerS.getPosX() + 1, playerS.getPosY());
                move.tryMove(playerS.getPosX() + 1, playerS.getPosY());
            }
        }
    }

    public void checkPoint(int x, int y) {
        if (puzzleTiles[x][y] == Tileset.STAR) {
            collectedStars++;
        }
    }

    public void setCharacter(char character) {
        if (character == 'x') {
            charFront = Tileset.XCHARACTERFRONT;
            charBack = Tileset.XCHARACTER;
            charLeft = Tileset.XCHARACTERLEFT;
            charRight = Tileset.XCHARACTERRIGHT;
        } else {
            charFront = Tileset.CHARACTERFRONT;
            charBack = Tileset.CHARACTER;
            charLeft = Tileset.CHARACTERLEFT;
            charRight = Tileset.CHARACTERRIGHT;
        }
    }

    public void runPuzzleFromInput(String input) {

        for (int i = 0; i < input.length(); i++) {
            char key = input.charAt(i);

            if (key == 'w' || key == 'W') {
                playerS.changeOrientation(charBack);
                checkPoint(playerS.getPosX(), playerS.getPosY() + 1);
                move.tryMove(playerS.getPosX(), playerS.getPosY() + 1);
                complete();
            } else if (key == 's' || key == 'S') {
                playerS.changeOrientation(charFront);
                checkPoint(playerS.getPosX(), playerS.getPosY() - 1);
                move.tryMove(playerS.getPosX(), playerS.getPosY() - 1);
                complete();
            } else if (key == 'a' || key == 'A') {
                playerS.changeOrientation(charLeft);
                checkPoint(playerS.getPosX() - 1, playerS.getPosY());
                move.tryMove(playerS.getPosX() - 1, playerS.getPosY());
                complete();
            } else if (key == 'd' || key == 'D') {
                playerS.changeOrientation(charRight);
                checkPoint(playerS.getPosX() + 1, playerS.getPosY());
                move.tryMove(playerS.getPosX() + 1, playerS.getPosY());
                complete();
            } else if (key == 'p') {
                break;
            }
        }

    }

    public void hiddenView() {
        int dist = 5;
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 90; j++) {
                if (Math.hypot(playerS.getPosX() - j, playerS.getPosY() - i) < dist) {
                    hiddenPuzzleTiles[j][i] = puzzleTiles[j][i];
                } else {
                    hiddenPuzzleTiles[j][i] = Tileset.NOTHING;
                }
            }
        }
    }


}

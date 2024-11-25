package core;


import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import utils.FileUtils;

import java.awt.*;
import java.util.HashMap;
import java.util.Random;

public class World {
    private int height;
    private int width;
    private TERenderer ter;
    private Random random;
    private TETile[][] currentWorld;
    private TETile[][] hiddenWorld;
    private boolean isHidden;

    private int startx;
    private int starty;
    private boolean turn;

    private int numOfRooms;

    private Graph worldGraph;

    private int points;

    private Player player;

    private Movement movement;

    private long seed;

    private TETile[][] base;
    private String commands;

    private String SAVE_FILE = "save.txt";

    private Game game;

    private boolean isGameOver;
    private String firstKey = "";
    private boolean inPuzzle = false;
    private TETile currentPuzzle;
    private int currPuzzleX;
    private int currPuzzleY;

    private Puzzle puzzleInstance;
    private TETile charFront;
    private TETile charBack;
    private TETile charLeft;
    private TETile charRight;
    private HashMap<Point, PuzzleInfo> puzzleMap;

    private char character;

    // build your own world!

    // generating base > all water
    public World(long seed, char character) {
        height = 50;
        width = 90;
        this.seed = seed;
        random = new Random(seed);
        currentWorld = new TETile[width][height];
        hiddenWorld = new TETile[width][height];
        isHidden = false;
        puzzleMap = new HashMap<>();
        startx = random.nextInt(width / 3, 2 * width / 3);
        starty = random.nextInt(height / 3 - 2, 2 * height / 3 - 2);
        worldGraph = new Graph(random, currentWorld);
        points = 100;
        isGameOver = false;
        commands = "";
        this.character = character;
        genBackground(Tileset.WATER2);
        genRooms();
        connectRooms();
        setCharacter(character);
        placePlayer();
        placePuzzle(10);
        placeHiddenPuzzle();
        movement = new Movement(width, height, player, currentWorld);
    }

    public World() {
        height = 50;
        width = 90;
        currentWorld = new TETile[width][height];
        genBackground(Tileset.NOTHING);
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public TETile[][] getWorld() {
        return currentWorld;
    }

    public void genBackground(TETile tileType) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                currentWorld[j][i] = tileType;
            }
        }
    }

    public void genRooms() {
        // stop at 50% capacity
        int boardArea = width * height;
        int totalArea = 0;
        while (((double) totalArea / boardArea) <= 0.42) {
            int numOfTries = 2 * 2 * 5 * 5;
            while (numOfTries > 0) {
                Room startRoom = new Room(random, startx, starty, currentWorld);
                if (startRoom.canPlace()) {
                    startRoom.generateRoom();
                    totalArea += startRoom.getArea();
                    worldGraph.addRoom(startRoom);
                    numOfTries = 0;
                    numOfRooms++;
                }
                startx = random.nextInt(2, width - 6);
                starty = random.nextInt(2, height - 6);
                numOfTries--;
            }
        }

    }

    public void connectRooms() {
        worldGraph.connectRooms();
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void addPoint(int p) {
        points += p;
    }

    public void placePlayer() {
        Room startRoom = worldGraph.getRandomStartRoom();
        while (currentWorld[startRoom.getCoord("x")][startRoom.getCoord("y")] == Tileset.LOCKEDFLOOR) {
            startRoom = worldGraph.getRandomStartRoom();
        }
        int x = findXPoint(startRoom);
        int y = findYPoint(startRoom);
        player = new Player(x, y, currentWorld, charBack);
        player.placePlayer();
    }

    public void placePuzzle(int numPuzzles) {
        while (numPuzzles > 0) {
            Room startRoom = worldGraph.getRandomStartRoom();
            int x = findXPoint(startRoom);
            int y = findYPoint(startRoom);
            if (currentWorld[x][y] == Tileset.FLOOR2) {
                currentWorld[x][y] = Tileset.PUZZLE;
                int level = Math.floorMod(random.nextInt(), 3) + 1;
                int sd = random.nextInt();
                puzzleMap.put(new Point(x, y), new PuzzleInfo(level, sd, false));
                numPuzzles--;
            }
        }
    }

    public void placeHiddenPuzzle() {
        Room startRoom = worldGraph.getRandomStartRoom();
        int x = findXPoint(startRoom);
        int y = findYPoint(startRoom);
        while (currentWorld[x][y] != Tileset.FLOOR2) {
            startRoom = worldGraph.getRandomStartRoom();
            x = findXPoint(startRoom);
            y = findYPoint(startRoom);
        }
        currentWorld[x][y] = Tileset.PUZZLEHIDDEN;
        int level = Math.floorMod(random.nextInt(), 3) + 1;
        int sd = random.nextInt();
        puzzleMap.put(new Point(x, y), new PuzzleInfo(level, sd, true));
    }

    public int getPoints() {
        return points;
    }

    public void runGame() {
        ter = new TERenderer();
        ter.initialize(width, height, 0, 2);
        if (!isGameOver) {
            StdDraw.enableDoubleBuffering();
            loadScreen();
            ter.drawTiles(currentWorld);
            renderHUD();
        }
        while (!isGameOver) {
            while (StdDraw.hasNextKeyTyped()) {
                updateBoard();
                if (inPuzzle) {
                    int l = puzzleMap.get(new Point(currPuzzleX, currPuzzleY)).level;
                    int s = puzzleMap.get(new Point(currPuzzleX, currPuzzleY)).seed;
                    boolean h = puzzleMap.get(new Point(currPuzzleX, currPuzzleY)).hidden;
                    puzzleInstance = new Puzzle(l, s, character, h);
                    puzzleInstance.runPuzzle();
                    if (puzzleInstance.completed) {
                        addPoint(puzzleInstance.totalPoint);
                        currentWorld[currPuzzleX][currPuzzleY] = Tileset.PUZZLECOMPLETED;
                    }
                    commands = "";
                    inPuzzle = false;
                }
                if (isHidden) {
                    hiddenView();
                    ter.drawTiles(hiddenWorld);
                } else {
                    ter.drawTiles(currentWorld);
                }
            }
            renderHUD();
            StdDraw.show();
        }
    }


    public void loadScreen() {
        StdDraw.clear(StdDraw.BLACK);
        String message = "Generating world...";
        StdDraw.setPenColor(StdDraw.WHITE);
        Font font = new Font("Arial", Font.PLAIN, 40);
        StdDraw.setFont(font);
        StdDraw.text(45, 25, message);
        StdDraw.show();
        StdDraw.pause(900);
    }

    public void updateBoard() {
        if (StdDraw.hasNextKeyTyped()) {
            char key = StdDraw.nextKeyTyped();

            if (!inPuzzle) {
                if (key == 'w' || key == 'W') {
                    player.changeOrientation(charBack);
                    movement.tryMove(player.getPosX(), player.getPosY() + 1);
                    commands += key;
                } else if (key == 's' || key == 'S') {
                    player.changeOrientation(charFront);
                    movement.tryMove(player.getPosX(), player.getPosY() - 1);
                    commands += key;
                } else if (key == 'a' || key == 'A') {
                    player.changeOrientation(charLeft);
                    movement.tryMove(player.getPosX() - 1, player.getPosY());
                    commands += key;
                } else if (key == 'd' || key == 'D') {
                    player.changeOrientation(charRight);
                    movement.tryMove(player.getPosX() + 1, player.getPosY());
                    commands += key;
                } else if (key == ':') {
                    commands += key;
                    firstKey = "";
                    firstKey += key;
                } else if (key == 'q' || key == 'Q') {
                    firstKey += key;
                    if (firstKey.equals(":q") || firstKey.equals(":Q")) {
                        commands += key;
                        firstKey = "";
                        quitGame();
                    }
                } else if (key == 'g' || key == 'G') {
                    commands += key;
                    saveGame();
                    seed += 7;
                    game.runNewWorld(seed);
                } else if (key == 'h' || key == 'H') {
                    commands += key;
                    isHidden = !isHidden;
                } else if (key == 'o' || key == 'O') {
                    commands += key;
                    if (nearPuzzle(player.getPosX(), player.getPosY()) != null) {
                        System.out.println("start of puzzle");
                        saveGame();
                        currentPuzzle = nearPuzzle(player.getPosX(), player.getPosY());
                        inPuzzle = true;
                    }

                }
            }
        }
    }

    public TETile nearPuzzle(int playerx, int playery) {
        TETile p = Tileset.PUZZLE;
        TETile ph = Tileset.PUZZLEHIDDEN;
        if (currentWorld[playerx + 1][playery] == p || currentWorld[playerx + 1][playery] == ph) {
            currPuzzleX = playerx + 1;
            currPuzzleY = playery;
            return currentWorld[playerx + 1][playery];
        }
        if (currentWorld[playerx - 1][playery] == p || currentWorld[playerx - 1][playery] == ph) {
            currPuzzleX = playerx - 1;
            currPuzzleY = playery;
            return currentWorld[playerx - 1][playery];
        }
        if (currentWorld[playerx + 1][playery + 1] == p || currentWorld[playerx + 1][playery + 1] == ph) {
            currPuzzleX = playerx + 1;
            currPuzzleY = playery + 1;
            return currentWorld[playerx + 1][playery + 1];
        }
        if (currentWorld[playerx + 1][playery - 1] == p || currentWorld[playerx + 1][playery - 1] == ph) {
            currPuzzleX = playerx + 1;
            currPuzzleY = playery - 1;
            return currentWorld[playerx + 1][playery - 1];
        }
        if (currentWorld[playerx - 1][playery + 1] == p || currentWorld[playerx - 1][playery + 1] == ph) {
            currPuzzleX = playerx - 1;
            currPuzzleY = playery + 1;
            return currentWorld[playerx - 1][playery + 1];
        }
        if (currentWorld[playerx - 1][playery - 1] == p || currentWorld[playerx - 1][playery - 1] == ph) {
            currPuzzleX = playerx - 1;
            currPuzzleY = playery - 1;
            return currentWorld[playerx - 1][playery - 1];
        }
        if (currentWorld[playerx][playery - 1] == p || currentWorld[playerx][playery - 1] == ph) {
            currPuzzleX = playerx;
            currPuzzleY = playery - 1;
            return currentWorld[playerx][playery - 1];
        }
        if (currentWorld[playerx][playery + 1] == p || currentWorld[playerx][playery + 1] == ph) {
            currPuzzleX = playerx;
            currPuzzleY = playery + 1;
            return currentWorld[playerx][playery + 1];
        }
        return null;
    }

    public void renderHUD() {
        StdDraw.setFont();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledRectangle(3, 1, width, 1);
        String hud = "Tile:  " + getTile();
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(5, 1, hud);
        renderPoint();
        renderCommands();
        StdDraw.show();
    }

    public void renderPoint() {
        StdDraw.setFont();
        String pts = "Points: ";
        StdDraw.setPenColor(255, 255, 255);
        StdDraw.text(15, 1, pts);
        renderPointNumber();
    }

    public void renderCommands() {
        StdDraw.setFont();
        StdDraw.setPenColor(255, 255, 255);
        String cmds = "Hidden View (H)     Open Puzzle (O)     Generate New World (G)     Move (AWSD)     Quit (:Q)";
        StdDraw.text(50, 1, cmds);
    }

    public void renderPointNumber() {
        StdDraw.setFont();
        String pts = String.valueOf(getPoints());
        StdDraw.setPenColor(255, 255, 255);
        StdDraw.text(18, 1, pts);
    }

    public int findXPoint(Room startRoom) {
        int startX = startRoom.getCoord("x");
        int wd = startRoom.getDim("w");
        return random.nextInt(startX, startX + wd);
    }

    public int findYPoint(Room startRoom) {
        int startY = startRoom.getCoord("y");
        int ht = startRoom.getDim("h");
        return random.nextInt(startY, startY + ht);
    }
    public String getTile() {
        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY();

        mouseY -= 2;

        if (mouseX >= 0 && mouseX < getWidth() && mouseY < getHeight() && mouseY >= 0) {
            if (currentWorld[mouseX][mouseY] == Tileset.PUZZLE) {
                return "* puzzle";
            } else if (currentWorld[mouseX][mouseY] == Tileset.PUZZLEHIDDEN) {
                return "** puzzle";
            } else if (currentWorld[mouseX][mouseY] == Tileset.PUZZLECOMPLETED) {
                return "completed";
            } else if (currentWorld[mouseX][mouseY] == Tileset.FLOOR2) {
                return "floor";
            } else if (currentWorld[mouseX][mouseY] == Tileset.WALL2) {
                return "wall";
            } else if (currentWorld[mouseX][mouseY] == Tileset.WATER2) {
                return "water";
            } else if (currentWorld[mouseX][mouseY] == charBack) {
                return "you";
            } else if (currentWorld[mouseX][mouseY] == charLeft) {
                return "you";
            } else if (currentWorld[mouseX][mouseY] == charRight) {
                return "you";
            } else if (currentWorld[mouseX][mouseY] == charFront) {
                return "you";
            }
        }
        return "";
    }

    public void runWorldFromInput(String input, boolean fromInput) {

        for (int i = 0; i < input.length(); i++) {
            char key = input.charAt(i);

            if (!inPuzzle) {
                if (key == 'w' || key == 'W') {
                    player.changeOrientation(charBack);
                    movement.tryMove(player.getPosX(), player.getPosY() + 1);
                } else if (key == 's' || key == 'S') {
                    player.changeOrientation(charFront);
                    movement.tryMove(player.getPosX(), player.getPosY() - 1);
                } else if (key == 'a' || key == 'A') {
                    player.changeOrientation(charLeft);
                    movement.tryMove(player.getPosX() - 1, player.getPosY());
                } else if (key == 'd' || key == 'D') {
                    player.changeOrientation(charRight);
                    movement.tryMove(player.getPosX() + 1, player.getPosY());
                } else if (key == ':') {
                    firstKey = "";
                    firstKey += key;
                } else if (key == 'q' || key == 'Q') {
                    firstKey += key;
                    if (firstKey.equals(":q") || firstKey.equals(":Q")) {
                        isGameOver = true;
                    }
                } else if (key == 'g' || key == 'G') {
                    seed += 7;
                    game.runNewWorldFromInput(seed, input.substring(i + 1), fromInput);
                    break;
                } else if (key == 'l' || key == 'L') {
                    isGameOver = false;
                } else if (key == 'h') {
                    isHidden = !isHidden;
                } else if (key == 'o' || key == 'O') {
                    if (nearPuzzle(player.getPosX(), player.getPosY()) != null) {
                        currentPuzzle = nearPuzzle(player.getPosX(), player.getPosY());
                        inPuzzle = true;
                        int l = puzzleMap.get(new Point(currPuzzleX, currPuzzleY)).level;
                        int s = puzzleMap.get(new Point(currPuzzleX, currPuzzleY)).seed;
                        boolean h = puzzleMap.get(new Point(currPuzzleX, currPuzzleY)).hidden;
                        puzzleInstance = new Puzzle(l, s, character, h);
                        puzzleInstance.runPuzzleFromInput(input.substring(i + 1));
                        if (puzzleInstance.completed) {
                            addPoint(puzzleInstance.totalPoint);
                            currentWorld[currPuzzleX][currPuzzleY] = Tileset.PUZZLECOMPLETED;
                        }
                    }

                }
            }
            if (key == 'p') {
                inPuzzle = false;
            }
        }
        if (fromInput) {
            commands = input;
            saveGame();
        } else {
            runGame();
        }
    }

    public void saveGame() {
        String existCommand = FileUtils.readFile(SAVE_FILE);
        existCommand += commands;
        FileUtils.writeFile(SAVE_FILE, existCommand);
    }
    public void quitGame() {
        isGameOver = true;
        saveGame();
        System.exit(0);
    }

    public void hiddenView() {
        int dist = 5;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (Math.hypot(player.getPosX() - j, player.getPosY() - i) < dist) {
                    hiddenWorld[j][i] = currentWorld[j][i];
                } else {
                    hiddenWorld[j][i] = Tileset.NOTHING;
                }
            }
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
    private class PuzzleInfo {
        int level;
        int seed;
        boolean hidden;
        public PuzzleInfo(int level, int seed, boolean hidden) {
            this.level = level;
            this.seed = seed;
            this.hidden = hidden;

        }
    }

    public class Location {
        int x;
        int y;
        public Location(int x, int y) {
            this.x = x;
            this.y = y;
        }

    }
}

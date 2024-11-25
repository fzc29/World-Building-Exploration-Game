package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.util.Random;

public class Room {
    private int height;
    private int width;
    private int startY;
    private int startX;
    private Random random;
    private TETile[][] currentWorld;

    private boolean isConnected;
    private int area;

    private boolean isLocked = false;

    public Room(Random r, int startx, int starty, TETile[][] currWorld) {
        random = r;
        startY = starty;
        startX = startx;
        currentWorld = currWorld;
        width = Math.floorMod(random.nextInt(), 9) + 4;
        height = Math.floorMod(random.nextInt(), 9) + 4;
        isConnected = false;
    }

    public void generateRoom() {
        for (int i = startY; i < startY + height; i++) {
            for (int j = startX; j < startX + width; j++) {
                if (j + 2 >= currentWorld.length) {
                    width--;
                }
                if (i + 2 >= currentWorld[0].length) {
                    height--;
                } else {
                    currentWorld[j][i] = Tileset.FLOOR2;
                }
            }
        }
        placeWalls();
    }

    public boolean canPlace() {
        for (int i = startY - 1; i < startY + height + 1; i++) {
            for (int j = startX - 1; j < startX + width + 1; j++) {
                int l = currentWorld.length;
                if (currentWorld[j][i] == Tileset.FLOOR2 || j + 3 >= l || i + 3 >= currentWorld[0].length) {
                    return false;
                } else if (currentWorld[j + 1][i] == Tileset.WALL2 || currentWorld[j][i + 1] == Tileset.WALL2) {
                    return false;
                } else if (currentWorld[j - 1][i] == Tileset.WALL2 || currentWorld[j][i - 1] == Tileset.WALL2) {
                    return false;
                }
            }
        }
        return true;
    }
    public void placeWalls() {
        for (int i = startY - 1; i < startY + height + 1; i++) {
            for (int j = startX - 1; j < startX + width + 1; j++) {
                if (currentWorld[j][i] != Tileset.FLOOR2) {
                    currentWorld[j][i] = Tileset.WALL2;
                }
            }
        }
        area = (width + 2) * (height + 2);
    }

    public int getArea() {
        return area;
    }

    public int getCoord(String xy) {
        if (xy.equals("x")) {
            return startX;
        }
        if (xy.equals("y")) {
            return startY;
        }
        return 0;
    }

    public int getDim(String hw) {
        if (hw.equals("w")) {
            return width;
        }
        if (hw.equals("h")) {
            return height;
        }
        return 0;
    }

    public void lock(TETile tile) {
        for (int i = startY; i < startY + height; i++) {
            for (int j = startX; j < startX + width; j++) {
                if (j + 2 >= currentWorld.length) {
                    width--;
                }
                if (i + 2 >= currentWorld[0].length) {
                    height--;
                } else {
                    currentWorld[j][i] = tile;
                }
            }
        }
    }



}

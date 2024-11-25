package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.awt.*;

public class Player {

    private int attack;

    private Point pos;

    private TETile character;
    private TETile[][] currentWorld;
    private char version;
    public Player(int x, int y, TETile[][] currWorld, TETile version) {
        character = version;
        attack = 100;
        pos = new Point(x, y);
        currentWorld = currWorld;
    }

    public int getPosX() {
        return pos.x;
    }

    public int getPosY() {
        return pos.y;
    }

    public void changeOrientation(TETile newO) {
        character = newO;
    }

    public void placePlayer() {
        currentWorld[pos.x][pos.y] = character;
    }

    public void move(int newX, int newY) {
        currentWorld[pos.x][pos.y] = Tileset.FLOOR2;
        currentWorld[newX][newY] = character;
        pos.x = newX;
        pos.y = newY;
    }

    public void increaseAttack(String weapon) {

    }
}

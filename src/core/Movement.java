package core;

import tileengine.TETile;
import tileengine.Tileset;

public class Movement {
    private int width;

    private int height;

    private Player player;

    TETile[][] currentWorld;

    public Movement(int width, int height, Player player, TETile[][] currentWorld) {
        this.width = width;
        this.height = height;
        this.player = player;
        this.currentWorld = currentWorld;
    }

    public boolean tryMove(int newX, int newY) {
        TETile c = currentWorld[newX][newY];
        if (c == Tileset.WALL2 || c == Tileset.PUZZLE || c == Tileset.PUZZLECOMPLETED || c == Tileset.PUZZLEHIDDEN) {
            return false;
        } else {
            player.move(newX, newY);
            return true;
        }
    }

}

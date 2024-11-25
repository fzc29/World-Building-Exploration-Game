package tileengine;

import java.awt.Color;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset {
    public static final TETile AVATAR = new TETile('@', Color.white, Color.black, "you", 0);
    public static final TETile WALL = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "wall", 1);
    public static final TETile FLOOR = new TETile('·', new Color(128, 192, 128), Color.darkGray, "floor", 2);
    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black, "nothing", 3);
    public static final TETile GRASS = new TETile('"', Color.green, Color.black, "grass", 4);
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water", 5);
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower", 6);
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, Color.black,
            "locked door", 7);
    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange, Color.black,
            "unlocked door", 8);
    public static final TETile SAND = new TETile('▒', Color.yellow, Color.black, "sand", 9);
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "mountain", 10);
    public static final TETile TREE = new TETile('♠', Color.green, Color.black, "tree", 11);

    public static final TETile CELL = new TETile('█', Color.white, Color.black, "cell", 12);

    public static final TETile WATER2 = new TETile('≈', Color.blue, new Color(111, 208, 255), "water", "./src/images/water3.png", 13);

    public static final TETile FLOOR2 = new TETile('·', new Color(128, 192, 128), Color.darkGray, "floor", "./src/images/floor.png", 14);

    public static final TETile WALL2 = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "wall", "./src/images/wall.png", 15);
    public static final TETile CHARACTER = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "character", "./src/images/characterBACK.png", 16);

    public static final TETile CHARACTERFRONT = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "character", "./src/images/characterFRONT.png", 17);

    public static final TETile CHARACTERLEFT = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "character", "./src/images/characterLEFT.png", 18);

    public static final TETile CHARACTERRIGHT = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "character", "./src/images/characterRIGHT.png", 19);

    public static final TETile XCHARACTER = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "character", "./src/images/xback.png", 16);

    public static final TETile XCHARACTERFRONT = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "character", "./src/images/xfront.png", 17);

    public static final TETile XCHARACTERLEFT = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "character", "./src/images/xleft.png", 18);

    public static final TETile XCHARACTERRIGHT = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "character", "./src/images/xright.png", 16);

    public static final TETile LOCKEDFLOOR = new TETile('·', new Color(122, 123, 144), Color.darkGray,
            "lockedfloor", 20);

    public static final TETile PUZZLE = new TETile('·', new Color(102, 223, 144), new Color(102, 223, 144),
            "puzzle", 21);

    public static final TETile STAR = new TETile('*', new Color(255, 255, 0), Color.yellow,
            "star", "./src/images/pixel-frame-0.png", 22);

    public static final TETile PUZZLECOMPLETED = new TETile('·', new Color(200, 223, 204), new Color(200, 223, 204),
            "puzzle", 23);

    public static final TETile PUZZLEHIDDEN = new TETile('·', new Color(222, 49, 99), new Color(222, 49, 99),
            "puzzle", 24);
}



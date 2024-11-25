package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.util.*;


public class Graph {

    // marking connections between graphs

    ArrayList<Room> roomsList;
    ArrayList<Room> roomsListExtra;
    LinkedHashMap<Room, ArrayList<Room>> roomMap;
    LinkedHashMap<Room, ArrayList<Room>> connectMap;
    int distanceLimit;
    Random r;
    Room connectStart;
    TETile[][] currentTiles;

    public Graph(Random random, TETile[][] tiles) {
        roomsList = new ArrayList<>();
        roomMap = new LinkedHashMap<>();
        connectMap = new LinkedHashMap<>();
        distanceLimit = 25;
        r = random;
        currentTiles = tiles;

    }

    public Graph(int distance, Random random, TETile[][] tiles) {
        roomMap = new LinkedHashMap<>();
        connectMap = new LinkedHashMap<>();
        distanceLimit = distance;
        currentTiles = tiles;
        r = random;
    }

    public void addRoom(Room newRoom) {
        roomsList.add(newRoom);
        roomMap.put(newRoom, new ArrayList<>());
        connectMap.put(newRoom, new ArrayList<>());
        for (Room ro : roomMap.keySet()) {
            if (withinDistance(newRoom, ro) && ro != newRoom) {
                roomMap.get(newRoom).add(ro);
                roomMap.get(ro).add(newRoom);
            }
        }
        connectStart = newRoom;
    }

    public double getDistance(Room start, Room end) {
        // should compute distance
        int startx = start.getCoord("x");
        int starty = start.getCoord("y");

        int endx = end.getCoord("x");
        int endy = end.getCoord("y");

        return Math.hypot(startx - endx, starty - endy);
    }

    public boolean withinDistance(Room start, Room end) {
        return getDistance(start, end) < distanceLimit;
    }


    public boolean isConnected(Room start, Room end) {
        return connectMap.get(start).contains(end) || connectMap.get(end).contains(start);
    }


    public Room getRandomRoom(Room start) {
        Room roomToConnect = null;

        for (Room ro : roomMap.get(start)) {
            int index = Math.floorMod(r.nextInt(), roomMap.get(start).size());
            Room roomToConnectMaybe = roomMap.get(start).get(index);
            if (!isConnected(start, roomToConnectMaybe)) {
                roomToConnect = roomToConnectMaybe;
                return roomToConnect;
            }
        }
        return roomToConnect;
    }
    
    public Room getRandomStartRoom() {
        int index = Math.floorMod(r.nextInt(), roomMap.size());
        Set<Room> rooms = roomMap.keySet();
        List<Room> roomsLists = new ArrayList<>(rooms);

        return roomsLists.get(index);
    }


    public void connectRooms() {
        // instead iterate through each room individually

        for (Room ro : roomMap.keySet()) {
            // int numConnections = Math.floorMod(r.nextInt(), 2) + 1;
            for (Room om : roomMap.get(ro)) {
                if (!isConnected(ro, om)) {
                    connect(ro, om);
                }
            }
        }

    }

    public void connectRooms2() {
        Room start = connectStart;
        int n = roomMap.size();
        while (n > 0) {
            Room next = getRandomRoom(start);
            connect(start, next);
            start = next;
            n--;
        }
    }

    public void connect(Room start, Room end) {
        connectMap.get(start).add(end);
        connectMap.get(end).add(start);
        // pick direction to go into -> start with longer
        int hDir = horizontal(start, end);
        int vDir = vertical(start, end);

        // go in direction of longer distance > need to find overlaps in other orientation
        if (Math.abs(hDir) >  Math.abs(vDir)) {
            // find overlap in vertical
            // if vDir negative go look at
            int c = overlaps(start.getCoord("y"), start.getDim("h"), end.getCoord("y"), end.getDim("h"));
            if (c > 0) {
                if (hDir > 0) {
                    makeDirectHallway("hor", c, start, end);
                } else {
                    makeDirectHallway("hor", c, end, start);
                }
            }
        } else {
            // find overlap in horizontal
            int c = overlaps(start.getCoord("x"), start.getDim("w"), end.getCoord("x"), end.getDim("w"));
            if (c > 0) {
                if (vDir > 0) {
                    makeDirectHallway("ver", c, start, end);
                } else {
                    makeDirectHallway("ver", c, end, start);
                }
            }
        }

    }

    // check
    public int horizontal(Room start, Room end) {
        return end.getCoord("x") - start.getCoord("x");
    }
    public int vertical(Room start, Room end) {
        return end.getCoord("y") - start.getCoord("y");
    }

    public int overlaps(int start, int startDim, int end, int endDim) {
        ArrayList<Integer> coords = new ArrayList<>();
        int n = end;
        while (n < end + endDim) {
            if (n >= start && n < start + startDim) {
                coords.add(n);
            }
            n += 1;
        }

        if (coords.isEmpty()) {
            return -1;
        } else {
            int rand = Math.floorMod(r.nextInt(), coords.size());
            return coords.get(rand);
        }
    }

    public void makeDirectHallway(String direction, int c, Room start, Room end) {
        if (direction.equals("hor")) {
            for (int i = start.getCoord("x") + start.getDim("w"); i < end.getCoord("x"); i++) {
                // make into floor
                currentTiles[i][c] = Tileset.FLOOR2;
                if (currentTiles[i][c + 1] != Tileset.FLOOR2) {
                    currentTiles[i][c + 1] = Tileset.WALL2;
                }
                if (currentTiles[i][c - 1] != Tileset.FLOOR2) {
                    currentTiles[i][c - 1] = Tileset.WALL2;
                }
            }
        } else if (direction.equals("ver")) {
            for (int i = start.getCoord("y") + start.getDim("h"); i < end.getCoord("y"); i++) {
                // make into floor
                currentTiles[c][i] = Tileset.FLOOR2;
                if (currentTiles[c - 1][i] != Tileset.FLOOR2) {
                    currentTiles[c - 1][i] = Tileset.WALL2;
                }
                if (currentTiles[c + 1][i] != Tileset.FLOOR2) {
                    currentTiles[c + 1][i] = Tileset.WALL2;
                }
            }
        }
    }

    public void createdLockedRoom() {
        int randomIndex = Math.floorMod(r.nextInt(), roomMap.size());
        Room locked = roomsList.get(randomIndex);
        locked.lock(Tileset.LOCKEDFLOOR);
    }

}

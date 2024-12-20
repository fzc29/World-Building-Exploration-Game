import core.AutograderBuddy;
import edu.princeton.cs.algs4.StdDraw;
import org.junit.jupiter.api.Test;
import tileengine.TERenderer;
import tileengine.TETile;

public class WorldGenTests {
    @Test
    public void basicTest() {
        // put different seeds here to test different worlds
        TETile[][] tiles = AutograderBuddy.getWorldFromInput("n1234567890123456789s");

        TERenderer ter = new TERenderer();
        ter.initialize(tiles.length, tiles[0].length);
        ter.renderFrame(tiles);
        StdDraw.pause(5000); // pause for 5 seconds so you can see the output
    }

    @Test
    public void basicInteractivityTest() {
        // TODO: write a test that uses an input like "n123swasdwasd"
//        TETile[][] ogTile = AutograderBuddy.getWorldFromInput("N999SDDDWWWDDD");
        AutograderBuddy.getWorldFromInput("N999SDDDWWWDDD:Q");
        AutograderBuddy.getWorldFromInput("L:Q");

        AutograderBuddy.getWorldFromInput("L:Q");
        TETile[][] tilesLoad = AutograderBuddy.getWorldFromInput("LWWWDDD");

    }
}

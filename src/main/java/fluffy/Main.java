package fluffy;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) throws URISyntaxException, IOException, FontFormatException {
        // If not enabled frames are only updated with interaction
        System.setProperty("sun.java2d.opengl", "true");

        System.out.println("Working Directory: " + System.getProperty("user.dir"));

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(Font.createFont(Font.TRUETYPE_FONT,
                Main.class.getClassLoader().getResourceAsStream("PressStart.ttf")));

        final var character = new Prefab(Main.class.getClassLoader().getResourceAsStream("fluffy.png"), 4);
        final var enemy = new Prefab(Main.class.getClassLoader().getResourceAsStream("plane.png"), 4);
        final var fog = new Prefab(Main.class.getClassLoader().getResourceAsStream("fog.png"), 1);
        final var powerup = new Prefab(Main.class.getClassLoader().getResourceAsStream("bird.png"), 5);
        final var pickup = new Prefab(Main.class.getClassLoader().getResourceAsStream("bird_pickup.png"), 1);

        new GamePanel(character, enemy, fog, powerup, pickup);
    }
}

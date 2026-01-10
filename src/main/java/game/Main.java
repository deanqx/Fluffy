package game;

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

        var prefaps = new Prefaps();

        new GamePanel(prefaps);
    }
}

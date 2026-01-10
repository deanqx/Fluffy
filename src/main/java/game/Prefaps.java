package game;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

public class Prefaps {
    private BufferedImage[] character;
    private BufferedImage[] enemy;
    private BufferedImage[] fog;
    private BufferedImage[] powerup;
    private BufferedImage[] powerupPickup;

    public Prefaps() throws URISyntaxException, IOException {
        character = loadPics(getClass().getClassLoader().getResourceAsStream("fluffy.png"), 4);
        enemy = loadPics(getClass().getClassLoader().getResourceAsStream("plane.png"), 4);
        fog = loadPics(getClass().getClassLoader().getResourceAsStream("fog.png"), 1);
        powerup = loadPics(getClass().getClassLoader().getResourceAsStream("bird.png"), 5);
        powerupPickup = loadPics(getClass().getClassLoader().getResourceAsStream("bird_pickup.png"), 1);
    }

    public BufferedImage[] getCharacter() {
        return character;
    }

    public BufferedImage[] getEnemy() {
        return enemy;
    }

    public BufferedImage[] getFog() {
        return fog;
    }

    public BufferedImage[] getPowerup() {
        return powerup;
    }

    public BufferedImage[] getPowerupPickup() {
        return powerupPickup;
    }

    // Bilder müssen horizontal hintereinander in einem Bild sein
    private BufferedImage[] loadPics(InputStream input, int picCount) throws IOException {
        BufferedImage[] pics = new BufferedImage[picCount];
        BufferedImage source = null;

        source = ImageIO.read(input);

        for (int x = 0; x < picCount; x++) {
            // Bild mit picCount aufteilen
            pics[x] = source.getSubimage(x * source.getWidth() / picCount, 0, source.getWidth() / picCount,
                    source.getHeight());
        }

        return pics;
    }
}

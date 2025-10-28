package game;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

public class Prefaps {
    private BufferedImage[] character;
    private BufferedImage[] enemy;
    private BufferedImage[] fog;
    private BufferedImage[] powerup;
    private BufferedImage[] powerup_pickup;

    public Prefaps() throws URISyntaxException, IOException {
        character = load_pics(new File(getClass().getClassLoader().getResource("fluffy.png").toURI()), 4);
        enemy = load_pics(new File(getClass().getClassLoader().getResource("plane.png").toURI()), 4);
        fog = load_pics(new File(getClass().getClassLoader().getResource("fog.png").toURI()), 1);
        powerup = load_pics(new File(getClass().getClassLoader().getResource("bird.png").toURI()), 5);
        powerup_pickup = load_pics(new File(getClass().getClassLoader().getResource("bird_pickup.png").toURI()), 1);
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

    public BufferedImage[] getPowerup_pickup() {
        return powerup_pickup;
    }

    // Bilder müssen horizontal hintereinander in einem Bild sein
    private BufferedImage[] load_pics(File file, int picCount) throws IOException {
        BufferedImage[] pics = new BufferedImage[picCount];
        BufferedImage source = null;

        source = ImageIO.read(file);

        for (int x = 0; x < picCount; x++) {
            // Bild mit picCount aufteilen
            pics[x] = source.getSubimage(x * source.getWidth() / picCount, 0, source.getWidth() / picCount,
                    source.getHeight());
        }

        return pics;
    }
}

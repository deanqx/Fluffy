package game;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

/*
 * Preloaded images to prevent loading same images over and over again.
 */
public class Prefab {
    private final BufferedImage[] images;

    /*
     * The animation is stored as different images next to each other.
     */
    public Prefab(final InputStream input, final int image_count) throws IOException {
        images = new BufferedImage[image_count];
        BufferedImage source = null;

        source = ImageIO.read(input);

        for (int x = 0; x < image_count; x++) {
            // divide image into smaler images
            images[x] = source.getSubimage(x * source.getWidth() / image_count, 0, source.getWidth() / image_count,
                    source.getHeight());
        }
    }

    public int getImageCount() {
        return images.length;
    }

    public BufferedImage getImage(final int index) throws IllegalArgumentException {
        if (0 > index || index >= images.length) {
            throw new IllegalArgumentException(
                    String.format("Image index is out of range (0 <= %d < %d)", index, images.length));
        }

        return images[index];
    }
}

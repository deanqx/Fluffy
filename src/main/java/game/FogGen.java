package game;

import java.awt.image.BufferedImage;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class FogGen {
    private GamePanel panel;
    private Vector<Sprite> fogs;
    private BufferedImage[] fogPrefab;
    private final double minScale;
    private final double maxScale;

    private final double spawnTop;
    private final double spawnLeft;
    private final double spawnBottom;
    private final double spawnRight;

    public FogGen(GamePanel panel, Vector<Sprite> fogs, BufferedImage[] fog_prefab, double min_scale,
            double max_scale) {
        this.panel = panel;
        this.fogs = fogs;
        this.fogPrefab = fog_prefab;
        this.minScale = min_scale;
        this.maxScale = max_scale;

        spawnTop = -2.0 * fog_prefab[0].getHeight() * max_scale;
        spawnLeft = -2.0 * fog_prefab[0].getWidth() * max_scale;
        spawnBottom = panel.getSize().height + fog_prefab[0].getHeight() * max_scale;
        spawnRight = panel.getSize().width + fog_prefab[0].getWidth() * max_scale;
    }

    public void spawn(int amount, double speed) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();

        for (int i = 0; i < amount; i++) {
            double scale = rng.nextDouble(minScale, maxScale);
            double x = rng.nextDouble(spawnLeft, spawnRight - fogPrefab[0].getWidth() * scale);
            double y = rng.nextDouble(spawnTop, spawnBottom - fogPrefab[0].getHeight() * scale);
            double x_vel_variance = rng.nextDouble(0.8, 1.0);
            double y_vel_variance = rng.nextDouble(0.8, 1.0);

            Sprite new_fog = new Sprite(panel, fogPrefab, x, y, scale, 0, speed);
            new_fog.xVelocity = speed * x_vel_variance;
            new_fog.yVelocity = speed * y_vel_variance;

            fogs.add(new_fog);
        }
    }

    public void reuseOutOfBounds() {
        ThreadLocalRandom rng = ThreadLocalRandom.current();

        for (Sprite fog : fogs) {
            if (!fog.isOutOfBounds()) {
                continue;
            }

            boolean top_or_left = rng.nextInt(0, 2) == 1;

            if (top_or_left) {
                fog.x = rng.nextDouble(spawnLeft, spawnRight - fog.widthScaled);
                fog.y = spawnTop + 1.0;
            } else {
                fog.x = spawnLeft + 1.0;
                fog.y = rng.nextDouble(spawnTop, spawnBottom - fog.heightScaled);
            }

            // TODO bug clouds getting slower
            double x_vel_variance = rng.nextDouble(0.8, 1.0);
            double y_vel_variance = rng.nextDouble(0.8, 1.0);

            fog.xVelocity = fog.speed * x_vel_variance;
            fog.yVelocity = fog.speed * y_vel_variance;
        }
    }
}

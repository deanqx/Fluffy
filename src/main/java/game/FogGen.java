package game;

import java.awt.image.BufferedImage;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class FogGen {
    private GamePanel panel;
    private Vector<Sprite> fogs;
    private BufferedImage[] fogPrefab;
    private final float minScale;
    private final float maxScale;

    private final float spawnTop;
    private final float spawnLeft;
    private final float spawnBottom;
    private final float spawnRight;

    public FogGen(GamePanel panel, Vector<Sprite> fogs, BufferedImage[] fog_prefab, float min_scale,
            float max_scale) {
        this.panel = panel;
        this.fogs = fogs;
        this.fogPrefab = fog_prefab;
        this.minScale = min_scale;
        this.maxScale = max_scale;

        spawnTop = -2.0f * fog_prefab[0].getHeight() * max_scale;
        spawnLeft = -2.0f * fog_prefab[0].getWidth() * max_scale;
        spawnBottom = panel.getSize().height + fog_prefab[0].getHeight() * max_scale;
        spawnRight = panel.getSize().width + fog_prefab[0].getWidth() * max_scale;
    }

    public void spawn(int amount, float speed) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();

        for (int i = 0; i < amount; i++) {
            float scale = rng.nextFloat(minScale, maxScale);
            float x = rng.nextFloat(spawnLeft, spawnRight - fogPrefab[0].getWidth() * scale);
            float y = rng.nextFloat(spawnTop, spawnBottom - fogPrefab[0].getHeight() * scale);
            float x_vel_variance = rng.nextFloat(0.8f, 1.0f);
            float y_vel_variance = rng.nextFloat(0.8f, 1.0f);

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
                fog.x = rng.nextFloat(spawnLeft, spawnRight - fog.widthScaled);
                fog.y = spawnTop + 1.0f;
            } else {
                fog.x = spawnLeft + 1.0f;
                fog.y = rng.nextFloat(spawnTop, spawnBottom - fog.heightScaled);
            }

            // TODO bug clouds getting slower
            float x_vel_variance = rng.nextFloat(0.8f, 1.0f);
            float y_vel_variance = rng.nextFloat(0.8f, 1.0f);

            fog.xVelocity = fog.speed * x_vel_variance;
            fog.yVelocity = fog.speed * y_vel_variance;
        }
    }
}

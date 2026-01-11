package game;

import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class FogGen {
    private final GamePanel panel;
    private final Prefab fogPrefab;
    private final float minScale;
    private final float maxScale;

    private final float spawnTop;
    private final float spawnLeft;
    private final float spawnBottom;
    private final float spawnRight;

    public FogGen(final GamePanel panel, final Prefab fog_prefab, final float min_scale, final float max_scale) {
        this.panel = panel;
        this.fogPrefab = fog_prefab;
        this.minScale = min_scale;
        this.maxScale = max_scale;

        spawnTop = -2.0f * fog_prefab.getImage(0).getHeight() * max_scale;
        spawnLeft = -2.0f * fog_prefab.getImage(0).getWidth() * max_scale;
        spawnBottom = panel.getSize().height + fog_prefab.getImage(0).getHeight() * max_scale;
        spawnRight = panel.getSize().width + fog_prefab.getImage(0).getWidth() * max_scale;
    }

    public void spawn(final int amount, final float speed) {
        final ThreadLocalRandom rng = ThreadLocalRandom.current();

        for (int i = 0; i < amount; i++) {
            final float scale = rng.nextFloat(minScale, maxScale);
            final float x = rng.nextFloat(spawnLeft, spawnRight - fogPrefab.getImage(0).getWidth() * scale);
            final float y = rng.nextFloat(spawnTop, spawnBottom - fogPrefab.getImage(0).getHeight() * scale);
            final float x_vel_variance = rng.nextFloat(0.8f, 1.0f);
            final float y_vel_variance = rng.nextFloat(0.8f, 1.0f);

            final Sprite new_fog = new Fog(panel, fogPrefab);
            new_fog.setX(x);
            new_fog.setY(y);
            new_fog.setSpriteScale(scale);
            new_fog.setSpeed(speed);
            new_fog.xVelocity = speed * x_vel_variance;
            new_fog.yVelocity = speed * y_vel_variance;

            panel.addObject(new_fog);
        }
    }

    public void reuseOutOfBounds() {
        final ThreadLocalRandom rng = ThreadLocalRandom.current();

        for (final Iterator<GameObject> objects_it = panel.iterateObjects(); objects_it.hasNext();) {
            if (objects_it.next() instanceof Fog fog) {
                if (!fog.isOutOfBounds()) {
                    continue;
                }

                final boolean top_or_left = rng.nextInt(0, 2) == 1;

                if (top_or_left) {
                    fog.x = rng.nextFloat(spawnLeft, spawnRight - fog.widthScaled);
                    fog.y = spawnTop + 1.0f;
                } else {
                    fog.x = spawnLeft + 1.0f;
                    fog.y = rng.nextFloat(spawnTop, spawnBottom - fog.heightScaled);
                }

                final float x_vel_variance = rng.nextFloat(0.8f, 1.0f);
                final float y_vel_variance = rng.nextFloat(0.8f, 1.0f);

                fog.xVelocity = fog.speed * x_vel_variance;
                fog.yVelocity = fog.speed * y_vel_variance;
            }
        }
    }
}

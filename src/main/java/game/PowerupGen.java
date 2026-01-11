package game;

import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class PowerupGen {
    private final float spawnRotations[] = { 0.0f, 0.5f, 0.75f, 0.25f, 0.125f, 0.375f, 0.625f, 0.875f };

    private final GamePanel panel;
    private final Prefab pickupPrefab;
    private final Prefab powerupPrefab;
    private final float rotationRadius;
    private final float pickupWidthScaled;
    private final float pickupHeightScaled;
    private final float scale;

    private float fallingSpeed = 0.0f;
    private float rotationSpeed = 0.0f;
    private float rotation = 0.0f;

    public PowerupGen(final GamePanel panel, final Prefab pickup_prefab, final Prefab powerup_prefab, final float scale,
            final float rotation_radius) {
        this.panel = panel;
        this.pickupPrefab = pickup_prefab;
        this.powerupPrefab = powerup_prefab;
        this.rotationRadius = rotation_radius;
        this.scale = scale;

        pickupWidthScaled = pickup_prefab.getImage(0).getWidth() * scale;
        pickupHeightScaled = pickup_prefab.getImage(0).getHeight() * scale;
    }

    public void spawn(final int amount) {
        final ThreadLocalRandom t = ThreadLocalRandom.current();

        for (int i = 0; i < amount; i++) {
            final float x = t.nextFloat(1.0f, panel.getGameWidth() - pickupWidthScaled - 1.0f);
            final float y = pickupHeightScaled * -scale;

            final Sprite new_pickup = new Pickup(panel, pickupPrefab);
            new_pickup.setX(x);
            new_pickup.setY(y);
            new_pickup.setSpriteScale(scale);
            new_pickup.setSpeed(fallingSpeed);
            new_pickup.yVelocity = fallingSpeed;

            panel.addObject(new_pickup);
        }
    }

    public void pickup() {
        int powerup_index = 0;

        for (final Iterator<GameObject> objects_it = panel.iterateObjects(); objects_it.hasNext();) {
            if (objects_it.next() instanceof final Powerup powerup) {
                // reuse old powerup
                if (!powerup.visible) {
                    powerup.visible = true;
                    return;
                }

                powerup_index += 1;
            }
        }

        if (powerup_index == 8) {
            panel.addScore(200.0f);
            return;
        }

        if (powerup_index == 0) {
            rotation = 0.0f;
        }

        final var powerup = new Powerup(panel, powerupPrefab);
        powerup.setSpriteScale(scale);
        powerup.setAnimationImageTime(200f);
        powerup.setSpeed(rotationSpeed);

        powerup.setX(rotationRadius * (float) Math.cos(2.0f * (float) Math.PI * spawnRotations[powerup_index]));
        powerup.setY(rotationRadius * (float) Math.sin(2.0f * (float) Math.PI * spawnRotations[powerup_index]));

        panel.getCharacter().addChild(powerup);
        panel.addObject(powerup);
    }

    public void moveAll() {
        int powerup_index = 0;

        for (final Iterator<GameObject> objects_it = panel.iterateObjects(); objects_it.hasNext();) {
            if (objects_it.next() instanceof final Powerup powerup) {
                final float rot = 2.0f * (float) Math.PI * (rotation + spawnRotations[powerup_index]);

                final float rotation_cos = (float) Math.cos(rot);
                final float rotation_sin = (float) Math.sin(rot);

                powerup.x = panel.getCharacter().x + panel.getCharacter().xMidOffset - powerup.xMidOffset
                        + rotationRadius * rotation_cos;
                powerup.y = panel.getCharacter().y + panel.getCharacter().yMidOffset - powerup.yMidOffset
                        + rotationRadius * rotation_sin;

                if (rotation_cos < 0.6f && rotation_sin < 0.8f) {
                    if (powerup.widthScaled >= 0.0f) {
                        powerup.widthScaled *= -1.0f;
                    }
                } else {
                    if (powerup.widthScaled < 0.0f) {
                        powerup.widthScaled *= -1.0f;
                    }
                }

                powerup_index += 1;
            }
        }

        rotation += panel.getDeltaTimeMs() * rotationSpeed * 1e-3;

        if (rotation > 1.0f) {
            rotation = 0.0f;
        }
    }

    public void clean() {
        for (final Iterator<GameObject> objects_it = panel.iterateObjects(); objects_it.hasNext();) {
            if (objects_it.next() instanceof final Pickup pickup) {
                if (pickup.isOutOfBounds()) {
                    pickup.toRemove = true;
                }
            }
        }
    }

    public void setFallingSpeed(final float fallingSpeed) {
        this.fallingSpeed = fallingSpeed;
    }

    public void setRotationSpeed(final float rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }
}

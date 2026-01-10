package game;

import java.awt.image.BufferedImage;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class PowerupGen {
    private GamePanel panel;
    private Sprite cloud;
    private Vector<Sprite> pickups;
    private Vector<Sprite> powerups;
    private BufferedImage[] pickupPrefab;
    private BufferedImage[] powerupPrefab;
    private final float fallingSpeed;
    private final float rotationSpeed;
    private final float rotationRadius;
    private float rotation = 0.0f;

    private final float pickupWidthScaled;
    private final float pickupHeightScaled;
    private final float scale;

    private final float spawnRotations[] = { 0.0f, 0.5f, 0.75f, 0.25f, 0.125f, 0.375f, 0.625f, 0.875f };

    public PowerupGen(GamePanel panel, Sprite cloud, Vector<Sprite> pickups, Vector<Sprite> powerups,
            BufferedImage[] pickup_prefab, BufferedImage[] powerup_prefab, float scale, float falling_speed,
            float rotation_speed, float rotation_radius) {
        this.panel = panel;
        this.cloud = cloud;
        this.pickups = pickups;
        this.powerups = powerups;
        this.pickupPrefab = pickup_prefab;
        this.powerupPrefab = powerup_prefab;
        this.fallingSpeed = falling_speed;
        this.rotationSpeed = rotation_speed;
        this.rotationRadius = rotation_radius;
        this.scale = scale;

        pickupWidthScaled = pickup_prefab[0].getWidth() * scale;
        pickupHeightScaled = pickup_prefab[0].getHeight() * scale;
    }

    public void spawn(int amount) {
        ThreadLocalRandom t = ThreadLocalRandom.current();

        for (int i = 0; i < amount; i++) {
            float x = t.nextFloat(1.0f, panel.width - pickupWidthScaled - 1.0f);
            float y = pickupHeightScaled * -scale;

            Sprite new_pickup = new Sprite(panel, pickupPrefab, x, y, scale, 0, fallingSpeed);
            new_pickup.yVelocity = fallingSpeed;

            pickups.add(new_pickup);
        }
    }

    public void pickup() {
        for (Sprite powerup : powerups) {
            if (!powerup.visible) {
                powerup.visible = true;
                return;
            }
        }

        if (powerups.size() == 8) {
            panel.score += 200f;
            return;
        }

        if (powerups.size() == 0) {
            rotation = 0.0f;
        }

        Sprite powerup = new Sprite(panel, powerupPrefab, 0.0f, 0.0f, scale, 200f, rotationSpeed);

        powerup.x = rotationRadius * (float) Math.cos(2.0f * (float) Math.PI * spawnRotations[powerups.size()]);
        powerup.y = rotationRadius * (float) Math.sin(2.0f * (float) Math.PI * spawnRotations[powerups.size()]);

        cloud.addChild(powerup);
        powerups.add(powerup);
    }

    public void moveAll() {
        for (int i = 0; i < powerups.size(); i++) {
            float rot = 2.0f * (float) Math.PI * (rotation + spawnRotations[i]);

            float rotation_cos = (float) Math.cos(rot);
            float rotation_sin = (float) Math.sin(rot);

            powerups.get(i).x = cloud.x + cloud.xMidOffset - powerups.get(i).xMidOffset
                    + rotationRadius * rotation_cos;
            powerups.get(i).y = cloud.y + cloud.yMidOffset - powerups.get(i).yMidOffset
                    + rotationRadius * rotation_sin;

            if (rotation_cos < 0.6f && rotation_sin < 0.8f) {
                if (powerups.get(i).widthScaled >= 0.0f)
                    powerups.get(i).widthScaled *= -1.0f;
            } else {
                if (powerups.get(i).widthScaled < 0.0f)
                    powerups.get(i).widthScaled *= -1.0f;
            }
        }

        rotation += panel.deltaTime * rotationSpeed * 1e-3;

        if (rotation > 1.0f) {
            rotation = 0.0f;
        }
    }

    public void clean() {
        for (Sprite pickup : pickups) {
            if (pickup.isOutOfBounds()) {
                pickup.toRemove = true;
            }
        }
    }
}

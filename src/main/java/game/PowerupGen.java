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
    private final double fallingSpeed;
    private final double rotationSpeed;
    private final double rotationRadius;
    private double rotation = 0.0;

    private final double pickupWidthScaled;
    private final double pickupHeightScaled;
    private final double scale;

    private final double spawnRotations[] = { 0.0, 0.5, 0.75, 0.25, 0.125, 0.375, 0.625, 0.875 };

    public PowerupGen(GamePanel panel, Sprite cloud, Vector<Sprite> pickups, Vector<Sprite> powerups,
            BufferedImage[] pickup_prefab, BufferedImage[] powerup_prefab, double scale, double falling_speed,
            double rotation_speed, double rotation_radius) {
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
            double x = t.nextDouble(1.0, panel.width - pickupWidthScaled - 1.0);
            double y = pickupHeightScaled * -scale;

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
            panel.score += 200;
            return;
        }

        if (powerups.size() == 0) {
            rotation = 0.0;
        }

        Sprite powerup = new Sprite(panel, powerupPrefab, 0.0, 0.0, scale, 200, rotationSpeed);

        powerup.x = rotationRadius * Math.cos(2.0 * Math.PI * spawnRotations[powerups.size()]);
        powerup.y = rotationRadius * Math.sin(2.0 * Math.PI * spawnRotations[powerups.size()]);

        cloud.addChild(powerup);
        powerups.add(powerup);
    }

    public void moveAll() {
        for (int i = 0; i < powerups.size(); i++) {
            double rot = 2.0 * Math.PI * (rotation + spawnRotations[i]);

            powerups.get(i).x = cloud.x + cloud.xMidOffset - powerups.get(i).xMidOffset
                    + rotationRadius * Math.cos(rot);
            powerups.get(i).y = cloud.y + cloud.yMidOffset - powerups.get(i).yMidOffset
                    + rotationRadius * Math.sin(rot);

            if (Math.cos(rot) < 0.6 && Math.sin(rot) < 0.8) {
                if (powerups.get(i).widthScaled >= 0.0)
                    powerups.get(i).widthScaled *= -1.0;
            } else {
                if (powerups.get(i).widthScaled < 0.0)
                    powerups.get(i).widthScaled *= -1.0;
            }
        }

        rotation += panel.deltaTime * rotationSpeed * 1e-3;

        if (rotation > 1.0) {
            rotation = 0.0;
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

package game;

import java.awt.image.BufferedImage;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class PowerupGen
{
    private GamePanel panel;
    private Sprite cloud;
    private Vector<Sprite> pickups;
    private Vector<Sprite> powerups;
    private BufferedImage[] pickup_prefab;
    private BufferedImage[] powerup_prefab;
    private final double falling_speed;
    private final double rotation_speed;
    private final double rotation_radius;
    private double rotation = 0.0;

    private final double pickup_width_scaled;
    private final double pickup_height_scaled;
    private final double scale;

    private final double spawn_rotations[] =
    { 0.0, 0.5, 0.75, 0.25, 0.125, 0.375, 0.625, 0.875 };

    public PowerupGen(GamePanel panel, Sprite cloud, Vector<Sprite> pickups, Vector<Sprite> powerups, BufferedImage[] pickup_prefab, BufferedImage[] powerup_prefab, double scale, double falling_speed, double rotation_speed, double rotation_radius)
    {
        this.panel = panel;
        this.cloud = cloud;
        this.pickups = pickups;
        this.powerups = powerups;
        this.pickup_prefab = pickup_prefab;
        this.powerup_prefab = powerup_prefab;
        this.falling_speed = falling_speed;
        this.rotation_speed = rotation_speed;
        this.rotation_radius = rotation_radius;
        this.scale = scale;

        pickup_width_scaled = pickup_prefab[0].getWidth() * scale;
        pickup_height_scaled = pickup_prefab[0].getHeight() * scale;
    }

    public void spawn(int n)
    {
        ThreadLocalRandom t = ThreadLocalRandom.current();

        for (int i = 0; i < n; i++)
        {
            double x = t.nextDouble(1.0, panel.width - pickup_width_scaled - 1.0);
            double y = pickup_height_scaled * -scale;

            Sprite new_pickup = new Sprite(panel, pickup_prefab, x, y, scale, 0, falling_speed);
            new_pickup.y_velocity = falling_speed;

            pickups.add(new_pickup);
        }
    }

    public void pickup()
    {
        for (Sprite powerup : powerups)
        {
            if (!powerup.visible)
            {
                powerup.visible = true;
                return;
            }
        }

        if (powerups.size() == 8)
        {
            panel.score += 200;
            return;
        }

        if (powerups.size() == 0)
        {
            rotation = 0.0;
        }

        Sprite powerup = new Sprite(panel, powerup_prefab, 0.0, 0.0, scale, 200, rotation_speed);

        powerup.x = rotation_radius * Math.cos(2.0 * Math.PI * spawn_rotations[powerups.size()]);
        powerup.y = rotation_radius * Math.sin(2.0 * Math.PI * spawn_rotations[powerups.size()]);

        cloud.add_child(powerup);
        powerups.add(powerup);
    }

    public void move_all()
    {
        for (int i = 0; i < powerups.size(); i++)
        {
            double rot = 2.0 * Math.PI * (rotation + spawn_rotations[i]);

            powerups.get(i).x = cloud.x + cloud.x_mid_offset - powerups.get(i).x_mid_offset + rotation_radius * Math.cos(rot);
            powerups.get(i).y = cloud.y + cloud.y_mid_offset - powerups.get(i).y_mid_offset + rotation_radius * Math.sin(rot);

            if (Math.cos(rot) < 0.6 && Math.sin(rot) < 0.8)
            {
                if (powerups.get(i).width_scaled >= 0.0)
                    powerups.get(i).width_scaled *= -1.0;
            } else
            {
                if (powerups.get(i).width_scaled < 0.0)
                    powerups.get(i).width_scaled *= -1.0;
            }
        }

        rotation += panel.deltaTime * rotation_speed * 1e-3;

        if (rotation > 1.0)
        {
            rotation = 0.0;
        }
    }

    public void clean()
    {
        for (Sprite pickup : pickups)
        {
            if (pickup.is_outside())
            {
                pickup.to_remove = true;
            }
        }
    }
}

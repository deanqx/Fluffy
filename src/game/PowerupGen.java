package game;

import java.awt.image.BufferedImage;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

import structs.Bounds;

public class PowerupGen
{
    private GamePanel panel;
    private Sprite cloud;
    private Vector<Sprite> pickups;
    private Vector<Sprite> powerups;
    private BufferedImage[] pickup_prefab;
    private BufferedImage[] powerup_prefab;
    private Bounds bounds;
    private final double falling_speed;
    private final double rotation_speed;
    private double rotation = 0.0;

    private final double pickup_width_scaled;
    private final double pickup_height_scaled;
    private final double scale;

    public PowerupGen(GamePanel panel, Sprite cloud, Vector<Sprite> pickups, Vector<Sprite> powerups, BufferedImage[] pickup_prefab, BufferedImage[] powerup_prefab, double scale, double falling_speed, double rotation_speed)
    {
        this.panel = panel;
        this.cloud = cloud;
        this.pickups = pickups;
        this.powerups = powerups;
        this.pickup_prefab = pickup_prefab;
        this.powerup_prefab = powerup_prefab;
        // bounds = new Bounds(.0f, panel.getSize().height, .0f, panel.getSize().width);
        this.falling_speed = falling_speed;
        this.rotation_speed = rotation_speed;
        this.scale = scale;

        pickup_width_scaled = pickup_prefab[0].getWidth() * scale;
        pickup_height_scaled = pickup_prefab[0].getHeight() * scale;
        bounds = new Bounds(-pickup_height_scaled, panel.getSize().height + pickup_height_scaled, 0.0, panel.getSize().width);
    }

    public void spawn(int n)
    {
        ThreadLocalRandom t = ThreadLocalRandom.current();

        for (int i = 0; i < n; i++)
        {
            double x = t.nextDouble(1.0, bounds.right - pickup_width_scaled - 1.0);
            double y = bounds.top;

            Sprite new_pickup = new Sprite(panel, pickup_prefab, x, y, scale, bounds, 0, falling_speed);
            new_pickup.y_velocity = falling_speed;

            pickups.add(new_pickup);
        }
    }

    public void pickup()
    {
        // TODO Spawning multiple
        double x = Math.cos(2.0 * Math.PI * rotation) * (cloud.x_mid_offset - 13.0);
        double y = Math.sin(2.0 * Math.PI * rotation) * (-cloud.y_mid_offset - 6.0);

        Sprite powerup = new Sprite(panel, powerup_prefab, x, y, scale, bounds, 200, rotation_speed);

        cloud.add_child(powerup);
        powerups.add(powerup);

        if (powerups.size() == 1)
        {
            rotation = 0.0;
        }
    }

    public void move_all()
    {
        for (Sprite powerup : powerups)
        {
            // powerup.x += Math.cos(2.0f * (double) Math.PI * rotation);
            // powerup.y += Math.sin(2.0f * (double) Math.PI * rotation);
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
            if (pickup.bounds.hit)
            {
                pickup.to_remove = true;
            }
        }
    }
}

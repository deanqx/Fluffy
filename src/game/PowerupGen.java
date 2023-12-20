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

    private double rotation_with_offset(int spawn_index)
    {
        return spawn_rotations[powerups.size() - 1];
    }

    public void pickup()
    {
        if (powerups.size() == 8)
        {
            return;
        }

        Sprite powerup = new Sprite(panel, powerup_prefab, 0.0, 0.0, scale, null, 200, rotation_speed);

        if (powerups.size() == 0)
        {
            rotation = 0.0;
        }

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

            if (0.8 < rot || rot < 0.2)
            {
                System.out.println("mirror");
                if (powerups.get(i).width > 0)
                    powerups.get(i).width *= -1.0;
            } else
            {
                System.out.println("no mirror");
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
            if (pickup.bounds.hit)
            {
                pickup.to_remove = true;
            }
        }
    }
}

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
    private final float falling_speed;
    private final float rotation_speed;
    private float rotation = 0.0f;

    private final float pickup_width_scaled;
    private final float pickup_height_scaled;
    private final float scale;

    public PowerupGen(GamePanel panel, Sprite cloud, Vector<Sprite> pickups, Vector<Sprite> powerups, BufferedImage[] pickup_prefab, BufferedImage[] powerup_prefab, float scale, float falling_speed, float rotation_speed)
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
        bounds = new Bounds(-pickup_height_scaled, panel.getSize().height + pickup_height_scaled, .0f, panel.getSize().width);
    }

    public void spawn(int n)
    {
        ThreadLocalRandom t = ThreadLocalRandom.current();

        for (int i = 0; i < n; i++)
        {
            float x = t.nextFloat(1.0f, bounds.right - pickup_width_scaled - 1.0f);
            float y = bounds.top;

            Sprite new_pickup = new Sprite(panel, pickup_prefab, x, y, scale, bounds, 0, falling_speed);
            new_pickup.y_velocity = falling_speed;

            pickups.add(new_pickup);
        }
    }

    public void pickup()
    {
        float x = cloud.x_mid_offset - 13.0f;
        float y = -cloud.y_mid_offset - 6.0f;

        Sprite powerup = new Sprite(panel, powerup_prefab, x, y, scale, bounds, 200, rotation_speed);

        cloud.add_child(powerup);
        powerups.add(powerup);

        if (powerups.size() == 1)
        {
            rotation = 0.0f;
        }
    }

    public void move_all()
    {
        for (Sprite powerup : powerups)
        {
            powerup.x += Math.cos(2.0f * (float) Math.PI * rotation);
            powerup.y += Math.sin(2.0f * (float) Math.PI * rotation);
        }

        rotation += panel.deltaTime * rotation_speed * 1e-3f;

        if (rotation > 1.0f)
        {
            rotation = .0f;
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

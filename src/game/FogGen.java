package game;

import java.awt.image.BufferedImage;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

import structs.Bounds;

public class FogGen
{
    private GamePanel panel;
    private Vector<Sprite> fogs;
    private BufferedImage[] fog_prefab;
    private Bounds bounds;
    private final double min_scale;
    private final double max_scale;

    public FogGen(GamePanel panel, Vector<Sprite> fogs, BufferedImage[] fog_prefab, double min_scale, double max_scale)
    {
        this.panel = panel;
        this.fogs = fogs;
        this.fog_prefab = fog_prefab;
        // bounds = new Bounds(.0f, panel.getSize().height, .0f, panel.getSize().width);
        bounds = new Bounds(-2.0 * fog_prefab[0].getHeight() * max_scale, panel.getSize().height + fog_prefab[0].getHeight() * max_scale, -2.0 * fog_prefab[0].getWidth() * max_scale, panel.getSize().width + fog_prefab[0].getWidth() * max_scale);
        this.min_scale = min_scale;
        this.max_scale = max_scale;
    }

    public void spawn(int n, double speed)
    {
        ThreadLocalRandom t = ThreadLocalRandom.current();

        for (int i = 0; i < n; i++)
        {
            double scale = t.nextDouble(min_scale, max_scale);
            double x = t.nextDouble(bounds.left, bounds.right - fog_prefab[0].getWidth() * scale);
            double y = t.nextDouble(bounds.top, bounds.bottom - fog_prefab[0].getHeight() * scale);
            double x_vel_variance = t.nextDouble(0.8, 1.0);
            double y_vel_variance = t.nextDouble(0.8, 1.0);

            Sprite new_fog = new Sprite(panel, fog_prefab, x, y, scale, bounds, 0, speed);
            new_fog.x_velocity = speed * x_vel_variance;
            new_fog.y_velocity = speed * y_vel_variance;

            fogs.add(new_fog);
        }
    }

    public void redirect()
    {
        ThreadLocalRandom t = ThreadLocalRandom.current();

        for (Sprite fog : fogs)
        {
            if (!fog.bounds.hit)
            {
                continue;
            }

            boolean top_or_left = t.nextInt(0, 2) == 1;

            if (top_or_left)
            {
                fog.x = (double) t.nextDouble(bounds.left, bounds.right - fog.width_scaled);
                fog.y = bounds.top + 1.0;
            } else
            {
                fog.x = bounds.left + 1.0;
                fog.y = (double) t.nextDouble(bounds.top, bounds.bottom - fog.height_scaled);
            }

            double x_vel_variance = t.nextDouble(0.8, 1.0);
            double y_vel_variance = t.nextDouble(0.8, 1.0);

            fog.x_velocity = fog.speed * x_vel_variance;
            fog.y_velocity = fog.speed * y_vel_variance;
        }
    }
}

package game;

import java.awt.image.BufferedImage;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class FogGen
{
    private GamePanel panel;
    private Vector<Sprite> actors;
    private BufferedImage[] fog_prefab;
    private Bounds bounds;
    private final float min_scale;
    private final float max_scale;

    public FogGen(GamePanel panel, Vector<Sprite> actors, BufferedImage[] fog_prefab, float min_scale, float max_scale)
    {
        this.panel = panel;
        this.actors = actors;
        this.fog_prefab = fog_prefab;
        // bounds = new Bounds(.0f, panel.getSize().height, .0f, panel.getSize().width);
        bounds = new Bounds(-2.0f * fog_prefab[0].getHeight() * max_scale, panel.getSize().height + fog_prefab[0].getHeight() * max_scale, -2.0f * fog_prefab[0].getWidth() * max_scale, panel.getSize().width + fog_prefab[0].getWidth() * max_scale);
        this.min_scale = min_scale;
        this.max_scale = max_scale;
    }

    public void spawn_fog(int n, float speed)
    {
        ThreadLocalRandom t = ThreadLocalRandom.current();

        for (int i = 0; i < n; i++)
        {
            float scale = t.nextFloat(min_scale, max_scale);
            float x = t.nextFloat(bounds.left, bounds.right - fog_prefab[0].getWidth() * scale);
            float y = t.nextFloat(bounds.top, bounds.bottom - fog_prefab[0].getHeight() * scale);
            float x_vel_variance = t.nextFloat(0.8f, 1.0f);
            float y_vel_variance = t.nextFloat(0.8f, 1.0f);

            Sprite new_fog = new Sprite(panel, Sprite.Tag.Fog, fog_prefab, x, y, scale, bounds, 0, speed);
            new_fog.x_velocity = speed * x_vel_variance;
            new_fog.y_velocity = speed * y_vel_variance;

            actors.add(new_fog);
        }
    }

    public void redirect_fog()
    {
        ThreadLocalRandom t = ThreadLocalRandom.current();

        int i = 0;
        for (Sprite fog : actors)
        {
            if (fog.tag == Sprite.Tag.Fog && fog.bounds.hit)
            {
                boolean top_or_left = t.nextInt(0, 2) == 1;

                if (top_or_left)
                {
                    fog.x = (double) t.nextFloat(bounds.left, bounds.right - fog.width_scaled);
                    fog.y = bounds.top + 1.0f;
                } else
                {
                    fog.x = bounds.left + 1.0f;
                    fog.y = (double) t.nextFloat(bounds.top, bounds.bottom - fog.height_scaled);
                }

                float x_vel_variance = t.nextFloat(0.8f, 1.0f);
                float y_vel_variance = t.nextFloat(0.8f, 1.0f);

                fog.x_velocity = fog.speed * x_vel_variance;
                fog.y_velocity = fog.speed * y_vel_variance;
                i++;
            }
        }
    }
}

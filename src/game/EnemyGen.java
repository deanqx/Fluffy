package game;

import java.awt.image.BufferedImage;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class EnemyGen
{
    private GamePanel panel;
    private Vector<Sprite> actors;
    private BufferedImage[] enemy_prefab;
    private Bounds bounds;
    private final float scale;

    public EnemyGen(GamePanel panel, Vector<Sprite> actors, BufferedImage[] enemy_prefab, float scale)
    {
        this.panel = panel;
        this.actors = actors;
        this.enemy_prefab = enemy_prefab;
        // bounds = new Bounds(.0f, panel.getSize().height, .0f, panel.getSize().width);
        bounds = new Bounds(-enemy_prefab[0].getHeight() * scale, panel.getSize().height + enemy_prefab[0].getHeight() * scale, .0f, panel.getSize().width);
        this.scale = scale;
    }

    public void spawn(int n, float speed)
    {
        ThreadLocalRandom t = ThreadLocalRandom.current();

        for (int i = 0; i < n; i++)
        {
            float x = t.nextFloat(1.0f, bounds.right - enemy_prefab[0].getWidth() * scale - 1.0f);
            float y = bounds.top;

            Sprite new_enemy = new Sprite(panel, Sprite.Tag.Fog, enemy_prefab, x, y, scale, bounds, 500, speed);
            new_enemy.y_velocity = speed;

            actors.add(new_enemy);
        }
    }

    public void clean()
    {
        for (Sprite enemy : actors)
        {
            if (enemy.tag == Sprite.Tag.Enemy && enemy.bounds.hit)
            {
                enemy.to_remove = true;
            }
        }
    }
}

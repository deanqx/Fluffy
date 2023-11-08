package game;

import java.awt.image.BufferedImage;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

import structs.Bounds;

public class EnemyGen
{
    private GamePanel panel;
    private Vector<Sprite> enemys;
    private BufferedImage[] enemy_prefab;
    private Bounds bounds;
    private final float scale;
    private float speed;

    public EnemyGen(GamePanel panel, Vector<Sprite> enemys, BufferedImage[] enemy_prefab, float scale, float speed)
    {
        this.panel = panel;
        this.enemys = enemys;
        this.enemy_prefab = enemy_prefab;
        // bounds = new Bounds(.0f, panel.getSize().height, .0f, panel.getSize().width);
        bounds = new Bounds(-4.0f * enemy_prefab[0].getHeight() * scale, panel.getSize().height + enemy_prefab[0].getHeight() * scale, .0f, panel.getSize().width);
        this.scale = scale;
        this.speed = speed;
    }

    public void spawn(int n)
    {
        ThreadLocalRandom t = ThreadLocalRandom.current();

        for (int i = 0; i < n; i++)
        {
            float x = t.nextFloat(1.0f, bounds.right - enemy_prefab[0].getWidth() * scale - 1.0f);
            float y = t.nextFloat(bounds.top, -enemy_prefab[0].getHeight() * scale);

            Sprite new_enemy = new Sprite(panel, enemy_prefab, x, y, scale, bounds, 500, speed);
            new_enemy.y_velocity = speed;

            enemys.add(new_enemy);
        }
    }

    public void reposition()
    {
        ThreadLocalRandom t = ThreadLocalRandom.current();

        for (Sprite enemy : enemys)
        {
            if (enemy.bounds.hit)
            {
                enemy.x = t.nextFloat(1.0f, bounds.right - enemy_prefab[0].getWidth() * scale - 1.0f);
                enemy.y = t.nextFloat(bounds.top, -enemy_prefab[0].getHeight() * scale);
                enemy.speed = speed;
                enemy.y_velocity = speed;
            }
        }
    }
}

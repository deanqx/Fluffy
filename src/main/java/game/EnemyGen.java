package game;

import java.awt.image.BufferedImage;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class EnemyGen
{
    private GamePanel panel;
    private Vector<Sprite> enemys;
    private BufferedImage[] enemy_prefab;
    public double speed;
     
    private final double enemy_width_scaled;
    private final double enemy_height_scaled;
    private final double scale;

    public EnemyGen(GamePanel panel, Vector<Sprite> enemys, BufferedImage[] enemy_prefab, double scale, double speed)
    {
        this.panel = panel;
        this.enemys = enemys;
        this.enemy_prefab = enemy_prefab;
        this.scale = scale;
        this.speed = speed;
        enemy_width_scaled = enemy_prefab[0].getWidth() * scale;
        enemy_height_scaled = enemy_prefab[0].getHeight() * scale;
    }

    public void spawn(int n)
    {
        ThreadLocalRandom t = ThreadLocalRandom.current();

        for (int i = 0; i < n; i++)
        {
            double x = t.nextDouble(1.0, panel.width - enemy_width_scaled - 1.0);
            double y = enemy_height_scaled * -scale;

            Sprite new_enemy = new Sprite(panel, enemy_prefab, x, y, scale, 500.0, speed);
            new_enemy.y_velocity = speed;

            enemys.add(new_enemy);
        }
    }

    public void reposition()
    {
        ThreadLocalRandom t = ThreadLocalRandom.current();

        for (Sprite enemy : enemys)
        {
            if (enemy.is_outside())
            {
                enemy.x = t.nextDouble(1.0, panel.width - enemy_width_scaled - 1.0);
                enemy.y = enemy_height_scaled * -scale;
                enemy.speed = speed;
                enemy.y_velocity = speed;
            }
        }
    }
}

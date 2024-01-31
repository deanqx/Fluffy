package game;

import java.awt.image.BufferedImage;
import java.util.concurrent.ThreadLocalRandom;

public class EnemyGen
{
    private GamePanel panel;
    private BufferedImage[] enemy_prefab;
    public double speed;
     
    public EnemyGen(GamePanel panel, BufferedImage[] enemy_prefab, double speed)
    {
        this.panel = panel;
        this.enemy_prefab = enemy_prefab;
        this.speed = speed;
    }

    public void spawn(int n)
    {
        ThreadLocalRandom t = ThreadLocalRandom.current();

        for (int i = 0; i < n; i++)
        {
            double x = t.nextDouble(1.0, panel.width - enemy_prefab[0].getHeight());
            double y = -enemy_prefab[0].getHeight();

            Sprite new_enemy = new Sprite(panel, enemy_prefab, x, y, 500.0, speed);
            new_enemy.y_velocity = speed;

            panel.enemys.add(new_enemy);
        }
    }
}

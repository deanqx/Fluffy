package game;

import java.awt.image.BufferedImage;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class EnemyGen {
    private GamePanel panel;
    private Vector<Sprite> enemies;
    private BufferedImage[] enemyPrefab;
    private final double enemyWidthScaled;
    private final double enemyHeightScaled;
    private final double scale;

    public double speed;

    public EnemyGen(GamePanel panel, Vector<Sprite> enemies, BufferedImage[] enemy_prefab, double scale, double speed) {
        this.panel = panel;
        this.enemies = enemies;
        this.scale = scale;
        this.speed = speed;
        this.enemyPrefab = enemy_prefab;

        enemyWidthScaled = enemy_prefab[0].getWidth() * scale;
        enemyHeightScaled = enemy_prefab[0].getHeight() * scale;
    }

    public void spawn(int amount) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();

        for (int i = 0; i < amount; i++) {
            double x = rng.nextDouble(1.0, panel.width - enemyWidthScaled - 1.0);
            double y = enemyHeightScaled * -scale;

            Sprite new_enemy = new Sprite(panel, enemyPrefab, x, y, scale, 500.0, speed);
            new_enemy.yVelocity = speed;

            enemies.add(new_enemy);
        }
    }

    public void reuseOutOfBounds() {
        ThreadLocalRandom rng = ThreadLocalRandom.current();

        for (Sprite enemy : enemies) {
            if (enemy.isOutOfBounds()) {
                enemy.x = rng.nextDouble(1.0, panel.width - enemyWidthScaled - 1.0);
                enemy.y = enemyHeightScaled * -scale;
                enemy.speed = speed;
                enemy.yVelocity = speed;
            }
        }
    }
}

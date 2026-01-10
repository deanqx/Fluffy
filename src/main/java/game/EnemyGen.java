package game;

import java.awt.image.BufferedImage;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class EnemyGen {
    private GamePanel panel;
    private Vector<Sprite> enemies;
    private BufferedImage[] enemyPrefab;
    private final float enemyWidthScaled;
    private final float enemyHeightScaled;
    private final float scale;

    public float speed;

    public EnemyGen(GamePanel panel, Vector<Sprite> enemies, BufferedImage[] enemy_prefab, float scale, float speed) {
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
            float x = rng.nextFloat(1.0f, panel.width - enemyWidthScaled - 1.0f);
            float y = enemyHeightScaled * -scale;

            Sprite new_enemy = new Sprite(panel, enemyPrefab, x, y, scale, 500.0f, speed);
            new_enemy.yVelocity = speed;

            enemies.add(new_enemy);
        }
    }

    public void reuseOutOfBounds() {
        ThreadLocalRandom rng = ThreadLocalRandom.current();

        for (Sprite enemy : enemies) {
            if (enemy.isOutOfBounds()) {
                enemy.x = rng.nextFloat(1.0f, panel.width - enemyWidthScaled - 1.0f);
                enemy.y = enemyHeightScaled * -scale;
                enemy.speed = speed;
                enemy.yVelocity = speed;
            }
        }
    }
}

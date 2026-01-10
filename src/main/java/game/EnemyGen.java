package game;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class EnemyGen {
    private final GamePanel panel;
    private final ArrayList<Sprite> enemies;
    private final Prefab enemyPrefab;
    private final float enemyWidthScaled;
    private final float enemyHeightScaled;
    private final float scale;
    private float speed;

    public EnemyGen(final GamePanel panel, final ArrayList<Sprite> enemies, final Prefab enemy_prefab,
            final float scale, final float speed) {
        this.panel = panel;
        this.enemies = enemies;
        this.scale = scale;
        this.speed = speed;
        this.enemyPrefab = enemy_prefab;

        enemyWidthScaled = enemy_prefab.getImage(0).getWidth() * scale;
        enemyHeightScaled = enemy_prefab.getImage(0).getHeight() * scale;
    }

    public void spawn(final int amount) {
        final ThreadLocalRandom rng = ThreadLocalRandom.current();

        for (int i = 0; i < amount; i++) {
            final float x = rng.nextFloat(1.0f, panel.getGameWidth() - enemyWidthScaled - 1.0f);
            final float y = enemyHeightScaled * -scale;

            final Sprite new_enemy = new Sprite(panel, enemyPrefab, x, y, scale, 500.0f, speed);
            new_enemy.yVelocity = speed;

            enemies.add(new_enemy);
        }
    }

    public void reuseOutOfBounds() {
        final ThreadLocalRandom rng = ThreadLocalRandom.current();

        for (final Sprite enemy : enemies) {
            if (enemy.isOutOfBounds()) {
                enemy.x = rng.nextFloat(1.0f, panel.getGameWidth() - enemyWidthScaled - 1.0f);
                enemy.y = enemyHeightScaled * -scale;
                enemy.speed = speed;
                enemy.yVelocity = speed;
            }
        }
    }

    public void setSpeed(final float speed) {
        this.speed = speed;
    }
}

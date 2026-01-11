package fluffy;

import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class EnemyGen {
    private final GamePanel panel;
    private final Prefab enemyPrefab;
    private final float enemyWidthScaled;
    private final float enemyHeightScaled;
    private final float scale;

    private float speed = 0.0f;

    public EnemyGen(final GamePanel panel, final Prefab enemy_prefab, final float scale) {
        this.panel = panel;
        this.scale = scale;
        this.enemyPrefab = enemy_prefab;

        enemyWidthScaled = enemy_prefab.getImage(0).getWidth() * scale;
        enemyHeightScaled = enemy_prefab.getImage(0).getHeight() * scale;
    }

    public void spawn(final int amount) {
        final ThreadLocalRandom rng = ThreadLocalRandom.current();

        for (int i = 0; i < amount; i++) {
            final float x = rng.nextFloat(1.0f, panel.getGameWidth() - enemyWidthScaled - 1.0f);
            final float y = enemyHeightScaled * -scale;

            final Sprite new_enemy = new Enemy(panel, enemyPrefab);
            new_enemy.setX(x);
            new_enemy.setY(y);
            new_enemy.setSpriteScale(scale);
            new_enemy.setAnimationImageTime(500.0f);
            new_enemy.setSpeed(speed);
            new_enemy.yVelocity = speed;

            panel.addObject(new_enemy);
        }
    }

    public void reuseOutOfBounds() {
        final ThreadLocalRandom rng = ThreadLocalRandom.current();

        for (final Iterator<GameObject> objects_it = panel.iterateObjects(); objects_it.hasNext();) {
            if (objects_it.next() instanceof final Enemy enemy) {
                if (enemy.isOutOfBounds()) {
                    enemy.x = rng.nextFloat(1.0f, panel.getGameWidth() - enemyWidthScaled - 1.0f);
                    enemy.y = enemyHeightScaled * -scale;
                    enemy.speed = speed;
                    enemy.yVelocity = speed;
                }
            }
        }
    }

    public void setSpeed(final float speed) {
        this.speed = speed;
    }
}

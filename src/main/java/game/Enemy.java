package game;

public class Enemy extends Sprite {
    public Enemy(GamePanel panel, Prefab prefab, float x, float y, float scale, float each_image_duration,
            float speed) {
        super(panel, prefab, x, y, scale, each_image_duration, speed);
    }
}

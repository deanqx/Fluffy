package game;

public class Character extends Sprite {
    public Character(GamePanel panel, Prefab prefab, float x, float y, float scale, float each_image_duration,
            float speed, float custom_radius_factor, float custom_x_mid_factor, float custom_y_mid_factor) {
        super(panel, prefab, x, y, scale, each_image_duration, speed, custom_radius_factor, custom_x_mid_factor,
                custom_y_mid_factor);
    }
}

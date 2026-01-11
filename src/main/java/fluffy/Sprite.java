package fluffy;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class Sprite extends GameObject {
    /// x middle offset
    float xMidOffset;
    /// y middle offset
    float yMidOffset;
    float xVelocity;
    float yVelocity;
    float widthScaled;
    float heightScaled;
    float speed;
    private final float local_scale;
    private float hitboxRadius;

    private final float customRadiusFactor;
    private final float customXMidFactor;
    private final float customYNidFactor;

    private final GamePanel panel;
    private final Prefab prefab;
    private final float each_image_duration;
    private float current_image_time = 0.0f;
    private int currentImageIndex = 0;
    private final ArrayList<Sprite> childs = new ArrayList<>();
    private final ArrayList<Gizmo> gizmos = new ArrayList<>();

    public void rescale() {
        widthScaled = width * local_scale;
        heightScaled = height * local_scale;
        xMidOffset = widthScaled / 2.0f * customXMidFactor;
        yMidOffset = heightScaled / 2.0f * customYNidFactor;
        hitboxRadius = Math.max(widthScaled, heightScaled) / 2.0f * customRadiusFactor;
    }

    public Sprite(final GamePanel panel, final Prefab prefab, final float x, final float y, final float scale,
            final float each_image_duration, final float speed) {
        this(panel, prefab, x, y, scale, each_image_duration, speed, 1.0f, 1.0f, 1.0f);
    }

    // TODO reduce parameter count
    public Sprite(final GamePanel panel, final Prefab prefab, final float x, final float y, final float scale,
            final float each_image_duration, final float speed,
            final float custom_radius_factor, final float custom_x_mid_factor, final float custom_y_mid_factor) {
        this.x = x;
        this.y = y;
        this.width = prefab.getImage(0).getWidth();
        this.height = prefab.getImage(0).getHeight();

        this.panel = panel;
        this.speed = speed;
        this.local_scale = scale;
        this.prefab = prefab;
        this.each_image_duration = each_image_duration;
        this.customRadiusFactor = custom_radius_factor;
        this.customXMidFactor = custom_x_mid_factor;
        this.customYNidFactor = custom_y_mid_factor;

        rescale();

        var hitboxGizmo = new Gizmo(
                new Rectangle2D.Float(xMidOffset - hitboxRadius, yMidOffset - hitboxRadius,
                        2.0f * hitboxRadius, 2.0f * hitboxRadius),
                Color.MAGENTA, Gizmo.Shape.OVAL);
        addGizmo(hitboxGizmo);
    }

    public boolean hasCollided(final Sprite to) {
        final float a = (to.x + to.xMidOffset) - (x + xMidOffset);
        final float b = (to.y + to.yMidOffset) - (y + yMidOffset);

        // Pythagorean theorem
        final float center_distance = (float) Math.sqrt(a * a + b * b);
        final float center_distance_from_hitbox = center_distance - hitboxRadius - to.hitboxRadius;

        return center_distance_from_hitbox <= 0.0f;
    }

    public boolean isOutOfBounds() {
        if (x > panel.getGameWidth()) {
            return true;
        } else if (y > panel.getGameHeight()) {
            return true;
        }

        return false;
    }

    private void advanceAnimation() {
        currentImageIndex = (currentImageIndex + 1) % prefab.getImageCount();
    }

    public void draw(final Graphics g) {
        if (!visible || toRemove)
            return;

        final float _x = widthScaled > 0.0f ? x : x - widthScaled;

        g.drawImage(prefab.getImage(currentImageIndex), (int) (_x * panel.getScale()), (int) (y * panel.getScale()),
                (int) (widthScaled * panel.getScale()), (int) (heightScaled * panel.getScale()), null);
    }

    public void drawGizmos(final Graphics g) {
        for (Gizmo gizmo : gizmos) {
            gizmo.drawGizmo(g, panel.getScale());
        }
    }

    public void update() {
        if (prefab.getImageCount() <= 1) {
            return;
        }

        current_image_time += panel.getDeltaTimeMs();

        if (current_image_time > each_image_duration) {
            current_image_time = 0.0f;
            advanceAnimation();
        }
    }

    public void addChild(final Sprite child) {
        child.x += x + xMidOffset - child.xMidOffset;
        child.y += y + yMidOffset - child.yMidOffset;
        childs.add(child);
    }

    public void move() {
        final float x_moved = xVelocity * panel.getDeltaTimeMs();
        final float y_moved = yVelocity * panel.getDeltaTimeMs();

        x += x_moved;
        y += y_moved;

        for (final Sprite child : childs) {
            child.x += x_moved;
            child.y += y_moved;
        }
    }

    public void addGizmo(Gizmo gizmo) {
        gizmo.setParent(this);
        gizmos.add(gizmo);
    }
}

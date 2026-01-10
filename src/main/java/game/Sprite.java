package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

public class Sprite extends Rectangle2D.Float {
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

    boolean visible = true;
    boolean toRemove = false;

    private final float customRadiusFactor;
    private final float customXMidFactor;
    private final float customYNidFactor;

    private final GamePanel panel;
    private final Prefab prefab;
    // Time between images
    private final float delay;
    private float current_animation_time = 0.0f;
    private int currentImageIndex = 0;
    private final Vector<Sprite> childs = new Vector<>();

    // TODO replace Vector with ArrayList
    private final Vector<Gizmo> gizmos = new Vector<>();

    public void rescale() {
        widthScaled = width * local_scale;
        heightScaled = height * local_scale;
        xMidOffset = widthScaled / 2.0f * customXMidFactor;
        yMidOffset = heightScaled / 2.0f * customYNidFactor;
        hitboxRadius = Math.max(widthScaled, heightScaled) / 2.0f * customRadiusFactor;
    }

    public Sprite(final GamePanel panel, final Prefab prefab, final float x, final float y, final float scale,
            final float delay, final float speed) {
        this(panel, prefab, x, y, scale, delay, speed, 1.0f, 1.0f, 1.0f);
    }

    // TODO reduce parameter count
    public Sprite(final GamePanel panel, final Prefab prefab, final float x, final float y, final float scale,
            final float delay, final float speed,
            final float custom_radius_factor, final float custom_x_mid_factor, final float custom_y_mid_factor) {
        this.x = x;
        this.y = y;
        this.width = prefab.getImage(0).getWidth();
        this.height = prefab.getImage(0).getHeight();

        this.panel = panel;
        this.speed = speed;
        this.local_scale = scale;
        this.prefab = prefab;
        this.delay = delay;
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

    // TODO replace with hasCollided()
    public float distance(final Sprite to) {
        final float a = (to.x + to.xMidOffset) - (x + xMidOffset);
        final float b = (to.y + to.yMidOffset) - (y + yMidOffset);

        // Pythagorean theorem
        final float center_distance = (float) Math.sqrt(a * a + b * b);

        return center_distance - hitboxRadius - to.hitboxRadius;
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

        current_animation_time += panel.getDeltaTime();

        if (current_animation_time > delay) {
            current_animation_time = 0.0f;
            advanceAnimation();
        }
    }

    public void addChild(final Sprite child) {
        child.x += x + xMidOffset - child.xMidOffset;
        child.y += y + yMidOffset - child.yMidOffset;
        childs.add(child);
    }

    public void move() {
        final float x_moved = xVelocity * panel.getDeltaTime();
        final float y_moved = yVelocity * panel.getDeltaTime();

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

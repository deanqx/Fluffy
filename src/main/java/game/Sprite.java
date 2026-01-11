package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class Sprite extends GameObject {
    private final GamePanel panel;
    private final Prefab prefab;
    private final float customRadiusFactor;
    private final float customXMidFactor;
    private final float customYMidFactor;
    private final ArrayList<Sprite> childs = new ArrayList<>();
    private final ArrayList<Gizmo> gizmos = new ArrayList<>();

    private float local_scale = 1.0f;
    private float hitboxRadius = 0.0f;
    private float timePerImageMs = 500.0f;
    private float currentImageTimeMs = 0.0f;
    private int currentImageIndex = 0;

    // TODO make private
    /// x middle offset
    float xMidOffset = 0.0f;
    /// y middle offset
    float yMidOffset = 0.0f;
    float xVelocity = 0.0f;
    float yVelocity = 0.0f;
    float widthScaled = 0.0f;
    float heightScaled = 0.0f;
    float speed = 0.0f;

    public Sprite(final GamePanel panel, final Prefab prefab) {
        this(panel, prefab, 1.0f, 1.0f, 1.0f);
    }

    public Sprite(final GamePanel panel, final Prefab prefab, float customRadiusFactor, float customXMidFactor,
            float customYMidFactor) {
        this.width = prefab.getImage(0).getWidth();
        this.height = prefab.getImage(0).getHeight();
        this.panel = panel;
        this.prefab = prefab;
        this.customXMidFactor = customXMidFactor;
        this.customYMidFactor = customYMidFactor;
        this.customRadiusFactor = customRadiusFactor;
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

        currentImageTimeMs += panel.getDeltaTimeMs();

        if (currentImageTimeMs > timePerImageMs) {
            currentImageTimeMs = 0.0f;
            advanceAnimation();
        }
    }

    public void addChild(final Sprite child) {
        child.x += x + xMidOffset - child.xMidOffset;
        child.y += y + yMidOffset - child.yMidOffset;
        childs.add(child);
    }

    public void move() {
        final float new_x = xVelocity * panel.getDeltaTimeMs();
        final float new_y = yVelocity * panel.getDeltaTimeMs();

        x += new_x;
        y += new_y;

        for (final Sprite child : childs) {
            child.x += new_x;
            child.y += new_y;
        }
    }

    public void addGizmo(Gizmo gizmo) {
        gizmo.setParent(this);
        gizmos.add(gizmo);
    }

    public void setSpriteScale(float scale) {
        this.local_scale = scale;

        widthScaled = width * local_scale;
        heightScaled = height * local_scale;
        xMidOffset = widthScaled / 2.0f * customXMidFactor;
        yMidOffset = heightScaled / 2.0f * customYMidFactor;
        hitboxRadius = Math.max(widthScaled, heightScaled) / 2.0f * customRadiusFactor;

        var hitboxGizmo = new Gizmo(
                new Rectangle2D.Float(xMidOffset - hitboxRadius, yMidOffset - hitboxRadius,
                        2.0f * hitboxRadius, 2.0f * hitboxRadius),
                Color.MAGENTA, Gizmo.Shape.OVAL);
        addGizmo(hitboxGizmo);
    }

    public void setAnimationImageTime(float time_per_image) {
        this.timePerImageMs = time_per_image;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}

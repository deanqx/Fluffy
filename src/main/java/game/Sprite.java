package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Vector;

public class Sprite extends Rectangle2D.Float {
    float speed;
    // x middle offset
    float xMidOffset;
    // y middle offset
    float yMidOffset;
    float xVelocity;
    float yVelocity;
    float scale;
    float widthScaled;
    float heightScaled;
    float radius;
    boolean visible = true;
    boolean toRemove = false;

    float customRadiusFactor;
    float customXMidFactor;
    float customYNidFactor;

    private GamePanel panel;
    // Time between images
    private float delay;
    private float animation = 0.0f;
    private BufferedImage[] pics;
    private int currentPic = 0;
    private Vector<Sprite> childs = new Vector<>();

    // TODO move into class
    private Vector<Color> gizmoColors = new Vector<>();
    private Vector<java.lang.Float> gizmoXs = new Vector<>();
    private Vector<java.lang.Float> gizmoYs = new Vector<>();
    private Vector<java.lang.Float> gizmoRotations = new Vector<>();

    public void rescale() {
        widthScaled = width * scale;
        heightScaled = height * scale;
        xMidOffset = widthScaled / 2.0f * customXMidFactor;
        yMidOffset = heightScaled / 2.0f * customYNidFactor;
        radius = Math.max(widthScaled, heightScaled) / 2.0f * customRadiusFactor;
    }

    public Sprite(GamePanel p, BufferedImage[] imgs, float x, float y, float scale, float delay, float speed) {
        this(p, imgs, x, y, scale, delay, speed, 1.0f, 1.0f, 1.0f);
    }

    public Sprite(GamePanel p, BufferedImage[] imgs, float x, float y, float scale, float delay, float speed,
            float custom_radius_factor, float custom_x_mid_factor, float custom_y_mid_factor) {
        panel = p;
        this.speed = speed;
        this.scale = scale;
        pics = imgs;
        this.x = x;
        this.y = y;
        this.delay = delay;
        width = pics[0].getWidth();
        height = pics[0].getHeight();
        this.customRadiusFactor = custom_radius_factor;
        this.customXMidFactor = custom_x_mid_factor;
        this.customYNidFactor = custom_y_mid_factor;

        rescale();
        addGizmoCircle(Color.MAGENTA, (int) xMidOffset, (int) yMidOffset, (int) radius);
    }

    public float distance(Sprite to) {
        float a = (to.x + to.xMidOffset) - (x + xMidOffset);
        float b = (to.y + to.yMidOffset) - (y + yMidOffset);

        // Pythagorean theorem
        float center_distance = (float) Math.sqrt(a * a + b * b);

        return center_distance - radius - to.radius;
    }

    public boolean isOutOfBounds() {
        if (x > panel.width) {
            return true;
        } else if (y > panel.height) {
            return true;
        }

        return false;
    }

    private void advanceAnimation() {
        currentPic++;

        if (currentPic >= pics.length) {
            currentPic = 0;
        }
    }

    public void draw(Graphics g) {
        if (!visible || toRemove)
            return;

        float _x;

        if (widthScaled > 0.0f)
            _x = x;
        else
            _x = x - widthScaled;

        g.drawImage(pics[currentPic], (int) (_x * panel.scale), (int) (y * panel.scale),
                (int) (widthScaled * panel.scale), (int) (heightScaled * panel.scale), null);
    }

    void drawCircle(Graphics g, Color c, float x_center, float y_center, float r) {
        g.setColor(c);
        g.drawOval((int) ((x_center - r) * panel.scale), (int) ((y_center - r) * panel.scale),
                (int) (r * 2.0f * panel.scale), (int) (r * 2.0f * panel.scale));
    }

    public void addGizmoCircle(Color c, float x_center, float y_center, float r) {
        gizmoColors.add(c);
        gizmoXs.add(x_center);
        gizmoYs.add(y_center);
        gizmoRotations.add(r);
    }

    public void drawGizmos(Graphics g) {
        for (int i = 0; i < gizmoXs.size(); i++) {
            drawCircle(g, gizmoColors.get(i), x + gizmoXs.get(i), y + gizmoYs.get(i), gizmoRotations.get(i));
        }
    }

    public void update() {
        if (pics.length > 1) {
            animation += panel.deltaTime;

            if (animation > delay) {
                animation = 0.0f;
                advanceAnimation();
            }
        }
    }

    public void addChild(Sprite child) {
        child.x += x + xMidOffset - child.xMidOffset;
        child.y += y + yMidOffset - child.yMidOffset;
        childs.add(child);
    }

    public void move() {
        float x_moved = xVelocity * panel.deltaTime;
        float y_moved = yVelocity * panel.deltaTime;

        x += x_moved;
        y += y_moved;

        for (Sprite child : childs) {
            child.x += x_moved;
            child.y += y_moved;
        }
    }
}

package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Vector;

public class Sprite extends Rectangle2D.Double {
    double speed;
    // x middle offset
    double xMidOffset;
    // y middle offset
    double yMidOffset;
    double xVelocity;
    double yVelocity;
    double scale;
    double widthScaled;
    double heightScaled;
    double radius;
    boolean visible = true;
    boolean toRemove = false;

    double customRadiusFactor;
    double customXMidFactor;
    double customYNidFactor;

    private GamePanel panel;
    // Time between images
    private double delay;
    private double animation = 0.0f;
    private BufferedImage[] pics;
    private int currentPic = 0;
    private Vector<Sprite> childs = new Vector<>();

    // TODO move into class
    private Vector<Color> gizmoColors = new Vector<>();
    private Vector<java.lang.Double> gizmoXs = new Vector<>();
    private Vector<java.lang.Double> gizmoYs = new Vector<>();
    private Vector<java.lang.Double> gizmoRotations = new Vector<>();

    public void rescale() {
        widthScaled = width * scale;
        heightScaled = height * scale;
        xMidOffset = widthScaled / 2.0 * customXMidFactor;
        yMidOffset = heightScaled / 2.0 * customYNidFactor;
        radius = Math.max(widthScaled, heightScaled) / 2.0 * customRadiusFactor;
    }

    public Sprite(GamePanel p, BufferedImage[] imgs, double x, double y, double scale, double delay, double speed) {
        this(p, imgs, x, y, scale, delay, speed, 1.0, 1.0, 1.0);
    }

    public Sprite(GamePanel p, BufferedImage[] imgs, double x, double y, double scale, double delay, double speed,
            double custom_radius_factor, double custom_x_mid_factor, double custom_y_mid_factor) {
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

    public double distance(Sprite to) {
        double a = (to.x + to.xMidOffset) - (x + xMidOffset);
        double b = (to.y + to.yMidOffset) - (y + yMidOffset);

        return Math.sqrt(a * a + b * b) - radius - to.radius;
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

        double _x;

        if (widthScaled > 0.0)
            _x = x;
        else
            _x = x - widthScaled;

        g.drawImage(pics[currentPic], (int) (_x * panel.scale), (int) (y * panel.scale),
                (int) (widthScaled * panel.scale), (int) (heightScaled * panel.scale), null);
    }

    void drawCircle(Graphics g, Color c, double x_center, double y_center, double r) {
        g.setColor(c);
        g.drawOval((int) ((x_center - r) * panel.scale), (int) ((y_center - r) * panel.scale),
                (int) (r * 2.0 * panel.scale), (int) (r * 2.0 * panel.scale));
    }

    public void addGizmoCircle(Color c, double x_center, double y_center, double r) {
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
        double x_moved = xVelocity * panel.deltaTime;
        double y_moved = yVelocity * panel.deltaTime;

        x += x_moved;
        y += y_moved;

        for (Sprite child : childs) {
            child.x += x_moved;
            child.y += y_moved;
        }
    }
}

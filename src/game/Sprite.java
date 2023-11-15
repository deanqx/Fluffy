package game;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Vector;

import structs.Bounds;

public class Sprite extends Rectangle2D.Double
{
    protected final double scale;
    protected double speed;
    protected double x_mid_offset;
    protected double y_mid_offset;
    protected double x_velocity;
    protected double y_velocity;
    protected final double width_scaled;
    protected final double height_scaled;
    protected Bounds bounds;
    protected double radius;
    protected boolean to_remove = false;

    private GamePanel panel;
    // Time between images
    private double delay;
    private double animation = 0.0f;
    private BufferedImage[] pics;
    private int current_pic = 0;
    private Vector<Sprite> childs = new Vector<Sprite>();

    public Sprite(GamePanel p, BufferedImage[] imgs, double x, double y, double scale, Bounds b, int delay, double speed)
    {
        this.speed = speed;
        pics = imgs;
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.delay = (double) delay;
        width = pics[0].getWidth();
        height = pics[0].getHeight();
        width_scaled = (double) width * scale;
        height_scaled = (double) height * scale;
        x_mid_offset = width_scaled / 2.0f;
        y_mid_offset = height_scaled / 2.0f;
        radius = Math.max(width_scaled, height_scaled) / 2.0f;
        panel = p;
        bounds = b.clone();
    }

    public double distance(Sprite to)
    {
        double a = ((double) to.x + to.x_mid_offset) - ((double) x + x_mid_offset);
        double b = ((double) to.y + to.y_mid_offset) - ((double) y + y_mid_offset);

        return (double) Math.sqrt(a * a + b * b) - radius - to.radius;
    }

    private void compute_animation()
    {
        current_pic++;

        if (current_pic >= pics.length)
        {
            current_pic = 0;
        }
    }

    public void draw_objects(Graphics g)
    {
        g.drawImage(pics[current_pic], (int) x, (int) y, (int) width_scaled, (int) height_scaled, null);
    }

    public void draw_gizmos(Graphics g)
    {
        g.drawOval((int) (x + x_mid_offset - radius), (int) (y + y_mid_offset - radius), (int) radius * 2, (int) radius * 2);
    }

    public void check_bounds()
    {
        if (bounds.hit && x > bounds.left && y > bounds.top && x + width_scaled < bounds.right && y + height_scaled < bounds.bottom)
        {
            bounds.hit = false;
            return;
        }

        if (x < bounds.left)
        {
            x = bounds.left;
            bounds.hit = true;
        }

        if (y < bounds.top)
        {
            y = bounds.top;
            bounds.hit = true;
        }

        if (x + width_scaled > bounds.right)
        {
            x = bounds.right - width_scaled;
            bounds.hit = true;
        }

        if (y + height_scaled > bounds.bottom)
        {
            y = bounds.bottom - height_scaled;
            bounds.hit = true;
        }
    }

    public void update()
    {
        if (pics.length > 1)
        {
            animation += panel.deltaTime;

            if (animation > delay)
            {
                animation = 0.0f;
                compute_animation();
            }
        }

        check_bounds();
    }

    public void add_child(Sprite child)
    {
        child.x += x;
        child.y += y;
        childs.add(child);
    }

    public void move()
    {
        double x_moved = x_velocity * panel.deltaTime;
        double y_moved = y_velocity * panel.deltaTime;

        x += x_moved;
        y += y_moved;

        for (Sprite child : childs)
        {
            child.x += x_moved;
            child.y += y_moved;
        }
    }
}

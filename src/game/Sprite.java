package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Vector;

public class Sprite extends Rectangle2D.Double
{
    double speed;
    double x_velocity;
    double y_velocity;
    boolean to_remove = false;

    private GamePanel panel;
    private double delay;
    private double animation = 0.0f;
    private BufferedImage[] pics;
    private int current_pic = 0;

    public Sprite(GamePanel p, BufferedImage[] imgs, double x, double y, double scale, double delay, double speed)
    {
        panel = p;
        this.speed = speed;
        pics = imgs;
        this.x = x;
        this.y = y;
        this.delay = delay;
        width = pics[0].getWidth();
        height = pics[0].getHeight();
        x_mid = width / 2.0;
        y_mid = height / 2.0;
    }

    public double distance(Sprite to)
    {
        double a = (to.x + to.x_mid_offset) - (x + x_mid_offset);
        double b = (to.y + to.y_mid_offset) - (y + y_mid_offset);

        return Math.sqrt(a * a + b * b) - radius - to.radius;
    }

    public boolean is_outside()
    {
        if (x > panel.width)
        {
            return true;
        }
        else if (y > panel.height)
        {
            return true;
        }

        return false;
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
        if (!visible || to_remove)
            return;

        double _x;

        if (width_scaled > 0.0)
            _x = x;
        else
            _x = x - width_scaled;

        g.drawImage(pics[current_pic], (int) (_x * panel.global_scale), (int) (y * panel.global_scale), (int) (width_scaled * panel.global_scale), (int) (height_scaled * panel.global_scale), null);
    }

    void draw_circle_by_center(Graphics g, Color c, double x_center, double y_center, double r)
    {
        g.setColor(c);
        g.drawOval((int) ((x_center - r) * panel.global_scale), (int) ((y_center - r) * panel.global_scale), (int) (r * 2.0 * panel.global_scale), (int) (r * 2.0 * panel.global_scale));
    }

    public void add_gizmo_circle(Color c, double x_center, double y_center, double r)
    {
        gizmos_c.add(c);
        gizmos_x.add(x_center);
        gizmos_y.add(y_center);
        gizmos_r.add(r);
    }

    public void draw_gizmos(Graphics g)
    {
        for (int i = 0; i < gizmos_x.size(); i++)
        {
            draw_circle_by_center(g, gizmos_c.get(i), x + gizmos_x.get(i), y + gizmos_y.get(i), gizmos_r.get(i));
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
    }

    public void add_child(Sprite child)
    {
        child.x += x + x_mid_offset - child.x_mid_offset;
        child.y += y + y_mid_offset - child.y_mid_offset;
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

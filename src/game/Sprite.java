package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Vector;

public class Sprite extends Rectangle2D.Double
{
    double speed;
    double x_mid_offset;
    double y_mid_offset;
    double x_velocity;
    double y_velocity;
    double scale;
    double width_scaled;
    double height_scaled;
    double radius;
    boolean visible = true;
    boolean to_remove = false;

    double custom_radius = 0.0;
    double custom_x_mid_offset = 0.0;
    double custom_y_mid_offset = 0.0;

    private GamePanel panel;
    // Time between images
    private double delay;
    private double animation = 0.0f;
    private BufferedImage[] pics;
    private int current_pic = 0;
    private Vector<Sprite> childs = new Vector<>();

    private Vector<Color> added_gizmos_c = new Vector<>();
    private Vector<Integer> added_gizmos_x = new Vector<>();
    private Vector<Integer> added_gizmos_y = new Vector<>();
    private Vector<Integer> added_gizmos_r = new Vector<>();

    public void rescale()
    {
        width_scaled = width * scale;
        height_scaled = height * scale;
        x_mid_offset = width_scaled / 2.0 + custom_x_mid_offset * scale;
        y_mid_offset = height_scaled / 2.0 + custom_y_mid_offset * scale;
        radius = Math.max(width_scaled, height_scaled) / 2.0 + custom_radius * scale; 
    }

    public Sprite(GamePanel p, BufferedImage[] imgs, double x, double y, double scale, double delay, double speed)
    {
        panel = p;
        this.speed = speed;
        this.scale = scale;
        pics = imgs;
        this.x = x;
        this.y = y;
        this.delay = delay;
        width = pics[0].getWidth();
        height = pics[0].getHeight();

        rescale();
    }

    public Sprite(GamePanel p, BufferedImage[] imgs, double x, double y, double scale, double delay, double speed, double custom_radius, double custom_x_mid_offset, double custom_y_mid_offset)
    {
        this(p, imgs, x, y, scale, delay, speed);

        this.custom_radius = custom_radius;
        this.custom_x_mid_offset = custom_x_mid_offset;
        this.custom_y_mid_offset = custom_y_mid_offset;
        rescale();
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

        int _x;

        if (width_scaled > 0.0)
            _x = (int) x;
        else
            _x = (int) (x - width_scaled);

        g.drawImage(pics[current_pic], _x, (int) y, (int) (width_scaled * panel.global_scale), (int) (height_scaled * panel.global_scale), null);
    }

    void draw_circle_by_center(Graphics g, Color c, int x_center, int y_center, int r)
    {
        g.setColor(c);
        g.drawOval(x_center - r, y_center - r, r * 2, r * 2);
    }

    public void add_gizmo_circle(Color c, int x_center, int y_center, int r)
    {
        added_gizmos_c.add(c);
        added_gizmos_x.add(x_center);
        added_gizmos_y.add(y_center);
        added_gizmos_r.add(r);
    }

    public void draw_gizmos(Graphics g)
    {
        draw_circle_by_center(g, Color.MAGENTA, (int) (x + x_mid_offset), (int) (y + y_mid_offset), (int) radius);

        for (int i = 0; i < added_gizmos_x.size(); i++)
        {
            draw_circle_by_center(g, added_gizmos_c.get(i), (int) x + added_gizmos_x.get(i), (int) y + added_gizmos_y.get(i), added_gizmos_r.get(i));
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
        double x_moved = x_velocity * panel.deltaTime * panel.global_scale;
        double y_moved = y_velocity * panel.deltaTime * panel.global_scale;

        x += x_moved;
        y += y_moved;

        for (Sprite child : childs)
        {
            child.x += x_moved;
            child.y += y_moved;
        }
    }
}

package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Vector;

import structs.Bounds;

public class Sprite extends Rectangle2D.Double
{
    protected double speed;
    protected double x_mid_offset;
    protected double y_mid_offset;
    protected double x_velocity;
    protected double y_velocity;
    public double scale;
    public double width_scaled;
    public double height_scaled;
    protected Bounds bounds;
    protected double radius;
    public boolean visible = true;
    protected boolean to_remove = false;

    protected double custom_radius = 0.0;
    protected double custom_x_mid_offset = 0.0;
    protected double custom_y_mid_offset = 0.0;

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
        width_scaled = width * scale * panel.global_scale;
        height_scaled = height * scale * panel.global_scale;
        x_mid_offset = width_scaled / 2.0 + custom_x_mid_offset * scale * panel.global_scale;
        y_mid_offset = height_scaled / 2.0 + custom_y_mid_offset * scale * panel.global_scale;
        radius = Math.max(width_scaled, height_scaled) / 2.0 + custom_radius * scale * panel.global_scale; 
    }

    public Sprite(GamePanel p, BufferedImage[] imgs, double x, double y, double scale, Bounds b, double delay, double speed)
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

        if (b == null)
            bounds = null;
        else
            bounds = b.clone();
    }

    public Sprite(GamePanel p, BufferedImage[] imgs, double x, double y, double scale, Bounds b, double delay, double speed, double custom_radius, double custom_x_mid_offset, double custom_y_mid_offset)
    {
        this(p, imgs, x, y, scale, b, delay, speed);

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

        g.drawImage(pics[current_pic], _x, (int) y, (int) width_scaled, (int) height_scaled, null);
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

        g.setColor(Color.orange);
        g.drawRect((int) bounds.left, (int) bounds.top, (int) (bounds.right - bounds.left), (int) (bounds.bottom - bounds.top));
    }

    public void check_bounds()
    {
        if (bounds == null)
        {
            return;
        }

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

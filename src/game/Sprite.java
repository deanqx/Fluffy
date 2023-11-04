package game;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Sprite extends Rectangle2D.Double implements Drawable, Moveable
{
    enum Tag
    {
        Cloud, Fog
    }

    protected final Tag tag;
    protected final float scale;
    protected final float speed;
    protected float x_velocity;
    protected float y_velocity;
    protected final float width_scaled;
    protected final float height_scaled;
    protected Bounds bounds;

    private GamePanel panel;
    // Time between images
    private float delay;
    private float animation = 0.0f;
    private BufferedImage[] pics;
    private int current_pic = 0;

    public Sprite(GamePanel p, Tag tag, BufferedImage[] imgs, float x, float y, float scale, Bounds b, int delay, float speed)
    {
        this.tag = tag;
        this.speed = speed;
        pics = imgs;
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.delay = (float) delay;
        width = pics[0].getWidth();
        height = pics[0].getHeight();
        width_scaled = (float) width * scale;
        height_scaled = (float) height * scale;
        panel = p;
        bounds = b.clone();
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

    public void after_move()
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

    public void move()
    {
        x += x_velocity * panel.deltaTime;
        y += y_velocity * panel.deltaTime;
    }
}

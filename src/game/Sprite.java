package game;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Sprite extends Rectangle2D.Double implements Drawable, Moveable
{
    protected float x_velocity;
    protected float y_velocity;
    private float width_scaled;
    private float height_scaled;

    private GamePanel panel;
    // Time between images
    private float delay;
    private float animation = 0.0f;
    private BufferedImage[] pics;
    private int current_pic = 0;
    private Bounds bounds;

    private final float SCALE = 2;

    public Sprite(BufferedImage[] imgs, float x, float y, int delay, GamePanel p, Bounds b)
    {
        pics = imgs;
        this.x = x;
        this.y = y;
        this.delay = (float) delay;
        width = pics[0].getWidth();
        height = pics[0].getHeight();
        width_scaled = (float) width * (float) SCALE;
        height_scaled = (float) height * (float) SCALE;
        panel = p;
        bounds = b;
    }

    private void computeAnimation()
    {
        current_pic++;

        if (current_pic >= pics.length)
        {
            current_pic = 0;
        }
    }

    public void drawObjects(Graphics g)
    {
        g.drawImage(pics[current_pic], (int) x, (int) y, (int) width_scaled, (int) height_scaled, null);
    }

    public void checkBounds()
    {
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

        if (x > bounds.left && y > bounds.top && x + width_scaled < bounds.right && y + height_scaled < bounds.bottom)
            bounds.hit = false;
    }

    public void afterLogic()
    {
        animation += panel.deltaTime;

        if (animation > delay)
        {
            animation = 0.0f;
            computeAnimation();
        }

        checkBounds();
    }

    public void move()
    {
        x += x_velocity * panel.deltaTime;
        y += y_velocity * panel.deltaTime;
    }
}

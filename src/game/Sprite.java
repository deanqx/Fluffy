package game;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Sprite extends Rectangle2D.Double implements Drawable, Moveable
{
    // Time between images (ms)
    float delay;
    float animation = 0.0f;
    GamePanel panel;
    BufferedImage[] pics;
    int current_pic = 0;

    protected float x_velocity;
    protected float y_velocity;

    public Sprite(BufferedImage[] imgs, float x, float y, float delay, GamePanel p)
    {
        pics = imgs;
        this.x = x;
        this.y = y;
        this.delay = delay;
        this.width = pics[0].getWidth();
        this.width = pics[0].getHeight();
        panel = p;
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
        g.drawImage(pics[current_pic], (int) x, (int) y, null);
    }

    public void doLogic(final float delta)
    {
        animation += delta;

        if (animation > delay)
        {
            animation = 0.0f;
            computeAnimation();
        }
    }

    public void move(final float delta)
    {
        if (x_velocity != 0)
        {
            x += x_velocity * delta;
        }

        if (y_velocity != 0)
        {
            y += y_velocity * delta;
        }
    }
}

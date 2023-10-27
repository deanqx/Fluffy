package game;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Sprite extends Rectangle2D.Double implements Drawable, Moveable
{
    // Time between images
    float delay;
    float animation = 0.0f;
    GamePanel panel;
    BufferedImage[] pics;
    int current_pic = 0;

    protected float x_velocity;
    protected float y_velocity;

    private final int SCALE = 2;

    public Sprite(BufferedImage[] imgs, float x, float y, int delay, GamePanel p)
    {
        pics = imgs;
        this.x = x;
        this.y = y;
        this.delay = (float) delay;
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
        g.drawImage(pics[current_pic], (int) x, (int) y, pics[current_pic].getWidth() * SCALE, pics[current_pic].getHeight() * SCALE, null);
    }

    public void doLogic()
    {
        animation += panel.deltaTime;

        if (animation > delay)
        {
            animation = 0.0f;
            computeAnimation();
        }
    }

    public void move()
    {
        x += x_velocity * panel.deltaTime;
        y += y_velocity * panel.deltaTime;
    }
}

package game;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Sprite extends Rectangle2D.Double
{
    double speed;
    double x_velocity;
    double y_velocity;
    double x_mid;
    double y_mid;

    GamePanel panel;
    double delay;
    double animation = 0.0f;
    BufferedImage[] pics;
    int current_pic = 0;
    boolean to_remove = false;

    public Sprite(GamePanel p, BufferedImage[] imgs, double x, double y, double delay, double speed)
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
        double a = (to.x + to.x_mid) - (x + x_mid);
        double b = (to.y + to.y_mid) - (y + y_mid);

        return Math.sqrt(a * a + b * b) - y_mid - to.y_mid;
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
        if (to_remove)
            return;

        g.drawImage(pics[current_pic], (int) x, (int) y, (int) width, (int) height, null);
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

    public void move()
    {
        x += x_velocity * panel.deltaTime;
        y += y_velocity * panel.deltaTime;
    }
}

package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable, KeyListener
{
    JFrame frame;

    float fps = 0.0f;
    float deltaTime;
    float fixed_update_counter;
    final float fixed_update_interval = 1000.0f;

    Sprite cloud;
    FogGen fog_gen;
    Vector<Sprite> actors;

    boolean key_up;
    boolean key_left;
    boolean key_down;
    boolean key_right;

    public GamePanel(int w, int h)
    {
        this.setPreferredSize(new Dimension(w, h));
        this.setBackground(new Color(89, 108, 171, 255));
        frame = new JFrame("GameDemo");
        frame.setLocation(100, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.addKeyListener(this);
        frame.pack();
        frame.setVisible(true);

        actors = new Vector<Sprite>();

        init();

        Thread t = new Thread(this);
        t.start();
    }

    private void init()
    {
        cloud = new Sprite(this, Sprite.Tag.Cloud, load_pics("res/fluffy.png", 4), 372, 400, 2.0f, new Bounds(.0f, this.getSize().height, .0f, this.getSize().width), 500, 0.3f);
        actors.add(cloud);

        BufferedImage[] fog_prefab = load_pics("res/fog.png", 1);
        fog_gen = new FogGen(this, actors, fog_prefab, 0.5f, 1.2f);
        fog_gen.spawn(10, 0.03f);
    }

    // Bilder müssen horizontal hintereinander in einem Bild sein
    private BufferedImage[] load_pics(String path, int picCount)
    {
        BufferedImage[] pics = new BufferedImage[picCount];
        BufferedImage source = null;

        File pic = new File(path);

        try
        {
            source = ImageIO.read(pic);
        } catch (IOException e)
        {}

        for (int x = 0; x < picCount; x++)
        {
            // Bild mit picCount aufteilen
            pics[x] = source.getSubimage(x * source.getWidth() / picCount, 0, source.getWidth() / picCount, source.getHeight());
        }

        return pics;
    }

    private void move_objects()
    {
        for (Sprite it : actors)
        {
            it.move();
        }
    }

    private void after_move()
    {
        fog_gen.redirect();

        for (Sprite it : actors)
        {
            it.after_move();
        }
    }

    private void check_keys()
    {
        if (key_up)
            cloud.y_velocity = -cloud.speed;
        else if (key_down)
            cloud.y_velocity = cloud.speed;

        if (key_left)
            cloud.x_velocity = -cloud.speed;
        else if (key_right)
            cloud.x_velocity = cloud.speed;

        if (key_up == key_down)
            cloud.y_velocity = 0.0f;
        if (key_left == key_right)
            cloud.x_velocity = 0.0f;
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        for (Sprite it : actors)
        {
            it.draw_objects(g);
        }

        g.setColor(Color.red);
        g.drawString(String.format("%.1f", fps) + " fps", 10, 20);
    }

    public void fixed_update()
    {
        for (int i = actors.size() - 1; i >= 0; i--)
        {
            if (actors.get(i).to_remove)
            {
                actors.remove(i);
            }
        }
    }

    @Override
    public void run()
    {
        long last = System.nanoTime();
        while (frame.isVisible())
        {
            // Um Verschiedene Frame raten auszugleichen kann man mit diesem wert multiplezieren
            deltaTime = (float) (System.nanoTime() - last) * 1e-6f;
            last = System.nanoTime();
            fixed_update_counter += deltaTime;
            fps = 1e3f / deltaTime;

            check_keys();
            move_objects();
            after_move();

            if (fixed_update_counter >= fixed_update_interval)
            {
                fixed_update_counter = 0.0f;
                fixed_update();
            }

            repaint();

            try
            {
                Thread.sleep(8);
            } catch (InterruptedException e)
            {
                System.out.println("Thread got interrupted");
            }
        }
    }

    void update_keys(KeyEvent e, boolean pressed)
    {
        switch (e.getKeyCode())
        {
        case KeyEvent.VK_W:
            key_up = pressed;
            break;
        case KeyEvent.VK_A:
            key_left = pressed;
            break;
        case KeyEvent.VK_S:
            key_down = pressed;
            break;
        case KeyEvent.VK_D:
            key_right = pressed;
            break;
        }
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        update_keys(e, true);
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        update_keys(e, false);
    }

    @Override
    public void keyTyped(KeyEvent e)
    {}
}

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

import structs.Bounds;

public class GamePanel extends JPanel implements Runnable, KeyListener
{
    JFrame frame;

    double fps = 0.0;
    double deltaTime;
    double fixed_update_counter;
    final double fixed_update_interval = 1000.0;

    Sprite cloud;
    // 0: pickups 1: powerups 2: enemys 3: fogs
    Vector<Vector<Sprite>> actors = new Vector<Vector<Sprite>>();
    PowerupGen powerup_gen;
    EnemyGen enemy_gen;
    FogGen fog_gen;

    boolean debug_mode = true;
    boolean key_up;
    boolean key_left;
    boolean key_down;
    boolean key_right;

    public GamePanel(int w, int h)
    {
        this.setPreferredSize(new Dimension(w, h));
        this.setBackground(new Color(89, 108, 171, 255));
        frame = new JFrame("Fluffy");
        frame.setLocation(100, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.addKeyListener(this);
        frame.pack();
        frame.setVisible(true);

        init();

        Thread t = new Thread(this);
        t.start();
    }

    private void init()
    {
        cloud = new Sprite(this, load_pics("res/fluffy.png", 4), 372, 400, 2.0, new Bounds(.0, this.getSize().height, .0, this.getSize().width), 500, 0.3);
        cloud.x_mid_offset -= 4.0;
        cloud.y_mid_offset += 6.0;
        cloud.radius -= 18.0;

        actors.add(new Vector<>());
        actors.add(new Vector<>());
        actors.add(new Vector<>());
        actors.add(new Vector<>());

        BufferedImage[] pickup_prefab = load_pics("res/bird_pickup.png", 1);
        BufferedImage[] powerup_prefab = load_pics("res/bird.png", 5);
        powerup_gen = new PowerupGen(this, cloud, actors.get(0), actors.get(1), pickup_prefab, powerup_prefab, 2.0, 0.03, 0.3, 64.0);
        cloud.add_gizmo_circle(Color.GREEN, (int) cloud.x_mid_offset, (int) cloud.y_mid_offset, 64);
        powerup_gen.spawn(2);

        BufferedImage[] enemy_prefab = load_pics("res/plane.png", 4);
        enemy_gen = new EnemyGen(this, actors.get(2), enemy_prefab, 2.0, 0.05);
        enemy_gen.spawn(0);

        BufferedImage[] fog_prefab = load_pics("res/fog.png", 1);
        fog_gen = new FogGen(this, actors.get(3), fog_prefab, 0.5, 1.2);
        fog_gen.spawn(10, 0.03);
    }

    private void reset()
    {
        actors.clear();
        init();
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
        cloud.move();
        powerup_gen.move_all();

        for (Vector<Sprite> layer : actors)
        {
            for (Sprite it : layer)
            {
                it.move();
            }
        }
    }

    private void update()
    {
        cloud.update();

        for (Vector<Sprite> layer : actors)
        {
            for (Sprite it : layer)
            {
                it.update();
            }
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
            cloud.y_velocity = 0.0;
        if (key_left == key_right)
            cloud.x_velocity = 0.0;
    }

    private void check_kollision()
    {
        for (Sprite it : actors.get(0))
        {
            if (cloud.distance(it) <= 0.0)
            {
                it.to_remove = true;
                powerup_gen.pickup();
            }
        }

        for (Sprite powerup : actors.get(1))
        {
            for (Sprite enemy : actors.get(2))
            {
                if (powerup.distance(enemy) <= 0.0)
                {
                    powerup.to_remove = true;
                    enemy.to_remove = true;
                    break;
                }
            }
        }

        for (Sprite it : actors.get(2))
        {
            if (cloud.distance(it) <= 0.0)
            {
                reset();
                return;
            }
        }
    }

    private void draw_gizmos(Graphics g)
    {
        if (cloud != null)
        {
            cloud.draw_gizmos(g);
        }

        for (Vector<Sprite> layer : actors)
        {
            for (Sprite it : layer)
            {
                it.draw_gizmos(g);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        if (cloud != null)
        {
            cloud.draw_objects(g);
        }

        for (Vector<Sprite> layer : actors)
        {
            for (Sprite it : layer)
            {
                it.draw_objects(g);
            }
        }

        if (debug_mode)
        {
            draw_gizmos(g);

            g.setColor(Color.red);
            g.drawString(String.format("%.1f fps", fps), 10, 20);
        }
    }

    public void fixed_update()
    {
        powerup_gen.clean();
        enemy_gen.reposition();
        fog_gen.redirect();
    }

    @Override
    public void run()
    {
        long last = System.nanoTime();

        while (frame.isVisible())
        {
            // Um Verschiedene Frame raten auszugleichen kann man mit diesem wert multiplezieren
            deltaTime = (double) (System.nanoTime() - last) * 1e-6;
            last = System.nanoTime();
            fixed_update_counter += deltaTime;
            fps = 1e3 / deltaTime;

            check_keys();
            move_objects();
            check_kollision();
            update();

            if (fixed_update_counter >= fixed_update_interval)
            {
                fixed_update_counter = 0.0;
                fixed_update();
            }

            for (Vector<Sprite> it : actors)
            {
                for (int i = it.size() - 1; i >= 0; i--)
                {
                    if (it.get(i).to_remove)
                    {
                        it.remove(i);
                    }
                }
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
        if (e.getKeyCode() == KeyEvent.VK_G)
        {
            debug_mode = !debug_mode;
        }

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

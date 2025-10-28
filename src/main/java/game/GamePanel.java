package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable, KeyListener
{
    JFrame frame;
    double global_scale = 1.0;
    double width = 1280.0;
    double height = 720.0;

    double fps = 0.0;
    double deltaTime;
    double fixed_update_counter;
    final double fixed_update_interval = 1000.0;

    double score;
    double score_best;

    Sprite cloud;
    // 0: pickups 1: powerups 2: enemys 3: fogs
    Vector<Vector<Sprite>> actors = new Vector<Vector<Sprite>>();
    PowerupGen powerup_gen;
    EnemyGen enemy_gen;
    FogGen fog_gen;
    
    boolean debug_mode = false;
    boolean key_up;
    boolean key_left;
    boolean key_down;
    boolean key_right;

    public GamePanel()
    {
        try
        {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("res/PressStart.ttf")));
        } catch (IOException|FontFormatException e)
        {
        }

        JPanel panel = this;
        this.setPreferredSize(new Dimension((int) width, (int) height));
        this.setBackground(new Color(89, 108, 171, 255));

        JPanel content_panel = new JPanel();
        content_panel.setBackground(Color.black);
        content_panel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        content_panel.add(this);

        frame = new JFrame("Fluffy");
        frame.setLocation(100, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addKeyListener(this);
        frame.setContentPane(content_panel);
        frame.pack();
        frame.setVisible(true);

        content_panel.addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent e)
            {
                Dimension d = new Dimension(content_panel.getWidth(), content_panel.getWidth() * 9 / 16);

                if (d.height > content_panel.getHeight())
                {
                    d.height = content_panel.getHeight();
                    d.width = content_panel.getHeight() * 16 / 9;
                }

                global_scale = (double) d.width / width;

                panel.setPreferredSize(d);
                content_panel.revalidate();
                frame.revalidate();
            }
        });
        init();

        Thread t = new Thread(this);
        t.start();
    }
    
    private void init()
    {
        score = 0;

        cloud = new Sprite(this, load_pics("res/fluffy.png", 4), 375, 400, 2.0, 500, 0.3, 0.625, 0.92, 1.1875);

        actors.add(new Vector<>());
        actors.add(new Vector<>());
        actors.add(new Vector<>());
        actors.add(new Vector<>());

        BufferedImage[] pickup_prefab = load_pics("res/bird_pickup.png", 1);
        BufferedImage[] powerup_prefab = load_pics("res/bird.png", 5);
        powerup_gen = new PowerupGen(this, cloud, actors.get(0), actors.get(1), pickup_prefab, powerup_prefab, 2.0, 0.03, 0.3, 64.0);
        cloud.add_gizmo_circle(Color.GREEN, cloud.x_mid_offset, cloud.y_mid_offset, 64.0);

        BufferedImage[] enemy_prefab = load_pics("res/plane.png", 4);
        enemy_gen = new EnemyGen(this, actors.get(2), enemy_prefab, 2.0, 0.05);

        BufferedImage[] fog_prefab = load_pics("res/fog.png", 1);
        fog_gen = new FogGen(this, actors.get(3), fog_prefab, 0.5, 1.2);
        fog_gen.spawn(10, 0.03);
    }

    private void reset()
    {
        if (score > score_best)
        {
            score_best = score;
        }

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

    public void check_bounds()
    {
        // if (cloud.x > 0.0 && cloud.y > 0.0 && cloud.x + cloud.width_scaled < this.getWidth() && cloud.y + cloud.height_scaled < this.getHeight())
        // {
        //     bounds.hit = false;
        //     return;
        // }

        if (cloud.x < 0.0)
        {
            cloud.x = 0.0;
        }

        if (cloud.y < 0.0)
        {
            cloud.y = 0.0;
        }

        if (cloud.x + cloud.width_scaled > width)
        {
            cloud.x = width - cloud.width_scaled;
        }

        if (cloud.y + cloud.height_scaled > height)
        {
            cloud.y = height - cloud.height_scaled;
        }
    }
    private void check_kollision()
    {
        check_bounds();
         
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
                if (powerup.visible && powerup.distance(enemy) <= 0.0)
                {
                    powerup.visible = false;
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

        g.setFont(new Font("Press Start 2P", Font.PLAIN, 24));
        
        g.setColor(Color.white);
        g.drawString(Integer.toString((int) (score * 1e-1) * 10), this.getWidth() / 3, this.getHeight() - 40);
        
        g.setColor(Color.orange);
        g.drawString("Best: " + Integer.toString((int) (score_best * 1e-1) * 10), this.getWidth() / 3, this.getHeight() - 10);
    }

    public void spawn()
    {
        ThreadLocalRandom t = ThreadLocalRandom.current();
        
        double powerup_chance = 0.25;
        double enemy_chance = 0.5;

        if (score < 1000.0)
        {
            enemy_gen.speed = 0.075;
        }
        else if (score < 3000.0)
        {
            powerup_chance = 0.5;
            enemy_chance = 0.35;
            enemy_gen.speed = 0.1;
        }
        else if (score < 4000.0)
        {
            enemy_chance = 0.3;
            enemy_gen.speed = 0.15;
        }

        if (t.nextDouble(0.0, 1.0) <= powerup_chance)
        {
            powerup_gen.spawn(1);
        }

        if (t.nextDouble(0.0, 1.0) <= enemy_chance)
        {
            enemy_gen.spawn(1);
        }
    }

    public void fixed_update()
    {
        spawn();

        powerup_gen.clean();
        fog_gen.redirect();
    }

    @Override
    public void run()
    {
        long last = System.nanoTime();

        while (frame.isVisible())
        {
            // Um Verschiedene Frame raten auszugleichen kann man mit diesem wert
            // multiplezieren
            deltaTime = (double) (System.nanoTime() - last) * 1e-6;
            last = System.nanoTime();
            fixed_update_counter += deltaTime;
            fps = 1e3 / deltaTime;

            // Add 25 per second
            score += deltaTime * 0.025;

            check_keys();
            move_objects();
            check_kollision();
            update();

            if (fixed_update_counter >= fixed_update_interval)
            {
                fixed_update_counter = 0.0;
                fixed_update();
            }

            enemy_gen.reposition();

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
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                key_up = pressed;
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                key_left = pressed;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                key_down = pressed;
                break;
            case KeyEvent.VK_RIGHT:
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

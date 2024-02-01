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
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable, KeyListener
{
    JFrame frame;

    double fps = 0.0;
    double deltaTime;
    int width = 720;
    int height = 500;

    Sprite cloud;
    Vector<Sprite> enemys = new Vector<Sprite>();
    EnemyGen enemy_gen;
    
    boolean key_up;
    boolean key_left;
    boolean key_down;
    boolean key_right;

    double enemy_chance = 0.5;

    public GamePanel()
    {
        this.setPreferredSize(new Dimension(width, height));
        this.setBackground(new Color(89, 108, 171, 255));

        frame = new JFrame("Fluffy");
        frame.setLocation(100, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addKeyListener(this);
        frame.add(this);
        frame.pack();
        frame.setVisible(true);

        init();

        Thread t = new Thread(this);
        t.start();
    }
    
    private void init()
    {
        cloud = new Sprite(this, load_pics("res/fluffy.png", 4), 375.0, 400.0, 500, 0.3);

        BufferedImage[] enemy_prefab = load_pics("res/plane.png", 4);
        enemy_gen = new EnemyGen(this, enemy_prefab, 0.05);
        enemy_gen.spawn(1);
    }

    private void reset()
    {
        enemys.clear();
        init();
    }

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
            pics[x] = source.getSubimage(x * source.getWidth() / picCount, 0, source.getWidth() / picCount, source.getHeight());
        }

        return pics;
    }

    private void move_objects()
    {
        cloud.move();

        for (Sprite it : enemys)
        {
            it.move();
        }
    }

    private void update()
    {
        cloud.update();

        for (Sprite it : enemys)
        {
            it.update();
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
        if (cloud.x < 0.0)
        {
            cloud.x = 0.0;
        }

        if (cloud.y < 0.0)
        {
            cloud.y = 0.0;
        }

        if (cloud.x + cloud.width > getWidth())
        {
            cloud.x = getWidth() - cloud.width;
        }

        if (cloud.y + cloud.height > getHeight())
        {
            cloud.y = getHeight() - cloud.height;
        }
    }
    private void check_kollision()
    {
        check_bounds();

        for (Sprite it : enemys)
        {
            if (cloud.distance(it) <= 0.0)
            {
                reset();
                return;
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

        for (Sprite it : enemys)
        {
            it.draw_objects(g);
        }

        g.setColor(Color.red);
        g.drawString(String.format("%.1f fps", fps), 10, 20);
    }

    public void spawn()
    {
        ThreadLocalRandom t = ThreadLocalRandom.current();
        
        if (t.nextDouble(0.0, 1.0) <= enemy_chance)
        {
            enemy_gen.spawn(1);
        }
    }

    @Override
    public void run()
    {
        long last = System.nanoTime();

        while (frame.isVisible())
        {
            deltaTime = (double) (System.nanoTime() - last) * 1e-6;
            last = System.nanoTime();
            fps = 1e3 / deltaTime;

            check_keys();
            move_objects();
            check_kollision();
            update();

            for (int i = enemys.size() - 1; i >= 0; i--)
            {
                if (enemys.get(i).to_remove)
                {
                    enemys.remove(i);
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
        update_keys(e, true);
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        update_keys(e, false);
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
    }
}

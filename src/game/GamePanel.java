package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ListIterator;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable
{
    private static final long serialVersionUID = 1L;
    JFrame frame;

    float fps = 0.0f;

    Sprite heli;
    Vector<Sprite> actors;

    public GamePanel(int w, int h)
    {
        this.setPreferredSize(new Dimension(w, h));
        frame = new JFrame("GameDemo");
        frame.setLocation(100, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.pack();
        frame.setVisible(true);

        actors = new Vector<Sprite>();

        init();

        Thread t = new Thread(this);
        t.start();
    }

    private void init()
    {
        heli = new Sprite(loadPics("res/heli.gif", 4), 400, 300, 100, this);
        actors.add(heli);
    }

    private BufferedImage[] loadPics(String path, int pics)
    {
        BufferedImage[] anim = new BufferedImage[pics];
        BufferedImage source = null;

        URL pic_url = getClass().getClassLoader().getResource(path);

        try
        {
            source = ImageIO.read(pic_url);
        } catch (IOException e)
        {}

        for (int x = 0; x < pics; x++)
        {
            anim[x] = source.getSubimage(x * source.getWidth() / pics, 0, source.getWidth() / pics, source.getHeight());
        }

        return anim;
    }

    private void moveObjects()
    {}

    private void doLogic(final float delta)
    {
        for (Sprite it : actors)
        {
            it.doLogic(delta);
        }
    }

    private void checkKeys(final float delta)
    {
        for (Sprite it : actors)
        {
            it.move(delta);
        }
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        g.setColor(Color.red);
        g.drawString(String.format("%.1f", fps) + " fps - Dean", 10, 20);

        for (Sprite it : actors)
        {
            it.drawObjects(g);
        }
    }

    @Override
    public void run()
    {
        long last = System.nanoTime();
        while (frame.isVisible())
        {
            final float delta = (float) (System.nanoTime() - last) * 1e-6f;
            last = System.nanoTime();
            fps = 1e-3f / delta;

            checkKeys(delta);
            doLogic(delta);
            moveObjects();

            repaint();

            try
            {
                Thread.sleep(12);
            } catch (InterruptedException e)
            {
                System.out.println("Thread got interrupted");
            }
        }
    }
}

package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable
{
    JFrame frame;

    float fps = 0.0f;
    float deltaTime;

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
        heli = new Sprite(loadPics("res/fluffy.png", 4), 372, 400, 500, this);
        actors.add(heli);
    }

    // Bilder müssen horizontal hintereinander in einem Bild sein
    private BufferedImage[] loadPics(String path, int picCount)
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

    private void moveObjects()
    {}

    private void doLogic()
    {
        for (Sprite it : actors)
        {
            it.doLogic();
        }
    }

    private void checkKeys()
    {
        for (Sprite it : actors)
        {
            it.move();
        }
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        g.setColor(Color.red);
        g.drawString(String.format("%.1f", fps) + " fps", 10, 20);

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
            // Um Verschiedene Frame raten auszugleichen kann man mit diesem wert multiplezieren
            deltaTime = (float) (System.nanoTime() - last) * 1e-6f;
            last = System.nanoTime();
            fps = 1e3f / deltaTime;

            checkKeys();
            doLogic();
            moveObjects();

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
}

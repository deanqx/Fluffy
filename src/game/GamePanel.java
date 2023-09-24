package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable
{
    private static final long serialVersionUID = 1L;
    JFrame frame;
    long delta = 0;
    long last = 0;
    long fps = 0;
    
    public GamePanel(int w, int h)
    {
        this.setPreferredSize(new Dimension(w, h));
        frame = new JFrame("GameDemo");
        frame.setLocation(100, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.pack();
        frame.setVisible(true);
        
        init();
        
        Thread t = new Thread(this);
        t.start();
    }
    
    private void init()
    {
        last = System.nanoTime();
    }
    
    private void moveObjects()
    {
    }
    
    private void doLogic()
    {
    }
    
    private void checkKeys()
    {
    }
    
    private void computeDelta()
    {
        delta = System.nanoTime() - last;
        last = System.nanoTime();
        fps = ((long) 1e+9) / delta;
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        g.setColor(Color.red);
        g.drawString(Long.toString(fps) + " fps - Dean", 10, 20);
    }
    
    @Override
    public void run()
    {
        while (frame.isVisible())
        {
            computeDelta();
            
            checkKeys();
            doLogic();
            moveObjects();
            
            repaint();
            
            try
            {
                Thread.sleep(10);
            }
            catch (InterruptedException e)
            {
                System.out.println("Error");
            }
        }
    }
}

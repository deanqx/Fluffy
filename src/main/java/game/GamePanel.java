package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable, KeyListener {
    JFrame frame;
    double scale = 1.0;
    double width = 1280.0;
    double height = 720.0;

    double fps = 0.0;
    double deltaTime;
    double fixedUpdateCounter;
    final double fixedUpdateInterval = 1000.0;

    double score;
    double scoreBest;

    Prefaps prefaps;
    Sprite cloud = null;
    /// 0: pickups 1: powerups 2: enemies 3: fogs TODO remove 2D anonymos array
    Vector<Vector<Sprite>> actors = new Vector<Vector<Sprite>>();
    PowerupGen powerupGen;
    EnemyGen enemyGen;
    FogGen fogGen;

    boolean debugMode = false;
    boolean keyUp;
    boolean keyLeft;
    boolean keyDown;
    boolean keyRight;

    public GamePanel(Prefaps prefaps) {
        this.prefaps = prefaps;

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

        content_panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension d = new Dimension(content_panel.getWidth(), content_panel.getWidth() * 9 / 16);

                if (d.height > content_panel.getHeight()) {
                    d.height = content_panel.getHeight();
                    d.width = content_panel.getHeight() * 16 / 9;
                }

                scale = (double) d.width / width;

                panel.setPreferredSize(d);
                content_panel.revalidate();
                frame.revalidate();
            }
        });
        init();

        Thread t = new Thread(this);
        t.start();
    }

    private void init() {
        score = 0;

        cloud = new Sprite(this, prefaps.getCharacter(), 375, 400, 2.0, 500, 0.3, 0.625, 0.92, 1.1875);

        actors.add(new Vector<>());
        actors.add(new Vector<>());
        actors.add(new Vector<>());
        actors.add(new Vector<>());

        powerupGen = new PowerupGen(this, cloud, actors.get(0), actors.get(1), prefaps.getPowerupPickup(),
                prefaps.getPowerup(), 2.0,
                0.03, 0.3, 64.0);

        cloud.addGizmoCircle(Color.GREEN, cloud.xMidOffset, cloud.yMidOffset, 64.0);

        enemyGen = new EnemyGen(this, actors.get(2), prefaps.getEnemy(), 2.0, 0.05);

        fogGen = new FogGen(this, actors.get(3), prefaps.getFog(), 0.5, 1.2);
        fogGen.spawn(10, 0.03);
    }

    private void reset() {
        if (score > scoreBest) {
            scoreBest = score;
        }

        actors.clear();
        init();
    }

    private void moveObjects() {
        cloud.move();
        powerupGen.moveAll();

        for (Vector<Sprite> layer : actors) {
            for (Sprite it : layer) {
                it.move();
            }
        }
    }

    private void update() {
        cloud.update();

        for (Vector<Sprite> layer : actors) {
            for (Sprite it : layer) {
                it.update();
            }
        }
    }

    private void updateVelocity() {
        if (keyUp)
            cloud.yVelocity = -cloud.speed;
        else if (keyDown)
            cloud.yVelocity = cloud.speed;

        if (keyLeft)
            cloud.xVelocity = -cloud.speed;
        else if (keyRight)
            cloud.xVelocity = cloud.speed;

        if (keyUp == keyDown)
            cloud.yVelocity = 0.0;
        if (keyLeft == keyRight)
            cloud.xVelocity = 0.0;
    }

    public void collisionBounds() {
        if (cloud.x < 0.0) {
            cloud.x = 0.0;
        }

        if (cloud.y < 0.0) {
            cloud.y = 0.0;
        }

        if (cloud.x + cloud.widthScaled > width) {
            cloud.x = width - cloud.widthScaled;
        }

        if (cloud.y + cloud.heightScaled > height) {
            cloud.y = height - cloud.heightScaled;
        }
    }

    private void collisionObjects() {
        collisionBounds();

        for (Sprite pickup : actors.get(0)) {
            if (cloud.distance(pickup) <= 0.0) {
                pickup.toRemove = true;
                powerupGen.pickup();
            }
        }

        for (Sprite powerup : actors.get(1)) {
            for (Sprite enemy : actors.get(2)) {
                if (powerup.visible && powerup.distance(enemy) <= 0.0) {
                    powerup.visible = false;
                    enemy.toRemove = true;

                    break;
                }
            }
        }

        for (Sprite enemies : actors.get(2)) {
            if (cloud.distance(enemies) <= 0.0) {
                reset();
                return;
            }
        }
    }

    private void drawGizmos(Graphics g) {
        if (cloud != null) {
            cloud.drawGizmos(g);
        }

        for (Vector<Sprite> layer : actors) {
            for (Sprite sprite : layer) {
                sprite.drawGizmos(g);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (cloud != null) {
            cloud.draw(g);
        }

        for (Vector<Sprite> layer : actors) {
            for (Sprite sprite : layer) {
                sprite.draw(g);
            }
        }

        if (debugMode) {
            drawGizmos(g);

            g.setColor(Color.red);
            g.drawString(String.format("%.1f fps", fps), 10, 20);
        }

        g.setFont(new Font("Press Start 2P", Font.PLAIN, 24));

        g.setColor(Color.white);
        g.drawString(Integer.toString((int) (score * 1e-1) * 10), this.getWidth() / 3, this.getHeight() - 40);

        g.setColor(Color.orange);
        g.drawString("Best: " + Integer.toString((int) (scoreBest * 1e-1) * 10), this.getWidth() / 3,
                this.getHeight() - 10);
    }

    public void spawn() {
        ThreadLocalRandom t = ThreadLocalRandom.current();

        double powerup_chance = 0.25;
        double enemy_chance = 0.5;

        if (score < 1000.0) {
            enemyGen.speed = 0.075;
        } else if (score < 3000.0) {
            powerup_chance = 0.5;
            enemy_chance = 0.35;
            enemyGen.speed = 0.1;
        } else if (score < 4000.0) {
            enemy_chance = 0.3;
            enemyGen.speed = 0.15;
        }

        if (t.nextDouble(0.0, 1.0) <= powerup_chance) {
            powerupGen.spawn(1);
        }

        if (t.nextDouble(0.0, 1.0) <= enemy_chance) {
            enemyGen.spawn(1);
        }
    }

    public void fixedUpdate() {
        spawn();

        powerupGen.clean();
        fogGen.reuseOutOfBounds();
    }

    @Override
    public void run() {
        long last = System.nanoTime();

        while (frame.isVisible()) {
            // Um Verschiedene Frame raten auszugleichen kann man mit diesem wert
            // multiplezieren
            deltaTime = (double) (System.nanoTime() - last) * 1e-6;
            last = System.nanoTime();
            fixedUpdateCounter += deltaTime;
            fps = 1e3 / deltaTime;

            // Add 25 per second
            score += deltaTime * 0.025;

            updateVelocity();
            moveObjects();
            collisionObjects();
            update();

            if (fixedUpdateCounter >= fixedUpdateInterval) {
                fixedUpdateCounter = 0.0;
                fixedUpdate();
            }

            enemyGen.reuseOutOfBounds();

            for (Vector<Sprite> it : actors) {
                for (int i = it.size() - 1; i >= 0; i--) {
                    if (it.get(i).toRemove) {
                        it.remove(i);
                    }
                }
            }

            repaint();

            try {
                Thread.sleep(8);
            } catch (InterruptedException e) {
                System.out.println("Thread got interrupted");
            }
        }
    }

    void keyAction(KeyEvent e, boolean pressed) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP, KeyEvent.VK_W -> keyUp = pressed;
            case KeyEvent.VK_LEFT, KeyEvent.VK_A -> keyLeft = pressed;
            case KeyEvent.VK_DOWN, KeyEvent.VK_S -> keyDown = pressed;
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> keyRight = pressed;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_G) {
            debugMode = !debugMode;
        }

        keyAction(e, true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keyAction(e, false);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}

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
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable, KeyListener {
    private final JFrame frame;
    private float scale = 1.0f;
    private final float gameWidth = 1280.0f;
    private final float gameHeight = 720.0f;

    private float fps = 0.0f;
    private float deltaTime;
    private float fixedUpdateCounter;
    private final float fixedUpdateInterval = 1000.0f;

    private float score;
    private float scoreBest;

    private final Prefab prefabCharacter;
    private final Prefab prefabEnemy;
    private final Prefab prefabFog;
    private final Prefab prefabPowerup;
    private final Prefab prefabPickup;

    private Sprite cloud = null;
    /// 0: pickups 1: powerups 2: enemies 3: fogs TODO remove 2D anonymos array
    private final ArrayList<ArrayList<Sprite>> actors = new ArrayList<ArrayList<Sprite>>();
    private PowerupGen powerupGen;
    private EnemyGen enemyGen;
    private FogGen fogGen;

    private boolean debugMode = false;
    private boolean keyUp;
    private boolean keyLeft;
    private boolean keyDown;
    private boolean keyRight;

    public GamePanel(final Prefab character, final Prefab enemy, final Prefab fog, final Prefab powerup,
            final Prefab pickup) {
        this.prefabCharacter = character;
        this.prefabEnemy = enemy;
        this.prefabFog = fog;
        this.prefabPowerup = powerup;
        this.prefabPickup = pickup;

        final JPanel panel = this;
        this.setPreferredSize(new Dimension((int) gameWidth, (int) gameHeight));
        this.setBackground(new Color(89, 108, 171, 255));

        final JPanel content_panel = new JPanel();
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
            public void componentResized(final ComponentEvent e) {
                final Dimension d = new Dimension(content_panel.getWidth(), content_panel.getWidth() * 9 / 16);

                if (d.height > content_panel.getHeight()) {
                    d.height = content_panel.getHeight();
                    d.width = content_panel.getHeight() * 16 / 9;
                }

                scale = (float) d.width / gameWidth;

                panel.setPreferredSize(d);
                content_panel.revalidate();
                frame.revalidate();
            }
        });
        init();

        final Thread t = new Thread(this);
        t.start();
    }

    private void init() {
        score = 0;

        cloud = new Sprite(this, prefabCharacter, 375f, 400f, 2.0f, 500f, 0.3f, 0.625f, 0.92f, 1.1875f);

        actors.add(new ArrayList<>());
        actors.add(new ArrayList<>());
        actors.add(new ArrayList<>());
        actors.add(new ArrayList<>());

        final float flight_path_radius = 64.0f;

        // TODO remove magic numbers
        powerupGen = new PowerupGen(this, cloud, actors.get(0), actors.get(1), prefabPickup, prefabPowerup, 2.0f, 0.03f,
                0.3f, flight_path_radius);

        final var flight_path = new Gizmo(
                new Rectangle2D.Float(cloud.xMidOffset - flight_path_radius, cloud.yMidOffset - flight_path_radius,
                        2.0f * flight_path_radius, 2.0f * flight_path_radius),
                Color.GREEN, Gizmo.Shape.OVAL);
        cloud.addGizmo(flight_path);

        enemyGen = new EnemyGen(this, actors.get(2), prefabEnemy, 2.0f, 0.05f);

        fogGen = new FogGen(this, actors.get(3), prefabFog, 0.5f, 1.2f);
        fogGen.spawn(10, 0.03f);
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

        for (final ArrayList<Sprite> layer : actors) {
            for (final Sprite it : layer) {
                it.move();
            }
        }
    }

    private void update() {
        cloud.update();

        for (final ArrayList<Sprite> layer : actors) {
            for (final Sprite it : layer) {
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
            cloud.yVelocity = 0.0f;
        if (keyLeft == keyRight)
            cloud.xVelocity = 0.0f;
    }

    public void collisionBounds() {
        if (cloud.x < 0.0f) {
            cloud.x = 0.0f;
        }

        if (cloud.y < 0.0f) {
            cloud.y = 0.0f;
        }

        if (cloud.x + cloud.widthScaled > gameWidth) {
            cloud.x = gameWidth - cloud.widthScaled;
        }

        if (cloud.y + cloud.heightScaled > gameHeight) {
            cloud.y = gameHeight - cloud.heightScaled;
        }
    }

    private void collisionObjects() {
        collisionBounds();

        for (final Sprite pickup : actors.get(0)) {
            if (cloud.distance(pickup) <= 0.0f) {
                pickup.toRemove = true;
                powerupGen.pickup();
            }
        }

        for (final Sprite powerup : actors.get(1)) {
            for (final Sprite enemy : actors.get(2)) {
                if (powerup.visible && powerup.distance(enemy) <= 0.0f) {
                    powerup.visible = false;
                    enemy.toRemove = true;

                    break;
                }
            }
        }

        for (final Sprite enemies : actors.get(2)) {
            if (cloud.distance(enemies) <= 0.0f) {
                reset();
                return;
            }
        }
    }

    private void drawGizmos(final Graphics g) {
        if (cloud != null) {
            cloud.drawGizmos(g);
        }

        for (final ArrayList<Sprite> layer : actors) {
            for (final Sprite sprite : layer) {
                sprite.drawGizmos(g);
            }
        }
    }

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);

        if (cloud != null) {
            cloud.draw(g);
        }

        for (final ArrayList<Sprite> layer : actors) {
            for (final Sprite sprite : layer) {
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
        final ThreadLocalRandom t = ThreadLocalRandom.current();

        float powerup_chance = 0.25f;
        float enemy_chance = 0.5f;

        if (score < 1000.0f) {
            enemyGen.setSpeed(0.075f);
        } else if (score < 3000.0f) {
            powerup_chance = 0.5f;
            enemy_chance = 0.35f;
            enemyGen.setSpeed(0.1f);
        } else if (score < 4000.0f) {
            enemy_chance = 0.3f;
            enemyGen.setSpeed(0.15f);
        }

        if (t.nextFloat(0.0f, 1.0f) <= powerup_chance) {
            powerupGen.spawn(1);
        }

        if (t.nextFloat(0.0f, 1.0f) <= enemy_chance) {
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
            // frame time difference converted to one over milli seconds floating point
            deltaTime = (float) (System.nanoTime() - last) * 1e-6f;
            last = System.nanoTime();
            fixedUpdateCounter += deltaTime;
            fps = 1e3f / deltaTime;

            // Add 25 per second
            score += deltaTime * 0.025f;

            updateVelocity();
            moveObjects();
            collisionObjects();
            update();

            if (fixedUpdateCounter >= fixedUpdateInterval) {
                fixedUpdateCounter = 0.0f;
                fixedUpdate();
            }

            enemyGen.reuseOutOfBounds();

            for (final ArrayList<Sprite> it : actors) {
                for (int i = it.size() - 1; i >= 0; i--) {
                    if (it.get(i).toRemove) {
                        it.remove(i);
                    }
                }
            }

            repaint();

            try {
                Thread.sleep(8);
            } catch (final InterruptedException e) {
                System.out.println("Thread got interrupted");
            }
        }
    }

    void keyAction(final KeyEvent e, final boolean pressed) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP, KeyEvent.VK_W -> keyUp = pressed;
            case KeyEvent.VK_LEFT, KeyEvent.VK_A -> keyLeft = pressed;
            case KeyEvent.VK_DOWN, KeyEvent.VK_S -> keyDown = pressed;
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> keyRight = pressed;
        }
    }

    @Override
    public void keyPressed(final KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_G) {
            debugMode = !debugMode;
        }

        keyAction(e, true);
    }

    @Override
    public void keyReleased(final KeyEvent e) {
        keyAction(e, false);
    }

    @Override
    public void keyTyped(final KeyEvent e) {
    }

    public void addScore(final float amount) {
        score += amount;
    }

    public float getDeltaTime() {
        return deltaTime;
    }

    public float getGameHeight() {
        return gameHeight;
    }

    public float getScale() {
        return scale;
    }

    public float getGameWidth() {
        return gameWidth;
    }
}

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
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable, KeyListener {
    private final JFrame frame;
    private float scale = 1.0f;
    private final float gameWidth = 1280.0f;
    private final float gameHeight = 720.0f;

    private float fps = 0.0f;
    /// Time difference between last frame. Used to sync different frame rates.
    private float deltaTimeMs;
    private float fixedUpdateCounter;
    private final float fixedUpdateInterval = 1000.0f;

    private float score;
    private float scoreBest;

    private final Prefab prefabCharacter;
    private final Prefab prefabEnemy;
    private final Prefab prefabFog;
    private final Prefab prefabPowerup;
    private final Prefab prefabPickup;

    private Sprite character = null;

    private PowerupGen powerupGen;
    private EnemyGen enemyGen;
    private FogGen fogGen;

    private ArrayList<Entity> entities = new ArrayList<>();
    /// these Entities are added in the next frame
    private ArrayList<Entity> entitiesAddQueue = new ArrayList<>();

    // TODO use joystick
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
        content_panel.setBackground(Color.BLACK);
        content_panel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        content_panel.add(this);

        frame = new JFrame("Fluffy");
        frame.setLocation(100, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addKeyListener(this);
        frame.setBackground(Color.BLACK);
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

        // TODO replace factor with offset
        final float customRadiusFactor = 0.625f;
        final float customXMidFactor = 0.92f;
        final float customYMidFactor = 1.1875f;
        character = new Character(this, prefabCharacter, customRadiusFactor, customXMidFactor, customYMidFactor);
        character.setX(375f);
        character.setY(400f);
        character.setSpriteScale(2.0f);
        character.setAnimationImageTime(500f);
        character.setSpeed(0.3f);
        entities.add(character);

        final float flight_path_radius = 64.0f;
        final float powerup_pickup_scale = 2.0f;
        powerupGen = new PowerupGen(this, prefabPickup, prefabPowerup, powerup_pickup_scale, flight_path_radius);
        powerupGen.setFallingSpeed(0.03f);
        powerupGen.setRotationSpeed(0.3f);

        final var flight_path = new Gizmo(
                new Rectangle2D.Float(character.xMidOffset - flight_path_radius,
                        character.yMidOffset - flight_path_radius,
                        2.0f * flight_path_radius, 2.0f * flight_path_radius),
                Color.GREEN, Gizmo.Shape.OVAL);
        character.addGizmo(flight_path);

        final float enemy_scale = 2.0f;
        enemyGen = new EnemyGen(this, prefabEnemy, enemy_scale);
        enemyGen.setSpeed(0.05f);

        final float fog_min_scale = 0.5f;
        final float fog_max_scale = 1.2f;
        fogGen = new FogGen(this, prefabFog, fog_min_scale, fog_max_scale);
        fogGen.spawn(10, 0.03f);
    }

    private void reset() {
        if (score > scoreBest) {
            scoreBest = score;
        }

        entities.clear();
        init();
    }

    private void updateVelocity() {
        if (keyUp) {
            character.yVelocity = -character.speed;
        } else if (keyDown) {
            character.yVelocity = character.speed;
        } else {
            character.yVelocity = 0;
        }

        if (keyLeft) {
            character.xVelocity = -character.speed;
        } else if (keyRight) {
            character.xVelocity = character.speed;
        } else {
            character.xVelocity = 0;
        }
    }

    public void collisionBounds() {
        if (character.x < 0.0f) {
            character.x = 0.0f;
        }

        if (character.y < 0.0f) {
            character.y = 0.0f;
        }

        if (character.x + character.widthScaled > gameWidth) {
            character.x = gameWidth - character.widthScaled;
        }

        if (character.y + character.heightScaled > gameHeight) {
            character.y = gameHeight - character.heightScaled;
        }
    }

    private void collisionObjects() {
        collisionBounds();

        for (final Entity entity : entities) {
            switch (entity) {
                case Pickup pickup -> {
                    if (pickup.hasCollided(character)) {
                        pickup.toRemove = true;
                        powerupGen.pickup();
                    }
                }
                case Enemy enemy -> {
                    if (enemy.hasCollided(character)) {
                        reset();
                        return;
                    }

                    for (final Entity entity2 : entities) {
                        if (entity2 instanceof final Powerup powerup) {
                            if (powerup.visible && powerup.hasCollided(enemy)) {
                                powerup.visible = false;
                                enemy.visible = false;
                                enemy.toRemove = true;

                                break;
                            }
                        }
                    }
                }
                default -> {
                }
            }
        }
    }

    private void drawGizmos(final Graphics g) {
        if (character != null) {
            character.drawGizmos(g);
        }

        for (final Entity entity : entities) {
            entity.drawGizmos(g);
        }
    }

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);

        if (character != null) {
            character.draw(g);
        }

        for (final Entity entity : entities) {
            entity.draw(g);
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

    private void moveAll() {
        // TODO overwrite pickup and powerup move function
        powerupGen.moveAll();

        for (final Entity entity : entities) {
            entity.move();
        }
    }

    @Override
    public void run() {
        long last = System.nanoTime();

        while (frame.isVisible()) {
            deltaTimeMs = (float) (System.nanoTime() - last) * 1e-6f;
            last = System.nanoTime();
            fixedUpdateCounter += deltaTimeMs;
            fps = 1e3f / deltaTimeMs;

            // Add 25 per second
            score += deltaTimeMs * 0.025f;

            if (entitiesAddQueue.size() > 0) {
                entities.addAll(entitiesAddQueue);
                entitiesAddQueue.clear();
            }

            updateVelocity();

            moveAll();

            collisionObjects();

            for (final Entity entity : entities) {
                entity.update();
            }

            if (fixedUpdateCounter >= fixedUpdateInterval) {
                fixedUpdateCounter = 0.0f;
                fixedUpdate();
            }

            enemyGen.reuseOutOfBounds();

            entities.removeIf(entity -> entity.toRemove);

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

    public float getDeltaTimeMs() {
        return deltaTimeMs;
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

    public Iterator<Entity> iterateObjects() {
        return entities.iterator();
    }

    public void addObject(Entity entity) {
        entitiesAddQueue.add(entity);
    }

    public Sprite getCharacter() {
        return character;
    }
}

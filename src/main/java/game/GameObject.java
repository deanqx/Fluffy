// TODO rename to fluffy
package game;

import java.awt.Graphics;

public abstract class GameObject {
    public float x = 0.0f;
    public float y = 0.0f;
    public float width = 0.0f;
    public float height = 0.0f;
    public boolean visible = true;
    public boolean toRemove = false;

    public abstract void move();

    public abstract void update();

    public abstract void draw(Graphics g);

    public abstract void drawGizmos(Graphics g);

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}

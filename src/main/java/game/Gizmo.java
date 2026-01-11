package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

/*
 * Gizmos (also called Ghosts) are used to display things like hitbox in debug mode.
 */
public class Gizmo extends Rectangle2D.Float {
    public enum Shape {
        OVAL,
    }

    private final Color color;
    private final Shape shape;
    /// can be null
    private GameObject parent = null;

    public Gizmo(final Rectangle2D.Float position_dimension, final Color color, final Shape shape) {
        this.color = color;
        this.shape = shape;
        this.x = position_dimension.x;
        this.y = position_dimension.y;
        this.width = position_dimension.width;
        this.height = position_dimension.height;
    }

    public void drawGizmo(final Graphics g, final float scale) {
        final float parent_x = parent == null ? 0.0f : (float) parent.getX();
        final float parent_y = parent == null ? 0.0f : (float) parent.getY();

        switch (shape) {
            case OVAL -> {
                final int x_scaled = (int) ((parent_x + x) * scale);
                final int y_scaled = (int) ((parent_y + y) * scale);
                final int width_scaled = (int) (width * scale);
                final int height_scaled = (int) (height * scale);

                g.setColor(color);
                g.drawOval(x_scaled, y_scaled, width_scaled, height_scaled);
            }
        }
    }

    public void setParent(GameObject parent) {
        this.parent = parent;
    }
}

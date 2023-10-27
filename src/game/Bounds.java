package game;

public class Bounds
{
    public float top;
    public float bottom;
    public float left;
    public float right;
    public boolean hit;

    public Bounds(float top, float bottom, float left, float right)
    {
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
    }
}

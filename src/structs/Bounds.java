package structs;

public class Bounds
{
    public double top;
    public double bottom;
    public double left;
    public double right;
    public boolean hit;

    public Bounds(double top, double bottom, double left, double right)
    {
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
    }

    public Bounds clone()
    {
        Bounds clone = new Bounds(top, bottom, left, right);
        clone.hit = hit;
        return clone;
    }
}

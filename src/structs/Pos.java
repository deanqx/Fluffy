package structs;

public class Pos
{
    public double x;
    public double y;

    public double distance(Pos to)
    {
        double a = to.x - x;
        double b = to.y - y;
        return Math.sqrt(a * a + b * b);
    }
}

package structs;

public class Pos
{
    public float x;
    public float y;

    public float distance(Pos to)
    {
        float a = to.x - x;
        float b = to.y - y;
        return (float) Math.sqrt(a * a + b * b);
    }
}

package game;

public class Main
{
    // unterschied zwischen initialisieren und deklarieren

    public static void main(String[] args)
    {
        System.out.println("Working Directory: " + System.getProperty("user.dir"));
        GamePanel panel = new GamePanel(800, 600);
    }
}

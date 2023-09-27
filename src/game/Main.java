package game;

public class Main
{
    // Hausaufgaben:
    int x0 = 1; // Dies nennt man initialisierung
    int x1; // Dies nennt man deklaration

    public static void main(String[] args)
    {
        System.out.println("Working Directory: " + System.getProperty("user.dir"));
        GamePanel panel = new GamePanel(800, 600);
    }
}

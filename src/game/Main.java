package game;

public class Main
{
    public static void main(String[] args)
    {
        System.setProperty("sun.java2d.opengl", "true"); // If not enabled frames are only updated with interaction

        System.out.println("Working Directory: " + System.getProperty("user.dir"));
        GamePanel panel = new GamePanel(800, 600);
    }
}

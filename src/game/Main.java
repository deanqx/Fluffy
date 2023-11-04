package game;

public class Main
{
    public static void main(String[] args)
    {
        // If not enabled frames are only updated with interaction
        System.setProperty("sun.java2d.opengl", "true");

        System.out.println("Working Directory: " + System.getProperty("user.dir"));
        new GamePanel(800, 600);
    }
}

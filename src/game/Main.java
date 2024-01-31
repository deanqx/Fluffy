package game;

public class Main
{
    public static void main(String[] args)
    {
        System.setProperty("sun.java2d.opengl", "true");

        System.out.println("Working Directory: " + System.getProperty("user.dir"));
        new GamePanel();
    }
}

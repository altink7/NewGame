package at.altin.local;
import at.altin.local.display.Window;
import at.altin.local.service.GraphicsLoader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.ServerSocket;


public class Game extends Canvas implements Runnable{
    public static final int WIDTH = 1200;
    public static final int HEIGHT = 750;
    public boolean running;
    public static boolean gameover;
    public static BufferedImage img_welcome;
    public static int score;
    Thread thread;
    public ServerSocket serverSocket;

    /***JavaDoc
     * -Hier wird das Spiel ausgeführt
     *
     *
     */
    public Game() {
    }

    /***Main
     *ein neues Fenster wird hinzugefügt,
     *eine Änderung von der Größe wird nicht empfohlen
     *
     */
    public static void main(String[] args) {
        new Window(1000, 750, "NewGame", new Game());
    }

    public synchronized void start() {
        this.running = true;
        this.thread = new Thread();
        this.thread.start();
        this.run();
    }

    @Override
    public void run() {
    }
}
package at.altin.local;
import at.altin.local.display.Window;
import at.altin.local.handlers.KeyHandler;
import at.altin.local.handlers.MouseHandler;
import at.altin.local.handlers.ObjectHandler;
import at.altin.local.service.GraphicsLoader;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
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
        new Window(WIDTH, HEIGHT, "NewGame", new Game());
    }

    public synchronized void start() {
        this.addKeyListener(new KeyHandler());
        this.addMouseListener(new MouseHandler());
        this.running = true;
        this.thread = new Thread();
        this.thread.start();
        this.run();
    }

    public void init() {
        score=0;
        gameover=false;
        running=true;
        img_welcome = GraphicsLoader.readGraphics("C:\\Users\\User\\IdeaProjects\\NewGame\\welcome.png");
    }

    public void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
        } else {
            Graphics g = bs.getDrawGraphics();
            ObjectHandler.render(g);
            g.fillRect(0,0,1200,750);
            g.drawImage(img_welcome, 0, 0, null);
            g.setFont(new Font("Arial", 1, 48));
            g.setColor(Color.WHITE);
            String s = "Press Space";
            int textWidth = g.getFontMetrics().stringWidth(s);
            g.drawString(s, 950 - textWidth / 2, 200);
            g.dispose();
            bs.show();
        }
    }

    public void tick() {
        if (!gameover) {
            ObjectHandler.tick();
        }

    }

    public void test() {
    }



    @Override
    public void run() {
        this.init();
        this.requestFocus();
        long pastTime = System.nanoTime();
        double amountOfTicks = 1.0D;
        double ns = 1.0E9D / amountOfTicks;
        double delta = 0.0D;
        long timer = System.currentTimeMillis();
        int updates = 0;
        int frames = 0;
        while(this.running) {
            long now = System.nanoTime();
            delta += (double)(now - pastTime) / ns;

            for(pastTime = now; delta > 0.0D; --delta) {
                this.tick();
                ++updates;
                this.render();
                ++frames;
            }

            if (System.currentTimeMillis() - timer > 1000L) {
                timer += 1000L;
                System.out.println("FPS: " + frames + " | TICKS: " + updates);
                updates = 0;
                frames = 0;
            }
        }
    }
}
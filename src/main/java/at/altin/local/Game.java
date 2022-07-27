package at.altin.local;
import at.altin.local.display.ClickArea;
import at.altin.local.display.Window;
import at.altin.local.gameObjects.Enemy;
import at.altin.local.gameObjects.Item;
import at.altin.local.gameObjects.Spaceship;
import at.altin.local.handlers.KeyHandler;
import at.altin.local.handlers.MouseHandler;
import at.altin.local.handlers.ObjectHandler;
import at.altin.local.levels.Level1;
import at.altin.local.service.GraphicsLoader;
import at.altin.local.slides.StaticSlide;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.net.ServerSocket;


public class Game extends Canvas implements Runnable{

    public static int phase =0;
    public static final int WIDTH = 1200;
    public static final int HEIGHT = 750;
    public boolean running;
    public static boolean gameover;
    public static BufferedImage img_welcome;
    public static BufferedImage img_spaceships;
    public static BufferedImage img_button;
    public static boolean spaceshipSelected;
    public static ClickArea[] button_select = new ClickArea[4];
    public static Spaceship spaceship;
    public static int score;
    Thread thread;
    public ServerSocket serverSocket;
    public static int keyNumber=0;
    public Spaceship ship= new Spaceship(WIDTH/2-40,550);
    Level1 l1= new Level1(ship);
    public Item[] fire = new Item[1000];// nur 1000 amo, sonst gameover
    public Item[] enemy_fire = new Item[1000];// nur 1000 amo, sonst gamewin
    public int fireCounter =0;
    public int enemyFireCounter =0;

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
        running=true; //Bilder werden vorübergehend durch Absolut-Path geladen
        img_welcome = GraphicsLoader.readGraphics("welcome_rev1.png");
        img_spaceships = GraphicsLoader.readGraphics("spaceships.png");
        img_button=GraphicsLoader.readGraphics("button.png");

        int xValue=10;
        for(int i =0;i< button_select.length;i++) {
            button_select[i] = new ClickArea(xValue, 600, 150, 85, img_button);
            xValue+=295;
        }
    }

    public void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
        } else {
            Graphics g = bs.getDrawGraphics();
            ObjectHandler.render(g);
            if(keyNumber==0&&!spaceshipSelected) {
                phase=1; // Phase 1 ist Startbildschirm

                StaticSlide p1 = new StaticSlide(Color.lightGray,1200,750,img_welcome,0,0,"Arial",
                        1,48,Color.WHITE,"Press Space",0,950,200);
                p1.drawGraphics(g);

            }
            else if(keyNumber==10&&!spaceshipSelected){
                phase=2; //Phase 2: hier wird ein Raumschiff gewählt

                StaticSlide p2= new StaticSlide(Color.lightGray,1200,750,img_spaceships,0,0,"Arial", 2,48,
                        Color.WHITE,"Wähle dein Raumschiff!",0,WIDTH/2 - g.getFontMetrics().stringWidth("Wähle dein Raumschiff!") / 2,100);
                p2.drawGraphics(g);

                for(ClickArea b: button_select){
                    b.render(g);
                }
            }
            //hier beginnt das eigentliche Spiel!
            else if(spaceshipSelected){
                phase=3; //Phase 3:level1
                ship.setImg_spaceship(MouseHandler.selectedButton);
                l1.setSpaceship(ship);
                l1.drawGraphics(g);

                showFire(g,7,10); //updateSpeed=wie oft es schießen soll(bsp 7: s/FPS*7), fireSpeed= Schussgeschwindigkeit
                showEnemyFire(g,7*10,10,l1);

            }
            g.dispose();
            bs.show();
        }
    }

    public void showFire(Graphics g, int updateSpeed,int fireSpeed) {
        for (Item i : fire) {
            if (i != null) i.updateY(fireSpeed);
        }
            if (fireCounter % updateSpeed == 0) {
                fire[fireCounter / updateSpeed] = new Item(ship);
            }
            for (int i = 0; i * updateSpeed <= fireCounter; i++) {
                fire[i].initFire(g);
            }
            fireCounter++;
        }
    public void showEnemyFire(Graphics g, int updateSpeed,int fireSpeed, Level1 l1) {
        for (Item i : enemy_fire) {
            if (i != null) i.updateY(fireSpeed);
        }
        if (enemyFireCounter % updateSpeed == 0) {
            for(int j=0;j<l1.enemys.size();j++) {
                enemy_fire[enemyFireCounter / updateSpeed] = new Item(l1.enemys.get(j));
                enemy_fire[enemyFireCounter / updateSpeed].setImage(GraphicsLoader.readGraphics("enemy_fire.png"));
                enemyFireCounter++;
            }
        }
        for (int i = 0; i * updateSpeed <= enemyFireCounter; i++) {
                enemy_fire[i].initFire(g);
        }
        enemyFireCounter++;
    }

    public void tick() {
        if (!gameover) {
            ObjectHandler.tick();
        }

    }

    @Override
    public void run() {
        this.init();
        this.requestFocus();
        long pastTime = System.nanoTime();
        double amountOfTicks = 60.0D; //Frames einstellen
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
package at.altin.local;
import at.altin.local.display.ClickArea;
import at.altin.local.display.Window;
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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class Game extends Canvas implements Runnable{

    public static int phase =0;
    public static final int WIDTH = 1200;
    public static final int HEIGHT = 750;
    public boolean running;
    public static boolean gameover;
    public static BufferedImage img_gameover;
    public static BufferedImage img_welcome;
    public static BufferedImage img_spaceships;
    public static BufferedImage img_button;
    public static boolean spaceshipSelected;
    public static ClickArea[] button_select = new ClickArea[4];
    public static int score;
    Thread thread;
    public ServerSocket serverSocket;
    public static int keyNumber=0;
    public static Spaceship ship= new Spaceship(WIDTH/2-40,550);
    public static List<Spaceship> enemy_ships= new LinkedList<>();
    Level1 l1= new Level1(ship);
    ArrayList<Integer> deleteEnemy = new ArrayList<>();
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
        img_gameover=GraphicsLoader.readGraphics("gameover.png");

        int xValue=10;
        for(int i =0;i< button_select.length;i++) {
            button_select[i] = new ClickArea(xValue, 600, 150, 85, img_button);
            xValue+=295;
        }
        for(int i =0;i< 10;i++) {
            enemy_ships.add(new Spaceship(80,86,GraphicsLoader.readGraphics("enemy.png"),i));
            enemy_ships.get(i).setX(100*(i+1));
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

                if(gameover){
                    p1.setImage(GraphicsLoader.readGraphics("gameover.png"));
                }
                p1.drawGraphics(g);
            }
            else if(keyNumber==10&&!spaceshipSelected){
                phase=2; //Phase 2: hier wird ein Raumschiff gewählt
                gameover=false;
                StaticSlide p2= new StaticSlide(Color.lightGray,1200,750,img_spaceships,0,0,"Arial", 2,48,
                        Color.WHITE,"Wähle dein Raumschiff!",0,WIDTH/2 - g.getFontMetrics().stringWidth("Wähle dein Raumschiff!") / 2,100);
                p2.drawGraphics(g);

                for(ClickArea b: button_select){
                    b.render(g);
                }
            }

            //hier beginnt das eigentliche Spiel!
            else if(spaceshipSelected) {
                phase = 3; //Phase 3:level1
                ship.setImg_spaceship(MouseHandler.selectedButton);
                l1.setSpaceship(ship);
                l1.setEnemys(enemy_ships);
                l1.drawGraphics(g);

                ship.showFire(g,7,10,GraphicsLoader.readGraphics("spaceship_fire.png"),false); //updateSpeed=wie oft es schießen soll(bsp 7: s/FPS*7), fireSpeed= Schussgeschwindigkeit
                for(Spaceship e:enemy_ships){
                    e.showFire(g,30,-10,GraphicsLoader.readGraphics("enemy_fire.png"),true);
                }
                for (Integer i :checkCollisions()) {
                    enemy_ships.removeIf(e -> e.getId() == i);
                }

                if(enemy_ships.size()==0){
                    phase= 4;
                    g.drawImage(GraphicsLoader.readGraphics("level1_finish.png"),0,0,null);
                    g.drawString("Press Space",WIDTH/2-g.getFontMetrics().stringWidth("Press Space")/2,700);

                    if(keyNumber==10){
                        //Level2 folgt
                    }
                }
                gameOver();
            }


            g.dispose();
            bs.show();
        }
    }

    public ArrayList<Integer> checkCollisions(){
        for(Spaceship sp : enemy_ships) {
            for (Item f : ship.fire) {
                if (sp.getBounds().contains(f.getPoint())) {
                    deleteEnemy.add((int) sp.getId());
                }
            }
        }
        return deleteEnemy;
    }

    public void gameOver() {
        for (Spaceship sp : enemy_ships) {
            for (Item f : sp.fire) {
                if (ship.getBounds().contains(f.getPoint())) {
                    gameover=true;
                    keyNumber=0;
                    spaceshipSelected=false;

                    enemy_ships = new LinkedList<>();
                    for(int i =0;i< 10;i++) {
                        enemy_ships.add(new Spaceship(80,86,GraphicsLoader.readGraphics("enemy.png"),i));
                        enemy_ships.get(i).setX(100*(i+1));
                    }
                    deleteEnemy = new ArrayList<>();
                    ship= new Spaceship(WIDTH/2-40,550);

                }
            }
        }
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
            while (this.running) {
                long now = System.nanoTime();
                delta += (double) (now - pastTime) / ns;

                for (pastTime = now; delta > 0.0D; --delta) {
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
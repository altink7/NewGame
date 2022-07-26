package at.altin.local.gameObjects;

import at.altin.local.Game;
import at.altin.local.display.GameObject;
import at.altin.local.service.GraphicsLoader;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class Spaceship extends GameObject {
    boolean running;
    public int xVal;
    public int yVal;
    public BufferedImage img_spaceship;

    public Spaceship(){

    }
    public Spaceship(int xVal, int yVal){
        this.xVal=xVal;
        this.yVal=yVal;
    }
    public Spaceship(int n, int xVal, int yVal){
        this.img_spaceship =GraphicsLoader.readGraphics("spaceship_"+n+".png");
        this.xVal=xVal;
        this.yVal=yVal;
    }

    public BufferedImage getImg_spaceship() {
        return img_spaceship;
    }

    public void setImg_spaceship(int n_img) {
        this.img_spaceship = GraphicsLoader.readGraphics("spaceship_"+n_img+".png");
    }

    public Spaceship(Spaceship other){
        this.img_spaceship =other.img_spaceship;
        this.xVal=other.xVal;
        this.yVal=other.yVal;
    }


    public Spaceship(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public void checkCollisions(){

    }

    public int checkBordersX(){
        //checkt die Grenzen und sagt in welche richtung man nicht mehr gehen kann-X-Achse

        if(xVal < 0 ) return 1;
        if(xVal > Game.WIDTH-100) return 2;
            return 0;
    }
    public int checkBordersY(){
        //checkt die Grenzen und sagt in welche richtung man nicht mehr gehen kann-Y-Achse
        if(yVal < 0) return 1;
        if(yVal > Game.HEIGHT-150) return 2;
        return 0;
    }

    public void moveSpaceship(){
            int keyEvent=Game.keyNumber;
            if (keyEvent==2&&checkBordersY()!=2) {
                yVal += 7;
                //Game.keyNumber=0;
            }
            if (keyEvent==4&&checkBordersX()!=1) {
                xVal -= 7;
                //Game.keyNumber=0;
            }
            if (keyEvent==8&&checkBordersY()!=1) {
                yVal -= 7;
                //Game.keyNumber=0;
            }
            if (keyEvent==6&&checkBordersX()!=2) {
                xVal += 7;
                //Game.keyNumber=0;
            }
    }


    @Override
    public void tick() {

    }

    @Override
    public void render(Graphics var1) {

    }



}

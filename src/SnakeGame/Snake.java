package SnakeGame;

import GUI.Game;
import SnakeGame.KeyBoard;
import Support.Model.Player;

import java.awt.*;

public class Snake {

    private Point[] snakeBody = new Point[100];

    private int lengthOfSnake = 3;

    private int scores = 0;

    private Player player;

    private boolean isDie = false;

    public boolean getDie() {
        return isDie;
    }

    public void setDie(boolean die) {
        isDie = die;
    }


    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public KeyBoard getKey() {
        return key;
    }

    public void setKey(KeyBoard key) {
        this.key = key;
    }

    private  KeyBoard key;


    public Snake(int ID){

        int x = 0;
        int y = 0;

        key = new KeyBoard();

        if( ID == 0){

            x = 3;
            y = 5;

            key.setKeyRight();
        }
        else if( ID == 1){
            x = 3;
            y = 7;

            key.setKeyRight();
        }
        else if( ID == 2){
            x = 3;
            y = 9;
            key.setKeyRight();
        }
        else if( ID == 3){
            x = 3;
            y = 11;
            key.setKeyRight();
        }

        for (int i = 0; i < 100; i++)
            snakeBody[i] = new Point( x , y );

    }

    public  void updateSnake(){
        if(!isDie) {
            for (int i = lengthOfSnake - 1; i >= 1; --i)
                snakeBody[i].setLocation(snakeBody[i - 1]);
                if (key.up)
                    snakeBody[0].y -= 1;
                else if (key.down)
                    snakeBody[0].y += 1;
                else if (key.left)
                    snakeBody[0].x -= 1;
                else if (key.right)
                    snakeBody[0].x += 1;
        }
    }

    public void upScore(int score){
        scores = score;
    }

    public void drawSnake(Graphics g , int id){

        for (int i = 0; i < lengthOfSnake; ++i) {

            g.setColor(Color.darkGray);
            g.drawRect(snakeBody[i].x, snakeBody[i].y, Game.SCALE, Game.SCALE);
            switchColors(g , id);
            g.fillRect(snakeBody[i].x, snakeBody[i].y, Game.SCALE - 1, Game.SCALE - 1 );
        }
    }

    public boolean dieCollideWithAnotherSnake(Snake snake){
        Point head = snakeBody[0];

        for(int i = 0 ; i < snake.getLengthOfSnake() ; i++){
            if(head.equals(snake.getSnakeBody()[i]))
                return false;
        }
       return true;
    }


    public int getLengthOfSnake(){
        return  lengthOfSnake;
    }


    private void switchColors(Graphics g , int ID){

        switch (ID){
            case 0: g.setColor(Color.green); break;
            case 1: g.setColor(Color.red); break;
            case 2: g.setColor(Color.orange); break;
            case 3: g.setColor(Color.blue); break;
        }
    }

    public boolean isOutOfBounds(int width , int height) {
        Point head = snakeBody[0];

        return head.getX() < 0 || head.getY() < 0
                || head.getX() >= Game.width  || head.getY() >= Game.height;
    }


    public boolean isCollidingWith(Food food) {
        return snakeBody[0].equals(food.getPosition());
    }
    public boolean isDead() {
        for(int i = 1 ; i < lengthOfSnake ; i++)
            if(snakeBody[0].x == snakeBody[i].x && snakeBody[0].y == snakeBody[i].y)
                return true;

        return  false;
    }

    public int getScores(){
        return scores;
    }
    public void grow() {
        lengthOfSnake++;
        scores++;
    }
    public Point[] getSnakeBody(){
        return snakeBody;
    }
}

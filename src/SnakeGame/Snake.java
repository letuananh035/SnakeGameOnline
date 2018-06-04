package SnakeGame;

import GUI.Game;
import SnakeGame.KeyBoard;

import java.awt.*;

public class Snake {

    private Point[] snakeBody = new Point[100];

    private int lengthOfSnake = 3;

    private int scores = 0;

    public Snake(int ID){

        int x = 0;
        int y = 0;

        if( ID == 0){

            x = Game.SCALE * 3;
            y = Game.SCALE * 10;
        }
        else if( ID == 1){
            x = Game.SCALE * 3;
            y = Game.SCALE * 15;
        }
        else if( ID == 2){
            x = Game.SCALE * 10;
            y = Game.SCALE * 10;
        }
        else if( ID == 3){
            x = Game.SCALE * 10;
            y = Game.SCALE * 15;
        }

        for (int i = 0; i < 100; i++)
            snakeBody[i] = new Point(x,y);

    }

    public void  updateSnake(KeyBoard key){

        for (int i = lengthOfSnake - 1; i >= 1; --i)
            snakeBody[i].setLocation(snakeBody[i - 1]);

        if (key.up)
            snakeBody[0].y -= Game.SCALE;
        else if (key.down)
            snakeBody[0].y += Game.SCALE;
        else if (key.left)
            snakeBody[0].x -= Game.SCALE;
        else if (key.right)
            snakeBody[0].x += Game.SCALE;
    }

    public void drawSnake(Graphics g , int id){

        for (int i = 0; i < lengthOfSnake; ++i) {

            g.setColor(Color.darkGray);
            g.drawRect(snakeBody[i].x, snakeBody[i].y, Game.SCALE, Game.SCALE);
            switchColors(g , id);
            g.fillRect(snakeBody[i].x, snakeBody[i].y, Game.SCALE - 1, Game.SCALE - 1 );
        }
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

        return head.getX() < Game.SCALE || head.getY() < Game.SCALE  * 8
                || head.getX() > ( width - 2 ) * Game.SCALE  || head.getY() > (height  - 2 )* Game.SCALE ;
    }


    public boolean isCollidingWith(Food food) {
        return snakeBody[0].equals(food.getPosition());
    }
    public boolean isDead() {
        for(int i = 1 ; i < lengthOfSnake ; i++)
            if(snakeBody[0].equals(snakeBody[i]))
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
}
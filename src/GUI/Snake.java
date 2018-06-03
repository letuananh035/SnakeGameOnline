package GUI;

import java.awt.*;

public class Snake {

    private Point[] snakeBody = new Point[100];

    private int lengthOfSnake = 5;

    private final int SCALE = 15;

    public Snake(){
        for (int i = 0; i < 100; i++)
            snakeBody[i] = new Point(200,300);

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

    public void drawSnake(Graphics g){

        for (int i = 0; i < lengthOfSnake; ++i) {
            g.setColor(Color.green);
            g.drawRect(snakeBody[i].x, snakeBody[i].y, 15, 15);
        }
    }
}

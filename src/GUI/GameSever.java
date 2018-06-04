package GUI;

import GUI.Sever.SeverLog;
import SnakeGame.Food;
import SnakeGame.KeyBoard;
import SnakeGame.Snake;
import Support.Model.Player;

import java.awt.*;
import java.util.List;
import java.util.Random;


public class GameSever implements Runnable{

    private static final long serialVersionUID = 1L;

    // screen game
    public static int width = 60;
    public static int height = 35;
    public static int SCALE = 15;
    public static String title = "Snake";
    private boolean running = false;


    int[][] dataTable = new int[height][width];

    // snake
    private Food food;
    private Snake[] snakeList = new Snake[4];
    private Random random = new Random();

    private int numberOfSnake = 4;
    // thread
    private int delay = 100;
    private Thread thread;

    // keyboard
    private KeyBoard key;

    SeverLog server;
    //socket;


    public GameSever() {
       // this.server = server;

//        RspHandler handler = new RspHandler();
//        BlockData blockData = new BlockData(TypeBlock.START, "Create a new room");
//
//        try {
//            client.send(blockData.toBytes(), handler);
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }
//        handler.waitForResponse();

        // create food

        food = new Food(getRandomPosition());

        // generate snake
        for(int i = 0 ; i < 4 ; i++){
            snakeList[i] = new Snake(i);
          //  snakeList[i].setPlayer(listPlayer.get(i));
        }

        start();
    }

    public  int[][] getDataTable(){
        return dataTable;
    }

    public void paintComponent(Graphics g) {
//
//        super.paintComponent(g);
//
//        g.setColor(Color.darkGray);
//        g.fillRect(0, 0, getWidth(), getHeight());
//
//        int blockSize = width / 4;
//
//        for (int i = 0 ; i < 4 ; ++i){
//            g.setColor(Color.white);
//            g.drawRect(blockSize * i * SCALE + SCALE, 10, (blockSize - 2 ) * SCALE , SCALE * 6);
//        }
//
//        for (int i = 0 ; i < numberOfSnake ; i++) {
//            switch (i){
//                case 0: g.setColor(Color.green); break;
//                case 1: g.setColor(Color.red); break;
//                case 2: g.setColor(Color.orange); break;
//                case 3: g.setColor(Color.blue); break;
//            }
//            g.setFont(new Font(Font.MONOSPACED ,Font.BOLD ,SCALE + 5 ));
//            g.drawString("Player: " + i ,blockSize * i * SCALE + 20 , SCALE * 3);
//            g.drawString("Score: " + snakeList[i].getScores()  ,blockSize * i * SCALE + 20 , SCALE * 5);
//            //  g.fillRect(blockSize * i * SCALE + 10, 10, (blockSize) * SCALE , 90);
//        }
//
//        g.setColor(Color.white);
//        g.drawRect(SCALE, SCALE * 8, width * SCALE - SCALE * 2 , height * SCALE - SCALE * 9);
//
//        g.setColor(Color.white);
//        g.drawRect((int)food.getPosition().getX(),(int)food.getPosition().getY(),SCALE ,SCALE );
//        g.fillRect((int)food.getPosition().getX(),(int)food.getPosition().getY(),SCALE ,SCALE );
//
//        for(int i = 0 ; i <  numberOfSnake ; i++)
//            snakeList[i].drawSnake( g , i);
//
//        g.dispose();
    }

    @Override
    public void run() {
        while (running) {
            update();
        }

    }

    public void update() {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        clearDataTable();

        for(int i = 0;i < snakeList.length;++i){

            snakeList[i].updateSnake();


            for(int iBody = 0 ; iBody < snakeList[i].getLengthOfSnake(); iBody++)
                dataTable[(int )snakeList[i].getSnakeBody()[iBody].y / Game.SCALE][(int )snakeList[i].getSnakeBody()[iBody].x/ Game.SCALE] = i + 1;


            if (snakeList[i].isCollidingWith(food)) {
                snakeList[i].grow();
                food.setPosition(getRandomPosition());

                dataTable[(int )food.getPosition().getY()/ Game.SCALE][(int )food.getPosition().getX()/ Game.SCALE] = -1;
                //Send update game
            }

            if (snakeList[i].isDead() || snakeList[i].isOutOfBounds(width ,height )) {
                //Send update game
                //JOptionPane.showMessageDialog(this, "Information", "Do you want to reset this game !", JOptionPane.INFORMATION_MESSAGE);
                //stop();
            }
        }

        //Save game;

        //repaint();
    }

    private void clearDataTable(){
        for (int i = 0 ; i < height ; i++){
            for (int j = 0 ; j < width ;j++){
                dataTable[i][j] = 0;
            }
        }
    }

    private Point getRandomPosition() {
        return new Point(random.nextInt(width  - 2) * SCALE + SCALE , random.nextInt(height - 9) * SCALE + SCALE * 9);
    }
    public synchronized void start() {
        running = true;
        thread = new Thread(this, "Display");
        thread.start();
    }

    public synchronized void stop() {
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

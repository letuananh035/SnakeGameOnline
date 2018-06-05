package GUI;

import Client.NioClient;
import GUI.Sever.SeverLog;
import SnakeGame.Food;
import SnakeGame.KeyBoard;
import SnakeGame.Snake;
import Support.Model.Player;
import javafx.geometry.Point2D;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;

import Support.Model.Player;
public class Game extends JPanel implements Runnable, ActionListener {

    private static final long serialVersionUID = 1L;

    // screen game
    public static int width = 58;
    public static int height = 26;
    public static int SCALE = 15;
    public static String title = "Snake";
    private boolean running = false;

    // snake
    private Food food;
    private Snake[] snakeList = new Snake[4];
    private Random random = new Random();

    private int numberOfSnake = 4;
    // thread
    private int delay = 10000;
    private Thread thread;

    // keyboard
    private KeyBoard key;

    NioClient client;
    int[][] data = new int[height][width];
    //socket;

    private int READY_COUNT = 10;

    public Game(NioClient client) {
        this.client = client;

        ClientLogin.roomGame = this;

//        RspHandler handler = new RspHandler();
//        BlockData blockData = new BlockData(TypeBlock.START, "Create a new room");
//
//        try {
//            client.send(blockData.toBytes(), handler);
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }
//        handler.waitForResponse();

        requestFocus();
        requestFocusInWindow();
        setFocusTraversalKeysEnabled(false);


        key = new KeyBoard();
        addKeyListener(key);
        setFocusable(true);
        //key.right = true;


        // create food

        food = new Food(getRandomPosition());

        // generate snake
        for(int i = 0 ; i < 4 ; i++)
            snakeList[i] = new Snake(i);

        for (int i = 0 ; i < height;i++){
            for (int j = 0 ; j < width;j++){
                data[i][j] = 0;
            }
        }

        Dimension size = new Dimension((width + 2) * SCALE, (height + 9) * SCALE);
        setPreferredSize(size);
        //start();
    }


    public void redraw(String list){

        String[] listItem = list.split("");
        try{
            for (int i = 0 ; i < height;i++){
                for (int j = 0 ; j < width;j++){
                    data[i][j] = Integer.parseInt(listItem[j + i * width]);
                }
            }
        }catch (Exception e){
            System.out.println(listItem.length);
        }

        repaint();
    }

    public void UpdateScore(String [] list){
            for(int i =0; i < list.length;++i){
                snakeList[i].upScore(Integer.parseInt(list[i]));
            }
            repaint();
    }

    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        g.setColor(Color.darkGray);
        g.fillRect(0, 0, getWidth(), getHeight());

        int blockSize = width / 4;

        for (int i = 0 ; i < 4 ; ++i){
            g.setColor(Color.white);
            g.drawRect(blockSize * i * SCALE + SCALE, 10, (blockSize - 2 ) * SCALE , SCALE * 6);
        }

        for (int i = 0 ; i < numberOfSnake ; i++) {

            switch (i){
                case 0: g.setColor(Color.green); break;
                case 1: g.setColor(Color.red); break;
                case 2: g.setColor(Color.orange); break;
                case 3: g.setColor(Color.blue); break;
            }

            g.setFont(new Font(Font.MONOSPACED ,Font.BOLD ,SCALE + 5 ));
            g.drawString("Player: " + i ,blockSize * i * SCALE + 20 , SCALE * 3);
            g.drawString("Score: " + snakeList[i].getScores()  ,blockSize * i * SCALE + 20 , SCALE * 5);
            //g.fillRect(blockSize * i * SCALE + 10, 10, (blockSize) * SCALE , 90);
        }

      //  g.setColor(Color.white);
       // g.drawRect(SCALE, SCALE * 8, width * SCALE - SCALE * 2 , height * SCALE - SCALE * 9);
//
//        g.setColor(Color.white);
//        g.drawRect((int)food.getPosition().getX(),(int)food.getPosition().getY(),SCALE ,SCALE );
//        g.fillRect((int)food.getPosition().getX(),(int)food.getPosition().getY(),SCALE ,SCALE );

       // for(int i = 0 ; i <  numberOfSnake ; i++)
          //  snakeList[i].drawSnake( g , i);




        if(READY_COUNT > 0) {

            setReadyCount( g , READY_COUNT);
            READY_COUNT--;
        }
        else
        {
            for (int i = 0 ; i < height;i++){
                for (int j = 0 ; j < width;j++){
                    switchColors( g , data[i][j]);
                    g.drawRect(SCALE + (int) j * SCALE, SCALE * 8 + (int) i * SCALE, SCALE, SCALE);
                    g.fillRect(SCALE + (int) j * SCALE, SCALE * 8 + (int) i * SCALE, SCALE, SCALE);
                }
            }
        }

        g.dispose();
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

       /* snakeList[0].setKey(key);
        snakeList[0].updateSnake();

        if (snakeList[0].isCollidingWith(food)) {
            snakeList[0].grow();
            food.setPosition(getRandomPosition());
        }

        if (snakeList[0].isDead() || snakeList[0].isOutOfBounds(width ,height )) {

            JOptionPane.showMessageDialog(this, "Information", "Do you want to reset this game !", JOptionPane.INFORMATION_MESSAGE);
            stop();
        }

        for(int i = 1 ; i < 4 ; i++){

            snakeList[i].updateSnake();
        }*/

        repaint();
    }


    private void setReadyCount(Graphics g, int ready){
        g.setColor(Color.white);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, SCALE *5));
        g.drawString(ready + "", (int) (width / 2) * SCALE - SCALE * 2, SCALE * 16);
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
    private void switchColors(Graphics g , int ID){

        switch (ID){
            case 1: g.setColor(Color.green); break;
            case 2: g.setColor(Color.red); break;
            case 3: g.setColor(Color.orange); break;
            case 4: g.setColor(Color.blue); break;
            case 5: g.setColor(Color.white); break;
            case 0: g.setColor(Color.black); break;
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub

    }


}

package GUI;

import Client.NioClient;
import Client.RspHandler;
import Support.BlockData;
import Support.TypeBlock;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.*;
import java.io.IOException;
import java.net.SocketException;
import java.util.Random;

import javax.swing.*;


public class Game extends JPanel implements Runnable, ActionListener {

    private static final long serialVersionUID = 1L;

    // screen game
    private static int width = 60;
    private static int height = 35;
    public static int SCALE = 15;
    public static String title = "Snake";
    private boolean running = false;

    // snake
    private int lengthOfSnake = 5;
    private Point[] snakeBody = new Point[100];

    private Snake[] snakeList = new Snake[4];

    // thread
    private int delay = 100;
    private Thread thread;

    // keyboard
    private KeyBoard key;

    NioClient client;
    //socket;


    public Game(NioClient client) {
        this.client = client;

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
        key.right = true;


        for(int i = 0 ; i < 4 ; i++)
            snakeList[i] = new Snake();


        Dimension size = new Dimension(width * SCALE, height * SCALE);
        setPreferredSize(size);
        start();
    }



    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        g.setColor(Color.darkGray);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.white);
        g.drawRect(10, 10, width * SCALE - 30, 90);

        g.setColor(Color.white);
        g.drawRect(10, 110, width * SCALE - 30, height * SCALE - 120);


        for(int i = 0 ; i < 4 ; i++)
            snakeList[i].drawSnake(g);

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

//        for (int i = lengthOfSnake - 1; i >= 1; --i)
//            snakeBody[i].setLocation(snakeBody[i - 1]);
//        if (key.up)
//            snakeBody[0].y -= 20;
//        else if (key.down)
//            snakeBody[0].y += 20;
//        else if (key.left)
//            snakeBody[0].x -= 20;
//        else if (key.right)
//            snakeBody[0].x += 20;

        for(int i = 0 ; i < 4 ; i++){


            Random rand = new Random();
            int  n = rand.nextInt(50) + 1;

            n = n % 4;


            if (n == 0) {
                key.up = true;
                key.down = false;
                key.left = false;
                key.right = false;
            }
            else if (n == 1) {
                key.up = false;
                key.down = true;
                key.left = false;
                key.right = false;
            }
            else if (n == 2) {
                key.up = false;
                key.down = false;
                key.left = true;
                key.right = false;
            }
            else if (n == 3) {
                key.up = false;
                key. down = false;
                key. left = false;
                key. right = true;
            }

            snakeList[i].updateSnake(key);
        }

        repaint();
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

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub

    }


}

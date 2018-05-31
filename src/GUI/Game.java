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

import javax.swing.*;


public class Game extends JPanel implements Runnable, ActionListener {

    private static final long serialVersionUID = 1L;

    // screen game
    private static int width = 300;
    private static int height = 168;
    private static int scale = 3;
    public static String title = "Snake";
    private boolean running = false;

    // snake
    private int lengthOfSnake = 5;
    private Point[] snakeBody = new Point[100];

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

        for (int i = 0; i < 100; i++) {
            snakeBody[i] = new Point();
        }
        snakeBody[0].move(200, 300);

        Dimension size = new Dimension(width * scale, height * scale);
        setPreferredSize(size);
        start();
    }



    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        g.setColor(Color.darkGray);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.white);
        g.drawRect(10, 10, width * scale - 30, 90);

        g.setColor(Color.white);
        g.drawRect(10, 110, width * scale - 30, height * scale - 150);

        for (int i = 0; i < lengthOfSnake; ++i) {
            g.setColor(Color.green);
            g.drawRect(snakeBody[i].x, snakeBody[i].y, 15, 15);
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

        for (int i = lengthOfSnake - 1; i >= 1; --i)
            snakeBody[i].setLocation(snakeBody[i - 1]);
        if (key.up)
            snakeBody[0].y -= 20;
        else if (key.down)
            snakeBody[0].y += 20;
        else if (key.left)
            snakeBody[0].x -= 20;
        else if (key.right)
            snakeBody[0].x += 20;

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

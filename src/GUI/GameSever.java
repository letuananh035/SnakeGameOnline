package GUI;

import GUI.Sever.SeverLog;
import Server.NioServer;
import SnakeGame.Food;
import SnakeGame.KeyBoard;
import SnakeGame.Snake;
import Support.Model.Player;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;


public class GameSever implements Runnable {

    private static final long serialVersionUID = 1L;

    // screen game
    public static int width = 58;
    public static int height = 26;
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
    //socket;

    private HashMap<Long,Integer> queue = new HashMap<Long,Integer>();

    public GameSever(List<Player> playerList) {

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
        snakeList = new Snake[playerList.size()];
        // generate snake
        for (int i = 0; i < playerList.size(); i++) {
            snakeList[i] = new Snake(i);
            snakeList[i].setPlayer(playerList.get(i));
        }

    }

    public int[][] getDataTable() {
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
            clearDataTable();
            update();
        }
    }

    public  void update() {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dataTable[(int) food.getPosition().getY()][(int) food.getPosition().getX()] = 5;

        synchronized (queue){
            for(Map.Entry<Long, Integer> entry : queue.entrySet()) {
                Long key = entry.getKey();
                Integer value = entry.getValue();
                setKey(key,value);
            }
        }

        for (int i = 0; i < snakeList.length; ++i) {
            snakeList[i].updateSnake();
            if (snakeList[i].isDead() || snakeList[i].isOutOfBounds(width, height) && snakeList[i].getDie() == false) {
                //Send update game
                //JOptionPane.showMessageDialog(this, "Information", "Do you want to reset this game !", JOptionPane.INFORMATION_MESSAGE);
                snakeList[i].setDie(true);
                //NioServer.mainSever.sendDie(snakeList[i].getPlayer().getId());
                //stop();
            }

            if (snakeList[i].getDie() != true) {
                for (int iBody = 0; iBody < snakeList[i].getLengthOfSnake(); iBody++) {
                    dataTable[snakeList[i].getSnakeBody()[iBody].y][snakeList[i].getSnakeBody()[iBody].x] = i + 1;
                }
            }

            if (snakeList[i].isCollidingWith(food)) {
                snakeList[i].grow();
                dataTable[(int) food.getPosition().getY()][(int) food.getPosition().getX()] = 0;
                food.setPosition(getRandomPosition());
                dataTable[(int) food.getPosition().getY()][(int) food.getPosition().getX()] = 5;
                String dataList = "";
                for(int j =0; j < snakeList.length;++j){
                    dataList += Integer.toString(snakeList[j].getScores()) + "~";
                }
                NioServer.mainSever.sendScoreToRooom(Long.toString(snakeList[0].getPlayer().getRoom().getId()), dataList);
            }

        }

        //Save game;
        String dataList = "";
        int num = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                dataList += Integer.toString(dataTable[i][j]);
            }
        }
        NioServer.mainSever.sendGameToRooom(Long.toString(snakeList[0].getPlayer().getRoom().getId()), dataList);
        //repaint();
    }

    private void clearDataTable() {
        dataTable = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                dataTable[i][j] = 0;
            }
        }
    }

    public void updateKey(long id, int key){
        synchronized (queue){
            queue.put(id,key);
        }
    }

    private void setKey(long id, int key){
        for(int i =0; i < snakeList.length;++i){
            if(snakeList[i].getPlayer().getId() == id && snakeList[i].getDie() == false ){
                if ((key == KeyEvent.VK_UP || key == KeyEvent.VK_W) && !snakeList[i].getKey().down && !snakeList[i].getKey().up) {
                    snakeList[i].getKey().setKeyUp();
                }
                else if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) && !snakeList[i].getKey().up && !snakeList[i].getKey().down) {
                    snakeList[i].getKey().setKeyDown();
                }
                else if ((key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) && !snakeList[i].getKey().right && !snakeList[i].getKey().left) {
                    snakeList[i].getKey().setKeyLeft();
                }
                else if ((key == KeyEvent.VK_RIGHT ||key == KeyEvent.VK_D) && !snakeList[i].getKey().left && !snakeList[i].getKey().right) {
                    snakeList[i].getKey().setKeyRight();
                }
            }
        }
    }

    private Point getRandomPosition() {
        return new Point(random.nextInt(width - 1), random.nextInt(height - 1));
    }

    public synchronized void Start() {
        running = true;
        thread = new Thread(this, "Display");
        thread.start();
    }

    public synchronized void Stop() {
        running = false;
//        try {
//            System.out.println("StopThread");
//            thread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

}

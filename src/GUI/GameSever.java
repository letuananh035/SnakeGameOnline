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

    private int READY_COUNT = 10;

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
            snakeList[i].updateSnake();
            snakeList[i].updateSnake();
        }

    }

    public int[][] getDataTable() {
        return dataTable;
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
            if(READY_COUNT > 0){
                Thread.sleep(delay * 10);
                READY_COUNT--;
                NioServer.mainSever.sendCountToRooom(Long.toString(snakeList[0].getPlayer().getRoom().getId()), READY_COUNT);
            }else{
                Thread.sleep(delay);
            }

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
            if(READY_COUNT <= 0)
                snakeList[i].updateSnake();
            if(snakeList[i].getDie() == false){
                if (snakeList[i].isDead() || snakeList[i].isOutOfBounds(width, height)) {
                    //Send update game
                    //JOptionPane.showMessageDialog(this, "Information", "Do you want to reset this game !", JOptionPane.INFORMATION_MESSAGE);
                    snakeList[i].setDie(true);
                    //NioServer.mainSever.sendDie(snakeList[i].getPlayer().getId());
                    //stop();
                }

                for(int j = 0; j < snakeList.length;++j){
                    if(j != i && !snakeList[j].getDie() && snakeList[i].dieCollideWithAnotherSnake(snakeList[j])){
                        snakeList[i].setDie(true);
                        if(snakeList[j].dieCollideWithAnotherSnake(snakeList[i])){
                            snakeList[j].setDie(true);
                        }
                    }
                }
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
        if(READY_COUNT > 0) return;
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
        NioServer.mainSever.sendCountToRooom(Long.toString(snakeList[0].getPlayer().getRoom().getId()), READY_COUNT);
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

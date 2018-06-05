package GUI;

import Client.NioClient;
import Client.RspHandler;
import Support.BlockData;
import Support.Model.Player;
import Support.Model.Room;
import Support.TypeBlock;
import Support.Utils.DataUtil;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

public class ClientLogin{
    public static JFrame frame;
    private static JFrame frameGame;
    private static JFrame framePassword;
    private JPanel mainPanel;
    private JButton btnExit;
    private JButton btnListUser;
    private JButton btnHost;
    private JPanel buttonPanel;
    private JList clientUsingList;
    private JTabbedPane tabbedPane;
    private JList RoomList;


    public static NioClient client;
    public static RspHandler handler;
    public static Thread threadClient;
    public static Thread threadRsp;
    public static ClientLogin mActivity;
    public static long joinRoom;
    public static Lobby roomLobby;
    public static Game roomGame;
    // JFrame frame;
    public ClientLogin() {

        try {
            handler = new RspHandler();
            client = new NioClient(InetAddress.getByName("192.168.1.211"), 9090, handler);
            client.setGame(this);
            threadClient = new Thread(client);
            threadClient.setDaemon(true);
            threadClient.start();
            threadRsp = new Thread(handler);
            threadRsp.start();
            handler.client = client;

            mActivity = this;

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {

        }

        btnHost.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                joinRoom = -1;
                RoomPassword roomPassword = new RoomPassword();
                roomPassword.createAndShowGUI();
                frame.hide();
            }
        });

        RoomList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList)evt.getSource();
                if (evt.getClickCount() >= 2) {
                    int index = list.locationToIndex(evt.getPoint());
                    String id = list.getSelectedValue().toString();
                    joinRoom = Long.parseLong(id);
                    RoomPassword roomPassword = new RoomPassword();
                    roomPassword.createAndShowGUI();
                    // Double-click detected
                }
            }
        });

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if(client != null && client.getPlayer() != null){
                    System.out.println("Close");
                    BlockData blockData = new BlockData(TypeBlock.DISCONNECT,Long.toString(client.getPlayer().getId()));
                    try {
                        client.send(blockData.toBytes());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            @Override
            public void windowClosed(WindowEvent e) {
                threadRsp.stop();
                threadClient.stop();
                super.windowClosed(e);
            }
        });

    }

    public void Disconnect(){
        RoomList.setModel(new DefaultListModel<String>());
        ((DefaultListModel) RoomList.getModel()).addElement("Không có kết nối với máy chủ");
    }

    public void UpdateCount(String data){
        if(roomGame != null)
            roomGame.updateCount(Integer.parseInt(data));
    }

    public void UpdateScore(String data){
        String[] list = data.split("~");
        if(roomGame != null)
            roomGame.UpdateScore(list);
    }

    public void StartGame(){
        roomLobby.StartGame();
    }

    public void UpdateGame(String data){
        if(roomGame != null)
            roomGame.redraw(data);
    }

    public void UpdateListPlayerInGame(String list){
        String[] listPlayer = DataUtil.parseRoom(list);
        ClientLogin.client.getPlayer().getRoom().setPlayerHost(new Player(Long.parseLong(listPlayer[0])));
        ClientLogin.client.getPlayer().getRoom().removeAll();
        for(int i =0; i < listPlayer.length;++i){
            ClientLogin.client.getPlayer().getRoom().addPlayer(new Player(Long.parseLong(listPlayer[i])));
        }
        if(!ClientLogin.roomGame.checkWaitingRestart()){
            ClientLogin.roomGame.updateListPlayer();
        }
    }

    public void UpdateLobby(String list){
        String[] listPlayer = DataUtil.parseRoom(list);
        if(roomLobby != null){
            roomLobby.updateList(listPlayer);
        }
        else{
            if(joinRoom != -1){
                Room room = new Room(joinRoom);
                ClientLogin.client.getPlayer().setRoom(room);
            }
            Lobby lobby = new Lobby();
            lobby.createAndShowGUI();
            frame.hide();
        }

    }

    public void show(){
        frame.show();
    }

    public void UpdateList(List<String> rooms) {
        RoomList.setModel(new DefaultListModel<String>());
        rooms.forEach(item -> {
            ((DefaultListModel) RoomList.getModel()).addElement(item);
        });
    }
    public void UpdateListPlayer(List<String> players) {
        clientUsingList.setModel(new DefaultListModel<String>());
        players.forEach(item -> {
            ((DefaultListModel) clientUsingList.getModel()).addElement(item);
        });

    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                frame = new JFrame("ClientLogin");
                frame.setBounds(300, 300, 400, 400);
                frame.setContentPane(new ClientLogin().mainPanel);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}

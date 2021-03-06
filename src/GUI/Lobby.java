package GUI;

import Client.NioClient;
import Support.BlockData;
import Support.Model.Player;
import Support.Model.Room;
import Support.TypeBlock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class Lobby {
    private JList playerWaitingList;
    private JButton btnStartGame;
    private JPanel lobbyPanel;
    private  JFrame jFrameGame;
    private static JFrame jFrameLobby;

    private  NioClient client;
    private ClientLogin clientLogin;

    public Lobby() {
        this.client = ClientLogin.client;
        this.clientLogin = ClientLogin.mActivity;
        ClientLogin.roomLobby = this;
        playerWaitingList.setModel(new DefaultListModel<String>());
        ((DefaultListModel) playerWaitingList.getModel()).addElement("[You] "+ Long.toString(ClientLogin.client.getPlayer().getId()));

        btnStartGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(((DefaultListModel) playerWaitingList.getModel()).get(0).toString().indexOf("[You]") >= 0){
                    BlockData blockData = new BlockData(TypeBlock.START,Long.toString(ClientLogin.client.getPlayer().getRoom().getId()));
                    try {
                        ClientLogin.client.send(blockData.toBytes());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });


    }

    public void Show(){
        playerWaitingList.setModel(new DefaultListModel<String>());
        for(int i =0; i <  ClientLogin.client.getPlayer().getRoom().getListPlayer().size();++i){
            String id = Long.toString(ClientLogin.client.getPlayer().getRoom().getListPlayer().get(i).getId());
            if(id.equals(Long.toString(ClientLogin.client.getPlayer().getId()))){
                ((DefaultListModel) playerWaitingList.getModel()).addElement("[You] "+ id);
            }else{
                ((DefaultListModel) playerWaitingList.getModel()).addElement(id);
            }
        }
        ClientLogin.roomGame = null;
        jFrameLobby.show();
        jFrameGame.dispose();
    }

    public void StartGame(){
        Game game = new Game(client);
        jFrameGame = new JFrame("ClientLogin");
        jFrameGame.setBounds(300, 300, 400, 400);
        jFrameGame.setContentPane(game);
        jFrameGame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        jFrameGame.pack();
        jFrameGame.setVisible(true);
        jFrameGame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                ClientLogin.roomLobby = null;
                ClientLogin.roomGame = null;
                BlockData blockData = new BlockData(TypeBlock.OUTROOM,Long.toString(ClientLogin.client.getPlayer().getId()));
                ClientLogin.client.getPlayer().setRoom(null);
                try {
                    ClientLogin.client.send(blockData.toBytes());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                clientLogin.show();
                jFrameGame.dispose();
            }

        });
        jFrameLobby.hide();
    }

    public void updateList(String[] list){
        playerWaitingList.setModel(new DefaultListModel<String>());
        ClientLogin.client.getPlayer().getRoom().removeAll();
        ClientLogin.client.getPlayer().getRoom().setPlayerHost(new Player(Long.parseLong(list[0])));
        for(int i =0; i < list.length;++i){
            if(list[i].equals(Long.toString(ClientLogin.client.getPlayer().getId()))){
                ((DefaultListModel) playerWaitingList.getModel()).addElement("[You] "+ list[i]);
            }else{
                ((DefaultListModel) playerWaitingList.getModel()).addElement(list[i]);
            }
            ClientLogin.client.getPlayer().getRoom().addPlayer(new Player(Long.parseLong(list[i])));
        }
    }


    public static void createAndShowGUI() {
        String name = Long.toString(ClientLogin.client.getPlayer().getRoom().getId());
        jFrameLobby = new JFrame(name);
        jFrameLobby.setContentPane(new Lobby().lobbyPanel);
        //jFrameLobby.setSize(new Dimension(200,200));
        jFrameLobby.setBounds(300,300,200,300);
        jFrameLobby.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        jFrameLobby.pack();
        jFrameLobby.setVisible(true);
        jFrameLobby.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                ClientLogin.roomLobby = null;
                BlockData blockData = new BlockData(TypeBlock.OUTROOM,Long.toString(ClientLogin.client.getPlayer().getId()));
                ClientLogin.client.getPlayer().setRoom(null);
                try {
                    ClientLogin.client.send(blockData.toBytes());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                ClientLogin.mActivity.show();
                jFrameLobby.dispose();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
            }
        });

       // jFrameLobby.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
     //   jFrameLobby.addWindowListener(new WindowAdapter() {
       //     @Override
       //     public void windowClosing(WindowEvent windowEvent) {

        //    }
       // });
    }


    public JPanel getPanel(){
        return lobbyPanel;
    }
}

package GUI;

import Client.NioClient;
import Support.BlockData;
import Support.TypeBlock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class Lobby {
    private JList list1;
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
        btnStartGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

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
                        clientLogin.show();
                        jFrameGame.dispose();
                    }

                });
                jFrameLobby.dispose();
            }
        });


    }

    public void updateList(String[] list){
        list1.setModel(new DefaultListModel<String>());
        for(int i =0; i < list.length;++i){
            ((DefaultListModel) list1.getModel()).addElement(list[i]);
        }
    }


    public static void createAndShowGUI() {

        jFrameLobby = new JFrame("RoomPassword");
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

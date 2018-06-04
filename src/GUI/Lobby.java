package GUI;

import Client.NioClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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

        playerWaitingList.setModel(new DefaultListModel<String>());
       // rooms.forEach(item -> {
        ((DefaultListModel) playerWaitingList.getModel()).addElement(client.getPlayer().getId());


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


    public static void createAndShowGUI() {

        jFrameLobby = new JFrame("RoomPassword");
        jFrameLobby.setContentPane(new Lobby().lobbyPanel);
        //jFrameLobby.setSize(new Dimension(200,200));
        jFrameLobby.setBounds(300,300,200,300);
        jFrameLobby.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrameLobby.pack();
        jFrameLobby.setVisible(true);

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

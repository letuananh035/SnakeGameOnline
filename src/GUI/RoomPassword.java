package GUI;

import Client.NioClient;
import Support.BlockData;
import Support.TypeBlock;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class RoomPassword extends JPanel {
    private JPanel PasswordPanel;
    private JPasswordField txtPasswordField;
    private JButton btnEnterPassword;

    private static  JFrame framePassword;
    private NioClient client;
    private ClientLogin clientLogin;
    public RoomPassword() {
        client = ClientLogin.client;
        clientLogin = ClientLogin.mActivity;
        btnEnterPassword.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                String text = "123213";

                BlockData blockData = new BlockData(TypeBlock.CREATEROOM, Long.toString(client.getPlayer().getId()) + "~" + text);
                try {
                    client.send(blockData.toBytes());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                Lobby lobby = new Lobby();
                lobby.createAndShowGUI();

                framePassword.dispose();
        }
        });
    }

    public static void createAndShowGUI() {

        framePassword = new JFrame("RoomPassword");
        framePassword.setContentPane(new RoomPassword().PasswordPanel);
        framePassword.setBounds(300,300,200,200);
        framePassword.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        framePassword.pack();
        framePassword.setVisible(true);

    }

    public JPanel getPanel(){
        return PasswordPanel;
    }
}

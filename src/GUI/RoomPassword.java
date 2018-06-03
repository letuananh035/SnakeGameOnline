package GUI;

import Client.NioClient;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class RoomPassword {
    private JPanel PasswordPanel;
    private JPasswordField txtPasswordField;
    private JButton btnEnterPassword;

    private static  JFrame framePassword;


    public RoomPassword(NioClient client) {


        btnEnterPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Lobby lobby = new Lobby(client);
                lobby.createAndShowGUI(client);

                framePassword.dispose();
        }
        });
    }

    public static void createAndShowGUI(NioClient client) {

        framePassword = new JFrame("RoomPassword");
        framePassword.setContentPane(new RoomPassword(client).PasswordPanel);
        framePassword.setBounds(300,300,200,200);
        framePassword.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        framePassword.pack();
        framePassword.setVisible(true);

    }

    public JPanel getPanel(){
        return PasswordPanel;
    }
}

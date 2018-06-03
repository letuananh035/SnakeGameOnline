package GUI;

import Client.NioClient;
import Client.RspHandler;
import Support.BlockData;
import Support.TypeBlock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;

public class ClientLogin {
    public static JFrame frame;
    private static JFrame frameGame;
    private static JFrame framePassword;
    private JPanel mainPanel;
    private JButton btnExit;
    private JButton btnJoin;
    private JButton btnHost;
    private JPanel buttonPanel;
    private JList clientUsingList;


    NioClient client;

    // JFrame frame;
    public ClientLogin() {


        try {
            client = new NioClient(InetAddress.getByName("localhost"), 9090);
            Thread t = new Thread(client);
            t.setDaemon(true);
            t.start();

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

                RspHandler handler = new RspHandler();
                BlockData blockData = new BlockData(TypeBlock.START, "Create a new room");

                try {
                    client.send(blockData.toBytes(), handler);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                handler.waitForResponse();
                //frame.setContentPane(new Game());

                RoomPassword roomPassword = new RoomPassword(client);

                roomPassword.createAndShowGUI(client);

                frame.dispose();

            }
        });

        btnJoin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
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

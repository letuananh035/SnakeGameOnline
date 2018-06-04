package GUI;

import Client.NioClient;
import Client.RspHandler;
import Support.BlockData;
import Support.Model.Room;
import Support.TypeBlock;
import Support.Utils.DataUtil;

import javax.swing.*;
import java.awt.*;
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
    private JButton btnJoin;
    private JButton btnHost;
    private JPanel buttonPanel;
    private JList clientUsingList;


    public static NioClient client;
    public static RspHandler handler;
    public static Thread threadClient;
    public static Thread threadRsp;
    public static ClientLogin mActivity;
    public static long joinRoom;
    public static Lobby roomLobby;
    // JFrame frame;
    public ClientLogin() {

        try {
            handler = new RspHandler();
            client = new NioClient(InetAddress.getByName("localhost"), 9090, handler);
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

        clientUsingList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList)evt.getSource();
                if (evt.getClickCount() == 2) {
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
                if(client != null || client.getPlayer() != null){
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
                super.windowClosed(e);
                threadRsp.stop();
                threadClient.stop();
            }
        });

        btnJoin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

    }

    public void UpdateLobby(String list){
        String[] listPlayer = DataUtil.parseRoom(list);
        if(roomLobby != null){
            roomLobby.updateList(listPlayer);
        }
        else{
            Lobby lobby = new Lobby();
            lobby.createAndShowGUI();
            frame.hide();
        }

    }


    public void show(){
        frame.show();
    }

    public void UpdateList(List<String> rooms) {
        clientUsingList.setModel(new DefaultListModel<String>());
        rooms.forEach(item -> {
            ((DefaultListModel) clientUsingList.getModel()).addElement(item);
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


    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.setMinimumSize(new Dimension(200, 200));
        mainPanel.setPreferredSize(new Dimension(400, 200));
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(buttonPanel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        buttonPanel.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        buttonPanel.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnExit = new JButton();
        btnExit.setText("Exit");
        panel2.add(btnExit, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnHost = new JButton();
        btnHost.setMargin(new Insets(2, 14, 2, 20));
        btnHost.setText("Host");
        buttonPanel.add(btnHost, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnJoin = new JButton();
        btnJoin.setText("Join");
        buttonPanel.add(btnJoin, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        clientUsingList = new JList();
        mainPanel.add(clientUsingList, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}

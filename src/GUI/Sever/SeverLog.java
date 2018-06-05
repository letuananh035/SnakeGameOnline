package GUI.Sever;

import Server.EchoWorker;
import Server.NioServer;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class SeverLog {
    private JPanel panel1;
    private JList list1;
    private Thread threadWorker = null;
    private Thread threadSever = null;
    public static JFrame frame;
    public SeverLog() {
        list1.setModel(new DefaultListModel<String>());
        ((DefaultListModel) list1.getModel()).addElement("Sever create completed!");
        try {
            EchoWorker worker = new EchoWorker();
            threadWorker =  new Thread(worker);
            threadWorker.start();
            NioServer sever =  new NioServer(null, 9090, worker);
            sever.setSeverLog(this);
            threadSever = new Thread(sever);
            threadSever.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {

            }

            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                threadWorker.stop();
                threadSever.stop();
            }
        });
    }

    public void UpdateList(String item) {
        ((DefaultListModel) list1.getModel()).addElement(item);
    }

    public static void main(String[] args) {
        frame = new JFrame("SeverLog");
        frame.setContentPane(new SeverLog().panel1);
        frame.setBounds(300, 300, 400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
    }

}

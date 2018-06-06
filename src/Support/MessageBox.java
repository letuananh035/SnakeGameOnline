package Support;

import javax.swing.*;
import java.awt.*;

public class MessageBox {


    public static void showError(String content){
        JOptionPane.showMessageDialog(null, content,"Error",JOptionPane.ERROR_MESSAGE);  ;
    }

    public static void showInfo(String content){
        JOptionPane.showMessageDialog(null, content,"Information",JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showWarning(String content){
        JOptionPane.showMessageDialog(null, content,"Warning",JOptionPane.WARNING_MESSAGE);
    }
}

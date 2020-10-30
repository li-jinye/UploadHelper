package util;

import gui.Main;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * @author LiJinye
 * @date 2020/10/21
 */
public class Util {


    static Main href = Main.href;

    public static void info(String text) {
        JOptionPane.showMessageDialog(null, text, "提示", JOptionPane.INFORMATION_MESSAGE);
        href.success(text);
    }

    public static void error(String text) {
        JOptionPane.showMessageDialog(null, text, "错误", JOptionPane.ERROR_MESSAGE);
        href.error(text);
    }

    static JFileChooser chooser = new JFileChooser();

    static {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("请选择war包,一般在target目录下", "war");
        chooser.setFileFilter(filter);
    }

    public static String FileChooser() {
        int returnVal = chooser.showOpenDialog(new JPanel());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println(chooser.getSelectedFile().getPath());
            return chooser.getSelectedFile().getPath();
        }
        return null;
    }
}

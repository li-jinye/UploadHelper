package lintener;

import gui.Main;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * @author LiJinye
 * @date 2020/10/27
 */
public class Listener implements WindowListener {
    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        Main.href.saveSetting();
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
}

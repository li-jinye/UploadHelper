package gui;

import po.LinkPo;
import util.Util;

import javax.swing.*;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author LiJinye
 * @date 2020/9/30
 */
public class LinkChooser {
    Main href = Main.href;


    private JPanel main;
    private JButton cancelButton;
    private JButton confirmButton;
    private JComboBox<String> comboBox;
    private JFrame frame;

    private Integer index;

    void init() {
        List<LinkPo> list = href.linkList;
        comboBox.removeAllItems();
        IntStream.range(0, list.size()).forEach(i -> comboBox.addItem(i + " " + list.get(i).getInfo()));
        comboBox.setSelectedIndex(list.size() - 1);
    }

    public LinkChooser() {
        frame = new JFrame("选择连接");
        frame.setContentPane(main);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        //图标
        ImageIcon imageIcon = new ImageIcon(Main.path + "/lib/favicon.png");
        frame.setIconImage(imageIcon.getImage());
        init();

        cancelButton.addActionListener(e -> frame.dispose());
        confirmButton.addActionListener(e -> {
            int index = comboBox.getSelectedIndex();
            if (index == 0) {
                Util.error("选择无效");
                return;
            }
            href.setPackLink(index);
            frame.dispose();
        });
    }

    public static void main(String[] args) {
        new LinkChooser();
    }

}

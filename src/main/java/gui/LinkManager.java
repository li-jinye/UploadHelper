package gui;

import cn.hutool.core.util.StrUtil;
import po.LinkPo;
import util.Util;
import util.SshUtil;

import javax.swing.*;

/**
 * @author LiJinye
 * @date 2020/9/30
 */
public class LinkManager {
    Main href = Main.href;


    private JPanel main;
    private JTextField ip;
    private JTextField username;
    private JTextField port;
    private JTextField password;
    private JButton testButton;
    private JButton cancelButton;
    private JButton confirmButton;
    private JTextField name;
    private final JFrame frame;

    private Integer index;
    private String uuid;

    public void edit(Integer index) {
        this.index = index;
        LinkPo link = href.linkList.get(index);
        name.setText(link.getName());
        ip.setText(link.getIp());
        port.setText(link.getPort());
        username.setText(link.getUsername());
        password.setText(link.getPassword());
        uuid = link.getUuid();
    }

    public LinkManager() {
        frame = new JFrame("新增/修改连接");
        frame.setContentPane(main);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        //图标
        ImageIcon imageIcon = new ImageIcon(Main.path + "/lib/favicon.png");
        frame.setIconImage(imageIcon.getImage());
        cancelButton.addActionListener(e -> frame.dispose());
        confirmButton.addActionListener(e -> {
            if (!StrUtil.isAllNotEmpty(name.getText(), ip.getText(), port.getText(), username.getText(), password.getText())) {
                Util.error("不能为空！");
                return;
            }
            LinkPo linkPo = new LinkPo().setName(name.getText()).setIp(ip.getText()).setPort(port.getText())
                    .setUsername(username.getText()).setPassword(password.getText());
            //新增/修改
            if (index == null) {
                href.linkList.add(linkPo);
            } else {
                linkPo.setUuid(uuid);
                href.linkList.set(index, linkPo);
            }
            href.setComboBox();
            href.setPackTexts(href.currPackIndex);
            href.setPackJList();
            frame.dispose();
        });
        testButton.addActionListener(e -> {
            if (!StrUtil.isAllNotEmpty(name.getText(), ip.getText(), port.getText(), username.getText(), password.getText())) {
                Util.error("不能为空！");
                return;
            }
            SshUtil.TestAndMsg(ip.getText(), Integer.parseInt(port.getText()), username.getText(), password.getText());
        });
    }

}

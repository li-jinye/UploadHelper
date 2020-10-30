package gui;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.ssh.JschUtil;
import cn.hutool.extra.ssh.Sftp;
import com.jcraft.jsch.Session;
import po.LinkPo;
import util.Util;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author LiJinye
 * @date 2020/9/30
 */
public class LinkFileChooser {
    Main href = Main.href;

    LinkPo link;
    private JPanel main;
    private JButton cancelButton;
    private JButton confirmButton;
    private JList<String> jList;
    private JButton backButton;
    private JTextField pathTextField;
    private JFrame frame;

    private Integer index;

    private Session session;
    private Sftp sftp;

    private String path = "/";


    public void lsDir() {
        List<String> ls = sftp.lsDirs(path);
        jList.setListData(ls.toArray(new String[0]));
    }

    public void selcet() {
        String value = jList.getSelectedValue();
        path = StrUtil.removeSuffix(path, "/") + "/" + value;
        pathTextField.setText(path);
        lsDir();
    }

    public void back() {
        path = path.substring(0, path.lastIndexOf("/"));
        if (StrUtil.isEmpty(path)) {
            path = "/";
        }
        pathTextField.setText(path);
        lsDir();
    }


    void init() {
        try {
            session = JschUtil.createSession(link.getIp(), Integer.parseInt(link.getPort()), link.getUsername(), link.getPassword());
            session.setTimeout(5000);
            session.connect();
            String pwd = JschUtil.exec(session, "pwd", Charset.defaultCharset());
            System.out.println(pwd);
            sftp = JschUtil.createSftp(session);
            lsDir();
        } catch (Exception e) {
            Util.error("连接出错了,错误信息:" + e.getMessage());
            frame.dispose();
        }
    }

    public LinkFileChooser(LinkPo link) {
        frame = new JFrame("选择服务器目录");
        frame.setContentPane(main);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        //图标
        ImageIcon imageIcon = new ImageIcon(Main.path + "/lib/favicon.png");
        frame.setIconImage(imageIcon.getImage());
        this.link = link;
        init();

        cancelButton.addActionListener(e -> frame.dispose());
        confirmButton.addActionListener(e -> {
            href.setPackServer(pathTextField.getText());
            sftp.close();
            JschUtil.close(session);
            frame.dispose();
        });
        jList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2) {
                    selcet();
                }
            }
        });
        backButton.addActionListener(e -> back());
    }
}
